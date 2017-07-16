#define inPin1 A0
#define inPin2 A1
#define inPin3 A2
#define inPin4 A3
#define InCput A4
#define receivePin1 4
#define receivePin2 5
#define receivePin3 6
#define receivePin4 7
#define outPin1 8
#define outPin2 9
#define outPin3 10
#define outPin4 11
#define apporswitchsend 12
#define apporswitchreceive 13

long state1 = HIGH;
long state2 = HIGH;
long state3 = HIGH;
long state4 = HIGH;
long finalstate1 = HIGH;
long finalstate2 = HIGH;
long finalstate3 = HIGH;
long finalstate4 = HIGH;
long relaystate1 = HIGH;
long relaystate2 = HIGH;
long relaystate3 = HIGH;
long relaystate4 = HIGH;
long fromappstate1 = HIGH;
long fromappstate2 = HIGH;
long fromappstate3 = HIGH;
long fromappstate4 = HIGH;
long switchused = LOW;

long reading1;
long reading2;
long reading3;
long reading4;
long previous1 = LOW;
long previous2 = LOW;
long previous3 = LOW;
long previous4 = LOW;
long switchchangetime;
long time1 = 0;
long time2 = 0;
long time3 = 0;
long time4 = 0;
long debounce = 200;

void changeState(long &reading, long &state, long &previous, long &time) {
  if (reading == HIGH && previous == LOW && millis() - time > debounce) {
    if (state == HIGH)
      state = LOW;
    else
      state = HIGH;
    time = millis();
  }
  previous = reading;
}


void setup() {
  Serial.begin(9600);
  pinMode(inPin1, INPUT);
  pinMode(inPin2, INPUT);
  pinMode(inPin3, INPUT);
  pinMode(inPin4, INPUT);
  pinMode(InCput, INPUT);
  
  pinMode(outPin1, OUTPUT);
  pinMode(outPin2, OUTPUT);
  pinMode(outPin3, OUTPUT);
  pinMode(outPin4, OUTPUT);
  pinMode(receivePin1, INPUT);
  pinMode(receivePin2, INPUT);
  pinMode(receivePin3, INPUT);
  pinMode(receivePin4, INPUT);
  pinMode(apporswitchreceive, INPUT);
  pinMode(apporswitchsend, OUTPUT);
  digitalWrite(apporswitchsend, LOW);
  digitalWrite(outPin1, HIGH);
  digitalWrite(outPin2, HIGH);
  digitalWrite(outPin3, HIGH);
  digitalWrite(outPin4, HIGH);
  delay(30000);    
}


void loop()
{
  reading1 = digitalRead(inPin1);
  changeState(reading1, state1, previous1, time1);
  reading2 = digitalRead(inPin2);
  changeState(reading2, state2, previous2, time2);
  reading3 = digitalRead(inPin3);
  changeState(reading3, state3, previous3, time3);
  reading4 = digitalRead(inPin4);
  changeState(reading4, state4, previous4, time4);
  Serial.println(digitalRead(apporswitchreceive));
  if (!(relaystate1 == state1 && relaystate2 == state2 && relaystate3 == state3 && relaystate4 == state4)) { //switch
    Serial.println("switch");
    if (relaystate1 != state1) {
      digitalWrite(outPin1, state1);
      relaystate1 = state1;
      finalstate1 = state1;
    }
    if (relaystate2 != state2) {
      digitalWrite(outPin2, state2);
      relaystate2 = state2;
      finalstate2 = state2;
    }
    if (relaystate3 != state3) {
      digitalWrite(outPin3, state3);
      relaystate3 = state3;
      finalstate3 = state3;
    }
    if (relaystate4 != state4) {
      digitalWrite(outPin4, state4);
      relaystate4 = state4;
      finalstate4 = state4;
    }
    switchused = HIGH ;
    digitalWrite(apporswitchsend, HIGH);
    switchchangetime = millis();
  }

  if ((digitalRead(apporswitchreceive) == 1 && digitalRead(InCput) == 0 && millis()-switchchangetime>20000) || switchused==LOW) 
  { //app
    Serial.println("app");
    fromappstate1 = digitalRead(receivePin1);
    fromappstate2 = digitalRead(receivePin2);
    fromappstate3 = digitalRead(receivePin3);
    fromappstate4 = digitalRead(receivePin4);
    finalstate1 = fromappstate1;
    finalstate2 = fromappstate2;
    finalstate3 = fromappstate3;
    finalstate4 = fromappstate4;
    digitalWrite(outPin1, fromappstate1);
    digitalWrite(outPin2, fromappstate2);
    digitalWrite(outPin3, fromappstate3);
    digitalWrite(outPin4, fromappstate4);
    digitalWrite(apporswitchsend, LOW);

  }
  if (digitalRead(InCput) == 1)
  {
    Serial.println("switchputdone");
    digitalWrite(apporswitchsend, LOW);
  }
}
