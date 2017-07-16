#include <Arduino.h>
#include <SoftwareSerial.h>
#include "WizFi250.h"

#define SSID      "STEST"
#define KEY       "87654321"
#define AUTH       "WPA2" 

#define spi_CS  8

 
WizFi250 wizfi250(&Serial1);

void setup() {
 Serial.begin(115200);
Serial1.begin(115200);
pinMode(spi_CS,OUTPUT);
digitalWrite(spi_CS,HIGH);
Serial.println("--------- WIZFI250 TEST --------");
// wait for initilization of Wizfi250
// wizfi250.reset();
  delay(1000);
  Serial.println("Join " SSID );
  delay(10);
  if (wizfi250.join(SSID, KEY, AUTH)) {
    Serial.println("Successfully join  "  SSID);
  } else {
     Serial.println("Failed join " SSID);
  }
  wizfi250.clear();
 Serial.println("*************send command to get Wizfi250 status******************");
  wizfi250.sendCommand("AT+WSTAT\r");
  //delay(10);
  char c;
  while (wizfi250.receive((uint8_t *)&c, 1, 100) > 0) {
    Serial.print((char)c);
  }

}
void loop() {
  while (wizfi250.available()) {
    Serial.write(wizfi250.read());
  }
  while (Serial.available()) {
    wizfi250.write(Serial.read());
}
}