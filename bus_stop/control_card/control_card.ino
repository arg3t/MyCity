int pins[] = {13,8,9,10,11,12};
int light_pin = 0;

void setup() {

  for(int i = 0; i<6; i++){
    pinMode(pins[i],OUTPUT);
  }

}

void loop() {
  while(Serial.read() != '/'){
    continue;
  }
  char prev = '!';
  char cur = '!';
  String data = "";
  while(Serial.read() != '/'){
    if(cur == '!' || prev != cur){
      prev = cur;
      data += cur;
    }
    cur = Serial.read()
  }

  for(int i = 0; i<2;i++){
    switch(data[i]){
      
      case "g":
        digitalWrite(pins[i*3],HIGH);
        digitalWrite(pins[(i*2)+1],LOW);
        digitalWrite(pins[(i*2)+2],LOW);
      break;

      case "y":
        digitalWrite(pins[i*3],LOW);
        digitalWrite(pins[(i*2)+1],HIGH);
        digitalWrite(pins[(i*2)+1],LOW);
      break;

      case "r":
        digitalWrite(pins[i*3],LOW);
        digitalWrite(pins[(i*2)+1],LOW);
        digitalWrite(pins[(i*2)+1],HIGH);
      break;

      
    }
  }
  


}
