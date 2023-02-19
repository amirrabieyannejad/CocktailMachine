// supported features
// #define SD_CARD

// pinout
#if defined(SD_CARD)
#define PIN_SDCARD_CS A5
#endif

// general functionality
#include <Arduino.h>
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

Status* all_status[5];
Comm*   all_comm[2];

// UUIDs

#define UUID_BASE         	"0f7742d4-ea2d-43c1-9b98-bb4186be905d"
#define UUID_BASE_CHAR    	"c0605c38-3f94-33f6-ace6-7a5504544a80"
                          	
#define UUID_COMM         	"dad995d1-f228-38ec-8b0f-593953973406"
#define UUID_COMM_MSG     	"eb61e31a-f00b-335f-ad14-d654aac8353d"
#define UUID_COMM_RES     	"06dc28ef-79a4-3245-85ce-a6921e35529d"
                          	
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

// function declarations
int64_t timestamp_ms(void);
int64_t timestamp_usec(void);
void sleep_idle(uint64_t duration);
void sleep_light(uint64_t duration);
void sleep_deep(uint64_t duration);

// easier time constants
#define USEC(n) (n)
#define MS(n)   (n * 1000LL)
#define S(n)    (n * 1000LL * 1000LL)
#define MIN(n)  (n * 1000LL * 1000LL * 60LL)
#define HOUR(n) (n * 1000LL * 1000LL * 60LL * 60)

#define log(level, msg)                         \
  do {                                          \
    Serial.print(level " ");                    \
    Serial.print(msg);                          \
    Serial.println();                           \
  } while(0)
#define debug(msg) log("[DEBUG]", msg)
#define info(msg)  log("[INFO]",  msg)
#define error(msg) log("[ERROR]", msg)

#if defined(LED_BUILTIN)
void led_on(void);
void led_off(void);
void blink_leds(uint64_t on, uint64_t off);
#endif

void error_loop(void);

#if defined(SD_CARD)
bool sdcard_start(void);
void sdcard_stop(void);
bool sdcard_save(void);
#endif

bool ble_start(void);
void ble_stop(void);

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
  sleep_idle(MS(100));
}

// bluetooth
bool ble_start(void) {
  debug("starting ble");
  BLEDevice::init(BLE_NAME);
  BLEServer *server = BLEDevice::createServer();

  // setup callback
  server->setCallbacks(new BLECallback());

  // init services
  all_status[0] = new Status(server, UUID_BASE,    	UUID_BASE_CHAR,    	BLE_NAME);
  all_status[1] = new Status(server, UUID_STATE,   	UUID_STATE_CHAR,   	"init");
  all_status[2] = new Status(server, UUID_LIQUIDS, 	UUID_LIQUIDS_CHAR, 	"{}");
  all_status[3] = new Status(server, UUID_RECIPES, 	UUID_RECIPES_CHAR, 	"{}");
  all_status[4] = new Status(server, UUID_COCKTAIL,	UUID_COCKTAIL_CHAR,	"[]");

  all_comm[0] = new Comm(server, UUID_COMM, 	UUID_COMM_MSG, 	UUID_COMM_RES);
  all_comm[1] = new Comm(server, UUID_ADMIN,	UUID_ADMIN_MSG,	UUID_ADMIN_RES);

  // advertise services
  for (int i=0; i<(sizeof(all_status)/sizeof(all_status[0])); i++) {
    all_status[i]->advertise();
  }
  for (int i=0; i<(sizeof(all_comm)/sizeof(all_comm[0])); i++) {
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
