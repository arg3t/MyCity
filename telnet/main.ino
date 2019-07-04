#include "Arduino.h"
#include "Esp.h"
#include "ESP8266WiFi.h"
 
const char* ssid = "AirTies_Air5343";
const char* password =  "yigit007";
const int pins[2][3] = {{5,4,16},{2,14,0}};
WiFiServer listenServer(31);
WiFiServer broadcastServer(69);

int lights[2] = {0,2};

void setup() {
  
  Serial.begin(115200);
  pinMode(12,OUTPUT);
  delay(1000);
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(12,HIGH);
    delay(150);
    Serial.println("Connecting..");
    digitalWrite(12,LOW);
    delay(150);
  }
 
  Serial.print("Connected to WiFi. IP:");
  Serial.println(WiFi.localIP());
  for(int i = 0; i<2;i++){
    for(int j = 0; j<3; j++){
      pinMode(pins[i][j],OUTPUT);
    }
  }
  listenServer.begin();
  broadcastServer.begin();
  digitalWrite(12,HIGH);
  
}
 
void loop() {

  WiFiClient listener = listenServer.available();
  WiFiClient sender = broadcastServer.available();
  String message = "";
  if (listener) {
    while (listener.connected()) {
      if(!sender){
        Serial.println("Waiting sender");
        sender = broadcastServer.available();
      }
      message = "";
      while (listener.available()>0) {
        char c = listener.read();
        message += c;
      }
      if(message != ""){

        lights[0] =message.charAt(0)-'0';
        lights[1] = message.charAt(2)-'0';
        Serial.println(lights[0]);
        Serial.println(lights[1]);
        for(int i = 0; i<2;i++){
          for(int j = 0; j<3; j++){
              if(j == lights[i]){
                digitalWrite(pins[i][j],HIGH);
                continue;
              }
              digitalWrite(pins[i][j],LOW);
          }
        }

        Serial.println(message);
        Serial.print("Connected:");
        Serial.println(sender.connected());
        Serial.print("Available:");
        Serial.println(sender.available());
        if(sender && sender.connected()){
          Serial.println("Bookmark 1");
          sender.println(message);
        }
      }
      delay(10);
    }
    sender.stop();https://www.hackthebox.eu/login
    listener.stop();
 
  }
}