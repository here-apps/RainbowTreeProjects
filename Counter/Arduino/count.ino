#include <SoftwareSerial.h>

SoftwareSerial BTSerial(2,3);
const int pktsize=128;
const int led = 13;
int i=0;
// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an output.
  pinMode(led, OUTPUT);    
  Serial.begin(57600);
  BTSerial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() {  
  int i=0;
  char msgtmp[pktsize];
  String s="";
  if(Serial.available()>0)
  { 
    digitalWrite(led, HIGH);
    while(Serial.available())
    {  
      msgtmp[i]=Serial.read();
      i++;
    }
    digitalWrite(led, LOW);      
  }
  
  s=s+i+msgtmp;
  Serial.println(s);
  BTSerial.println(s);
}
