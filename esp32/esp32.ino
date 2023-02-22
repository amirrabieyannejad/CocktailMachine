// supported features
// #define SD_CARD

// pinout
#if defined(SD_CARD)
#define PIN_SDCARD_CS A5
#endif

// general functionality
#include <Arduino.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <SPI.h>
#include <sys/time.h>

// sd card
#if defined(SD_CARD)
#include "FS.h"
#include "SD.h"
#endif

// bluetooth
#include <BLEDevice.h>
#include <BLEServer.h>

#define BLE_NAME "Cocktail Machine ESP32"

#define PROP_READ  (BLECharacteristic::PROPERTY_READ  | BLECharacteristic::PROPERTY_NOTIFY)
#define PROP_WRITE (BLECharacteristic::PROPERTY_WRITE)

class Status {
  BLEServer *server;
  BLEService *ble_service;
  BLECharacteristic *ble_char;
  const char *uuid_service;
  const char *uuid_char;

public:
  Status(BLEServer *server, const char *uuid_service, const char *uuid_char, const char *init_value);
  void advertise();
  void update(const char *value);
};

class Comm {
  BLEServer *server;
  BLEService *ble_service;
  BLECharacteristic *ble_message;
  BLECharacteristic *ble_response;
  const char *uuid_service;
  const char *uuid_message;
  const char *uuid_response;

public:
  Comm(BLEServer *server, const char *uuid_service, const char *uuid_message, const char *uuid_response);
  void advertise();
  void respond(const char *value);
};

class BLECallback : public BLEServerCallbacks {
  void onConnect(BLEServer *server);
  void onDisconnect(BLEServer *server);
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

Status* all_status[NUM_STATUS];
Comm*   all_comm[NUM_COMM];

// UUIDs

#define UUID_BASE         	"0f7742d4-ea2d-43c1-9b98-bb4186be905d"
#define UUID_BASE_CHAR    	"c0605c38-3f94-33f6-ace6-7a5504544a80"
                          	
#define UUID_USER         	"dad995d1-f228-38ec-8b0f-593953973406"
#define UUID_USER_MSG     	"eb61e31a-f00b-335f-ad14-d654aac8353d"
#define UUID_USER_RES     	"06dc28ef-79a4-3245-85ce-a6921e35529d"
                          	
#define UUID_ADMIN        	"f94dd35e-4100-3ba7-bd2f-abc9659c82b1"
#define UUID_ADMIN_MSG    	"41044979-6a5d-36be-b9f1-d4d49e3f5b73"
#define UUID_ADMIN_RES    	"86608e6a-388a-3219-8f71-87270e2ad395"
                          	
#define UUID_LIQUIDS      	"17eed42a-f06b-3f58-9b26-60e78bccf857"
#define UUID_LIQUIDS_CHAR 	"fc60afb0-2b00-3af2-877a-69ae6815ca2f"
                          	
#define UUID_STATE        	"addf5391-2030-3cf0-a64f-31d5156d7f00"
#define UUID_STATE_CHAR   	"e9e4b3f2-fd3f-3b76-8688-088a0671843a"
                          	
#define UUID_RECIPES      	"8f0aec28-5985-335e-baa2-8e03ce08b513"
#define UUID_RECIPES_CHAR 	"9ede6e03-f89b-3e52-bb15-5c6c72605f6c"
                          	
#define UUID_COCKTAIL     	"8a421a72-b9a0-342d-ab57-afa3d67149d1"
#define UUID_COCKTAIL_CHAR	"7344136f-c552-3efc-b04f-a43793f16d43"

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
  uint32_t user;
  const char *name;
  CmdAddLiquid(uint32_t user, const char *name) {
    this->user = user;
    this->name = name;
  }
  retcode execute();
};

class CmdDefinePump : public Command {
public:
  uint32_t user;
  const char *liquid;
  // TODO
  CmdDefinePump(uint32_t user, const char *liquid) {
    this->user = user;
    this->liquid = liquid;
  }
  retcode execute();
};

class CmdDefineRecipe : public Command {
public:
  uint32_t user;
  const char *name;
  // TODO
  CmdDefineRecipe(uint32_t user, const char *name) {
    this->user = user;
    this->name = name;
  }
  retcode execute();
};

class CmdReset : public Command {
public:
  uint32_t user;
  CmdReset(uint32_t user) {
    this->user = user;
  }
  retcode execute();
};

class CmdClean : public Command {
public:
  uint32_t user;
  CmdClean(uint32_t user) {
    this->user = user;
  }
  retcode execute();
};

class CmdCalibratePumps : public Command {
public:
  uint32_t user;
  CmdCalibratePumps(uint32_t user) {
    this->user = user;
  }
  retcode execute();
};

typedef struct {
  Command *command;
  parse_error err;
} parsed;

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
#define debug(msg) log("[DEBUG]", msg)
#define info(msg)  log("[INFO]",  msg)
#define error(msg) log("[ERROR]", msg)

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

// init

void setup() {
  // setup serial communication
  Serial.begin(115200);
  while (!Serial) { sleep_idle(MS(10)); }
  sleep_idle(S(1)); // make sure we don't miss any output

  info("Cocktail Machine v1 starting up");

  // setup pins
#if defined(LED_BUILTIN)
  pinMode(LED_BUILTIN, OUTPUT);
#endif

  // setup sd card
#if defined(SD_CARD)
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
      Serial.printf("[DEBUG]  DIR : %s\n", file.name());
    } else {
      Serial.printf("[DEBUG]  FILE: %16s  SIZE: %d\n", file.name(), file.size());
    }
    file = root.openNextFile();
  }
#endif

  if (!ble_start()) {
    error("failed to start ble");
    error_loop();
  }

  debug("ready");
}

void loop() {
  // just wait
  process("{\"cmd\": \"test\"}");

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
  debug("processing json:");
  debug(json);

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
  BLEServer *server = BLEDevice::createServer();

  // setup callback
  server->setCallbacks(new BLECallback());

  // init services
  all_status[ID_BASE]    	= new Status(server, UUID_BASE,    	UUID_BASE_CHAR,    	BLE_NAME);
  all_status[ID_STATE]   	= new Status(server, UUID_STATE,   	UUID_STATE_CHAR,   	"init");
  all_status[ID_LIQUIDS] 	= new Status(server, UUID_LIQUIDS, 	UUID_LIQUIDS_CHAR, 	"{}");
  all_status[ID_RECIPES] 	= new Status(server, UUID_RECIPES, 	UUID_RECIPES_CHAR, 	"{}");
  all_status[ID_COCKTAIL]	= new Status(server, UUID_COCKTAIL,	UUID_COCKTAIL_CHAR,	"[]");

  all_comm[ID_USER] 	= new Comm(server, UUID_USER, 	UUID_USER_MSG, 	UUID_USER_RES);
  all_comm[ID_ADMIN]	= new Comm(server, UUID_ADMIN,	UUID_ADMIN_MSG,	UUID_ADMIN_RES);

  // advertise services
  for (int i=0; i<NUM_STATUS; i++) {
    all_status[i]->advertise();
  }
  for (int i=0; i<NUM_COMM; i++) {
    all_comm[i]->advertise();
  }

  // start advertising
  BLEAdvertising *adv = server->getAdvertising();
  adv->setScanResponse(true);
  adv->setMinPreferred(0x06); // functions that help with iPhone connections issue
  adv->setMinPreferred(0x12);
  adv->start();

  debug("ble ready");

  return true;
}

void BLECallback::onConnect(BLEServer *server) {
  debug("connected");
}

void BLECallback::onDisconnect(BLEServer *server) {
  debug("disconnected");
  server->getAdvertising()->start();
}

Status::Status(BLEServer *server, const char *uuid_service, const char *uuid_char, const char *init_value) {
  this->uuid_service	= uuid_service;
  this->uuid_char   	= uuid_char;

  this->server     	= server;
  this->ble_service	= server->createService(uuid_service);
  this->ble_char   	= this->ble_service->createCharacteristic(uuid_char, PROP_READ);

  this->ble_char->setValue(init_value);
  this->ble_service->start();
}

void Status::advertise() {
  BLEAdvertising *adv = this->server->getAdvertising();
  adv->addServiceUUID(this->uuid_service);
}

void Status::update(const char *value) {
  this->ble_char->setValue(value);
}

Comm::Comm(BLEServer *server, const char *uuid_service, const char *uuid_message, const char *uuid_response) {
  this->uuid_service 	= uuid_service;
  this->uuid_message 	= uuid_message;
  this->uuid_response	= uuid_response;

  this->server      	= server;
  this->ble_service 	= server->createService(uuid_service);
  this->ble_message 	= this->ble_service->createCharacteristic(uuid_message,  PROP_WRITE);
  this->ble_response	= this->ble_service->createCharacteristic(uuid_response, PROP_READ);

  this->ble_response->setValue("");
  this->ble_service->start();
}

void Comm::advertise() {
  BLEAdvertising *adv = this->server->getAdvertising();
  adv->addServiceUUID(this->uuid_service);
}

void Comm::respond(const char *value) {
  this->ble_response->setValue(value);
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
  char out[50];
  snprintf(out, sizeof(out), "sd card: %lluMB / %lluMB used", used/mb, size/mb);
  debug(out);

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
