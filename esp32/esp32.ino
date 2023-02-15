// general functionality
#include <Arduino.h>
#include <EEPROM.h>
#include <SPI.h>
#include <Wire.h>
#include <sys/time.h>

// sd card
#include "FS.h"
#include "SD.h"

// bluetooth
#include <BLEDevice.h>
#include <BLEServer.h>

BLEServer *ble_server;

// pinout
#define PIN_SDCARD_CS A5

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

void led_on(void);
void led_off(void);
void blink_leds(uint64_t on, uint64_t off);

void error_loop(void);

bool sdcard_start(void);
void sdcard_stop(void);
bool sdcard_save(void);

// init

void setup() {
  // setup serial communication
  Wire.begin();
  Serial.begin(115200);
  while (!Serial) { sleep_idle(MS(10)); }
  sleep_idle(S(1)); // make sure we don't miss any output

  info("Cocktail Machine v1 starting up");

  // setup pins
#if defined(LED_BUILTIN)
  pinMode(LED_BUILTIN, OUTPUT);
#endif

  // setup sd card
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

  debug("ready");
}

void loop() {
  // just wait
  sleep_light(MS(100));
}

// sd card
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

#if defined(OUTPUT_DEBUG)
  uint64_t mb  	= 1024 * 1024;
  uint64_t used	= SD.usedBytes();
  uint64_t size	= SD.totalBytes();
  char out[50];
  snprintf(out, sizeof(out), "sd card: %lluMB / %lluMB used", used/mb, size/mb);
  debug(out);
#endif

  return true;
}

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
void led_on(void) {
#if defined(LED_BUILTIN)
  digitalWrite(LED_BUILTIN, HIGH);
#endif
}

void led_off(void) {
#if defined(LED_BUILTIN)
  digitalWrite(LED_BUILTIN, LOW);
#endif
}

void blink_leds(uint64_t on, uint64_t off) {
#if defined(LED_BUILTIN)
  led_on(); 	sleep_idle(on);
  led_off();	sleep_idle(off);
#endif
}

void error_loop(void) {
  while(1) {
    blink_leds(MS(100), MS(100));
  }
}
