// supported features
// #define SD_CARD
#define SIMULATE

// pinout
#if defined(SD_CARD)
#define PIN_SDCARD_CS A5
#endif

// c++ standard library
#include <forward_list>
#include <queue>
#include <sys/time.h>
#include <unordered_map>
using namespace std;

// general chip functionality
#include <Arduino.h>
#include <EEPROM.h>
#include <SPI.h>

// json
#include <ArduinoJson.h>

// sd card
#if defined(SD_CARD)
#include "FS.h"
#include "SD.h"
#endif

// bluetooth
#include <BLE2902.h>
#include <BLEAddress.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>

#define BLE_NAME "Cocktail Machine ESP32"

#define PROP_READ  (BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY)
#define PROP_WRITE (BLECharacteristic::PROPERTY_WRITE)

struct Service {
  BLECharacteristic *ble_char;
  Service(const char *uuid_service, const char *uuid_char, const uint32_t props);
};

struct Status : Service {
  Status(const char *uuid_char, const char *init_value);
  void update(const char *value);
};

struct Comm : Service {
  // FIXME conn_id isn't stable across reconnections, so we should switch to MAC or something similar
  unordered_map<uint16_t, const char *> responses;
  Comm(const char *uuid_char);

  void respond(uint16_t id, const char *value);
  bool is_id(int id);
};

struct ServerCB : BLEServerCallbacks {
  void onConnect(BLEServer *server);
  void onConnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
  void onDisconnect(BLEServer *server);
  void onDisconnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
};

struct CommCB: BLECharacteristicCallbacks {
  Comm *comm;
  CommCB(Comm *comm);

  void onRead(BLECharacteristic  *ble_char, esp_ble_gatts_cb_param_t *param);
  void onWrite(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param);
};

// convenient grouping of all statuses / comms

#define ID_BASE    	0
#define ID_LIQUIDS 	1
#define ID_STATE   	2
#define ID_RECIPES 	3
#define ID_COCKTAIL	4
#define NUM_STATUS 	5

#define ID_USER 	0
#define ID_ADMIN	1
#define NUM_COMM	2

// UUIDs

#define UUID_STATUS         	"0f7742d4-ea2d-43c1-9b98-bb4186be905d"
#define UUID_STATUS_BASE    	"c0605c38-3f94-33f6-ace6-7a5504544a80"
#define UUID_STATUS_STATE   	"e9e4b3f2-fd3f-3b76-8688-088a0671843a"
#define UUID_STATUS_LIQUIDS 	"fc60afb0-2b00-3af2-877a-69ae6815ca2f"
#define UUID_STATUS_RECIPES 	"9ede6e03-f89b-3e52-bb15-5c6c72605f6c"
#define UUID_STATUS_COCKTAIL	"7344136f-c552-3efc-b04f-a43793f16d43"
                            	
#define UUID_COMM           	"dad995d1-f228-38ec-8b0f-593953973406"
#define UUID_COMM_USER      	"eb61e31a-f00b-335f-ad14-d654aac8353d"
#define UUID_COMM_ADMIN     	"41044979-6a5d-36be-b9f1-d4d49e3f5b73"

// error codes
typedef enum {
  ok,
  unsupported,
  unauthorized,
  ret_user_id,
} retcode;

typedef enum {
  valid,
  invalid,
  too_big,
  incomplete,
  unknown_command,
  missing_command,
  wrong_comm,
} parse_error;

const char *retcode_str[] = {
  "\"ok\"",
  "\"unsupported\"",
  "\"unauthorized\"",
  "\"<user id>\"",
};

const char *parse_error_str[] = {
  "\"processing\"",
  "\"invalid json\"",
  "\"message too big\"",
  "\"missing arguments\"",
  "\"unknown command\"",
  "\"command missing even though it parsed right\"",
  "\"wrong comm channel\"",
};

// commands
typedef uint32_t User;

typedef struct {
  retcode ret;
  User user;
} Processed;

#define RET(code) Processed{code, -1} // default if no additional arguments are used

struct Command {
  virtual Processed execute();
  virtual const char* cmd_name();
  virtual bool is_valid_comm(Comm *comm);
};

#define def_cmd(name, channel)                                          \
  static constexpr char *json_name = name;                              \
  const char* cmd_name() override { return json_name; }                 \
  Processed execute() override;                                         \
  bool is_valid_comm(Comm *comm) override {                             \
    return comm->is_id(ID_ ## channel);                                 \
  }                                                                     \
  // [the remaining methods, particularly the constructor, go here]

struct CmdTest : public Command {
  def_cmd("test", USER);
};

struct CmdInitUser : public Command {
  def_cmd("init_user", USER);
  const char *name;
  CmdInitUser(const char *name) {
    this->name = name;
  }
};

struct CmdMakeRecipe : public Command {
  def_cmd("make_recipe", USER);
  const char *name;
  CmdMakeRecipe(const char *name) {
    this->name = name;
  }
};

struct CmdAddLiquid : public Command {
  def_cmd("add_liquid", ADMIN);
  User user;
  const char *name;
  CmdAddLiquid(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
};

struct CmdDefinePump : public Command {
  def_cmd("define_pump", ADMIN);
  User user;
  const char *liquid;
  float volume;
  CmdDefinePump(User user, const char *liquid, const float volume) {
    this->user = user;
    this->liquid = liquid;
    this->volume = volume;
  }
};

struct CmdDefineRecipe : public Command {
  def_cmd("define_recipe", USER);
  User user;
  const char *name;
  // TODO
  CmdDefineRecipe(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
};

struct CmdEditRecipe : public Command {
  def_cmd("edit_recipe", USER);
  User user;
  const char *name;
  // TODO
  CmdEditRecipe(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
};

struct CmdDeleteRecipe : public Command {
  def_cmd("delete_recipe", ADMIN);
  User user;
  const char *name;
  CmdDeleteRecipe(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
};

struct CmdReset : public Command {
  def_cmd("reset", USER);
  User user;
  CmdReset(User user) {
    this->user = user;
  }
};

struct CmdRestart : public Command {
  def_cmd("restart", ADMIN);
  User user;
  bool factory_reset;
  CmdRestart(User user, bool factory_reset) {
    this->user = user;
    this->factory_reset = factory_reset;
  }
};

struct CmdClean : public Command {
  def_cmd("clean", ADMIN);
  User user;
  CmdClean(User user) {
    this->user = user;
  }
};

struct CmdCalibratePumps : public Command {
  def_cmd("calibrate_pumps", ADMIN);
  User user;
  CmdCalibratePumps(User user) {
    this->user = user;
  }
};

typedef struct {
  Command *command;
  parse_error err;
} Parsed;

typedef struct {
  Command *command;
  Comm *comm;
  uint16_t conn_id;
} Queued;

// machine parts

struct Pump {
  const char *liquid;
  float volume;
  int pin;

  Pump(const char *liquid, float volume, int pin);
  retcode drain(float amount);
  retcode refill(float volume);
  retcode empty();
};

struct Ingredient {
  const char *name;
  float amount;
};

struct Recipe {
  const char *name;
  forward_list<Ingredient> ingredients;

  Recipe(const char *name, forward_list<Ingredient> ingredients);
  float total_volume();
  retcode make(User user);
};

// global state

BLEServer *ble_server;

Status* all_status[NUM_STATUS];
Comm*   all_comm[NUM_COMM];

forward_list<Recipe> recipes;
forward_list<Pump> pumps;
forward_list<Ingredient> cocktail_queue;
forward_list<Ingredient> cocktail;

queue<Queued> command_queue;

// function declarations

// various sleeps
int64_t timestamp_ms(void);
int64_t timestamp_usec(void);
void sleep_idle(uint64_t duration);
void sleep_light(uint64_t duration);
void sleep_deep(uint64_t duration);
void error_loop(void);

// easier time constants
#define USEC(n) (n)
#define MS(n)   (n * 1000LL)
#define S(n)    (n * 1000LL * 1000LL)
#define MIN(n)  (n * 1000LL * 1000LL * 60LL)
#define HOUR(n) (n * 1000LL * 1000LL * 60LL * 60)

// debugging info
#define log(level, msg)                         \
  do {                                          \
    Serial.print(level " ");                    \
    Serial.print(msg);                          \
    Serial.println();                           \
  } while(0)
#define logf(level, msg, ...)                           \
  do {                                                  \
    Serial.print(level " ");                            \
    Serial.printf(msg, __VA_ARGS__);                    \
    Serial.println();                                   \
  } while(0)

#define debug(msg) log("[DEBUG]", msg)
#define info(msg)  log("[INFO]",  msg)
#define error(msg) log("[ERROR]", msg)

#define debugf(msg, ...) logf("[DEBUG]", msg, __VA_ARGS__)
#define infof(msg, ...)  logf("[INFO]",  msg, __VA_ARGS__)
#define errorf(msg, ...) logf("[ERROR]", msg, __VA_ARGS__)

// led feedback (if possible)
#if defined(LED_BUILTIN)
void led_on(void);
void led_off(void);
void blink_leds(uint64_t on, uint64_t off);
#endif

// sdcard
#if defined(SD_CARD)
bool sdcard_start(void);
void sdcard_stop(void);
bool sdcard_save(void);
#endif

// bluetooth
bool ble_start(void);
void ble_stop(void);

// command processing
parse_error add_to_queue(const string json, uint16_t conn_id);
Parsed parse_command(const string json);
bool is_admin(User user);

// machine logic
Processed reset_machine(void);

// init

void setup() {
  { // setup serial communication
    Serial.begin(115200);
    while (!Serial) { sleep_idle(MS(10)); }
    sleep_idle(S(1)); // make sure we don't miss any output

    info("Cocktail Machine v1 starting up");
  }

  { // setup pins
#if defined(LED_BUILTIN)
    pinMode(LED_BUILTIN, OUTPUT);
#endif
  }

#if defined(SD_CARD)
  { // setup sd card
    if (!sdcard_start()) { error_loop(); }

    // we read the sd card to force the reader to actually access it
    File root = SD.open("/");
    if(!root) {
      error("failed to read SD card");
      error_loop();
    }

    File file = root.openNextFile();
    debug("sd card contents:");
    while(file){
      if(file.isDirectory()){
        debugf(" DIR : %s", file.name());
      } else {
        debugf(" FILE: %16s  SIZE: %d", file.name(), file.size());
      }
      file = root.openNextFile();
    }
  }
#endif

  { // initialize machine state
    for (int i=0; i<NUM_STATUS; i++)	all_status[i] = NULL;
    for (int i=0; i<NUM_COMM; i++)  	all_comm[i] = NULL;

    while (!command_queue.empty()) command_queue.pop();
    pumps.clear();
    recipes.clear();
    cocktail_queue.clear();
    cocktail.clear();

    ble_server = NULL;
  }

  { // start bluetooth
    if (!ble_start()) {
      error("failed to start ble");
      error_loop();
    }
  }

  debug("ready");
}

void loop() {
  // just wait
  // process("{\"cmd\": \"test\"}");

  while (!command_queue.empty()) {
    Queued q = command_queue.front();
    command_queue.pop();

    debugf("processing queue (#%d): %s (%d)", command_queue.size(), q.command->cmd_name(), q.conn_id);
    Processed p = q.command->execute();

    // cleanup
    delete q.command;

    // return message
    const char *msg;

    switch (p.ret) {
    case ret_user_id:
      // FIXME
      msg = "{\"user\": -1}";
      break;

    default:
      msg = retcode_str[p.ret];
      break;
    }

    // send response
    q.comm->respond(q.conn_id, msg);
  }

  sleep_idle(MS(100));
}

// command processing
parse_error add_to_queue(const string json, Comm *comm, uint16_t conn_id) {
  Parsed p = parse_command(json);
  if (p.err) return p.err;

  if (p.command == NULL) {
    error("command missing even though there wasn't a parse error");
    return missing_command;
  }

  // enforce comm separation
  if (!p.command->is_valid_comm(comm)) {
    delete p.command; // cleanup
    return wrong_comm;
  }

  debugf("parsed, adding to queue: %d, %s", conn_id, p.command->cmd_name());

  // add to process queue
  Queued q = {p.command, comm, conn_id};
  command_queue.push(q);

  return valid;
}

Parsed parse_command(const string json) {
  debugf("processing json: «%s»", json.c_str());

  StaticJsonDocument<1000> doc; // TODO capacity?
  DeserializationError err = deserializeJson(doc, json);

  if (err) {
    switch (err.code()) {
    case DeserializationError::EmptyInput:
    case DeserializationError::IncompleteInput:
    case DeserializationError::InvalidInput:
      return Parsed{NULL, invalid};

    case DeserializationError::NoMemory:
    case DeserializationError::TooDeep:
      return Parsed{NULL, too_big};

    default:
      return Parsed{NULL, invalid};
    }
  }

  // parse json document into command type
  Command *cmd;

  // helper macros
#define match_name(cls) (!strcmp(cmd_name, cls::json_name))

#define parse_str(field)                        \
  const char *field = doc[#field];              \
  if (!field) return Parsed{NULL, incomplete};

#define parse_as(field, type)                               \
  JsonVariant j_##field = doc[#field];                      \
  if (j_##field.isNull()) return Parsed{NULL, incomplete};  \
  type field = j_##field.as<type>();

#define parse_as_default(field, type, def_val)                          \
  JsonVariant j_##field = doc[#field];                                  \
  type field = (j_##field.isNull()) ? def_val : j_##field.as<type>();

#define parse_bool(field) 	parse_as(field, bool)
#define parse_float(field)	parse_as(field, float)
#define parse_opt(field)  	parse_as_default(field, bool, false)
#define parse_user()      	parse_as(user, uint32_t)

  const char *cmd_name = doc["cmd"];
  if (!cmd_name) return Parsed{NULL, incomplete};

  if (match_name(CmdTest)) {
    cmd = new CmdTest();

  } else if (match_name(CmdInitUser)) {
    parse_str(name);
    cmd = new CmdInitUser(name);

  } else if (match_name(CmdAddLiquid)) {
    parse_user();
    parse_str(name);
    cmd = new CmdAddLiquid(user, name);

  } else if (match_name(CmdMakeRecipe)) {
    parse_str(name);
    cmd = new CmdMakeRecipe(name);

  } else if (match_name(CmdDefineRecipe)) {
    parse_user();
    parse_str(name);
    // TODO missing args
    cmd = new CmdDefineRecipe(user, name);

  } else if (match_name(CmdEditRecipe)) {
    parse_user();
    parse_str(name);
    // TODO missing args

    cmd = new CmdEditRecipe(user, name);

  } else if (match_name(CmdDeleteRecipe)) {
    parse_user();
    parse_str(name);
    cmd = new CmdDeleteRecipe(user, name);

  } else if (match_name(CmdDefinePump)) {
    parse_user();
    parse_str(liquid);
    parse_float(volume);
    cmd = new CmdDefinePump(user, liquid, volume);

  } else if (match_name(CmdReset)) {
    parse_user();
    cmd = new CmdReset(user);

  } else if (match_name(CmdRestart)) {
    parse_user();
    parse_opt(factory_reset);
    cmd = new CmdRestart(user, factory_reset);

  } else if (match_name(CmdClean)) {
    parse_user();
    cmd = new CmdClean(user);

  } else if (match_name(CmdCalibratePumps)) {
    parse_user();
    cmd = new CmdCalibratePumps(user);

  } else {
    return Parsed{NULL, unknown_command};
  }

  return Parsed{cmd, valid};
}

// command logic
Processed CmdTest::execute() { return RET(ok); };

Processed CmdRestart::execute() {
  if (is_admin(this->user)) {
    if (this->factory_reset) {
      // FIXME reset settings once the EEPROM is used
    }
    ESP.restart();
  }
  return RET(unauthorized);
}

// TODO implement logic once we have the hardware
Processed CmdCalibratePumps::execute() { return reset_machine(); };
Processed CmdClean::execute() { return reset_machine(); };
Processed CmdReset::execute() { return reset_machine(); };

// FIXME
Processed CmdInitUser::execute() { return RET(unsupported); };
Processed CmdMakeRecipe::execute() { return RET(unsupported); };
Processed CmdAddLiquid::execute() { return RET(unsupported); };
Processed CmdDefinePump::execute() { return RET(unsupported); };
Processed CmdDefineRecipe::execute() { return RET(unsupported); };
Processed CmdEditRecipe::execute() { return RET(unsupported); };
Processed CmdDeleteRecipe::execute() { return RET(unsupported); };

bool is_admin(User user) {
  // TODO use roles etc
  return (user == 0);
}

Processed reset_machine(void) {
  cocktail_queue.clear();
  cocktail.clear();

  // update status
  all_status[ID_COCKTAIL]->update("[]");

  return RET(ok);
}

// bluetooth
bool ble_start(void) {
  debug("starting ble");
  BLEDevice::init(BLE_NAME);
  ble_server = BLEDevice::createServer();

  // setup callback
  ble_server->setCallbacks(new ServerCB());

  // init services
  all_status[ID_BASE]    	= new Status(UUID_STATUS_BASE,    	BLE_NAME);
  all_status[ID_STATE]   	= new Status(UUID_STATUS_STATE,   	"init");
  all_status[ID_LIQUIDS] 	= new Status(UUID_STATUS_LIQUIDS, 	"{}");
  all_status[ID_RECIPES] 	= new Status(UUID_STATUS_RECIPES, 	"{}");
  all_status[ID_COCKTAIL]	= new Status(UUID_STATUS_COCKTAIL,	"[]");

  all_comm[ID_USER] 	= new Comm(UUID_COMM_USER);
  all_comm[ID_ADMIN]	= new Comm(UUID_COMM_ADMIN);

  // start and advertise services
  BLEAdvertising *adv = ble_server->getAdvertising();
  const char *services[] = {UUID_STATUS, UUID_COMM};

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

  debugf("ble address: %s", BLEDevice::getAddress().toString().c_str());
  debug("ble ready");

  return true;
}

Service::Service(const char *uuid_service, const char *uuid_char, const uint32_t props) {
  debugf("creating service: %s / %s", uuid_service, uuid_char);

  BLEService* service = ble_server->getServiceByUUID(uuid_service);
  if (service == NULL) {
    service = ble_server->createService(uuid_service);
  }
  this->ble_char = service->createCharacteristic(uuid_char, props);

  // add descriptor for notifications
  this->ble_char->addDescriptor(new BLE2902());
}

Status::Status(const char *uuid_char, const char *init_value)
  : Service(UUID_STATUS, uuid_char, PROP_READ) {
  this->ble_char->setValue(init_value);
}

void Status::update(const char *value) {
  this->ble_char->setValue(value);
  this->ble_char->notify();
}

Comm::Comm(const char *uuid_char)
  : Service(UUID_COMM, uuid_char, PROP_READ|PROP_WRITE) {
  this->ble_char->setCallbacks(new CommCB(this));
  this->responses = {};
}

void Comm::respond(const uint16_t id, const char *value) {
  debugf("sending response to %d: %s", id, value);
  this->responses[id] = value;
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
    all_comm[i]->responses[id] = "ready";
  }
  debugf("  %s -> %d", remote_addr.toString().c_str(), id);
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
  debugf("  %s -> %d", remote_addr.toString().c_str(), id);
}

CommCB::CommCB(Comm *comm) {
  this->comm = comm;
}

void CommCB::onRead(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->read.conn_id;

  // we need to set the value for each active connection to multiplex properly,
  // or default to a dummy value
  const char *v;
  if (this->comm->responses.count(id)) {
    v = this->comm->responses[id];
  } else {
    v = "";
  }

  debugf("read: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), v);
  ble_char->setValue(v);
}

void CommCB::onWrite(BLECharacteristic *ble_char, esp_ble_gatts_cb_param_t *param) {
  uint16_t id = param->write.conn_id;

  if (this->comm->responses.count(id)) {
    const string v = ble_char->getValue();

    debugf("write: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), v.c_str());

    parse_error err = add_to_queue(v, this->comm, id);
    const char *ret = parse_error_str[id];

    debugf("parsed: %s", ret);

    if (err == valid) {
      // response happens after processing, but set it to a temporary "processing"
      this->comm->responses[id] = ret;
    } else {
      this->comm->respond(id, ret);
    }
  }
}

// sd card
#if defined(SD_CARD)
bool sdcard_start(void) {
  if (!SD.begin(PIN_SDCARD_CS)){
    error("failed to mount sdcard; maybe power is cut?");
    return false;
  }
  debug("sdcard init");

  uint8_t ct = SD.cardType();
  if(ct == CARD_NONE){
    error("no card found");
    return false;
  }

  return true;
}

void sdcard_stop(void) {
  SD.end();
}

bool sdcard_save(void) {
  debug("saving state to sd card");
  File file = SD.open("/config.txt", FILE_WRITE);
  if (!file) {
    debug("failed to open config file");
    return false;
  }

  file.print("[TODO]\n");
  file.close();

  uint64_t mb  	= 1024 * 1024;
  uint64_t used	= SD.usedBytes();
  uint64_t size	= SD.totalBytes();
  debugf("sd card: %lluMB / %lluMB used", used/mb, size/mb);

  return true;
}
#endif

// utilities

// current time since startup
int64_t timestamp_ms() {
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return tv.tv_sec * 1000LL + (tv.tv_usec / 1000LL);
}

int64_t timestamp_usec() {
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return tv.tv_sec * 1000LL * 1000LL + tv.tv_usec;
}

// three different sleep modes
void sleep_idle(uint64_t duration) {
  delay(duration / 1000LL);
}

void sleep_light(uint64_t duration) {
  esp_sleep_enable_timer_wakeup(duration);
  esp_light_sleep_start();
}

void sleep_deep(uint64_t duration) {
  esp_sleep_enable_timer_wakeup(duration);
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

void blink_leds(uint64_t on, uint64_t off) {
  led_on(); 	sleep_idle(on);
  led_off();	sleep_idle(off);
}
#endif

void error_loop(void) {
  while(1) {
#if defined(LED_BUILTIN)
    blink_leds(MS(100), MS(100));
#else
    log("[HALT]", "stop");
    sleep_idle(MS(100));
#endif
  }
}
