#include <SoftwareSerial.h>
//#include <avr/sleep.h>            //Arduino Sleep Mode
//#include <avr/wdt.h>              //Arduino WatchDog Timer setting : related to sleep mode
SoftwareSerial BTserial(2,3);
const int ledpin =13;
/*======================Sleep mode with WatchDog Timer=========================*/
// watchdog interrupt
//ISR(WDT_vect) 
//  {
//  wdt_disable();  // disable watchdog
//  }
//
//void myWatchdogEnable(const byte interval) 
//  {  
//  MCUSR = 0;                          // reset various flags
//  WDTCSR |= 0b00011000;               // see docs, set WDCE, WDE
//  WDTCSR =  0b01000000 | interval;    // set WDIE, and appropriate delay
//
//  wdt_reset();
//  set_sleep_mode (SLEEP_MODE_PWR_DOWN);  
//  sleep_mode();            // now goes to Sleep and waits for the interrupt
//  } 
  
void setup(){ 
 
 Serial.begin(57600); 
 BTserial.begin(9600); 
 pinMode(ledpin, OUTPUT);
 digitalWrite(ledpin, LOW);  
 
} 
 
void loop(){ 
 String output = "";
 int flag;
 int value;
 value = analogRead(0);
 
 if(value>0)
 {
   flag = 1;
   digitalWrite(ledpin, HIGH);
 }
 else
 {
   flag = 0;
   digitalWrite(ledpin, LOW);  
 }
 output = output+flag+","+value;
 
 BTserial.println(output);
 Serial.println(output);  

 delay(1000);
} 
// sleep bit patterns:
//  1 second:  0b000110
//  2 seconds: 0b000111
//  4 seconds: 0b100000
//  8 seconds: 0b100001        
