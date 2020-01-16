//#include <Firebase.h>
#include <FirebaseArduino.h>
//#include <FirebaseError.h>

  
#include <ESP8266WiFi.h>

#include <ESP8266HTTPClient.h>
#include <time.h>

#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define OLED_RESET LED_BUILTIN  //4
Adafruit_SSD1306 display(OLED_RESET);

#define FIREBASE_HOST "smartwatch.firebaseio.com"                         //database api url
#define FIREBASE_AUTH "5oilss23213434341vbEFbEc6p7k10tgy6eUmh4TJ"           //database secret

int clo=0;

const String watchid="watch1";

const char* ssid = "@k@as";
const char* password = "ghost12345";
int timezone = 7 * 3600;
int dst = 0;
char *med;
int h,m;

// MPU6050 Slave Device Address
const uint8_t MPU6050SlaveAddress = 0x68;

// Select SDA and SCL pins for I2C communication 
const uint8_t scl = D6;
const uint8_t sda = D7;

// sensitivity scale factor respective to full scale setting provided in datasheet 
const uint16_t AccelScaleFactor = 16384.00;
const uint16_t GyroScaleFactor = 131.07;

// MPU6050 few configuration register addresses
const uint8_t MPU6050_REGISTER_SMPLRT_DIV   =  0x19;
const uint8_t MPU6050_REGISTER_USER_CTRL    =  0x6A;
const uint8_t MPU6050_REGISTER_PWR_MGMT_1   =  0x6B;
const uint8_t MPU6050_REGISTER_PWR_MGMT_2   =  0x6C;
const uint8_t MPU6050_REGISTER_CONFIG       =  0x1A;
const uint8_t MPU6050_REGISTER_GYRO_CONFIG  =  0x1B;
const uint8_t MPU6050_REGISTER_ACCEL_CONFIG =  0x1C;
const uint8_t MPU6050_REGISTER_FIFO_EN      =  0x23;
const uint8_t MPU6050_REGISTER_INT_ENABLE   =  0x38;
const uint8_t MPU6050_REGISTER_ACCEL_XOUT_H =  0x3B;
const uint8_t MPU6050_REGISTER_SIGNAL_PATH_RESET  = 0x68;

int16_t AccelX, AccelY, AccelZ, Temperature, GyroX, GyroY, GyroZ;
boolean fall = false; //stores if a fall has occurred
boolean trigger1=false; //stores if first trigger (lower threshold) has occurred
boolean trigger2=false; //stores if second trigger (upper threshold) has occurred
boolean trigger3=false; //stores if third trigger (orientation change) has occurred

byte trigger1count=0; //stores the counts past since trigger 1 was set true
byte trigger2count=0; //stores the counts past since trigger 2 was set true
byte trigger3count=0; //stores the counts past since trigger 3 was set true
int angleChange=0;


int btn=15,vib=14;;
int buttonState=0;


//HEART RATE
int UpperThreshold = 522;
int LowerThreshold = 490;
int reading = 0;
float BPM = 0.0;
bool IgnoreReading = false;
bool FirstPulseDetected = false;
unsigned long FirstPulseTime = 0;
unsigned long SecondPulseTime = 0;
unsigned long PulseInterval = 0;
const unsigned long delayTime = 10;
const unsigned long delayTime2 = 1000;
const unsigned long baudRate = 112500;
unsigned long previousMillis = 0;
unsigned long previousMillis2 = 0;

  String parent,user,parentmsg,parentalarmtopic;
  int parentalarmhour,parentalarmminute,watchface,useralarmhour,useralarmminute;




void I2C_Write(uint8_t deviceAddress, uint8_t regAddress, uint8_t data){
  Wire.beginTransmission(deviceAddress);
  Wire.write(regAddress);
  Wire.write(data);
  Wire.endTransmission();
}

// read all 14 register
void Read_RawValue(uint8_t deviceAddress, uint8_t regAddress){
  Wire.beginTransmission(deviceAddress);
  Wire.write(regAddress);
  Wire.endTransmission();
  Wire.requestFrom(deviceAddress, (uint8_t)14);
  AccelX = (((int16_t)Wire.read()<<8) | Wire.read());
  AccelY = (((int16_t)Wire.read()<<8) | Wire.read());
  AccelZ = (((int16_t)Wire.read()<<8) | Wire.read());
  //Temperature = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroX = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroY = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroZ = (((int16_t)Wire.read()<<8) | Wire.read());
}

//configure MPU6050
void MPU6050_Init(){
  delay(150);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_SMPLRT_DIV, 0x07);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_PWR_MGMT_1, 0x01);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_PWR_MGMT_2, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_CONFIG, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_GYRO_CONFIG, 0x00);//set +/-250 degree/second full scale
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_ACCEL_CONFIG, 0x00);// set +/- 2g full scale
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_FIFO_EN, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_INT_ENABLE, 0x01);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_SIGNAL_PATH_RESET, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_USER_CTRL, 0x00);
}

















void setup() {
  
  Serial.begin(115200);
  
  Wire.begin(sda,scl);
  delay(500);
  MPU6050_Init();
  delay(200);
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  display.clearDisplay();
  display.display();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(0,5);
  display.print("W");
  delay(50);
  display.display();
  display.print("E");
  delay(50);
  display.display();
  display.print("L");
  delay(50);
  display.display();
  display.print("C");
  delay(50);
  display.display();
  display.print("O");
  delay(50);
  display.display();
  display.print("M");
  delay(50);
  display.display();
  display.print("E");
  delay(50);
  display.display();
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(WHITE);
  
  display.setCursor(0,0);
  display.println("Wifi connecting to ");
  display.println( ssid );

  WiFi.begin(ssid,password);
 
  display.println("\nConnecting");

  display.display();
  ///WiFi.mode(WIFI_NONE_SLEEP)
  while( WiFi.status() != WL_CONNECTED ){
      delay(500);
      display.print("."); 
      display.display();       
  }
  WiFi.setSleepMode(WIFI_NONE_SLEEP);
  // Clear the buffer.
  display.clearDisplay();
  display.display();
  display.setCursor(5,20);
  display.println("Wifi Connected!");
  display.print("IP:");
  display.println(WiFi.localIP() );
  display.display();

  
  delay(500);
  display.clearDisplay();
  display.setCursor(15,20);
  configTime(timezone, dst, "pool.ntp.org","time.nist.gov");
  delay(300);
  display.print("Connecting to\n Database"); 
  display.display();
   while(!time(nullptr)){
    delay(500);
      display.print("."); 
      display.display();     
  }

  display.clearDisplay();
  display.setCursor(13,30);
  display.setTextSize(2);
  display.print("Connected"); 
  display.display();
  pinMode(btn,INPUT);
  pinMode(vib,OUTPUT);
  Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
  //Firebase.setBool("lamp", false);
  
  parent=Firebase.getString("CHOOSE_WATCH/watch1/parentUID");
  Serial.println("PARENT IS:****************************: "+parent);
  
  user=Firebase.getString("CHOOSE_WATCH/"+watchid+"/userID");
  Serial.println("USER IS:****************************: "+user);
  
  fall=false;
  Firebase.setString("Parents/"+parent+"/children/"+user+"/Emergency_Status/Fall","false");
  
}











float Raw_AM;
int AM;
int state=0,c=0;
struct tm* p_tm;
double Ax, Ay, Az, T, Gx, Gy, Gz;
int heart=0;

time_t now;

//LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOPPPPPPPPPPPPPPPP
void loop() {

if(c==80)
c=0;
else if(c==10||c==20||c==30||c==40){
watchface=Firebase.getInt("Parents/"+parent+"/children/"+user+"/Watchface");
}
if(c==0)
{
  
   //parent=Firebase.getString("CHOOSE_WATCH/"+watchid+"/parentUID");
   //user=Firebase.getString("CHOOSE_WATCH/"+watchid+"/userID");
   parentalarmtopic=Firebase.getString("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/alarm topic/topic");
   parentalarmhour=Firebase.getInt("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/parentalarmhour");
   parentalarmminute=Firebase.getInt("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/parentalarmminute");
   useralarmhour=Firebase.getInt("Parents/"+parent+"/children/"+user+"/useralarmhour");
   useralarmminute=Firebase.getInt("Parents/"+parent+"/children/"+user+"/useralarmminute");
}

parentmsg=Firebase.getString("Parents/"+parent+"/children/"+user+"/PARENT_SMS/SMS");
//heart=Firebase.getInt("Parents/"+parent+"/children/"+user+"/bpmrequest"+"/value");


  // faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll faaaaaaaaaaaaaaaalllllllllllllllll

   Read_RawValue(MPU6050SlaveAddress, MPU6050_REGISTER_ACCEL_XOUT_H);
  
  //divide each with their sensitivity scale factor
  Ax = (AccelX-2050)/16384.00;
  Ay = (AccelY-77)/16384.00;
  Az = (AccelZ-1947)/16384.00;
  
  Gx = (GyroX+270)/131.07;
  Gy = (GyroY-351)/131.07;
  Gz = (GyroZ+136)/131.07;
  
   Raw_AM = pow((pow(Ax,2)+pow(Ay,2)+pow(Az,2)),0.5);
   AM = Raw_AM * 10;  // as values are within 0 to 1, I multiplied 
                         // it by for using if else conditions 
  Serial.println(AM);
  c++;
  if (trigger3==true){
     trigger3count++;
     //Serial.println(trigger3count);
     if (trigger3count>=5&&(AM==10||AM==11||AM==9)){ 
        angleChange = pow((pow(Gx,2)+pow(Gy,2)+pow(Gz,2)),0.5);
        //delay(10);
        Serial.print("ANGLE CHANGED: ");
        Serial.println(angleChange); 
        if ((angleChange>=0) && (angleChange<=43)){ //if orientation changes remains between 0-10(12) degrees
            fall=true; trigger3=false; trigger3count=0;
            Serial.println(angleChange);
              }
        else{ //user regained normal orientation
           trigger3=false; trigger3count=0;
           Serial.println("TRIGGER 3 DEACTIVATED");
        }
      }
   }
  if (fall==true){
    Serial.println("FALL DETECTED");//in event of a fall detection
    Firebase.setString("Parents/"+parent+"/children/"+user+"/Emergency_Status/Fall","true");
    while(true)
    {
    display.clearDisplay();
    display.setTextSize(2);
   display.setCursor(40,20);
  display.print("FALL \nDETECTED");
   display.display();
   String b=Firebase.getString("Parents/"+parent+"/children/"+user+"/Emergency_Status/Fall");
   buttonState = digitalRead(btn);
      if (buttonState == HIGH||b=="false") {
        break;}
  
    }
    fall=false;
    Firebase.setString("Parents/"+parent+"/children/"+user+"/Emergency_Status/Fall","false");
   // exit(1);
    }
  if (trigger2count>=6)
  { //allow 0.5s for orientation change
    trigger2=false; trigger2count=0;
    Serial.println("TRIGGER 2 DECACTIVATED");
    }
  if (trigger1count>=6)
   { //allow 0.5s for AM to break upper threshold
    trigger1=false; trigger1count=0;
    Serial.println("TRIGGER 1 DECACTIVATED");
    }
  if (trigger2==true){
    trigger2count++;
    trigger3=false;
    angleChange = pow((pow(Gx,2)+pow(Gy,2)+pow(Gz,2)),0.5);
    Serial.print("\nANGLE CHANGE: ");
    Serial.println(angleChange);
    if (angleChange>=6 && angleChange<=100){ //if orientation changes by between 80-100 degrees
      trigger3=true; trigger2=false; trigger2count=0;
      Serial.println(angleChange);
      Serial.println("TRIGGER 3 ACTIVATED");
        }
    }
  if (trigger1==true){
    trigger1count++;
    
    if (AM>3&&AM<=12){ //if AM breaks upper threshold (3g)
      trigger2=true;
      Serial.println("TRIGGER 2 ACTIVATED");
      trigger1=false; trigger1count=0;
      }
    }
    if ((AM<=8||AM>=13)&& trigger2==false){ //if AM breaks lower threshold (0.4g)
    trigger1=true;
    Serial.println("TRIGGER 1 ACTIVATED");
    }
  
  // faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll   faaaaaaaaaaaaaaaalllllllllllllllll faaaaaaaaaaaaaaaalllllllllllllllll

  if(heart==0){
 if(useralarmhour==parentalarmhour&&useralarmminute==parentalarmminute)
  {
    Firebase.setInt("Parents/"+parent+"/children/"+user+"/useralarmhour",200);
    Firebase.setInt("Parents/"+parent+"/children/"+user+"/useralarmminute",200);
  }

  
  if(parentmsg=="")
  {


   now = time(nullptr);             //cllllllllooooooooooooocccckkkkkkkkkkkkkkk set
   tm* p_tm = localtime(&now);
  h=abs(p_tm->tm_hour);
  h-=2;
  m=abs(p_tm->tm_min+30);
  
   if(m>=60)
  {
    int a=m/60;
    h+=a;
    m=m%60;
  }

if(useralarmhour==h&&useralarmminute==m)                                    //USER ALARM ////////////////
{
  int k=0;
  while(true){
     if(k==0){
    digitalWrite(vib,HIGH);
    delay(3000);}
    k++;
    digitalWrite(vib,LOW);
    if(k==30)
    k=0;
  display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(20,20);
  display.print("ALARM \nTRIGGERED");
  display.display();
   buttonState = digitalRead(btn);
      if (buttonState == HIGH) {
        break;
        digitalWrite(vib,LOW);
      } 
  
  }
  Firebase.setInt("Parents/"+parent+"/children/"+user+"/useralarmhour",200);
  Firebase.setInt("Parents/"+parent+"/children/"+user+"/useralarmminute",200);
  useralarmhour=useralarmminute=200;
}

if(parentalarmhour==h&&parentalarmminute==m)                                   //PARENT ALARM ///////////////
{
  int k=0;
  if(parentalarmtopic=="")
  {int k=0;

  while(true){
     buttonState = digitalRead(btn);
      if (buttonState == HIGH) {
        break;
        digitalWrite(vib,LOW);
      } 
     if(k==0){
    digitalWrite(vib,HIGH);
    delay(3000);}
    k++;
    digitalWrite(vib,LOW);
    if(k==30)
    k=0;
    display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(20,20);
  display.print("ALARM \nTRIGGERED ");
  display.display();
  }
  }
  else
  {
     int k=0;
  while(true){
     buttonState = digitalRead(btn);
      if (buttonState == HIGH) {
        break;
        digitalWrite(vib,LOW);
      } 
     if(k==0){
    digitalWrite(vib,HIGH);
    delay(3000);}
    k++;
    digitalWrite(vib,LOW);
    if(k==30)
    k=0;
    display.clearDisplay();
    display.setCursor(0,0);
    display.setTextSize(2);
    display.print(parentalarmtopic);
    display.display();
    }
    
  }
  Firebase.setInt("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/parentalarmhour",200);
  parentalarmhour=parentalarmminute=200;
  Firebase.setInt("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/parentalarmminute",200);
  Firebase.setString("Parents/"+parent+"/children/"+user+"/Parent_AlarmDetails/alarm topic/topic","");
}

  if(watchface==12)                                     //WATCHFACE 12
  {
    int ho;
   display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  if(h==12)
  {
     med="PM";
     ho=h;
  }
  else if(h>12)
  {
    ho=h-12;
    med="PM";
  }
  else
  {
    ho=h;
  med="AM";
  }
  
  display.setCursor(5,0);
  if(ho<10)
  display.print("0");
  display.print(ho);
  display.print(":");
  if( m <10)
   display.print("0"); 
  display.print(m);
  
  display.setTextSize(2);
  display.setCursor(93,5);
  display.print(med);
  display.setTextSize(2);
  display.setCursor(0,38);
  display.print(p_tm->tm_mday);
  display.print("/");
  display.print(p_tm->tm_mon + 1);
  display.print("/");
  display.print(p_tm->tm_year + 1900);
  display.display();
  } 
  else                                           //WATCHFACE  24
  {
    display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
    
   display.setCursor(20,0); 
   if(h<10)
  display.print("0");
  display.print(h);
  display.print(":");
  if( m <10)
    display.print("0"); 
  display.print(m);

  display.setTextSize(2);
  display.setCursor(0,38);
  display.print(p_tm->tm_mday);
  display.print("/");
  display.print(p_tm->tm_mon + 1);
  display.print("/");
  display.print(p_tm->tm_year + 1900);
  display.display();

    }
  }
  
  else
  {
    int k=0;
    while(true)
  {
    if(k==0){
    digitalWrite(vib,HIGH);
    delay(3000);}
    k++;
    if(k==30)
    k=0;
     display.clearDisplay();
    display.setCursor(0,0);
    display.setTextSize(2);
    display.print(parentmsg);
    display.setCursor(10,50);
    digitalWrite(vib,LOW);
    display.setTextSize(1);
    display.print("Press OK to Cancel");
    display.display();
     buttonState = digitalRead(btn);
      if (buttonState == HIGH) {
        break;
        digitalWrite(vib,LOW);
      } 
      //MPU6050_Init();

  }
     Firebase.setString("Parents/"+parent+"/children/"+user+"/PARENT_SMS/SMS","");
   
  }
}
  else   //HHHHEEEEEEEEEEAAAAAAAAAAAARRRRRRRRRRRRRTTTTTTTTTTTTTTTt
  {
    /*
  reading = analogRead(A0); 
  
    Serial.println(reading);
    Firebase.setInt("Parents/"+parent+"/children/"+user+"/bpmrequest"+"/value",reading);
    

time_t now = time(nullptr);             //cllllllllooooooooooooocccckkkkkkkkkkkkkkk set
  struct tm* p_tm = localtime(&now);
  h=abs(p_tm->tm_hour);
  h-=2;
  m=abs(p_tm->tm_min+30);
  
   if(m>=60)
  {
    int a=m/60;
    h+=a;
    m=m%60;
  }

   if(watchface==12)     //WATCHFACE 12
  {
    int ho;
   display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  if(h==12)
  {
     med="PM";
     ho=h;
  }
  else if(h>12)
  {
    ho=h-12;
    med="PM";
  }
  else
  {
    ho=h;
  med="AM";
  }
  
  display.setCursor(5,0);
  if(ho<10)
  display.print("0");
  display.print(ho);
  display.print(":");
  if( m <10)
   display.print("0"); 
  display.print(m);
  
  display.setTextSize(2);
  display.setCursor(93,5);
  display.print(med);
  display.setTextSize(2);
  display.setCursor(9,38);
  display.print(p_tm->tm_mday);
  display.print("/");
  display.print(p_tm->tm_mon + 1);
  display.print("/");
  display.print(p_tm->tm_year + 1900);
  display.display();
  } 
  else      //WATCHFACE  24
  {
    display.clearDisplay();
  display.setTextSize(3);
  display.setTextColor(WHITE);
    
   display.setCursor(20,0); 
   if(h<10)
  display.print("0");
  display.print(h);
  display.print(":");
  if( m <10)
    display.print("0"); 
  display.print(m);

  display.setTextSize(2);
  display.setCursor(9,38);
  display.print(p_tm->tm_mday);
  display.print("/");
  display.print(p_tm->tm_mon + 1);
  display.print("/");
  display.print(p_tm->tm_year + 1900);
  display.display();

    }*/
  }



  
    

}


//LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
