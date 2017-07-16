#include <Arduino.h>
#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include "WizFi250.h"


#define SSID      "Redmi"
#define KEY       "sgr12345"
#define AUTH       "WPA2"

#define TSN_HOST_IP        "192.168.43.34"
#define TSN_REMOTE_PORT    9081
#define LOCAL_PORT     10

#define inPin1  4
#define inPin2  5
#define inPin3  6
#define inPin4  7

#define OutCput A4
#define outPin1 8
#define outPin2 9
#define outPin3 10
#define outPin4 11

#define changedStateBySwitchInPin 12 // 0 means app - 1 means manual
#define changedStateByAppOutPin 13 // 1 means app - 0 means manual


long state1 = 1;
long state2 = 1;
long state3 = 1;
long state4 = 1;
long lastChangedBy = 0;
long switchvalueputtime = 0;
bool joined = false;
bool switchused = false;


SoftwareSerial Serial1(2, 3);
WizFi250 wizfi250(&Serial1);

void setup() {
  Serial.begin(115200);
  Serial1.begin(115200);

  pinMode(inPin1, INPUT);
  pinMode(inPin2, INPUT);
  pinMode(inPin3, INPUT);
  pinMode(inPin4, INPUT);
  pinMode(changedStateBySwitchInPin, INPUT);

  pinMode(OutCput, OUTPUT);
  pinMode(outPin1, OUTPUT);
  pinMode(outPin2, OUTPUT);
  pinMode(outPin3, OUTPUT);
  pinMode(outPin4, OUTPUT);
  pinMode(changedStateByAppOutPin, OUTPUT);
  digitalWrite(changedStateByAppOutPin, LOW);
  digitalWrite(OutCput, LOW);

  while (!Serial);
  delay(1000);
  Serial.println("Join " SSID );
  delay(10);

  while (!joined) {
    joined = wizfi250.join(SSID, KEY, AUTH);
    delay(30);
  }
  wizfi250.clear();
  wizfi250.connect(TSN_HOST_IP, TSN_REMOTE_PORT, LOCAL_PORT);


}

void loop() {
  getDataFromServer();
  Serial.println(digitalRead(changedStateBySwitchInPin));
  if (digitalRead(changedStateBySwitchInPin) == 1 && ((millis() - switchvalueputtime) > 5000)) { // switch is changed manually
    digitalWrite(OutCput, 1);
    putDataIntoServer();
    switchvalueputtime = millis();
    digitalWrite(changedStateByAppOutPin, 0);
    digitalWrite(OutCput, 0);
  }
  else if ((lastChangedBy == 0 && digitalRead(changedStateBySwitchInPin) == 0) || switchused == false) { // state is changed from web app; updated on getDataFromServer() call
    digitalWrite(outPin1, state1);
    digitalWrite(outPin2, state2);
    digitalWrite(outPin3, state3);
    digitalWrite(outPin4, state4);
    digitalWrite(changedStateByAppOutPin, 1);
  }
  else {
    //no change from either side
  }
  delay(50);
}


void getDataFromServer() {
  wizfi250.clear();
  delay(100);
  wizfi250.connect(TSN_HOST_IP, TSN_REMOTE_PORT, LOCAL_PORT);
  delay(10);
  wizfi250.send("GET /d/getListOfDevices HTTP/1.1\r\nHost: 192.168.43.34:9081\r\nc:2\r\nh:2001\r\nr:2001\r\n\r\n");
  char c[320];
  char dt[12]="{1,1,1,1,1}";
  for (int i = 0; i < 320; i++) {
    if (wizfi250.receive((uint8_t *)&c[i], 1, 100) > 0) {
      Serial.print(c[i]);
      }
  }
  for (int i = 190 ; i < 320; i++) {
    if (c[i] == '\r' && c[i+1] == '\n' && c[i+2] == '\r' && c[i+3] == '\n' && c[i+4] == '[') {
      Serial.println("dt print start \r \n");
      for (int j = 0 ; j < 12; j++) {
        dt[j] = (char)c[i + j + 4];
        Serial.println(i);
        Serial.print(dt[j]);
      }
      dt[12] = '\0';
      Serial.println("dt print End \r \n");
    }
  }
  delay(100);

  state1 = dt[1] - 48;
  state2 = dt[3] - 48;
  state3 = dt[5] - 48;
  state4 = dt[7] - 48;
  lastChangedBy = dt[9] - 48;
  
  //test purposes only
  Serial.print("state1: ");
  Serial.println(state1);
  Serial.print("state2: ");
  Serial.println(state2);
  Serial.print("state3: ");
  Serial.println(state3);
  Serial.print("state4: ");
  Serial.println(state4);
  Serial.print("changedStateByInPin: ");
  Serial.println(lastChangedBy);
}

void putDataIntoServer() {
  state1 = digitalRead(inPin1);
  state2 = digitalRead(inPin2);
  state3 = digitalRead(inPin3);
  state4 = digitalRead(inPin4);
  digitalWrite(outPin1, state1);
  digitalWrite(outPin2, state2);
  digitalWrite(outPin3, state3);
  digitalWrite(outPin4, state4);


  String putHttpQuery = "";
  putHttpQuery  = "PUT /d/updateDevicesState HTTP/1.1\r\nHost: 192.168.43.34:9081\r\ncontent-type: application/x-www-form-urlencoded\r\ncontent-length: 40\r\n\r\n";
  putHttpQuery += "c=2&u=2001&h=2001&r=2001&";
  putHttpQuery += "1=";
  putHttpQuery += state1;
  putHttpQuery += "&2=";
  putHttpQuery += state2;
  putHttpQuery += "&3=";
  putHttpQuery += state3;
  putHttpQuery += "&4=";
  putHttpQuery += state4;
  Serial.println(putHttpQuery);
  wizfi250.clear();
  delay(100);

  wizfi250.connect(TSN_HOST_IP, TSN_REMOTE_PORT, LOCAL_PORT);
 // wizfi250.send(putHttpQuery.c_str());
  wizfi250.send(putHttpQuery.c_str(), 20000);
  char ch[320];
  for (int i = 0; i < 320; i++) {
    if (wizfi250.receive((uint8_t *)&ch[i], 1, 100) > 0) {
      Serial.print(ch[i]);
      }
  }

  delay(100);
  Serial.println("End of Put");
  switchused = true;
}


