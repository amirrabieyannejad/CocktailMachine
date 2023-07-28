// general system settings
#define BLE_NAME             	"Cocktail Machine ESP32"	// bluetooth server name
#define CORE_DEBUG_LEVEL     	4                       	// 1 = error; 3 = info ; 4 = debug
const unsigned int VERSION   	= 6;                    	// version number (used for configs etc)
const unsigned char MAX_PUMPS	= 1 + 4*8;              	// maximum number of supported pumps;
const float LIQUID_CUTOFF    	= 0.1;                  	// minimum amount of liquid we round off

// general chip functionality
#include <Arduino.h>
#include <Preferences.h>
#include <SPI.h>
#include <Wire.h>

// pinout
typedef const unsigned char Pin;
Pin PIN_SDCARD_CS       	= 14;
                        	
Pin PIN_HX711_DOUT      	= 4;
Pin PIN_HX711_SCK       	= 5;
                        	
Pin PIN_SDA             	= 14;
Pin PIN_SCL             	= 32;
                        	
Pin PIN_BUILTIN_PUMP_IN1	= 33;
Pin PIN_BUILTIN_PUMP_IN2	= 27;

// c++ standard library
#include <algorithm>
#include <forward_list>
#include <inttypes.h>
#include <map>
#include <queue>
#include <sys/time.h>
#include <unordered_map>
#include <unordered_set>

// json
#include <ArduinoJson.h>

// sd card
#include <FS.h>
#include <SD.h>

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

struct Processed {
  virtual const String json();
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

  dur_t time_init;
  dur_t time_reverse;
  float rate;

  Pump(PumpSlot *slot, String liquid, float volume, dur_t time_init, dur_t time_reverse, float rate)
    : slot(slot), liquid(liquid), volume(volume),
      time_init(time_init), time_reverse(time_reverse), rate(rate) {};

  Pump(PumpSlot *slot, String liquid, float volume)
    : slot(slot), liquid(liquid), volume(volume),
      time_init(MS(1)), time_reverse(MS(1)), rate(1.0) {};

  Processed* drain(float amount);
  Processed* refill(float volume);
  Processed* empty();

  void run(dur_t time, bool reverse);
  void start(bool reverse);
  void stop();
  void pump(float amount);

  Processed* calibrate(dur_t time1, dur_t time2, float volume1, float volume2);
};

struct Ingredient {
  String name;
  float amount;
};

struct Recipe {
  String name;
  std::forward_list<Ingredient> ingredients;
  bool can_make;

  Recipe(String name, std::forward_list<Ingredient> ingredients) : name(name), ingredients(ingredients), can_make(false) {};
  float total_volume();
  Processed* make(User user);
};

// services
struct Service {
  BLECharacteristic *ble_char;
  Service(const char *uuid_service, const char *uuid_char, const uint32_t props, const int num_chars);
};

struct ID : Service {
  ID(const char *uuid_char, const char *init_value);
  void update(const char *value);
};

struct Status : Service {
  Status(const char *uuid_char, const char *init_value);
  void update(const char *value);
};

struct Comm : Service {
  // FIXME conn_id isn't stable across reconnections, so we should switch to MAC or something similar
  std::unordered_map<uint16_t, Processed *> responses;
  Comm(const char *uuid_char);

  void respond(uint16_t id, Processed *ret);
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
#define NUM_STATUS  	7
                    	
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
                             	
#define UUID_COMM            	"dad995d1-f228-38ec-8b0f-593953973406"
#define UUID_COMM_USER       	"eb61e31a-f00b-335f-ad14-d654aac8353d"
#define UUID_COMM_ADMIN      	"41044979-6a5d-36be-b9f1-d4d49e3f5b73"
                             	
#define UUID_ID              	"8ccbf239-1cd2-4eb7-8872-1cb76c980d14"
#define UUID_ID_NAME         	"c0605c38-3f94-33f6-ace6-7a5504544a80"


// return codes
#define def_ret(cls, msg)                                               \
  struct cls : public Processed {                                       \
    static constexpr char *_json = "\"" msg "\"";                       \
    const String json() override { return String(_json); }              \
  };

def_ret(Init,              	"init");
def_ret(Success,           	"ok");
def_ret(Processing,        	"processing");
def_ret(Ready,             	"ready");
def_ret(Pumping,           	"pumping");
def_ret(Mixing,            	"mixing");
def_ret(Done,              	"cocktail done");
def_ret(Unsupported,       	"unsupported");
def_ret(Unauthorized,      	"unauthorized");
def_ret(Invalid,           	"invalid json");
def_ret(TooBig,            	"message too big");
def_ret(Incomplete,        	"missing arguments");
def_ret(UnknownCommand,    	"unknown command");
def_ret(MissingCommand,    	"command missing even though it parsed right");
def_ret(WrongComm,         	"wrong comm channel");
def_ret(InvalidSlot,       	"invalid pump slot");
def_ret(InvalidVolume,     	"invalid volume");
def_ret(InvalidWeight,     	"invalid weight");
def_ret(InvalidTimes,      	"invalid times");
def_ret(Insufficient,      	"insufficient amounts of liquid available");
def_ret(MissingLiquid,     	"liquid unavailable");
def_ret(MissingRecipe,     	"recipe not found");
def_ret(DuplicateRecipe,   	"recipe already exists");
def_ret(MissingIngredients,	"missing ingredients");
def_ret(InvalidCalibration,	"invalid calibration data");

struct RetUserID : Processed {
  User user;
  RetUserID(User user) : user(user) {}
  static char out[BUF_RESPONSE]; // FIXME make this a string or something

  const String json() override {
    snprintf(out, sizeof(out), "{\"user\": %d}", user);
    debug("json: %s", out);
    return String(out);
  }
};
char RetUserID::out[BUF_RESPONSE];

// commands
struct Command {
  virtual Processed* execute();
  virtual const char* cmd_name();
  virtual bool is_valid_comm(Comm *comm);
};

#define def_cmd(name, channel)                                          \
  static constexpr char *json_name = name;                              \
  const char* cmd_name() override { return json_name; }                 \
  Processed* execute() override;                                        \
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

struct CmdMakeRecipe : public Command {
  def_cmd("make_recipe", USER);
  User user;
  String recipe;
  CmdMakeRecipe(User user, String recipe) : user(user), recipe(recipe) {}
};

struct CmdAddLiquid : public Command {
  def_cmd("add_liquid", USER);
  User user;
  String liquid;
  float volume;
  CmdAddLiquid(User user, String liquid, float volume) : user(user), liquid(liquid), volume(volume) {}
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
  std::forward_list<Ingredient> ingredients;
  CmdDefineRecipe(User user, String name, std::forward_list<Ingredient> ingredients)
    : user(user), name(name), ingredients(ingredients) {}
};

struct CmdEditRecipe : public Command {
  def_cmd("edit_recipe", USER);
  User user;
  String name;
  std::forward_list<Ingredient> ingredients;
  CmdEditRecipe(User user, String name, std::forward_list<Ingredient> ingredients)
    : user(user), name(name), ingredients(ingredients) {}
};

struct CmdDeleteRecipe : public Command {
  def_cmd("delete_recipe", ADMIN);
  User user;
  String name;
  CmdDeleteRecipe(User user, String name) : user(user), name(name) {}
};

struct CmdAbort : public Command {
  def_cmd("abort", USER);
  User user;
  CmdAbort(User user) : user(user) {}
};

struct CmdReset : public Command {
  def_cmd("reset", USER);
  User user;
  CmdReset(User user) : user(user) {}
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
  Processed *err;
};

struct Queued {
  Command *command;
  Comm *comm;
  uint16_t conn_id;
};

// global state

time_t config_state = 0;
Preferences preferences;

bool sdcard_available = false;

bool scale_available  = false;
HX711 scale;

PumpSlot* pump_slots[MAX_PUMPS];
Pump* pumps[MAX_PUMPS];

Processed* machine_state;

User current_user = -1;
std::map<User, String> users;

BLEServer *ble_server;
ID*     all_id[NUM_ID];
Status* all_status[NUM_STATUS];
Comm*   all_comm[NUM_COMM];

std::forward_list<Recipe>     recipes;
std::forward_list<Ingredient> cocktail;

std::queue<Queued> command_queue;

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

// sdcard
bool sdcard_setup(void);
bool sdcard_start(void);
void sdcard_stop(void);
bool sdcard_save(void);

// bluetooth
bool ble_start(void);
void ble_stop(void);

// scale
float scale_weigh();
Processed* scale_calibrate(float weight);
Processed* scale_tare();
Processed* scale_set_factor(float factor);

// command processing
Processed* add_to_queue(const String json, uint16_t conn_id);
Parsed parse_command(const String json);
bool is_admin(User user);

// update machine state
void update_cocktail(void);
void update_liquids(void);
void update_recipes(void);
void update_state(Processed *state);
void update_config_state(time_t ts);
void update_user(User user);
void update_all_possible_recipes();
void update_possible_recipes(String liquid);

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

  // setup sd card
  sdcard_start();

  { // setup pump slots
    for (int i=0; i<MAX_PUMPS; i++)	pump_slots[i]	= NULL;
    int used_slots = 0;
    pump_slots[used_slots++] = new PumpSlot{
      NULL,
      PIN_BUILTIN_PUMP_IN1,
      PIN_BUILTIN_PUMP_IN2,
    };

    for(int i=0; i<8; i++) {
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

    while (!command_queue.empty()) 	command_queue.pop();

    recipes.clear();
    cocktail.clear();

    users.clear();
    users[0] = "admin";

    machine_state = new Init();

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
  update_recipes();
  update_cocktail();
  update_user(USER_UNKNOWN);
  update_state(new Ready());

  debug("ready");
}

void loop() {
  if (!command_queue.empty()) {
    Queued q = command_queue.front();
    command_queue.pop();

    debug("processing queue (#%d): %s (%d)", command_queue.size(), q.command->cmd_name(), q.conn_id);
    Processed* p = q.command->execute();

    // cleanup
    delete q.command;

    // send response
    q.comm->respond(q.conn_id, p);
  }
}

// command processing
Processed* add_to_queue(const String json, Comm *comm, uint16_t conn_id) {
  Parsed p = parse_command(json);
  if (p.err) return p.err;

  if (p.command == NULL) {
    error("command missing even though there wasn't a parse error");
    return new MissingCommand();
  }

  // enforce comm separation
  if (!p.command->is_valid_comm(comm)) {
    delete p.command; // cleanup
    return new WrongComm();
  }

  debug("parsed, adding to queue: %d, %s", conn_id, p.command->cmd_name());

  // add to process queue
  Queued q = {p.command, comm, conn_id};
  command_queue.push(q);

  return NULL;
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
      return Parsed{NULL, new Invalid()};

    case DeserializationError::NoMemory:
    case DeserializationError::TooDeep:
      return Parsed{NULL, new TooBig()};

    default:
      return Parsed{NULL, new Invalid()};
    }
  }

  // parse json document into command type
  Command *cmd;

  // helper macros
#define match_name(cls) (!strcmp(cmd_name, cls::json_name))

  // TODO pass the missing argument in Incomplete(field)

#define parse_array(field)                                    \
  JsonArray field = doc[#field];                              \
  if (field.isNull()) return Parsed{NULL, new Incomplete()};

#define parse_ingredients(array)                                        \
  std::forward_list<Ingredient> ingredients = {};                       \
  parse_array(array);                                                   \
  for (JsonVariant _v : array) {                                        \
    JsonArray _tuple = _v.as<JsonArray>();                              \
    if (_tuple.isNull() || _tuple.size() != 2)                          \
      return Parsed{NULL, new Incomplete()};                            \
                                                                        \
    String _name   	= String(_tuple[0].as<const char*>());             \
    float _amount  	= _tuple[1].as<float>();                           \
    Ingredient _ing	= Ingredient{_name, _amount};                      \
                                                                        \
    ingredients.push_front(_ing);                                       \
  }

#define parse_str(field)                                 \
  const char *_##field = doc[#field];                    \
  if (!_##field) return Parsed{NULL, new Incomplete()};  \
  const String field = String(_##field)

#define parse_as(field, type)                                     \
  JsonVariant j_##field = doc[#field];                            \
  if (j_##field.isNull()) return Parsed{NULL, new Incomplete()};  \
  const type field = j_##field.as<type>();

#define parse_bool(field)    	parse_as(field, bool)
#define parse_float(field)   	parse_as(field, float)
#define parse_int32(field)   	parse_as(field, int32_t)
#define parse_duration(field)	parse_as(field, dur_t)
#define parse_user()         	parse_as(user,  int32_t)

  const char *cmd_name = doc["cmd"];
  if (!cmd_name) return Parsed{NULL, new Incomplete()};

  if (match_name(CmdTest)) {
    cmd = new CmdTest();

  } else if (match_name(CmdInitUser)) {
    parse_str(name);
    cmd = new CmdInitUser{name};

  } else if (match_name(CmdAddLiquid)) {
    parse_user();
    parse_str(liquid);
    parse_float(volume);
    cmd = new CmdAddLiquid(user, liquid, volume);

  } else if (match_name(CmdMakeRecipe)) {
    parse_user();
    parse_str(recipe);
    cmd = new CmdMakeRecipe(user, recipe);

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

  } else if (match_name(CmdAbort)) {
    parse_user();
    cmd = new CmdAbort(user);

  } else if (match_name(CmdReset)) {
    parse_user();
    cmd = new CmdReset(user);

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
    return Parsed{NULL, new UnknownCommand()};
  }

  return Parsed{cmd, NULL};
}

// command logic
Processed* CmdTest::execute() { return new Success(); };

Processed* CmdRestart::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  ESP.restart();
}

Processed* CmdFactoryReset::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  { // reset settings
    config_clear();

    for (int i=0; i<MAX_PUMPS; i++) pumps[i]	= NULL;

    if (scale_available) {
      scale.set_offset(0);
      scale.set_scale(1.0);
    }

    while (!command_queue.empty()) command_queue.pop();

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
  update_state(new Ready());
  update_user(USER_UNKNOWN);

  return new Success();
}

Processed* CmdRunPump::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot  = this->slot;
  dur_t time = this->time;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return new InvalidSlot();
  }
  Pump *p = pumps[slot];

  debug("running pump %d for %dms", slot, time);
  update_user(user);
  update_state(new Pumping());
  p->run(time, false);

  // nb: this will always fuck up our internal data unless the pump is already calibrated,
  // so you should refill the pump afterwards

  float volume = time * p->rate;
  cocktail.push_front(Ingredient{"<calibration>", volume});
  p->volume = std::max(p->volume - volume, 0.0f);

  update_liquids();
  update_possible_recipes(p->liquid);
  update_cocktail();
  update_state(new Done());

  return new Success();
};

Processed* CmdCalibratePump::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot = this->slot;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return new InvalidSlot();
  }
  Pump *p = pumps[slot];

  return p->calibrate(this->time1, this->time2, this->volume1, this->volume2);
};

Processed* CmdSetPumpTimes::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot = this->slot;
  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return new InvalidSlot();
  }
  Pump *p = pumps[slot];

  p->time_init    = this->time_init;
  p->time_reverse = this->time_reverse;
  p->rate         = this->rate;
  config_save();

  return new Success();
};

Processed* CmdCalibrateScale::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  Processed *err = scale_calibrate(this->weight);
  if (err) return err;
  return new Success();
};

Processed* CmdTareScale::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  Processed *err = scale_tare();
  if (err) return err;
  return new Success();
};

Processed* CmdSetScaleFactor::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  Processed *err = scale_set_factor(this->factor);
  if (err) return err;
  return new Success();
};

Processed* CmdClean::execute() {
  // FIXME implement
  if (!is_admin(this->user)) return new Unauthorized();
  return new Unsupported();
};

Processed* CmdAbort::execute() {
  // only allowed for admin or the current user
  if (current_user != USER_UNKNOWN &&
      current_user != this->user &&
      !is_admin(this->user))
    return new Unauthorized();

  // TODO stop active pumps
  // TODO abort remaining cocktail

  // update machine state
  update_cocktail();
  update_liquids();
  update_recipes();

  if (cocktail.empty()) {
    update_state(new Ready());
    update_user(USER_UNKNOWN);
  } else {
    update_state(new Done());
  }

  return new Success();
};

Processed* CmdReset::execute() {
  // TODO restart hardware modules, like the sd card reader?
  cocktail.clear();

  Processed *err = scale_tare();
  if (err) return err;

  // update machine state
  update_cocktail();
  update_user(USER_UNKNOWN);
  update_state(new Ready());

  return new Success();
};

Processed* CmdInitUser::execute() {
  User id = users.size();
  users[id] = this->name;

  config_save();
  return new RetUserID(id);
}

Processed* CmdDefinePump::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot = this->slot;
  float volume = this->volume;

  if (slot < 0 || slot >= MAX_PUMPS) {
    return new InvalidSlot();
  }

  if (volume < 0) {
    return new InvalidVolume();
  }

  // clear out old pump
  if (pumps[slot] != NULL) {
    delete pumps[slot];
  }

  // save new pump
  PumpSlot *pump_slot = pump_slots[slot];
  Pump *p = new Pump(pump_slot, this->liquid, volume);
  pumps[slot] = p;

  // update machine state
  update_liquids();
  update_config_state(timestamp_ms());

  return new Success();
}

Processed* CmdEditPump::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot = this->slot;

  if (slot < 0 || slot >= MAX_PUMPS) {
    return new InvalidSlot();
  }

  if (volume < 0) {
    return new InvalidVolume();
  }

  // edit pump
  Pump *pump = pumps[slot];

  if (pump == NULL) {
    return new InvalidSlot();
  }

  pump->liquid = this->liquid;
  pump->volume = this->volume;

  // update machine state
  update_liquids();
  update_config_state(timestamp_ms());

  return new Success();
}

Processed* CmdRefillPump::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  int32_t slot = this->slot;
  float volume = this->volume;

  if (slot < 0 || slot >= MAX_PUMPS || pumps[slot] == NULL) {
    return new InvalidSlot();
  }

  if (volume < 0) {
    return new InvalidVolume();
  }

  // refill pump
  Pump *p = pumps[slot];
  Processed *err = p->refill(volume);
  if (err) return err;

  // update machine state
  update_liquids();
  update_possible_recipes(p->liquid);

  return new Success();
}

Processed* CmdAddLiquid::execute() {
  // only allowed for admin or the current user
  if (current_user == this->user ||
      current_user == USER_UNKNOWN ||
      is_admin(this->user)) {
    Processed *err = add_liquid(this->user, this->liquid, this->volume);
    if (err) return err;
    return new Success();

  } else {
    return new Unauthorized();
  }
}

Processed* CmdDefineRecipe::execute() {
  // make sure the recipe is unique
  const String name = this->name;

  for (auto const &r : recipes) {
    if (r.name == name) return new DuplicateRecipe();
  }

  size_t num = 0;
  for (auto const &it : this->ingredients) num += 1;

  if (num < 1) return new MissingIngredients();

  // add it
  debug("adding recipe %s with %d ingredients", name, num);
  Recipe r = Recipe(name, this->ingredients);
  recipes.push_front(r);

  // update state
  update_recipes();

  return new Success();
}

Processed* CmdEditRecipe::execute() {
  // check ingredients
  const String name = this->name;
  size_t num = 0;
  for (auto const &it : this->ingredients) num += 1;
  if (num < 1) return new MissingIngredients();

  debug("updating recipe %s with %d ingredients", name, num);

  for (auto it = recipes.begin(); it != recipes.end(); it++) {
    if (it->name == name) {
      // FIXME memory leak?
      it->ingredients = this->ingredients;

      // update state
      update_recipes();

      return new Success();
    }
  }


  return new MissingRecipe();
}

Processed* CmdDeleteRecipe::execute() {
  if (!is_admin(this->user)) return new Unauthorized();

  const String name = this->name;

  debug("deleting recipe %s", name);
  recipes.remove_if([name](Recipe r){ return r.name == name; });

  // update state
  update_recipes();

  return new Success();
}

Processed* CmdMakeRecipe::execute() {
  const String name = this->recipe;
  const Recipe *recipe = NULL;
  for (auto const &r : recipes) {
    if (r.name == name) {
      recipe = &r;
      break;
    }
  }
  if (!recipe) return new MissingRecipe();

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
  for (auto ing = recipe->ingredients.begin(); ing != recipe->ingredients.end(); ing++) {
    std::string liquid	= ing->name.c_str();
    float have        	= liquids[liquid];
    float need        	= ing->amount;
    bool found        	= liquids.count(liquid) > 0;

    debug("    need %.1f / %.1f of %s", need, have, liquid.c_str());
    if (!found)                     	return new MissingLiquid();
    if (have - need < LIQUID_CUTOFF)	return new Insufficient();
    liquids[liquid] = have - need; // update total
  }

  debug("making recipe %s", name.c_str());
  update_user(this->user);
  update_state(new Mixing());

  for (auto ing = recipe->ingredients.begin(); ing != recipe->ingredients.end(); ing++) {
    Processed *err = add_liquid(this->user, ing->name, ing->amount);
    if (err) {
      update_state(new Done());
      return err;
    }
  }

  update_state(new Done());
  return new Success();
};

bool is_admin(User user) {
  // TODO use roles etc
  return (user == USER_ADMIN);
}

Processed* add_liquid(User user, const String liquid, float amount) {
  float need   	= amount;
  float have   	= 0;

  if (need < 0) {
    return new InvalidVolume();
  }

  debug("attempting to add %.1f of %s to cocktail", need, liquid.c_str());

  // check that we have enough liquid first
  bool found = false;
  for (int i=0; i<MAX_PUMPS; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;

    if (p->liquid == liquid) {
      found = true;
      debug("  in pump %d: %.1f", i, p->volume);
      have += p->volume;
    }
  }

  if (!found)                     	return new MissingLiquid();
  if (have - need < LIQUID_CUTOFF)	return new Insufficient();

  // tare the scale if the cocktail is empty
  Processed *err = scale_tare();
  if (err) return err;

  update_user(user);
  update_state(new Pumping());

  // add the liquid
  float used = 0;
  for (int i=0; i<MAX_PUMPS && need >= LIQUID_CUTOFF; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;

    if (p->liquid == liquid) {
      debug("  need %.1f, using pump %d: %.1f", need, i, p->volume);
      float use = std::min(p->volume, need);
      Processed *err = p->drain(use);
      if (err) return err;
      need -= use;
      used += use;
    }
  }

  // update cocktail state
  cocktail.push_front(Ingredient{liquid, used});

  // update state
  update_liquids();
  update_cocktail();
  update_possible_recipes(liquid);
  update_state(new Done());

  // shouldn't happen, but do a sanity check
  if (need >= LIQUID_CUTOFF) {
    return new Insufficient();
  }

  return NULL;
}

Processed* Pump::drain(float amount) {
  if (amount < 0) return new InvalidVolume();
  if (this->volume - amount < LIQUID_CUTOFF) return new Insufficient();

  this->pump(amount);
  this->volume -= amount;
  return NULL;
}

Processed* Pump::refill(float amount) {
  if (amount < 0) return new InvalidVolume();

  this->volume = amount;
  return NULL;
}

Processed* Pump::empty() {
  this->volume = 0;
  return NULL;
}

void update_cocktail() {
  // generate json output
  debug("updating cocktail state");

  float weight = scale_weigh();

  String out = String("{\"weight\":");
  out.concat(String(weight, 1));

  out.concat(",\"content\":[");
  for (auto ing = cocktail.begin(); ing != cocktail.end(); ing++) {
    debug("  ingredient: %s, amount: %.1f", ing->name.c_str(), ing->amount);

    out.concat("[\"");
    out.concat(ing->name);
    out.concat("\",");
    out.concat(String(ing->amount, 1));
    out.concat(']');

    if (next(ing) != cocktail.end()) out.concat(',');
  }
  out.concat("]}");

  all_status[ID_COCKTAIL]->update(out.c_str());
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
      out.concat("\":{\"liquid\":");
      out.concat(pump->liquid);
      out.concat("\",\"volume\":");
      out.concat(String(pump->volume, 1));
      out.concat('}');

      prev = true;
    }
    out.concat('}');
    all_status[ID_PUMPS]->update(out.c_str());
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

    all_status[ID_LIQUIDS]->update(out.c_str());
  }

  config_save();
}

void update_recipes() {
  // generate json output
  debug("updating recipes state");

  // update can_make state of all recipes
  update_all_possible_recipes();

  String out = String('{');
  for (auto r = recipes.begin(); r != recipes.end(); r++) {
    debug("  recipe: %s (can make: %s)", r->name.c_str(), r->can_make ? "yes" : "no");

    out.concat('"');
    out.concat(r->name);
    out.concat("\":{\"ingredients\":[");

    for (auto ing = r->ingredients.begin(); ing != r->ingredients.end(); ing++) {
      debug("    ingredient: %s, amount: %.1f", ing->name.c_str(), ing->amount);

      out.concat("[\"");
      out.concat(ing->name);
      out.concat("\",");
      out.concat(String(ing->amount, 1));
      out.concat(']');

      if (next(ing) != r->ingredients.end()) out.concat(',');
    }
    out.concat("],\"can_make\":");
    out.concat(r->can_make ? "true" : "false");
    out.concat('}');

    if (next(r) != recipes.end()) out.concat(',');
  }
  out.concat('}');

  all_status[ID_RECIPES]->update(out.c_str());

  update_config_state(timestamp_ms());
  config_save();
};

void update_state(Processed *state) {
  if (state == NULL) {
    error("invalid state");
    error_loop();
  }

  String json = state->json();
  debug("updating machine state: %s", json.c_str());

  // remove old state
  if (machine_state) delete machine_state;

  machine_state = state;
  all_status[ID_STATE]->update(json.c_str());
}

void update_config_state(time_t ts) {
  config_state = timestamp_ms();
  String s = String(ts);
  all_status[ID_TIMESTAMP]->update(s.c_str());
}

void update_user(User user) {
  current_user = user;
  String s = String(user);
  all_status[ID_USER]->update(s.c_str());
}

void update_all_possible_recipes() {
  // calculate total liquids
  std::unordered_set<std::string> liquids = {};
  for(int i=0; i<MAX_PUMPS; i++) {
    Pump *pump = pumps[i];
    if (pump == NULL) continue;

    std::string liquid = pump->liquid.c_str();
    liquids.insert(liquid);
  }

  for (auto const &liquid : liquids) {
    update_possible_recipes(String(liquid.c_str()));
  }
}

void update_possible_recipes(String liquid) {
  bool updated = false;

  // calculate the total available
  float have = 0;
  for (int i=0; i<MAX_PUMPS; i++) {
    Pump *p = pumps[i];
    if (p == NULL) continue;
    have += p->volume;
  }

  // adjust recipes
  for (auto &recipe : recipes) {
    float need = 0;
    for (auto const &ing : recipe.ingredients) {
      if (ing.name == liquid) {
        need += ing.amount;
      }
    }
    if (need > 0) {
      bool can_make = (have - need < LIQUID_CUTOFF);
      if (recipe.can_make != can_make) {
        updated = true;
        recipe.can_make = can_make;
      }
    }
  }

  if (updated) {
    update_config_state(timestamp_ms());
    config_save();
  }
}

// bluetooth
bool ble_start(void) {
  debug("starting ble");
  BLEDevice::init(BLE_NAME);
  ble_server = BLEDevice::createServer();

  // setup callback
  ble_server->setCallbacks(new ServerCB());

  // init services
  all_id[ID_NAME]         	= new ID(UUID_ID_NAME,             	BLE_NAME);
                          	                                   	
  all_status[ID_STATE]    	= new Status(UUID_STATUS_STATE,    	"\"init\"");
  all_status[ID_LIQUIDS]  	= new Status(UUID_STATUS_LIQUIDS,  	"{}");
  all_status[ID_PUMPS]    	= new Status(UUID_STATUS_PUMPS,    	"{}");
  all_status[ID_RECIPES]  	= new Status(UUID_STATUS_RECIPES,  	"{}");
  all_status[ID_COCKTAIL] 	= new Status(UUID_STATUS_COCKTAIL, 	"[]");
  all_status[ID_TIMESTAMP]	= new Status(UUID_STATUS_TIMESTAMP,	"0");
  all_status[ID_USER]     	= new Status(UUID_STATUS_USER,     	"-1");
                          	
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

ID::ID(const char *uuid_char, const char *init_value)
  : Service(UUID_ID, uuid_char,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY,
            NUM_ID) {
  this->ble_char->setCallbacks(new CharCB());
  this->ble_char->setValue(init_value);
}

void ID::update(const char *value) {
  this->ble_char->setValue(value);
  this->ble_char->notify();
}

Status::Status(const char *uuid_char, const char *init_value)
  : Service(UUID_STATUS, uuid_char,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY,
            NUM_STATUS) {
  this->ble_char->setCallbacks(new CharCB());
  this->ble_char->setValue(init_value);
}

void Status::update(const char *value) {
  this->ble_char->setValue(value);
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

void Comm::respond(const uint16_t id, Processed *ret) {
  if (!ret) {
    debug("tried to send empty respond");
    return;
  }

  debug("sending response to %d: %s", id, ret->json().c_str());
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
    all_comm[i]->responses[id] = new Ready();
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
  String value;
  if (this->comm->responses.
      count(id)) {
    value = this->comm->responses[id]->json();
  } else {
    value = String("");
  }

  debug("read: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), value.c_str());
  ble_char->setValue(value.c_str());
}

void CommCB::onWrite(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->write.conn_id;

  if (this->comm->responses.count(id)) {
    const std::string v = ble_char->getValue();

    debug("write: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), v.c_str());

    Processed *err = add_to_queue(String(v.c_str()), this->comm, id);

    if (err) {
      debug("failed to parse: %s", err->json().c_str());
      this->comm->respond(id, err);
    } else {
      debug("parsed, waiting for queue");
      this->comm->responses[id] = new Processing();
    }
  }
}

// configs

bool config_save(void) {
  if (sdcard_available) { // save state to SD card
    char *config = "FIXME";
    debug("saving state to SD card");

    File file = SD.open("/config.json", FILE_WRITE);
    if (!file) {
      debug("failed to open config file");
      return false;
    }
    file.print(config);
    file.close();

    uint64_t mb  	= 1024 * 1024;
    uint64_t used	= SD.usedBytes();
    uint64_t size	= SD.totalBytes();
    debug("SD card: %lluMB / %lluMB used", used/mb, size/mb);
  }

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
      preferences.putFloat("scale/t", scale.get_offset());
      preferences.putFloat("scale/f", scale.get_scale());
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
    float tare = preferences.getFloat("scale/t");
    float factor = preferences.getFloat("scale/f");
    scale.set_scale(factor);
    scale.set_offset(tare);
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

      snprintf(key, sizeof(key), "pump_%d/ti", i);
      time_t time_init = preferences.getULong64(key);

      snprintf(key, sizeof(key), "pump_%d/tr", i);
      time_t time_reverse = preferences.getULong64(key);

      snprintf(key, sizeof(key), "pump_%d/rt", i);
      float rate = preferences.getFloat(key);

      PumpSlot *pump_slot = pump_slots[i];
      // if (pump_slot == NULL) continue; // pump slot missing
      pumps[i] = new Pump(pump_slot, liquid, volume, time_init, time_reverse, rate);
    }
  }

  { // recipes
    char key[BUF_PREF_KEY];
    recipes.clear(); // clear old config

    unsigned int num_recipes = preferences.getUInt("num_recipes", 0);
    for (int i=0; i<num_recipes; i++) {
      snprintf(key, sizeof(key), "recipe_%d", i);
      String name = preferences.getString(key);

      std::forward_list<Ingredient> ingredients = {};

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

void Pump::run(dur_t time, bool reverse=false) {
  if (this->slot == NULL) return; // simulation

  this->start(reverse);
  sleep_idle(time);
  this->stop();
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

Processed* Pump::calibrate(dur_t time1, dur_t time2, float volume1, float volume2) {
  if (volume1 <= 0.0 || volume2 <= 0.0)	return new InvalidVolume();
  if (volume1 == volume2)              	return new InvalidVolume();
  if (time1 == time2)                  	return new InvalidTimes();

  float vol_diff  = volume1 - volume2;
  float time_diff = (float) time1 - (float) time2;
  float rate = vol_diff / time_diff;
  debug("calibration raw data: %0.1f, %0.1f, %f", vol_diff, time_diff, rate);

  if (rate <= 0.0) return new InvalidCalibration();

  this->rate = rate;
  debug("rate: %f", rate);

  float init1 = std::round((float) time1 - (volume1 / rate));
  float init2 = std::round((float) time2 - (volume2 / rate));

  // check for implausible results
  if (std::abs(init1 - init2) > S(1)) return new InvalidCalibration();

  dur_t init = std::round((init1 + init2) / 2.0);
  this->time_init = init;
  this->time_reverse = init; // TODO should this be different?
  debug("time init: %d, reverse: %d", time_init, time_reverse);

  config_save();
  return new Success();
}

// scale
float scale_weigh() {
  if (!scale_available) {
    float weight = 0.0;
    for (auto ing = cocktail.begin(); ing != cocktail.end(); ing++) {
      weight += ing->amount;
    }
    return weight;
  }

  // TODO maybe use a different read command or times value?
  float weight = scale.read_median();
  return weight;
}

Processed* scale_calibrate(float weight) {
  if (!scale_available) return NULL;

  if (weight <= 0.0) return new InvalidWeight();

  scale.calibrate_scale(weight);
  config_save();
  return NULL;
}

Processed* scale_tare() {
  if (!scale_available) return NULL;

  scale.tare();
  config_save();
  return NULL;
}

Processed* scale_set_factor(float factor) {
  if (!scale_available) return NULL;

  scale.set_scale(factor);
  config_save();
  return NULL;
}

// sd card

bool sdcard_setup(void) {
  if (!SD.begin(PIN_SDCARD_CS)){
    error("failed to load SD card reader");
    return false;
  }
  debug("SD card init");

  uint8_t ct = SD.cardType();
  if(ct == CARD_NONE){
    error("no SD card found");
    return false;
  }

  // we read the sd card to force the reader to actually access it
  File root = SD.open("/");
  if(!root) {
    error("failed to read SD card");
    return false;
  }

  File file = root.openNextFile();
  debug("SD card contents:");
  while(file){
    if(file.isDirectory()){
      debug(" DIR : %s", file.name());
    } else {
      debug(" FILE: %16s  SIZE: %d", file.name(), file.size());
    }
    file = root.openNextFile();
  }

  return true;
}

bool sdcard_start(void) {
  if (sdcard_available) sdcard_stop();

  sdcard_available = sdcard_setup();
  return sdcard_available;
}

void sdcard_stop(void) {
  if (sdcard_available) SD.end();
}

bool sdcard_save(void) {
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
  while(1) {
#if defined(LED_BUILTIN)
    blink_leds(MS(100), MS(100));
#else
    error("[HALT]");
    sleep_idle(MS(100));
#endif
  }
}
