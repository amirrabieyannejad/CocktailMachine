// general system settings
#define BLE_NAME             	"Cocktail Machine ESP32"	// bluetooth server name
#define CORE_DEBUG_LEVEL     	4                       	// 1 = error; 3 = info ; 4 = debug
const unsigned int VERSION   	= 8;                    	// version number (used for configs etc)
                             	                        	
const unsigned char MAX_PUMPS	= 1 + 4*8;              	// maximum number of supported pumps;
                             	                        	
const float LIQUID_CUTOFF    	= 0.1;                  	// minimum amount of liquid we round off
const float EMPTY_SCALE      	= 10.0;                 	// what counts as an empty scale
const float CONTAINER_WEIGHT 	= 50.0;                 	// what counts as a container
                             	                        	
const bool AUTOMATIC_SCALE   	= false;                	// progress automatically based on scale changes?
                             	                        	
const int CAL_TIME1          	= 10 * 1000;            	// calibration times to use (in ms)
const int CAL_TIME2          	= 20 * 1000;            	

// general chip functionality
#include <Arduino.h>
#include <Preferences.h>
#include <SPI.h>
#include <Wire.h>

// pinout
typedef const unsigned char Pin;
Pin PIN_HX711_DOUT      	= 4;
Pin PIN_HX711_SCK       	= 5;
                        	
Pin PIN_SDA             	= 14;
Pin PIN_SCL             	= 32;
                        	
Pin PIN_BUILTIN_PUMP_IN1	= 33;
Pin PIN_BUILTIN_PUMP_IN2	= 27;

// c++ standard library
#include <algorithm>
#include <deque>
#include <forward_list>
#include <inttypes.h>
#include <map>
#include <queue>
#include <sys/time.h>
#include <unordered_map>
#include <unordered_set>

// json
#include <ArduinoJson.h>

// scale
#include <HX711.h>

// IO extension cards
#include <PCF8574.h>

// bluetooth
#include <BLE2902.h>
#include <BLEAddress.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>

// debugging info
#define debug(...)	log_d(__VA_ARGS__)
#define error(...)	log_e(__VA_ARGS__)
#define info(...) 	log_i(__VA_ARGS__)
#define warn(...) 	log_w(__VA_ARGS__)

// easier time constants
typedef uint64_t dur_t;
#define MS(n)   ((time_t) (n))
#define S(n)    ((time_t) (MS(n)  * 1000LL))
#define MIN(n)  ((time_t) (S(n)   * 60LL))
#define HOUR(n) ((time_t) (MIN(N) * 60LL))

// TODO avoid passing around char* everywhere,
// and use either proper strings with memory management or pass buffers explicitly as needed
// SafeString might be a good library to solve this problem

// internal buffers
#define BUF_RESPONSE	100 	// max (json) response
#define BUF_JSON    	1000	// max json input
#define BUF_PREF_KEY	15  	// max key length of preferences, defined by ESP32 stdlib

// machine parts
typedef int32_t User;

#define USER_ADMIN  	User(0)
#define USER_UNKNOWN	User(-1)

#define J(str) ("\"" str "\"")

// recipe states
enum struct RecipeState {
  ready,
  wait_container,
  mixing,
  pumping,
  cocktail_done,
};

const char* rec_state_str[] = {
  J("ready"),
  J("waiting for container"),
  J("mixing"),
  J("pumping"),
  J("cocktail done"),
};

// calibration states
enum struct CalibrationState {
  inactive,
  empty,
  weight,
  pumps,
  calc,
  done,
};

const char* cal_state_str[] = {
  J("no calibration active"),
  J("calibration empty container"),
  J("calibration known weight"),
  J("calibration pumps"),
  J("calibration calculation"),
  J("calibration done"),
};

// return codes
enum struct Retcode {
  init,
  success,
  processing,
  unsupported,
  unauthorized,
  invalid,
  too_big,
  incomplete,
  unknown_command,
  missing_command,
  wrong_comm,
  invalid_slot,
  invalid_volume,
  invalid_weight,
  invalid_times,
  insufficient,
  missing_liquid,
  missing_recipe,
  duplicate_recipe,
  missing_ingredients,
  invalid_calibration,
  user_id, // nb: the return of the user id is handled separately!
  cant_start_recipe,
  cant_take_cocktail,
  invalid_cal_state,
};

const char* retcode_str[] = {
  J("init"),
  J("ok"),
  J("processing"),
  J("unsupported"),
  J("unauthorized"),
  J("invalid json"),
  J("message too big"),
  J("missing arguments"),
  J("unknown command"),
  J("command missing even though it parsed right"),
  J("wrong comm channel"),
  J("invalid pump slot"),
  J("invalid volume"),
  J("invalid weight"),
  J("invalid times"),
  J("insufficient amounts of liquid available"),
  J("liquid unavailable"),
  J("recipe not found"),
  J("recipe already exists"),
  J("missing ingredients"),
  J("invalid calibration data"),
  J("new user id"), // placeholder label
  J("can't start recipe yet"),
  J("can't take cocktail yet"),
  J("calibration command invalid at this time"),
};

struct PumpSlot {
  Pin in1;
  Pin in2;
  PCF8574 *pcf;

  PumpSlot(PCF8574 *pcf, Pin in1, Pin in2);
};

struct Pump {
  PumpSlot *slot;
  String liquid;
  float volume;

  bool calibrated;
  dur_t time_init;
  dur_t time_reverse;
  float rate;

  float cal_volume[2];

  Pump(PumpSlot *slot, String liquid, float volume, dur_t time_init, dur_t time_reverse, float rate, bool calibrated)
    : slot(slot), liquid(liquid), volume(volume),
      time_init(time_init), time_reverse(time_reverse), rate(rate),
      calibrated(calibrated), cal_volume()
    {};

  Retcode drain(float amount);
  Retcode refill(float volume);
  Retcode empty();

  Retcode run(dur_t time, bool reverse);
  void start(bool reverse);
  void stop();
  void pump(float amount);

  Retcode calibrate(dur_t time1, dur_t time2);
  Retcode calibrate(dur_t time1, dur_t time2, float volume1, float volume2);
};

struct Ingredient {
  String name;
  float amount;
};

struct Recipe {
  String name;
  std::deque<Ingredient> ingredients;
  bool can_make;

  Recipe(String name, std::deque<Ingredient> ingredients) : name(name), ingredients(ingredients), can_make(false) {};
};

struct ActiveRecipe {
  User user;
  String name;
  std::deque<Ingredient> ingredients;
  RecipeState state;

  ActiveRecipe(User user, String name, std::deque<Ingredient> ingredients, RecipeState state) : user(user), name(name), ingredients(ingredients), state(state) {};
};

// services
struct Service {
  BLECharacteristic *ble_char;
  Service(const char *uuid_service, const char *uuid_char, const uint32_t props, const int num_chars);
};

struct ID : Service {
  ID(const char *uuid_char, const String init_value);
  void update(const String value);
};

struct Status : Service {
  Status(const char *uuid_char, const String init_value);
  void update(const String value, bool force=true);
};

struct Comm : Service {
  // TODO conn_id isn't stable across reconnections, so it might be better to switch to MAC or something similar for some persistence
  std::unordered_map<uint16_t, String*> responses;
  Comm(const char *uuid_char);

  void respond(uint16_t id, Retcode ret);
  void respond(uint16_t id, String *ret);
  bool is_id(int id);
};

struct ServerCB : BLEServerCallbacks {
  void onConnect(BLEServer *server);
  void onConnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
  void onDisconnect(BLEServer *server);
  void onDisconnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
};

struct CharCB: BLECharacteristicCallbacks {
  void onRead(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param);
};

struct CommCB: BLECharacteristicCallbacks {
  Comm *comm;
  CommCB(Comm *comm) : comm(comm) {};

  void onRead(BLECharacteristic  *ble_char, esp_ble_gatts_cb_param_t *param);
  void onWrite(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param);
};

// convenient grouping of all statuses / comms

#define ID_LIQUIDS  	0
#define ID_PUMPS    	1
#define ID_STATE    	2
#define ID_RECIPES  	3
#define ID_COCKTAIL 	4
#define ID_TIMESTAMP	5
#define ID_USER     	6
#define ID_SCALE    	7
#define ID_ERROR    	8
#define NUM_STATUS  	9
                    	
#define ID_MSG_USER 	0
#define ID_MSG_ADMIN	1
#define NUM_COMM    	2
                    	
#define ID_NAME     	0
#define NUM_ID      	1

// UUIDs

#define UUID_STATUS          	"0f7742d4-ea2d-43c1-9b98-bb4186be905d"
#define UUID_STATUS_STATE    	"e9e4b3f2-fd3f-3b76-8688-088a0671843a"
#define UUID_STATUS_LIQUIDS  	"fc60afb0-2b00-3af2-877a-69ae6815ca2f"
#define UUID_STATUS_PUMPS    	"1a9a598a-17ce-3fcd-be03-40a48587d04e"
#define UUID_STATUS_RECIPES  	"9ede6e03-f89b-3e52-bb15-5c6c72605f6c"
#define UUID_STATUS_COCKTAIL 	"7344136f-c552-3efc-b04f-a43793f16d43"
#define UUID_STATUS_TIMESTAMP	"586b5706-5856-34e1-ad17-94f840298816"
#define UUID_STATUS_USER     	"2ce478ea-8d6f-30ba-9ac6-2389c8d5b172"
#define UUID_STATUS_SCALE    	"ff18f0ac-f039-4cd0-bee3-b546e3de5551"
#define UUID_STATUS_ERROR    	"2e03aa0c-b25f-456a-a327-bd175771111a"
                             	
#define UUID_COMM            	"dad995d1-f228-38ec-8b0f-593953973406"
#define UUID_COMM_USER       	"eb61e31a-f00b-335f-ad14-d654aac8353d"
#define UUID_COMM_ADMIN      	"41044979-6a5d-36be-b9f1-d4d49e3f5b73"
                             	
#define UUID_ID              	"8ccbf239-1cd2-4eb7-8872-1cb76c980d14"
#define UUID_ID_NAME         	"c0605c38-3f94-33f6-ace6-7a5504544a80"

// commands
struct Command {
  virtual Retcode execute();
  virtual const char* cmd_name();
  virtual bool is_valid_comm(Comm *comm);
};

#define def_cmd(name, channel)                                          \
  static constexpr char *json_name = name;                              \
  const char* cmd_name() override { return json_name; }                 \
  Retcode execute() override;                                        \
  bool is_valid_comm(Comm *comm) override {                             \
    return comm->is_id(ID_MSG_ ## channel);                             \
  }                                                                     \
  // [the remaining methods, particularly the constructor, go here]

struct CmdTest : public Command {
  def_cmd("test", USER);
};

struct CmdInitUser : public Command {
  def_cmd("init_user", USER);
  String name;
  CmdInitUser(String name) : name(name) {}
};

struct CmdReset : public Command {
  def_cmd("reset", ADMIN);
  User user;
  CmdReset(User user) : user(user) {}
};

struct CmdResetError : public Command {
  def_cmd("reset_error", ADMIN);
  User user;
  CmdResetError(User user) : user(user) {}
};

struct CmdQueueRecipe : public Command {
  def_cmd("queue_recipe", USER);
  User user;
  String recipe;
  CmdQueueRecipe(User user, String recipe) : user(user), recipe(recipe) {}
};

struct CmdAddLiquid : public Command {
  def_cmd("add_liquid", USER);
  User user;
  String liquid;
  float volume;
  CmdAddLiquid(User user, String liquid, float volume) : user(user), liquid(liquid), volume(volume) {}
};

struct CmdStartRecipe : public Command {
  def_cmd("start_recipe", USER);
  User user;
  CmdStartRecipe(User user) : user(user) {}
};

struct CmdCancelRecipe : public Command {
  def_cmd("cancel_recipe", USER);
  User user;
  CmdCancelRecipe(User user) : user(user) {}
};

struct CmdTakeCocktail : public Command {
  def_cmd("take_cocktail", USER);
  User user;
  CmdTakeCocktail(User user) : user(user) {}
};

struct CmdDefinePump : public Command {
  def_cmd("define_pump", ADMIN);
  User user;
  int32_t slot;
  String liquid;
  float volume;
  CmdDefinePump(User user, const int32_t slot, String liquid, const float volume) :
    user(user), slot(slot), liquid(liquid), volume(volume) {}
};

struct CmdEditPump : public Command {
  def_cmd("edit_pump", ADMIN);
  User user;
  int32_t slot;
  String liquid;
  float volume;
  CmdEditPump(User user, const int32_t slot, String liquid, const float volume) :
    user(user), slot(slot), liquid(liquid), volume(volume) {}
};

struct CmdRefillPump : public Command {
  def_cmd("refill_pump", ADMIN);
  User user;
  int32_t slot;
  float volume;
  CmdRefillPump(User user, const int32_t slot, const float volume) :
    user(user), slot(slot), volume(volume) {}
};

struct CmdDefineRecipe : public Command {
  def_cmd("define_recipe", USER);
  User user;
  String name;
  std::deque<Ingredient> ingredients;
  CmdDefineRecipe(User user, String name, std::deque<Ingredient> ingredients)
    : user(user), name(name), ingredients(ingredients) {}
};

struct CmdEditRecipe : public Command {
  def_cmd("edit_recipe", USER);
  User user;
  String name;
  std::deque<Ingredient> ingredients;
  CmdEditRecipe(User user, String name, std::deque<Ingredient> ingredients)
    : user(user), name(name), ingredients(ingredients) {}
};

struct CmdDeleteRecipe : public Command {
  def_cmd("delete_recipe", USER);
  User user;
  String name;
  CmdDeleteRecipe(User user, String name) : user(user), name(name) {}
};

struct CmdRestart : public Command {
  def_cmd("restart", ADMIN);
  User user;
  CmdRestart(User user) : user(user) {}
};

struct CmdFactoryReset : public Command {
  def_cmd("factory_reset", ADMIN);
  User user;
  CmdFactoryReset(User user) : user(user) {}
};

struct CmdClean : public Command {
  def_cmd("clean", ADMIN);
  User user;
  CmdClean(User user) : user(user) {}
};

struct CmdRunPump : public Command {
  def_cmd("run_pump", ADMIN);
  User user;
  int32_t slot;
  dur_t time;
  CmdRunPump(User user, int32_t slot, dur_t time) : user(user), slot(slot), time(time) {}
};

struct CmdCalibrationStart : public Command {
  def_cmd("calibration_start", ADMIN);
  User user;
  CmdCalibrationStart(User user) : user(user) {};
};

struct CmdCalibrationCancel : public Command {
  def_cmd("calibration_cancel", ADMIN);
  User user;
  CmdCalibrationCancel(User user) : user(user) {};
};

struct CmdCalibrationFinish : public Command {
  def_cmd("calibration_finish", ADMIN);
  User user;
  CmdCalibrationFinish(User user) : user(user) {};
};

struct CmdCalibrationAddEmpty : public Command {
  def_cmd("calibration_add_empty", ADMIN);
  User user;
  CmdCalibrationAddEmpty(User user) : user(user) {};
};

struct CmdCalibrationAddWeight : public Command {
  def_cmd("calibration_add_weight", ADMIN);
  User user;
  float weight;
  CmdCalibrationAddWeight(User user, float weight) : user(user), weight(weight) {};
};

struct CmdCalibratePump : public Command {
  def_cmd("calibrate_pump", ADMIN);
  User user;
  int32_t slot;
  dur_t time1;
  dur_t time2;
  float volume1;
  float volume2;
  CmdCalibratePump(User user, int32_t slot, dur_t time1, dur_t time2 ,float volume1, float volume2)
    : user(user), slot(slot), time1(time1), time2(time2), volume1(volume1), volume2(volume2) {}
};

struct CmdSetPumpTimes : public Command {
  def_cmd("set_pump_times", ADMIN);
  User user;
  int32_t slot;
  dur_t time_init;
  dur_t time_reverse;
  float rate;

  CmdSetPumpTimes(User user, int32_t slot, dur_t time_init, dur_t time_reverse, float rate)
    : user(user), slot(slot), time_init(time_init), time_reverse(time_reverse), rate(rate) {}
};

struct CmdCalibrateScale : public Command {
  def_cmd("calibrate_scale", ADMIN);
  User user;
  float weight;
  CmdCalibrateScale(User user, float weight) : user(user), weight(weight) {}
};

struct CmdSetScaleFactor : public Command {
  def_cmd("set_scale_factor", ADMIN);
  User user;
  float factor;
  CmdSetScaleFactor(User user, float factor) : user(user), factor(factor) {}
};

struct CmdTareScale : public Command {
  def_cmd("tare_scale", ADMIN);
  User user;
  CmdTareScale(User user) : user(user) {}
};

struct Parsed {
  Command *command;
  Retcode err;
};

struct CommandQueued {
  Command *command;
  Comm *comm;
  uint16_t conn_id;
};

// global state

time_t config_state = 0;
Preferences preferences;

bool scale_available  = false;
bool scale_calibrated = false;
HX711 scale;

PumpSlot* pump_slots[MAX_PUMPS];
Pump* pumps[MAX_PUMPS];

std::map<User, String> users;

BLEServer *ble_server;
ID*     all_id[NUM_ID];
Status* all_status[NUM_STATUS];
Comm*   all_comm[NUM_COMM];

std::forward_list<Recipe> recipes;
std::deque<Ingredient> cocktail;

std::queue<CommandQueued> command_queue;
std::deque<ActiveRecipe*> recipe_queue;

CalibrationState cal_state;
int cal_pass;
int cal_pump;

Retcode error_state;
User error_user;

// function declarations

// various sleeps
time_t timestamp_ms(void);
void sleep_idle(dur_t duration);
void sleep_light(dur_t duration);
void sleep_deep(dur_t duration);
void error_loop(void);

// led feedback (if possible)
#if defined(LED_BUILTIN)
void led_on(void);
void led_off(void);
void blink_leds(dur_t on, dur_t off);
#endif

// configs
bool config_save(void);
bool config_load(void);
bool config_clear(void);

// bluetooth
bool ble_start(void);
void ble_stop(void);

// scale
float scale_weigh(void);
float scale_estimate(dur_t init, dur_t time, float rate);
bool scale_empty(void);
bool scale_has_container(void);
Retcode scale_calibrate(float weight);
Retcode scale_tare(void);
Retcode scale_set_factor(float factor);

// recipes
Retcode add_to_recipe_queue(Recipe *recipe, User user);
Retcode reset_cocktail(void);
Retcode check_recipe(String name, std::deque<Ingredient> ingredients);

// command processing
Retcode add_to_command_queue(const String json, uint16_t conn_id);
Parsed parse_command(const String json);
bool is_admin(User user);
User current_user(void);

// update machine state
void update_cocktail(void);
void update_state(void);
void update_liquids(void);
void update_scale(void);
void update_recipes(void);
void update_config_state(time_t ts);
void update_user(void);
void update_possible_recipes(void);
void update_error(Retcode error, User user);

// init

void setup() {
  Wire.begin(PIN_SDA, PIN_SCL);

  { // setup serial communication
    Serial.begin(115200);
    while (!Serial) { sleep_idle(MS(10)); }
    sleep_idle(S(1)); // make sure we don't miss any output

    info("Cocktail Machine v%d starting up", VERSION);
  }

  { // setup pins
#if defined(LED_BUILTIN)
    pinMode(LED_BUILTIN, OUTPUT);
#endif
  }

  // setup eeprom
  preferences.begin("CoMa", false);

  { // setup pump slots
    for (int i=0; i<MAX_PUMPS; i++)	pump_slots[i]	= NULL;
    int used_slots = 0;
    pump_slots[used_slots++] = new PumpSlot{
      NULL,
      PIN_BUILTIN_PUMP_IN1,
      PIN_BUILTIN_PUMP_IN2,
    };

    for (int i=0; i<8; i++) {
      int address = 0x20 + i;
      PCF8574 *pcf = new PCF8574(address);

      if (pcf->begin() && pcf->isConnected()) {
        info("found extension card at address 0x%x", address);
        for (int j=0; j<8; j+=2) {
          pump_slots[used_slots++] = new PumpSlot{pcf, j, j+1};
        }
      } else {
        delete pcf;
        continue;
      }
    }

    info("total pump slots: %d/%d", used_slots, MAX_PUMPS);
  }

  { // setup scale
    scale.begin(PIN_HX711_DOUT, PIN_HX711_SCK);

    if (scale.wait_ready_timeout(1000, 1)) {
      // sanity check in case nothing is connected
      if (scale.read() == 0.0) {
        error("scale returned implausible value, assuming it's disconnected");
      } else {
        scale_available = true;
        info("scale initialized");
      }
    }

    if (!scale_available) {
      error("scale not found");
    }
  }

  { // initialize machine state
    for (int i=0; i<NUM_ID; i++)    	all_id[i]    	= NULL;
    for (int i=0; i<NUM_STATUS; i++)	all_status[i]	= NULL;
    for (int i=0; i<NUM_COMM; i++)  	all_comm[i]  	= NULL;
    for (int i=0; i<MAX_PUMPS; i++) 	pumps[i]     	= NULL;

    while (!command_queue.empty())	command_queue.pop();

    recipe_queue.clear();
    recipes.clear();
    cocktail.clear();

    users.clear();
    users[0] = "admin";

    cal_state = CalibrationState::inactive;

    error_state = Retcode::success;
    error_user  = USER_UNKNOWN;

    ble_server = NULL;
  }

  { // start bluetooth
    if (!ble_start()) {
      error("failed to start ble");
      error_loop();
    }
  }

  // try to load config
  config_load();
  for (const auto &[user, name] : users) {
    debug("user %d: %s", user, name.c_str());
  }

  // update all states
  update_liquids();
  update_scale();
  update_recipes();
  update_cocktail();
  update_user();
  update_state();
}

void loop() {
  // process general admin commands
  // any commands related to recipes will modify the recipe queue and then get processed later
  while (!command_queue.empty()) {
    CommandQueued q = command_queue.front();
    command_queue.pop();

    debug("processing queue (#%d): %s (%d)", command_queue.size(), q.command->cmd_name(), q.conn_id);
    Retcode p = q.command->execute();

    // cleanup
    delete q.command;

    // send response
    switch (p) {
    case Retcode::user_id:
      { // TODO this isn't thread-safe or anything
        User id = users.size() - 1;

        String *out = new String("{\"user\": ");
        out->concat(id);
        out->concat('}');

        q.comm->respond(q.conn_id, out);
      }
      break;

    default:
      q.comm->respond(q.conn_id, p);
      break;
    }
  }

  // next, process one of three situations, whichever applies first:
  // - errors
  // - calibration
  // - recipes

  if (error_state != Retcode::success) {
    // currently in an error state that needs acknowledgment
    return;
  }

  // advance calibration state
calibration:
  if (cal_state != CalibrationState::inactive) {
    Retcode err;

    switch (cal_state) {
    case CalibrationState::pumps:
      if (cal_pump >= MAX_PUMPS) {
        switch (cal_pass) {
        case 1:
          cal_pump  = 0;
          cal_pass += 1;
          return;

        case 2:
          cal_state = CalibrationState::calc;
          update_state();
          return;

        default:
          error("unknown calibration pass");
          error_loop();
          return;
        }
      }

      { // calibrate pump
        debug("calibrating pump %d (#%d)", cal_pump, cal_pass);

        int slot = cal_pump;
        cal_pump += 1;

        Pump *pump = pumps[slot];
        if (pump == NULL) goto calibration; // skip missing pumps

        time_t time = (cal_pass == 1) ? CAL_TIME1 : CAL_TIME2;
        if (!scale_available && time > S(1)) time /= S(1); // reduce the time for simulations
        debug("running pump %d (#%d) for %dms", slot, cal_pass, time);

        // run pump
        err = pump->run(time, false);
        if (err != Retcode::success) update_error(err, USER_ADMIN);

        // reverse to clear the pump
        err = pump->run(pump->time_reverse, true);
        if (err != Retcode::success) update_error(err, USER_ADMIN);

        sleep_idle(S(1));

        String ingredient = String("<calibration ");
        ingredient.concat(slot);
        ingredient.concat('>');

        float weight = scale_estimate(pump->time_init, time, pump->rate);
        cocktail.push_front(Ingredient{ingredient, weight});
        debug("  -> weight: %f", weight);

        pump->volume = std::max(pump->volume - weight, 0.0f);

        pump->cal_volume[cal_pass-1] = weight;
      }

      cal_state = CalibrationState::empty;
      update_state();

      return;

    case CalibrationState::calc:
      debug("calculating calibration data...");

      for (int i=0; i<MAX_PUMPS; i++) {
        Pump *pump = pumps[i];
        if (pump == NULL) continue;

        err = pump->calibrate(CAL_TIME1, CAL_TIME2);
        if (err != Retcode::success) update_error(err, USER_ADMIN);

      }
      cal_state = CalibrationState::done;
      update_state();

      return;

    default:
      // waiting for some calibration command, skipping
      break;
    }

    return; // done processing
  }

  // advance the recipe queue
  if (!recipe_queue.empty()) {
    ActiveRecipe *active = recipe_queue.front();
    Retcode err;

    switch (active->state) {
    case RecipeState::ready:
      err = check_recipe(active->name, active->ingredients);
      if (err != Retcode::success) {
        update_error(err, active->user);
        return;
      }

      debug("waiting for container");
      active->state = RecipeState::wait_container;
      update_state();
      break;

    case RecipeState::wait_container:
      // automatic transition using the scale
      if (AUTOMATIC_SCALE && scale_has_container()) {
        active->state = RecipeState::mixing;
        update_state();
      }

      // just wait otherwise
      break;

    case RecipeState::mixing:
      err = advance_recipe(active);
      if (err != Retcode::success) {
        update_error(err, active->user);
        return;
      }
      break;

    case RecipeState::pumping:
      // this shouldn't really happen, but regardless, there's nothing to do, so just wait
      debug("machine is currently pumping");
      break;

    case RecipeState::cocktail_done:
      // automatic transition using the scale
      if (AUTOMATIC_SCALE && scale_empty()) {
        // remove the recipe
        recipe_queue.pop_front();
        delete active;
        reset_cocktail();

        // update machine state
        update_state();
        update_user();
      }

      // just wait otherwise
      break;

    default:
      error("recipe in illegal state: %d", active->state);
      error_loop();
    }

    return;
  }
}

// command processing
Retcode add_to_command_queue(const String json, Comm *comm, uint16_t conn_id) {
  Parsed p = parse_command(json);
  if (p.err != Retcode::success) return p.err;

  if (p.command == NULL) {
    error("command missing even though there wasn't a parse error");
    return Retcode::missing_command;
  }

  // enforce comm separation
  if (!p.command->is_valid_comm(comm)) {
    delete p.command; // cleanup
    return Retcode::wrong_comm;
  }

  debug("parsed, adding to queue: %d, %s", conn_id, p.command->cmd_name());

  // add to process queue
  CommandQueued q = {p.command, comm, conn_id};
  command_queue.push(q);

  return Retcode::success;
}

Parsed parse_command(const String json) {
  debug("processing json: «%s»", json.c_str());

  StaticJsonDocument<BUF_JSON> doc;
  DeserializationError err = deserializeJson(doc, json);

  if (err) {
    switch (err.code()) {
    case DeserializationError::EmptyInput:
    case DeserializationError::IncompleteInput:
    case DeserializationError::InvalidInput:
      return Parsed{NULL, Retcode::invalid};

    case DeserializationError::NoMemory:
    case DeserializationError::TooDeep:
      return Parsed{NULL, Retcode::too_big};

    default:
      return Parsed{NULL, Retcode::invalid};
    }
  }

  // parse json document into command type
  Command *cmd;

  // helper macros
#define match_name(cls) (!strcmp(cmd_name, cls::json_name))

#define parse_array(field)                                      \
  JsonArray field = doc[#field];                                \
  if (field.isNull()) return Parsed{NULL, Retcode::incomplete};

#define parse_ingredients(array)                                        \
  std::deque<Ingredient> ingredients = {};                              \
  parse_array(array);                                                   \
  for (JsonVariant _v : array) {                                        \
    JsonArray _tuple = _v.as<JsonArray>();                              \
    if (_tuple.isNull() || _tuple.size() != 2)                          \
      return Parsed{NULL, Retcode::incomplete};                         \
                                                                        \
    String _name   	= String(_tuple[0].as<const char*>());             \
    float _amount  	= _tuple[1].as<float>();                           \
    Ingredient _ing	= Ingredient{_name, _amount};                      \
                                                                        \
    ingredients.push_back(_ing);                                        \
  }

#define parse_str(field)                                    \
  const char *_##field = doc[#field];                       \
  if (!_##field) return Parsed{NULL, Retcode::incomplete};  \
  const String field = String(_##field)

#define parse_duration(field)                                       \
  JsonVariant j_##field = doc[#field];                              \
  if (j_##field.isNull()) return Parsed{NULL, Retcode::incomplete}; \
  const dur_t field = (dur_t) j_##field.as<int32_t>();

#define parse_as(field, type)                                       \
  JsonVariant j_##field = doc[#field];                              \
  if (j_##field.isNull()) return Parsed{NULL, Retcode::incomplete}; \
  const type field = j_##field.as<type>();

#define parse_bool(field)    	parse_as(field, bool)
#define parse_float(field)   	parse_as(field, float)
#define parse_int32(field)   	parse_as(field, int32_t)
#define parse_user()         	parse_as(user,  int32_t)

  const char *cmd_name = doc["cmd"];
  if (!cmd_name) return Parsed{NULL, Retcode::incomplete};

  if (match_name(CmdTest)) {
    cmd = new CmdTest();

  } else if (match_name(CmdInitUser)) {
    parse_str(name);
    cmd = new CmdInitUser{name};

  } else if (match_name(CmdReset)) {
    parse_user();
    cmd = new CmdReset(user);

  } else if (match_name(CmdResetError)) {
    parse_user();
    cmd = new CmdResetError(user);

  } else if (match_name(CmdQueueRecipe)) {
    parse_user();
    parse_str(recipe);
    cmd = new CmdQueueRecipe(user, recipe);

  } else if (match_name(CmdAddLiquid)) {
    parse_user();
    parse_str(liquid);
    parse_float(volume);
    cmd = new CmdAddLiquid(user, liquid, volume);

  } else if (match_name(CmdStartRecipe)) {
    parse_user();
    cmd = new CmdStartRecipe(user);

  } else if (match_name(CmdCancelRecipe)) {
    parse_user();
    cmd = new CmdCancelRecipe(user);

  } else if (match_name(CmdTakeCocktail)) {
    parse_user();
    cmd = new CmdTakeCocktail(user);

  } else if (match_name(CmdDefineRecipe)) {
    parse_user();
    parse_str(name);
    parse_ingredients(liquids);
    cmd = new CmdDefineRecipe(user, name, ingredients);

  } else if (match_name(CmdEditRecipe)) {
    parse_user();
    parse_str(name);
    parse_ingredients(liquids);
    cmd = new CmdEditRecipe(user, name, ingredients);

  } else if (match_name(CmdDeleteRecipe)) {
    parse_user();
    parse_str(name);
    cmd = new CmdDeleteRecipe(user, name);

  } else if (match_name(CmdDefinePump)) {
    parse_user();
    parse_str(liquid);
    parse_float(volume);
    parse_int32(slot);
    cmd = new CmdDefinePump(user, slot, liquid, volume);

  } else if (match_name(CmdEditPump)) {
    parse_user();
    parse_str(liquid);
    parse_float(volume);
    parse_int32(slot);
    cmd = new CmdEditPump(user, slot, liquid, volume);

  } else if (match_name(CmdRefillPump)) {
    parse_user();
    parse_float(volume);
    parse_int32(slot);
    cmd = new CmdRefillPump(user, slot, volume);

  } else if (match_name(CmdRestart)) {
    parse_user();
    cmd = new CmdRestart(user);

  } else if (match_name(CmdFactoryReset)) {
    parse_user();
    cmd = new CmdFactoryReset(user);

  } else if (match_name(CmdClean)) {
    parse_user();
    cmd = new CmdClean(user);

  } else if (match_name(CmdRunPump)) {
    parse_user();
    parse_int32(slot);
    parse_duration(time);
    cmd = new CmdRunPump(user, slot, time);

  } else if (match_name(CmdCalibrationStart)) {
    parse_user();
    cmd = new CmdCalibrationStart(user);

  } else if (match_name(CmdCalibrationCancel)) {
    parse_user();
    cmd = new CmdCalibrationCancel(user);

  } else if (match_name(CmdCalibrationFinish)) {
    parse_user();
    cmd = new CmdCalibrationFinish(user);

  } else if (match_name(CmdCalibrationAddEmpty)) {
    parse_user();
    cmd = new CmdCalibrationAddEmpty(user);

  } else if (match_name(CmdCalibrationAddWeight)) {
    parse_user();
    parse_float(weight);
    cmd = new CmdCalibrationAddWeight(user, weight);

  } else if (match_name(CmdCalibratePump)) {
    parse_user();
    parse_int32(slot);
    parse_duration(time1);
    parse_duration(time2);
    parse_float(volume1);
    parse_float(volume2);
    cmd = new CmdCalibratePump(user, slot, time1, time2, volume1, volume2);

  } else if (match_name(CmdSetPumpTimes)) {
    parse_user();
    parse_int32(slot);
    parse_duration(time_init);
    parse_duration(time_reverse);
    parse_float(rate);
    cmd = new CmdSetPumpTimes(user, slot, time_init, time_reverse, rate);

  } else if (match_name(CmdTareScale)) {
    parse_user();
    cmd = new CmdTareScale(user);

  } else if (match_name(CmdCalibrateScale)) {
    parse_user();
    parse_float(weight);
    cmd = new CmdCalibrateScale(user, weight);

  } else if (match_name(CmdSetScaleFactor)) {
    parse_user();
    parse_float(factor);
    cmd = new CmdSetScaleFactor(user, factor);

  } else {
    return Parsed{NULL, Retcode::unknown_command};
  }

  return Parsed{cmd, Retcode::success};
}

// command logic
Retcode CmdTest::execute() { return Retcode::success; };

Retcode CmdRestart::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  ESP.restart();
}

Retcode CmdFactoryReset::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  { // reset settings
    config_clear();

    for (int i=0; i<MAX_PUMPS; i++) pumps[i]	= NULL;

    if (scale_available) {
      scale.set_offset(0);
      scale.set_scale(1.0);
    }

    while (!command_queue.empty()) command_queue.pop();

    recipe_queue.clear();
    recipes.clear();
    cocktail.clear();

    users.clear();
    users[0] = "admin";
  }

  // update machine state
  update_config_state(0);
  update_cocktail();
  update_recipes();
  update_liquids();
  update_scale();
  update_user();
  update_state();

  return Retcode::success;
}

Retcode CmdClean::execute() {
  // FIXME implement
  if (!is_admin(this->user)) return Retcode::unauthorized;
  return Retcode::unsupported;
};

Retcode CmdInitUser::execute() {
  User id = users.size();
  users[id] = this->name;

  config_save();

  return Retcode::user_id;
}

Retcode CmdResetError::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;
  error_state = Retcode::success;

  return Retcode::success;
}

Retcode CmdDefinePump::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot = this->slot;
  float volume = this->volume;

  if (slot < 0 || slot >= MAX_PUMPS) {
    return Retcode::invalid_slot;
  }

  if (volume < 0) {
    return Retcode::invalid_volume;
  }

  // clear out old pump
  if (pumps[slot] != NULL) {
    delete pumps[slot];
  }

  // save new pump
  PumpSlot *pump_slot = pump_slots[slot];
  Pump *p = new Pump(pump_slot, this->liquid, volume, S(1), S(1), 1.0, false);
  pumps[slot] = p;
  
  // update machine state
  update_liquids();
  update_config_state(timestamp_ms());

  return Retcode::success;
}

Retcode CmdEditPump::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot = this->slot;

  if (slot < 0 || slot >= MAX_PUMPS) {
    return Retcode::invalid_slot;
  }

  if (volume < 0) {
    return Retcode::invalid_volume;
  }

  // edit pump
  Pump *pump = pumps[slot];

  if (pump == NULL) {
    return Retcode::invalid_slot;
  }

  pump->liquid = this->liquid;
  pump->volume = this->volume;

  // update machine state
  update_liquids();
  update_config_state(timestamp_ms());

  return Retcode::success;
}

Retcode CmdRefillPump::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot = this->slot;
  float volume = this->volume;

  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return Retcode::invalid_slot;
  }

  if (volume < 0) {
    return Retcode::invalid_volume;
  }

  // refill pump
  Pump *p = pumps[slot];
  Retcode err = p->refill(volume);
  if (err != Retcode::success) return err;

  // update machine state
  update_liquids();
  update_possible_recipes();

  return Retcode::success;
}

Retcode CmdDefineRecipe::execute() {
  // make sure the recipe is unique
  const String name = this->name;

  for (auto const &r : recipes) {
    if (!strcmp(r.name.c_str(), name.c_str())) return Retcode::duplicate_recipe;
  }

  size_t num = 0;
  for (auto const &it : this->ingredients) num += 1;

  if (num < 1) return Retcode::missing_ingredients;

  // add it
  debug("adding recipe %s with %d ingredients", name, num);
  Recipe r = Recipe(name, this->ingredients);
  recipes.push_front(r);

  // update state
  update_recipes();

  return Retcode::success;
}

Retcode CmdEditRecipe::execute() {
  // check ingredients
  const String name = this->name;
  size_t num = 0;
  for (auto const &it : this->ingredients) num += 1;
  if (num < 1) return Retcode::missing_ingredients;

  debug("updating recipe %s with %d ingredients", name, num);

  for (auto it = recipes.begin(); it != recipes.end(); it++) {
    if (!strcmp(it->name.c_str(), name.c_str())) {
      // TODO memory leak?
      it->ingredients = this->ingredients;

      // update state
      update_recipes();

      return Retcode::success;
    }
  }

  return Retcode::missing_recipe;
}

Retcode CmdDeleteRecipe::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  const String name = this->name;

  debug("deleting recipe %s", name);
  recipes.remove_if([name](Recipe r){ return !strcmp(r.name.c_str(), name.c_str()); });

  // update state
  update_recipes();

  return Retcode::success;
}

Retcode CmdQueueRecipe::execute() {
  const String name = this->recipe;
  Recipe *recipe = NULL;

  // look for recipe
  for (auto &r : recipes) {
    if (!strcmp(r.name.c_str(), name.c_str())) {
      recipe = &r;
      break;
    }
  }
  if (!recipe) return Retcode::missing_recipe;

  Retcode err = check_recipe(name, recipe->ingredients);
  if (err != Retcode::success) return err;

  // add to recipe queue
  debug("recipe %s (%d) valid, adding to queue", name.c_str(), this->user);
  return add_to_recipe_queue(recipe, this->user);
};

Retcode CmdAddLiquid::execute() {
  // TODO it might be helpful to be able to refer to recipes by an ID

  // sanity check
  if (this->volume < 0) {
    return Retcode::invalid_volume;
  }

  Retcode err = check_liquid(this->liquid, this->volume);
  if (err != Retcode::success) return err;

  // look for active recipe and add the liquid
  for(auto &r : recipe_queue) {
    if (r->user == this->user || is_admin(this->user)) {
      r->ingredients.push_back(Ingredient{this->liquid, this->volume});
    }
    return Retcode::success;
  }

  // if that failed, then this user doesn't have an active recipe
  return Retcode::missing_recipe;
}

Retcode CmdStartRecipe::execute() {
  for(auto &r : recipe_queue) {
    if (r->user == this->user || is_admin(this->user)) {
      if (r->state == RecipeState::wait_container) {
        r->state = RecipeState::mixing;
        return Retcode::success;
      } else {
        return Retcode::cant_start_recipe;
      }
    }
  }

  return Retcode::missing_recipe;
}

Retcode CmdCancelRecipe::execute() {
  for (auto it = recipe_queue.begin(); it != recipe_queue.end(); it++) {
    ActiveRecipe *r = *it;

    if (r->user == this->user || is_admin(this->user)) {
      switch (r->state) {
      case RecipeState::ready:
      case RecipeState::wait_container:
        // remove the recipe
        recipe_queue.erase(it);

        // update machine state
        update_user();

        return Retcode::success;

      case RecipeState::mixing:
      case RecipeState::pumping:
        // TODO stop active pumps
        // switch to done
        r->ingredients.clear();
        r->state = RecipeState::cocktail_done;

        // update machine state
        update_cocktail();
        update_liquids();
        update_scale();
        update_user();
        update_state();

        return Retcode::success;

      case RecipeState::cocktail_done:
        // nothing to do, just stay in this state
        return Retcode::success;

      default:
        // this shouldn't happen
        error("recipe in illegal state: %d", r->state);
        error_loop();
      }
    }
  }

  return Retcode::missing_recipe;
}

Retcode CmdTakeCocktail::execute() {
  for (auto it = recipe_queue.begin(); it != recipe_queue.end(); it++) {
    ActiveRecipe *r = *it;

    if (r->user == this->user || is_admin(this->user)) {
      if (r->state == RecipeState::cocktail_done) {
        // remove the recipe
        recipe_queue.erase(it);
        delete r;
        reset_cocktail();

        // update machine state
        update_user();
        update_state();

        return Retcode::success;

      } else {
        return Retcode::cant_take_cocktail;
      }
    }
  }

  return Retcode::missing_recipe;
};

Retcode CmdReset::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  reset_cocktail();
  update_cocktail();
  update_state();

  return Retcode::success;
};


Retcode CmdCalibrationStart::execute() {
  if (cal_state != CalibrationState::inactive) return Retcode::invalid_cal_state;

  cal_pass = 0;
  cal_pump = 0;
  for (int i=0; i<MAX_PUMPS; i++) {
    Pump *pump = pumps[i];
    if (pump == NULL) continue; // skip missing pumps

    for (int n=0; n<2; n++) {
      pump->cal_volume[n] = 0;
    }
  }

  cal_state = CalibrationState::empty;
  update_state();

  return Retcode::success;
};

Retcode CmdCalibrationCancel::execute() {
  scale_tare();
  cal_state = CalibrationState::inactive;
  update_state();
  return Retcode::success;
};

Retcode CmdCalibrationFinish::execute() {
  scale_tare();
  cal_state = CalibrationState::inactive;
  update_state();
  return Retcode::success;
};

Retcode CmdCalibrationAddEmpty::execute() {
  if (cal_state != CalibrationState::empty) return Retcode::invalid_cal_state;

  reset_cocktail();
  scale_tare();

  switch (cal_pass) {
  case 0:
    cal_state = CalibrationState::weight;
    update_state();
    break;

  case 1:
  case 2:
    cal_state = CalibrationState::pumps;
    update_state();
    break;

  default:
    error("unknown calibration pass");
    error_loop();
    break;
  }

  return Retcode::success;
};

Retcode CmdCalibrationAddWeight::execute() {
  if (cal_state != CalibrationState::weight) return Retcode::invalid_cal_state;

  scale_calibrate(this->weight);
  cal_pass += 1;
  cal_state = CalibrationState::empty;
  update_state();

  return Retcode::success;
};

Retcode CmdRunPump::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot  = this->slot;
  dur_t time = this->time;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return Retcode::invalid_slot;
  }
  Pump *p = pumps[slot];

  debug("running pump %d for %lldms", slot, time);
  update_user();
  p->run(time, false);

  // nb: this will always fuck up our internal data unless the pump is already calibrated,
  // so you should refill the pump afterwards

  String ingredient = String("<run_pump ");
  ingredient.concat(slot);
  ingredient.concat('>');

  float volume = scale_estimate(p->time_init, time, p->rate);
  cocktail.push_front(Ingredient{ingredient, volume}); // TODO add a dummy recipe instead
  p->volume = std::max(p->volume - volume, 0.0f);

  update_liquids();
  update_possible_recipes();
  update_scale();
  update_cocktail();
  update_state();

  return Retcode::success;
};

Retcode CmdCalibratePump::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot = this->slot;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return Retcode::invalid_slot;
  }
  Pump *p = pumps[slot];

  return p->calibrate(this->time1, this->time2, this->volume1, this->volume2);
};

Retcode CmdSetPumpTimes::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  int32_t slot = this->slot;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return Retcode::invalid_slot;
  }
  Pump *p = pumps[slot];

  p->time_init    = this->time_init;
  p->time_reverse = this->time_reverse;
  p->rate         = this->rate;
  p->calibrated   = true;
  config_save();

  return Retcode::success;
};

Retcode CmdCalibrateScale::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  Retcode err = scale_calibrate(this->weight);
  if (err != Retcode::success) return err;

  update_scale();

  return Retcode::success;
};

Retcode CmdTareScale::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  Retcode err = scale_tare();
  if (err != Retcode::success) return err;

  update_scale();

  return Retcode::success;
};

Retcode CmdSetScaleFactor::execute() {
  if (!is_admin(this->user)) return Retcode::unauthorized;

  Retcode err = scale_set_factor(this->factor);
  if (err != Retcode::success) return err;

  update_scale();

  return Retcode::success;
};

bool is_admin(User user) {
  // TODO use roles etc
  return (user == USER_ADMIN);
}

Retcode add_to_recipe_queue(Recipe *recipe, User user) {
  std::deque<Ingredient> queue;
  for (auto const ing : recipe->ingredients) {
    queue.push_back(Ingredient{ing.name, ing.amount});
  }
  ActiveRecipe *r = new ActiveRecipe(user, recipe->name, queue, RecipeState::ready);

  recipe_queue.push_back(r);

  return Retcode::success;
}

Retcode reset_cocktail() {
  cocktail.clear();

  Retcode err = scale_tare();
  if (err != Retcode::success) return err;

  // update machine state
  update_cocktail();
  update_scale();
  update_user();

  return Retcode::success;
}

Retcode check_liquid(String liquid, float volume) {
  // check that we have enough liquid
  float need = volume;
  float have = 0;
  bool found = false;

  for (int i=0; i<MAX_PUMPS; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;

    if (!strcmp(p->liquid.c_str(), liquid.c_str())) {
      found = true;
      debug("  in pump %d: %.1f", i, p->volume);
      have += p->volume;
    }
  }

  if (!found)                     	return Retcode::missing_liquid;
  if (have - need < LIQUID_CUTOFF)	return Retcode::insufficient;

  return Retcode::success;
}

Retcode check_recipe(String name, std::deque<Ingredient> ingredients) {
  if (ingredients.empty()) return Retcode::success;

  debug("checking if recipe %s is possible", name.c_str());

  debug("  summing up all available liquids");
  std::unordered_map<std::string, float> liquids = {};
  for (int i=0; i<MAX_PUMPS; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;

    std::string liquid = p->liquid.c_str();
    debug("    pump: %d, liquid: %s, vol: %.1f", i, liquid.c_str(), p->volume);

    // update saved total
    float sum = liquids[liquid] + p->volume;
    liquids[liquid] = sum;
  }

  debug("  checking necessary ingredients");
  for (auto &ing : ingredients) {
    std::string liquid	= ing.name.c_str();
    float have        	= liquids[liquid];
    float need        	= ing.amount;
    bool found        	= liquids.count(liquid) > 0;

    debug("    need %.1f / %.1f of %s", need, have, liquid.c_str());
    if (!found)                     	return Retcode::missing_liquid;
    if (have - need < LIQUID_CUTOFF)	return Retcode::insufficient;
    liquids[liquid] = have - need; // update total
  }

  return Retcode::success;
}

Retcode advance_recipe(ActiveRecipe *recipe) {
  // quick sanity check
  Retcode err = check_recipe(recipe->name, recipe->ingredients);
  if (err != Retcode::success) return err;

  if (recipe->ingredients.empty()) { // nothing left to do
    recipe->state = RecipeState::cocktail_done;
    update_state();

    return Retcode::success;
  }

  recipe->state = RecipeState::mixing; // just to be sure it's in the right state
  update_state();

  Ingredient next_ing = recipe->ingredients.front();
  float need = next_ing.amount;
  float used = 0;

  // sanity check
  if (need < 0) { return Retcode::invalid_volume; }

  // add the liquid
  recipe->state = RecipeState::pumping;
  update_state();

  for (int i=0; i<MAX_PUMPS && need >= LIQUID_CUTOFF; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;

    if (!strcmp(p->liquid.c_str(), next_ing.name.c_str())) {
      debug("  need %.1f, using pump %d: %.1f", need, i, p->volume);
      float use = std::min(p->volume, need);
      Retcode err = p->drain(use);
      if (err != Retcode::success) {
        recipe->state = RecipeState::mixing;
        return err;
      }
      need -= use;
      used += use;
    }
  }

  // done pumping
  recipe->state = RecipeState::mixing;
  recipe->ingredients.pop_front();
  update_state();

  // update cocktail state
  cocktail.push_back(Ingredient{next_ing.name, used});

  // update machine state
  update_liquids();
  update_scale();
  update_cocktail();
  update_possible_recipes();

  // shouldn't happen, but do a sanity check
  if (need >= LIQUID_CUTOFF) return Retcode::insufficient;

  return Retcode::success;
}

Retcode Pump::drain(float amount) {
  if (amount < 0) return Retcode::invalid_volume;
  if (this->volume - amount < LIQUID_CUTOFF) return Retcode::insufficient;

  this->pump(amount);
  this->volume -= amount;
  return Retcode::success;
}

Retcode Pump::refill(float amount) {
  if (amount < 0) return Retcode::invalid_volume;

  this->volume = amount;
  return Retcode::success;
}

Retcode Pump::empty() {
  this->volume = 0;
  return Retcode::success;
}

void update_cocktail() {
  // generate json output
  debug("updating cocktail state");

  float weight = scale_weigh();

  String out = String("{\"weight\":");
  out.concat(String(weight, 1));

  out.concat(",\"content\":[");
  bool prev = false;
  for (auto const ing : cocktail) {
    debug("  ingredient: %s, amount: %.1f", ing.name.c_str(), ing.amount);

    if (prev) out.concat(',');
    out.concat("[\"");
    out.concat(ing.name);
    out.concat("\",");
    out.concat(String(ing.amount, 1));
    out.concat(']');

    prev = true;
  }
  out.concat("]}");

  all_status[ID_COCKTAIL]->update(out, false);
}

void update_state() {
  String json;

  if (cal_state != CalibrationState::inactive) {
    json = String(cal_state_str[static_cast<int>(cal_state)]);
  } else {
    RecipeState state = RecipeState::ready;
    if (!recipe_queue.empty()) {
      ActiveRecipe *r = recipe_queue.front();
      state = r->state;
    }
    json = String(rec_state_str[static_cast<int>(state)]);
  }

  debug("updating machine state: %s", json.c_str());

  all_status[ID_STATE]->update(json, false);
}

void update_error(Retcode error, User user) {
  String json = String(retcode_str[static_cast<int>(error)]);
  error_state = error;
  error_user  = user;

  debug("updating error state: %s", json.c_str());
  all_status[ID_ERROR]->update(json);
}

void update_scale() {
  // generate json output
  debug("updating scale state");

  float weight = scale_weigh();

  String out = String("{\"weight\":");
  out.concat(String(weight, 1));

  out.concat(",\"calibrated\":");
  out.concat(scale_calibrated ? "true" : "false");

  out.concat('}');

  all_status[ID_SCALE]->update(out, false);
}

void update_liquids() {
  std::unordered_map<std::string, float> liquids = {};

  {
    debug("updating pump state");

    bool prev 	= false;
    String out	= String('{');

    for(int i=0; i<MAX_PUMPS; i++) {
      Pump *pump = pumps[i];
      if (pump == NULL) continue;

      std::string liquid = pump->liquid.c_str();
      debug("  pump: %d, liquid: %s, vol: %.1f", i, liquid.c_str(), pump->volume);

      // update saved total
      float sum = liquids[liquid] + pump->volume;
      liquids[liquid] = sum;

      if (prev) out.concat(',');
      out.concat('"');
      out.concat(String(i));
      out.concat("\":{\"liquid\":\"");
      out.concat(pump->liquid);
      out.concat("\",\"volume\":");
      out.concat(String(pump->volume, 1));
      out.concat(",\"calibrated\":");
      out.concat(pump->calibrated ? "true" : "false");
      out.concat(",\"rate\":");
      out.concat(String(pump->rate, 1));
      out.concat(",\"time_init\":");
      out.concat(String(pump->time_init));
      out.concat(",\"time_reverse\":");
      out.concat(String(pump->time_reverse));
      out.concat('}');

      prev = true;
    }
    out.concat('}');
    all_status[ID_PUMPS]->update(out, false);
  }

  { debug("updating liquid state");
    int remaining	= liquids.size();
    String out   	= String('{');

    for(auto const &pair : liquids) {
      debug("  liquid: %s, vol: %.1f", pair.first.c_str(), pair.second);

      out.concat('"');
      out.concat(pair.first.c_str());
      out.concat("\":");
      out.concat(String(pair.second, 1));

      remaining -= 1;
      if (remaining) out.concat(',');
    }
    out.concat('}');

    all_status[ID_LIQUIDS]->update(out, false);
  }

  config_save();
}

void update_recipes() {
  // generate json output
  debug("updating recipes state");

  // update can_make state of all recipes
  update_possible_recipes();

  String out = String('{');
  for (auto r = recipes.begin(); r != recipes.end(); r++) {
    debug("  recipe: %s (can make: %s)", r->name.c_str(), r->can_make ? "yes" : "no");

    out.concat('"');
    out.concat(r->name);
    out.concat("\":{\"ingredients\":[");

    bool prev = false;
    for (auto const ing : r->ingredients) {
      debug("    ingredient: %s, amount: %.1f", ing.name.c_str(), ing.amount);

      if (prev) out.concat("[\"");
      out.concat(ing.name);
      out.concat("\",");
      out.concat(String(ing.amount, 1));
      out.concat(']');

      prev = true;
    }
    out.concat("],\"can_make\":");
    out.concat(r->can_make ? "true" : "false");
    out.concat('}');

    if (next(r) != recipes.end()) out.concat(',');
  }
  out.concat('}');

  all_status[ID_RECIPES]->update(out, false);

  update_config_state(timestamp_ms());
  config_save();
};

void update_config_state(time_t ts) {
  config_state = timestamp_ms();
  String s = String(ts);
  all_status[ID_TIMESTAMP]->update(s, false);
}

User current_user() {
  if (!recipe_queue.empty()) {
    ActiveRecipe *active = recipe_queue.front();
    return active->user;
  }
  return USER_UNKNOWN;
}

void update_user() {
  debug("updating user queue");
  String out = String('[');
  bool prev = false;

  if (!recipe_queue.empty()) {
    prev = true;
    ActiveRecipe *active = recipe_queue.front();
    out.concat(String(active->user));
  }

  for(auto const &r : recipe_queue) {
    if (prev) out.concat(',');
    out.concat(String(r->user));
    prev = true;
  }
  out.concat(']');

  all_status[ID_USER]->update(out, false);
}

void update_possible_recipes() {
  debug("updating possible recipes...");
  bool updated = false;
  
  // calculate total liquids
  std::unordered_map<std::string, float> liquids = {};
  for(int i=0; i<MAX_PUMPS; i++) {
    Pump *pump = pumps[i];
    if (pump == NULL) continue;

    std::string liquid = pump->liquid.c_str();

    // update saved total
    float sum = liquids[liquid] + pump->volume;
    liquids[liquid] = sum;
  }

  // adjust recipes
  for (auto &recipe : recipes) {
    bool can_make = true;

    // sum up the ingredients
    std::unordered_map<std::string, float> needs = {};
    for (auto const &ing : recipe.ingredients) {
      std::string liquid = ing.name.c_str();
      float need = needs[liquid] + ing.amount;
      needs[liquid] = need;
    }
    
    for(auto const &pair : needs) {
      std::string liquid	= pair.first;
      float need        	= pair.second;
      float have        	= liquids[liquid];
      float diff        	= have - need;
    
      debug("  -> %s needs %.1f of %s, we have %.1f, diff is %.1f", recipe.name.c_str(), need, liquid.c_str(), have, diff);
    
      if (need > 0 && diff <= LIQUID_CUTOFF) {
        can_make = false;
        break;
      }
    }
    
    if (recipe.can_make != can_make) {
      updated = true;
      recipe.can_make = can_make;
    }
  }

  if (updated) {
    update_config_state(timestamp_ms());
    config_save();
  }
}

// bluetooth
bool ble_start(void) {
  String name = String(BLE_NAME " v");
  name.concat(String(VERSION));
  
  debug("starting ble");
  BLEDevice::init(name.c_str());
  ble_server = BLEDevice::createServer();

  // setup callback
  ble_server->setCallbacks(new ServerCB());

  // init services
  all_id[ID_NAME]         	= new ID(UUID_ID_NAME,             	name);
                          	                                   	
  all_status[ID_STATE]    	= new Status(UUID_STATUS_STATE,    	String("\"init\""));
  all_status[ID_LIQUIDS]  	= new Status(UUID_STATUS_LIQUIDS,  	String("{}"));
  all_status[ID_PUMPS]    	= new Status(UUID_STATUS_PUMPS,    	String("{}"));
  all_status[ID_RECIPES]  	= new Status(UUID_STATUS_RECIPES,  	String("{}"));
  all_status[ID_COCKTAIL] 	= new Status(UUID_STATUS_COCKTAIL, 	String("[]"));
  all_status[ID_TIMESTAMP]	= new Status(UUID_STATUS_TIMESTAMP,	String("0"));
  all_status[ID_USER]     	= new Status(UUID_STATUS_USER,     	String("[]"));
  all_status[ID_SCALE]    	= new Status(UUID_STATUS_SCALE,    	String("{}"));
  all_status[ID_ERROR]    	= new Status(UUID_STATUS_ERROR,    	String(""));
                          	
  all_comm[ID_MSG_USER]   	= new Comm(UUID_COMM_USER);
  all_comm[ID_MSG_ADMIN]  	= new Comm(UUID_COMM_ADMIN);

  // start and advertise services
  BLEAdvertising *adv = ble_server->getAdvertising();
  const char *services[] = {UUID_ID, UUID_STATUS, UUID_COMM};

  for (int i=0; i<(sizeof(services)/sizeof(services[0])); i++) {
    const char *uuid = services[i];
    BLEService *s = ble_server->getServiceByUUID(uuid);

    s->start();
    adv->addServiceUUID(uuid);
  }

  adv->setScanResponse(true);
  adv->setMinPreferred(0x06); // functions that help with iPhone connections issue
  adv->setMinPreferred(0x12);
  adv->start();

  debug("ble address: %s", BLEDevice::getAddress().toString().c_str());
  debug("ble ready");

  return true;
}


Service::Service(const char *uuid_service, const char *uuid_char, const uint32_t props, const int num_chars) {
  debug("creating service: %s / %s", uuid_service, uuid_char);

  BLEService* service = ble_server->getServiceByUUID(uuid_service);
  if (service == NULL) {
    /* nb: each characteristic needs the following handles:
       - 1 for read
       - 1 for write
       - 1 for the BLE2902 notification characteristic

       additionally, each service needs a handle for itself
    */
    int num_handles = num_chars * 3 + 1;
    service = ble_server->createService(BLEUUID(uuid_service), num_handles);
  }
  this->ble_char = service->createCharacteristic(uuid_char, props);

  // add descriptor for notifications
  this->ble_char->addDescriptor(new BLE2902());
}

ID::ID(const char *uuid_char, const String init_value)
  : Service(UUID_ID, uuid_char,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY,
            NUM_ID) {
  this->ble_char->setCallbacks(new CharCB());
  this->ble_char->setValue(init_value.c_str());
}

void ID::update(const String value) {
  this->ble_char->setValue(value.c_str());
  this->ble_char->notify();
}

Status::Status(const char *uuid_char, const String init_value)
  : Service(UUID_STATUS, uuid_char,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY,
            NUM_STATUS) {
  this->ble_char->setCallbacks(new CharCB());
  this->ble_char->setValue(init_value.c_str());
}

void Status::update(const String value, bool force) {
  if (!force) {
    std::string old = this->ble_char->getValue();
    if (value.equals(String(old.c_str()))) return; // skip if the value hasn't changed
  }

  this->ble_char->setValue(value.c_str());
  this->ble_char->notify();
}

Comm::Comm(const char *uuid_char)
  : Service(UUID_COMM, uuid_char,
            BLECharacteristic::PROPERTY_READ  |
            BLECharacteristic::PROPERTY_WRITE |
            BLECharacteristic::PROPERTY_NOTIFY,
            NUM_COMM) {
  this->ble_char->setCallbacks(new CommCB(this));
  this->responses = {};
}

void Comm::respond(const uint16_t id, Retcode ret) {
  String *s = new String(retcode_str[static_cast<int>(ret)]);
  debug("sending response to %d: %s", id, s->c_str());
  delete this->responses[id];
  this->responses[id] = s;
  this->ble_char->notify();
}

void Comm::respond(const uint16_t id, String *ret) {
  debug("sending response to %d: %s", id, ret->c_str());
  delete this->responses[id];
  this->responses[id] = ret;
  this->ble_char->notify();
}

bool Comm::is_id(int id) {
  return all_comm[id] == this;
}

void ServerCB::onConnect(BLEServer *server) {
  debug("client connected:");
}

void ServerCB::onConnect(BLEServer *server, esp_ble_gatts_cb_param_t* param) {
  BLEAddress remote_addr(param->connect.remote_bda);
  uint16_t id = param->connect.conn_id;

  for (int i=0; i<NUM_COMM; i++) {
    String *s = new String(retcode_str[static_cast<int>(Retcode::init)]);
    all_comm[i]->responses[id] = s;
  }
  debug("  %s -> %d", remote_addr.toString().c_str(), id);
}

void ServerCB::onDisconnect(BLEServer *server) {
  debug("client disconnected:");
  server->getAdvertising()->start();
}

void ServerCB::onDisconnect(BLEServer *server, esp_ble_gatts_cb_param_t* param) {
  BLEAddress remote_addr(param->disconnect.remote_bda);
  uint16_t id = param->disconnect.conn_id;

  for (int i=0; i<NUM_COMM; i++) {
    all_comm[i]->responses.erase(id);
  }
  debug("  %s -> %d", remote_addr.toString().c_str(), id);
}

void CharCB::onRead(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->read.conn_id;
  std::string value = ble_char->getValue();

  debug("read: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), value.c_str());
}

void CommCB::onRead(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->read.conn_id;

  // we need to set the value for each active connection to multiplex properly,
  // or default to a dummy value
  String *value;
  if (this->comm->responses.count(id)) {
    value = this->comm->responses[id];
  } else {
    value = new String("");
  }

  debug("read: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), value->c_str());
  ble_char->setValue(value->c_str());
}

void CommCB::onWrite(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->write.conn_id;

  if (this->comm->responses.count(id)) {
    const std::string v = ble_char->getValue();

    debug("write: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), v.c_str());

    Retcode err = add_to_command_queue(String(v.c_str()), this->comm, id);

    // TODO does this make sense like this?
    if (err != Retcode::success) {
      const char *s = retcode_str[static_cast<int>(err)];
      debug("failed to parse: %s", s);
      this->comm->respond(id, err);
    } else {
      debug("parsed, waiting for queue");
      this->comm->responses[id] = new String("processing");
    }
  }
}

// configs

bool config_save(void) {
  { // save state to flash memory
    preferences.clear(); // always start fresh

    // metadata
    preferences.putUInt("version", VERSION);

    { // user ids
      char key[BUF_PREF_KEY];

      int num_users = users.size();
      preferences.putUInt("num_users", num_users);

      int i=0;
      for (const auto &[user, name] : users) {
        snprintf(key, sizeof(key), "users_%d/i", i);
        preferences.putUInt(key, user);

        snprintf(key, sizeof(key), "users_%d/n", i);
        preferences.putString(key, name);

        i += 1;
      }
    }

    if (scale_available) { // scale
      if (scale_calibrated) {
        preferences.putBool("scale", true);
        preferences.putFloat("scale/t", scale.get_offset());
        preferences.putFloat("scale/f", scale.get_scale());
      } else {
        preferences.putBool("scale", false);
      }
    }

    { // pumps
      char key[BUF_PREF_KEY];
      preferences.putUInt("num_pumps", MAX_PUMPS);
      for (int i=0; i<MAX_PUMPS; i++) {
        Pump *p = pumps[i];
        if (p == NULL) continue;
        snprintf(key, sizeof(key), "pump_%d", i);
        preferences.putBool(key, true);

        snprintf(key, sizeof(key), "pump_%d/l", i);
        preferences.putString(key, p->liquid);

        snprintf(key, sizeof(key), "pump_%d/v", i);
        preferences.putFloat(key, p->volume);

        snprintf(key, sizeof(key), "pump_%d/c", i);
        preferences.putBool(key, p->calibrated);

        snprintf(key, sizeof(key), "pump_%d/ti", i);
        preferences.putULong64(key, p->time_init);

        snprintf(key, sizeof(key), "pump_%d/tr", i);
        preferences.putULong64(key, p->time_reverse);

        snprintf(key, sizeof(key), "pump_%d/rt", i);
        preferences.putFloat(key, p->rate);
      }
    }

    { // recipes
      char key[BUF_PREF_KEY];
      int num_recipes = 0;
      for (auto const &r : recipes) {
        snprintf(key, sizeof(key), "recipe_%d", num_recipes);
        preferences.putString(key, r.name);

        int num_ingredients = 0;
        for (auto const &ing : r.ingredients) {
          snprintf(key, sizeof(key), "recipe_%d/%d", num_recipes, num_ingredients);
          preferences.putString(key, ing.name);

          snprintf(key, sizeof(key), "recipe_%d/%d/v", num_recipes, num_ingredients);
          preferences.putFloat(key, ing.amount);

          num_ingredients += 1;
        }

        snprintf(key, sizeof(key), "recipe_%d/num", num_recipes);
        preferences.putUInt(key, num_ingredients);

        num_recipes += 1;
      }
      preferences.putUInt("num_recipes", num_recipes);
    }

    // final checksum
    preferences.putLong64("state", config_state);
  }

  debug("config saved: %" PRId64, config_state);
  return true;
}

bool config_load(void) {
  { // check metadata
    unsigned int version = preferences.getUInt("version", 0);
    if (version != VERSION) {
      warn("config version %d doesn't match current version %d", version, VERSION);
      warn("discarding config");
      config_clear();
      return false;
    }

    time_t state = preferences.getLong64("state", -1);
    if (state == config_state) {
      debug("config hasn't changed; skipping");
      return true;
    }
    update_config_state(state);
  }

  { // user ids
    char key[BUF_PREF_KEY];

    // remove old user config
    users.clear();

    int num_users = preferences.getUInt("num_users");

    for (int i=0; i<num_users; i++) {
      snprintf(key, sizeof(key), "users_%d/i", i);
      unsigned int user = preferences.getUInt(key);

      snprintf(key, sizeof(key), "users_%d/n", i);
      String name = preferences.getString(key);

      users[user] = name;
    }

    if (users.count(0) == 0) { // make sure admin exists
      users[0] = "admin";
    }
  }

  if (scale_available) { // scale
    bool calibrated = preferences.getBool("scale");
    if (calibrated) {
      float tare = preferences.getFloat("scale/t");
      float factor = preferences.getFloat("scale/f");
      scale.set_scale(factor);
      scale.set_offset(tare);
      scale_calibrated = true;
    } else {
      scale_calibrated = false;
    }
  }

  { // pumps
    for (int i=0; i<MAX_PUMPS; i++) pumps[i] = NULL; // clear old config

    char key[BUF_PREF_KEY];
    unsigned int num_pumps = preferences.getUInt("num_pumps", 0);
    num_pumps = std::min(num_pumps, (unsigned int) MAX_PUMPS);

    for (int i=0; i<num_pumps; i++) {
      snprintf(key, sizeof(key), "pump_%d", i);
      if (!preferences.getBool(key)) continue; // empty slot

      snprintf(key, sizeof(key), "pump_%d/l", i);
      String liquid = preferences.getString(key);

      snprintf(key, sizeof(key), "pump_%d/v", i);
      float volume = preferences.getFloat(key);

      snprintf(key, sizeof(key), "pump_%d/c", i);
      bool calibrated = preferences.getBool(key);

      snprintf(key, sizeof(key), "pump_%d/ti", i);
      time_t time_init = preferences.getULong64(key);

      snprintf(key, sizeof(key), "pump_%d/tr", i);
      time_t time_reverse = preferences.getULong64(key);

      snprintf(key, sizeof(key), "pump_%d/rt", i);
      float rate = preferences.getFloat(key);

      PumpSlot *pump_slot = pump_slots[i];
      // if (pump_slot == NULL) continue; // pump slot missing
      pumps[i] = new Pump(pump_slot, liquid, volume, time_init, time_reverse, rate, calibrated);
    }
  }

  { // recipes
    char key[BUF_PREF_KEY];
    recipes.clear(); // clear old config

    unsigned int num_recipes = preferences.getUInt("num_recipes", 0);
    for (int i=0; i<num_recipes; i++) {
      snprintf(key, sizeof(key), "recipe_%d", i);
      String name = preferences.getString(key);

      std::deque<Ingredient> ingredients = {};

      snprintf(key, sizeof(key), "recipe_%d/num", i);
      int num_ingredients = preferences.getUInt(key, 0);

      for (int j=0; j<num_ingredients; j++) {
        snprintf(key, sizeof(key), "recipe_%d/%d", i, j);
        String ing_name = preferences.getString(key);

        snprintf(key, sizeof(key), "recipe_%d/%d/v", i, j);
        float ing_amount = preferences.getFloat(key);

        Ingredient ing = Ingredient{ing_name, ing_amount};
        ingredients.push_front(ing);
      }

      Recipe r = Recipe(name, ingredients);
      recipes.push_front(r);
    }
  }

  debug("config loaded: %" PRId64, config_state);

  // update machine state
  update_liquids();
  update_recipes();

  return true;
}

bool config_clear(void) {
  preferences.clear();
  return false;
}

// pumps
PumpSlot::PumpSlot(PCF8574 *pcf, Pin in1, Pin in2)
  : pcf(pcf), in1(in1), in2(in2)
{
  if (pcf == NULL) {
    pinMode(in1, OUTPUT);
    pinMode(in2, OUTPUT);
  }
}

Retcode Pump::run(dur_t time, bool reverse=false) {
  if (this->slot == NULL) return Retcode::success; // simulation
  if (time == 0) return Retcode::success; // nothing to do

  this->start(reverse);
  sleep_idle(time);
  this->stop();

  return Retcode::success;
}

void Pump::start(bool reverse=false) {
  if (this->slot == NULL) return; // simulation

  PumpSlot *slot = this->slot;
  if (slot->pcf == NULL) {
    digitalWrite(slot->in1, reverse ? HIGH : LOW);
    digitalWrite(slot->in2, reverse ? LOW  : HIGH);
  } else {
    slot->pcf->write(slot->in1, reverse ? HIGH : LOW);
    slot->pcf->write(slot->in2, reverse ? LOW  : HIGH);
  }
}

void Pump::stop() {
  if (this->slot == NULL) return; // simulation

  PumpSlot *slot = this->slot;
  if (slot->pcf == NULL) {
    digitalWrite(slot->in1, LOW);
    digitalWrite(slot->in2, LOW);
  } else {
    slot->pcf->write(slot->in1, LOW);
    slot->pcf->write(slot->in2, LOW);
  }
}

void Pump::pump(float amount) {
  if (this->slot == NULL) return; // simulation

  PumpSlot *slot = this->slot;
  if (slot == NULL) { // no pump connected, simulate operation
    debug("pump would add: %.1f", amount);

  } else { // run real pump
    this->run(this->time_init);
    this->run(std::round(amount * this->rate));
    this->run(this->time_reverse, true);
  }
}

Retcode Pump::calibrate(dur_t time1, dur_t time2) {
  return this->calibrate(time1, time2, this->cal_volume[0], this->cal_volume[1]);
}

Retcode Pump::calibrate(dur_t time1, dur_t time2, float volume1, float volume2) {
  debug("trying to calibrate pump with t1: %lld, t2: %lld, v1: %.1f, v2: %.1f",
        time1, time2, volume1, volume2);
  if (volume1 <= 0.0 || volume2 <= 0.0)	return Retcode::invalid_volume;
  if (volume1 == volume2)              	return Retcode::invalid_volume;
  if (time1 == time2)                  	return Retcode::invalid_times;

  float vol_diff  = volume1 - volume2;
  float time_diff = (float) time1 - (float) time2;
  float rate = vol_diff / time_diff;
  debug("calibration raw data: %0.1f, %0.1f, %f", vol_diff, time_diff, rate);

  if (rate <= 0.0) return Retcode::invalid_calibration;

  this->rate = rate;
  debug("rate: %f", rate);

  float init1 = std::round((float) time1 - (volume1 / rate));
  float init2 = std::round((float) time2 - (volume2 / rate));
  dur_t init  = std::round((init1 + init2) / 2.0);

  // check for implausible results
  if (std::abs(init1 - init2) > S(1)) return Retcode::invalid_calibration;
  if (init <= 0) return Retcode::invalid_calibration;

  this->time_init = init;
  this->time_reverse = init; // TODO should this be different?
  debug("time init: %lld, reverse: %lld", time_init, time_reverse);

  this->calibrated = true;
  config_save();
  return Retcode::success;
}

// scale
float scale_weigh() {
  float weight = 0.0;

  if (scale_available) {
    // TODO maybe use a different read command or times value?
    weight = std::max(scale.read_median(), 0.0f);
  } else {
    for (auto const ing : cocktail) {
      weight += ing.amount;
    }
  }
  return weight;
}

float scale_estimate(dur_t init, dur_t time, float rate) {
  if (scale_calibrated) {
    return scale_weigh();
  } else {
    dur_t t = (init < time) ? (time - init) : MS(1);
    return t * rate;
  }
}

bool scale_empty() {
  return scale_weigh() <= EMPTY_SCALE;
}

bool scale_has_container() {
  return scale_weigh() >= CONTAINER_WEIGHT;
}

Retcode scale_calibrate(float weight) {
  if (!scale_available) return Retcode::success;

  if (weight <= 0.0) return Retcode::invalid_weight;

  scale.calibrate_scale(weight);
  scale_calibrated = true;
  config_save();
  return Retcode::success;
}

Retcode scale_tare() {
  if (!scale_available) return Retcode::success;

  scale.tare();
  config_save();
  return Retcode::success;
}

Retcode scale_set_factor(float factor) {
  if (!scale_available) return Retcode::success;

  scale.set_scale(factor);
  scale_calibrated = true;
  config_save();
  return Retcode::success;
}

// utilities

// current time since startup
time_t timestamp_ms() {
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return tv.tv_sec * 1000LL + (tv.tv_usec / 1000LL);
}

// three different sleep modes
void sleep_idle(dur_t duration) {
  delay(duration);
}

void sleep_light(dur_t duration) {
  esp_sleep_enable_timer_wakeup(duration * 1000LL);
  esp_light_sleep_start();
}

void sleep_deep(dur_t duration) {
  esp_sleep_enable_timer_wakeup(duration * 1000LL);
  esp_deep_sleep_start();
}

// LEDs (if available on the chip)
#if defined(LED_BUILTIN)
void led_on(void) {
  digitalWrite(LED_BUILTIN, HIGH);
}

void led_off(void) {
  digitalWrite(LED_BUILTIN, LOW);
}

void blink_leds(dur_t on, dur_t off) {
  led_on(); 	sleep_idle(on);
  led_off();	sleep_idle(off);
}
#endif

void error_loop(void) {
  error("[HALT]");
  while(1) {
#if defined(LED_BUILTIN)
    blink_leds(MS(100), MS(100));
#else
    sleep_idle(MS(100));
#endif
  }
}
