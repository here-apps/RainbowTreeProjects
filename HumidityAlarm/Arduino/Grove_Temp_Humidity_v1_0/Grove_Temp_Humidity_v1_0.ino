#include <Wire.h>
#include "DHT.h"
#include <SoftwareSerial.h>
#include <avr/sleep.h>            //Arduino Sleep Mode
#include <avr/wdt.h>              //Arduino WatchDog Timer setting : related to sleep mode

SoftwareSerial BTSerial(2,3);

const double lowerBound = 30;
const int ledPin=13;
double upperBound = 70;

const double upperWareHouse = 50;
const double upperBook = 55;
const double upperRoom = 60;
const double upperLiving = 65;
const double upperDefault = 70;

int incomingByte = 0;

#define DHTPIN A0     // what pin we're connected to
#define DHTTYPE DHT11   // DHT 11 
DHT dht(DHTPIN, DHTTYPE);

/*======================Sleep mode with WatchDog Timer=========================*/
// watchdog interrupt
ISR(WDT_vect) 
  {
  wdt_disable();  // disable watchdog
  }

void myWatchdogEnable(const byte interval) 
  {  
  MCUSR = 0;                          // reset various flags
  WDTCSR |= 0b00011000;               // see docs, set WDCE, WDE
  WDTCSR =  0b01000000 | interval;    // set WDIE, and appropriate delay

  wdt_reset();
  set_sleep_mode (SLEEP_MODE_PWR_DOWN);  
  sleep_mode();            // now goes to Sleep and waits for the interrupt
  } 
void setup() 
{
    Serial.begin(5700); 
    BTSerial.begin(9600);

    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);   
    dht.begin(); 
}

void loop()
{
    int i=1;
    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
    String outputData="";     
    char ftmp[10];
    double upperBound = upperLiving;        

    digitalWrite(ledPin, LOW);
    
    float h = dht.readHumidity();
    float t = dht.readTemperature();
  
  // check if returns are valid, if they are NaN (not a number) then something went wrong!
    if (isnan(t) || isnan(h)) 
    {
  //        Serial.println("Failed to read from DHT");
    } 
    else 
    {
        dtostrf(h,2,2,ftmp);                        
        outputData = outputData+ftmp+",";          
        dtostrf(t,2,2,ftmp); 
        outputData = outputData+ftmp+",";
               
        if(h>=upperBound)
        {
          digitalWrite(ledPin, HIGH);          
          outputData += "1";
        }else if(h<=lowerBound)
        {
          digitalWrite(ledPin, HIGH);          
          outputData += "2";
        }
        else
        {
          outputData += "0";
        }
    }
    BTSerial.println(outputData);
    
/*---------Sleep mode------------------*/
    for(int i=0;i<1;i++)
    {
      myWatchdogEnable (0b100001);
    }
// sleep bit patterns:
//  1 second:  0b000110
//  2 seconds: 0b000111
//  4 seconds: 0b100000
//  8 seconds: 0b100001        
}
