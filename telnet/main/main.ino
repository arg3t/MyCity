#include "Arduino.h"
#include "Esp.h"
#include "ESP8266WiFi.h"
 
const char* ssid = "AirTies_Air5343";
const char* password =  "yigit007";
 
WiFiServer listenServer(31);
WiFiServer broadcastServer(69);

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
        Serial.println(message);
        Serial.print("Connected:");
        Serial.println(sender.connected());
        Serial.print("Available:");
        Serial.println(sender.available());
        if(sender && sender.connected()){
          Serial.println("Bookmark 1");
          sender.println(message);
          sender.stop();
        }
      }
      delay(10);
    }
    
    listener.stop();
 
  }
}