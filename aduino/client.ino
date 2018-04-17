
#include <ArduinoJson.h>

#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include <SocketIOClient.h>
#include <SerialCommand.h>  


//include thư viện để kiểm tra free RAM trên con esp8266
extern "C" {
  #include "user_interface.h"
}
const byte RX = D1;
const byte TX = D2;

SoftwareSerial mySerial(RX, TX, false, 256); 
SerialCommand sCmd(mySerial); // Khai báo biến sử dụng thư viện Serial Command

SocketIOClient client;
const char* ssid = "amireite";          //Tên mạng Wifi mà Socket server của bạn đang kết nối
const char* password = "passw0rd";  //Pass mạng wifi ahihi, anh em rãnh thì share pass cho mình với.

char host[] = "192.168.43.109";  //Địa chỉ IP dịch vụ, hãy thay đổi nó theo địa chỉ IP Socket server của bạn.
int port = 3484;                  //Cổng dịch vụ socket server do chúng ta tạo!
char namespace_esp8266[] = "esp8266";   //Thêm Arduino!

//từ khóa extern: dùng để #include các biến toàn cục ở một số thư viện khác. Trong thư viện SocketIOClient có hai biến toàn cục
// mà chúng ta cần quan tâm đó là
// RID: Tên hàm (tên sự kiện
// Rfull: Danh sách biến (được đóng gói lại là chuối JSON)
extern String RID;
extern String Rfull;

int N1 = D1;
int N2 = D2;
int LED1 = D3;
int LED2 = D4;



void setup()
{
    pinMode(D1, OUTPUT);
    pinMode(D2, OUTPUT);
    pinMode(D3, OUTPUT);
    pinMode(D4, OUTPUT);
   //Set PWM frequency 500, default is 1000
    //Set range 0~100, default is 0~1023
    analogWriteFreq(500);
    analogWriteRange(100);
    //Bật baudrate ở mức 57600 để giao tiếp với máy tính qua Serial
    Serial.begin(57600);
    mySerial.begin(57600); //Bật software serial để giao tiếp với Arduino, nhớ để baudrate trùng với software serial trên mạch arduino
    delay(10);

    //Việc đầu tiên cần làm là kết nối vào mạng Wifi
    Serial.print("Ket noi vao mang ");
    Serial.println(ssid);

    //Kết nối vào mạng Wifi
    WiFi.begin(ssid, password);

    //Chờ đến khi đã được kết nối
    while (WiFi.status() != WL_CONNECTED) { //Thoát ra khỏi vòng 
        delay(500);
        Serial.print('.');
    }

    Serial.println();
    Serial.println(F("Da ket noi WiFi"));
    Serial.println(F("Di chi IP cua ESP8266 (Socket Client ESP8266): "));
    Serial.println(WiFi.localIP());

    if (!client.connect(host, port, namespace_esp8266)) {
        Serial.println(F("Ket noi den socket server that bai!"));
        return;
    }

    sCmd.addDefaultHandler(defaultCommand); //Lệnh nào đi qua nó cũng bắt hết, rồi chuyển xuống hàm defaultCommand!
    Serial.println("Da san sang nhan lenh");
    
}

void loop()
{

    //Khi bắt được bất kỳ sự kiện nào thì chúng ta có hai tham số:
    //  +RID: Tên sự kiện
    //  +RFull: Danh sách tham số được nén thành chuỗi JSON!
    if (client.monitor()) {
        StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.parseObject(Rfull);
        String tmp(RID);
        if(tmp.equals("FAN")){
          const char* fan = root["POWER"];
          int timer   = root["TIMER"];
          int amount = root["AMOUNT"];     
          Serial.println(fan);
          Serial.println(timer);
          Serial.println(amount);
          String str(fan);
          int value = str.equals("ON") ? amount : 0;
          analogWrite(N1, 0);
          analogWrite(N2, value);
          switch(timer){
            case 1: delay(360000); analogWrite(N2, 0); break;
            case 2: delay(180000); analogWrite(N2, 0); break;
            case 3: delay(60000); analogWrite(N2, 0); break;
            case 4: delay(5000); analogWrite(N2, 0); break;
          }
        }
        if(tmp.equals("LED 1")){
          const char* led = root["POWER"];
          int timer   = root["TIMER"];
          Serial.println(led);
          Serial.println(timer);
          String str(led);
          str.equals("ON") ? digitalWrite(LED1, 100) : digitalWrite(LED1, 0);
          switch(timer){
            case 1: delay(360000); digitalWrite(LED1, 0); break;
            case 2: delay(180000); digitalWrite(LED1, 0); break;
            case 3: delay(60000); digitalWrite(LED1, 0); break;
            case 4: delay(5000); digitalWrite(LED1, 0); break;
          }
        }
         if(tmp.equals("LED 2")){
          const char* led = root["POWER"];
          int timer   = root["TIMER"];
          Serial.println(led);
          Serial.println(timer);
          String str(led);
          str.equals("ON") ? digitalWrite(LED2, 100) : digitalWrite(LED2, 0);
          switch(timer){
            case 1: delay(360000); digitalWrite(LED2, 0); break;
            case 2: delay(180000); digitalWrite(LED2, 0); break;
            case 3: delay(60000); digitalWrite(LED2, 0); break;
            case 4: delay(5000); digitalWrite(LED2, 0); break;
          }
        }
        //Kiểm tra xem còn dư bao nhiêu RAM, để debug
        uint32_t free = system_get_free_heap_size();
        Serial.println(free);
    }

    //Kết nối lại!
    while (!client.connected()) {
      client.reconnect(host, port, namespace_esp8266);
    }

    sCmd.readSerial();
}

void defaultCommand(String command) {
  char *json = sCmd.next();
  client.send(command, (String) json);//gửi dữ liệu về cho Socket Server

  //In ra serial monitor để debug
  Serial.print(command);
  Serial.print(' ');
  Serial.println(json);
}
