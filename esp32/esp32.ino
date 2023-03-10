// supported features
// #define SD_CARD
#define SIMULATE

// pinout
#if defined(SD_CARD)
#define PIN_SDCARD_CS A5
#endif

// general functionality
#include <Arduino.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <SPI.h>
#include <forward_list>
#include <sys/time.h>
#include <unordered_map>

using namespace std;

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

class Service {
public:
  BLECharacteristic *ble_char;

  Service(const char *uuid_service, const char *uuid_char, const uint32_t props);
};

class Status : public Service {
public:
  Status(const char *uuid_char, const char *init_value);
  void update(const char *value);
};

class Comm : public Service {
public:
  unordered_map<uint16_t, const char *> responses;
  Comm(const char *uuid_char);
};

class ServerCB : public BLEServerCallbacks {
  void onConnect(BLEServer *server);
  void onConnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
  void onDisconnect(BLEServer *server);
  void onDisconnect(BLEServer *server, esp_ble_gatts_cb_param_t *param);
};

class CommCB: public BLECharacteristicCallbacks {
public:
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
  not_found,
} retcode;

typedef enum {
  valid,
  invalid,
  too_big,
  incomplete,
  unknown_command,
} parse_error;

// commands
typedef uint32_t User;

class Command {
public:
  virtual retcode execute();
};

class CmdTest : public Command {
  retcode execute();
};

class CmdInitUser : public Command {
public:
  const char *name;
  CmdInitUser(const char *name) {
    this->name = name;
  }
  retcode execute();
};

class CmdMakeRecipe : public Command {
public:
  const char *name;
  CmdMakeRecipe(const char *name) {
    this->name = name;
  }
  retcode execute();
};

class CmdAddLiquid : public Command {
public:
  User user;
  const char *name;
  CmdAddLiquid(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
  retcode execute();
};

class CmdDefinePump : public Command {
public:
  User user;
  const char *liquid;
  // TODO
  CmdDefinePump(User user, const char *liquid) {
    this->user = user;
    this->liquid = liquid;
  }
  retcode execute();
};

class CmdDefineRecipe : public Command {
public:
  User user;
  const char *name;
  // TODO
  CmdDefineRecipe(User user, const char *name) {
    this->user = user;
    this->name = name;
  }
  retcode execute();
};

class CmdReset : public Command {
public:
  User user;
  CmdReset(User user) {
    this->user = user;
  }
  retcode execute();
};

class CmdClean : public Command {
public:
  User user;
  CmdClean(User user) {
    this->user = user;
  }
  retcode execute();
};

class CmdCalibratePumps : public Command {
public:
  User user;
  CmdCalibratePumps(User user) {
    this->user = user;
  }
  retcode execute();
};

typedef struct {
  Command *command;
  parse_error err;
} parsed;

// machine parts

class Pump {
  const char *liquid;
  float volume;
  int pin;

  Pump(const char *liquid, float volume, int pin);
  retcode drain(float amount);
  retcode refill(float volume);
  retcode empty();
};

class Ingredient {
  const char *name;
  float amount;
};

class Recipe {
  const char *name;
  forward_list<Ingredient> ingredients;

  Recipe(const char *name, forward_list<Ingredient> ingredients);
  float total_volume();
  retcode make(User user);
};

// global state

BLEServer *ble_server;

#define QUEUE_SIZE 100

Status* all_status[NUM_STATUS];
Comm*   all_comm[NUM_COMM];

forward_list<Recipe> recipes;
forward_list<Pump> pumps;
Command* command_queue[QUEUE_SIZE];

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
void process(const char *json);
parsed parse_command(const char *json);
bool is_admin(User user);

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
    for (int i=0; i<QUEUE_SIZE; i++)	command_queue[i] = NULL;
    for (int i=0; i<NUM_STATUS; i++)	all_status[i] = NULL;
    for (int i=0; i<NUM_COMM; i++)  	all_comm[i] = NULL;

    pumps.clear();
    recipes.clear();

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

  sleep_idle(MS(100));
}

// command processing
void process(const char *json) {
  parsed p = parse_command(json);

  if (p.err) {
    // TODO error handling
    return;
  }

  // process command
  if (p.command == NULL) {
    error("command missing even though there wasn't a parse error");
    return;
  }
  retcode ret = p.command->execute();

  // cleanup
  delete p.command;

  // TODO handle return code
}

parsed parse_command(const char *json) {
  debugf("processing json: «%s»", json);

  StaticJsonDocument<1000> doc; // TODO capacity?
  DeserializationError err = deserializeJson(doc, json);

  if (err) {
    switch (err.code()) {
    case DeserializationError::EmptyInput:
    case DeserializationError::IncompleteInput:
    case DeserializationError::InvalidInput:
      return parsed{NULL, invalid};

    case DeserializationError::NoMemory:
    case DeserializationError::TooDeep:
      return parsed{NULL, too_big};

    default:
      return parsed{NULL, invalid};
    }
  }

  // TODO make sure everything is the right type

  // parse json document into command type
  Command *cmd;

  const char *cmd_name = doc["cmd"];
  if (!cmd_name) return parsed{NULL, incomplete};

  if (!strcmp(cmd_name, "test")) {
    cmd = new CmdTest();

  } else if (!strcmp(cmd_name, "init_user")) {
    const char *name = doc["name"];
    if (!name) return parsed{NULL, incomplete};

    cmd = new CmdInitUser(name);

  } else if (!strcmp(cmd_name, "add_liquid")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};
    const char *name = doc["name"];
    if (!name) return parsed{NULL, incomplete};

    cmd = new CmdAddLiquid(user.as<uint32_t>(), name);

  } else if (!strcmp(cmd_name, "make_recipe")) {
    const char *name = doc["name"];
    if (!name) return parsed{NULL, incomplete};

    cmd = new CmdMakeRecipe(name);

  } else if (!strcmp(cmd_name, "define_recipe")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};
    const char *name = doc["name"];
    if (!name) return parsed{NULL, incomplete};

    cmd = new CmdDefineRecipe(user.as<uint32_t>(), name);

  } else if (!strcmp(cmd_name, "define_pump")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};
    const char *liquid = doc["liquid"];
    if (!liquid) return parsed{NULL, incomplete};

    cmd = new CmdDefinePump(user.as<uint32_t>(), liquid);

  } else if (!strcmp(cmd_name, "reset")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};

    cmd = new CmdReset(user.as<uint32_t>());

  } else if (!strcmp(cmd_name, "clean")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};

    cmd = new CmdClean(user.as<uint32_t>());

  } else if (!strcmp(cmd_name, "calibrate_pumps")) {
    JsonVariant user = doc["user"];
    if (user.isNull()) return parsed{NULL, incomplete};

    cmd = new CmdCalibratePumps(user.as<uint32_t>());

  } else {
    return parsed{NULL, unknown_command};
  }

  return parsed{cmd, valid};
}

// command logic
retcode CmdTest::execute() { return ok; };
retcode CmdInitUser::execute() { return ok; };
retcode CmdMakeRecipe::execute() { return ok; };
retcode CmdAddLiquid::execute() { return ok; };
retcode CmdDefinePump::execute() { return ok; };
retcode CmdDefineRecipe::execute() { return ok; };
retcode CmdReset::execute() { return ok; };
retcode CmdClean::execute() { return ok; };
retcode CmdCalibratePumps::execute() { return ok; };

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
  debugf("attempt to write: %d", id);

  if (this->comm->responses.count(id)) {
    const string v = ble_char->getValue();
    // this->comm->responses[id];

    debugf("write: %d (%s) -> %s", id, ble_char->getUUID().toString().c_str(), v.c_str());
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
