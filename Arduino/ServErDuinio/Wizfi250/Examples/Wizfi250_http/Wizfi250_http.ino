/*
//This demo use Arduino Leonardo or Seeeduino Lite. The jumper connect D0-WIFI_TX, D1_WIFI_RX; Let the boot pin not connect
*/
#include <Arduino.h>
#include <SoftwareSerial.h>
#include "WizFi250.h"

#define SSID      "STEST"   //Set your SSID
#define KEY       "87654321" //Set your phrase
#define AUTH       "WPA2" //Set the encrypt type

#define TSN_HOST_IP        "74.125.128.103" //google.com server
//#define TSN_HOST_IP        "115.239.210.26" //baidu.com server
//#define TSN_HOST_IP      "192.168.1.254"      // broadcast
#define TSN_REMOTE_PORT    80
#define LOCAL_PORT     9000

#define spi_CS  8

//SoftwareSerial sprintSerial(4,5);   // The software serial port is not stable.
WizFi250 wizfi250(&Serial1);
void setup() {
 
Serial.begin(115200);
Serial1.begin(115200);
while (!Serial);
pinMode(spi_CS,OUTPUT);
digitalWrite(spi_CS,HIGH);
Serial.println("--------- WIZFI250 TEST --------");
// wait for initilization of Wizfi250
  delay(1000);
  Serial.println("Join " SSID );
  delay(10);
  if (!wizfi250.join(SSID, KEY, AUTH)) {
    Serial.println("Failed join " SSID); 
  } else {
    Serial.println("Successfully join  "  SSID);
 
  wizfi250.clear();
  
  wizfi250.connect(TSN_HOST_IP,TSN_REMOTE_PORT,LOCAL_PORT);
  delay(10);
  wizfi250.send("GET / HTTP/1.1\r\n\r\n");
}
   char c; 
   for(int i=0;i<320;i++){
   if (wizfi250.receive((uint8_t *)&c, 1, 100) > 0) {
    Serial.print((char)c);
   }
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