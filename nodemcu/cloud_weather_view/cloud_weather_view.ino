#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <ESP8266HTTPClient.h>
#include <time.h>

#include <FirebaseArduino.h>

#define FIREBASE_HOST "YOUR_FIREBASE_URL"
#define FIREBASE_AUTH "YOUR_FIREBASE_DATABASE_KEY"

#define WIFI_SSID "YOUR_SSID"
#define WIFI_PASSWORD "YOUR_PASSWORD"

#define OPEN_WEATHER_API_KEY "YOUR_APP_ID"
#define OPEN_WEATHER_LAT "YOUR_LATITUDE"
#define OPEN_WEATHER_LON "YOUR_LONGITUDE"
#define OPEN_WEATHER_ENDPOINT "/data/2.5/weather"
#define FINAL_ENDPOINT OPEN_WEATHER_ENDPOINT "?APPID=" OPEN_WEATHER_API_KEY "&lat=" OPEN_WEATHER_LAT "&lon=" OPEN_WEATHER_LON "&units=pt&untis=metric"

int openWeatherState = 0;

const char *   host = "api.openweathermap.org";
const uint16_t port = 443;
const char *   path = FINAL_ENDPOINT;

//LEDS
const int led1 = 15;
const int led2 = 13;
const int led3 = 12;
const int led4 = 14;
const int led5 = 10;

const int ledAlert = 16;
const int ledConnecting = 5;

const int leds[] = {led1, led2, led3, led4, led5};

int numberOfLeds = sizeof(leds) / sizeof(leds[0]);

//States
int state = 0;

const int THUNDERSTORM = 0;
const int RAIN = 1;
const int CLOUDS = 2;
const int FEW_CLOUDS = 3;
const int CLEAR = 4;
const int STATE_ERROR = -1; 

void setup() {
  Serial.begin(9600);
  Serial.println("\n############################");
  Serial.println("Starting Weather Cloud View");
  Serial.println("By Vitu");
  Serial.println("v1.0");
  Serial.println("############################");

  // ##########
  // Setup leds
  // ##########
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  pinMode(led4, OUTPUT);
  pinMode(led5, OUTPUT);
  
  pinMode(ledAlert, OUTPUT);
  pinMode(ledConnecting, OUTPUT);
  
  turnOffLeds();
  turnErrorLedOff();
  turnConnectingLedOff();
 
  // #################
  // Setup Connections
  // #################
  Serial.print("connecting to AP: ");
  Serial.println(WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Serial.println("Connecting to firebase...");
  turnConnectingLedOn();
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.stream("/cloud/state");
  turnConnectingLedOff();
  Serial.println("Finish connection with firebase");

  checkWeatherForecast();

  if(openWeatherState != STATE_ERROR) {
    state = openWeatherState;
  } else {
    turnConnectingLedOn();
    state = Firebase.getInt("cloud/state");
    turnConnectingLedOff();
  }

}

void loop() {
  if(Firebase.getBool("cloud/manual")) {
    state = Firebase.getInt("cloud/state");
  } else {
    state = openWeatherState;
  }
  
  Serial.print("Next state: ");
  Serial.println(state);
  Serial.println("Starting effects");

  switch (state) {
    case 0:
      thunderstormEffect();
      break;
    case 1:
      rainEffect();
      break;
    case 2:
      cloudsEffect();
      delay(2000);
      break;
    case 3:
      fewCloudsEffect();
      delay(2000);
      break;
    case 4:
      clearDayEffect();
      delay(2000);
      break;
  }
}

void turnOffLeds() {
  for(int i =0; i < numberOfLeds; i++) {
    digitalWrite(leds[i], LOW);
  }
}

int getRandomLed() {
  return random(0, numberOfLeds);
}

void turnErrorLedOn() {
  digitalWrite(ledAlert, HIGH);
}

void turnErrorLedOff() {
  digitalWrite(ledAlert, LOW);
}

void turnConnectingLedOn() {
  digitalWrite(ledAlert, HIGH);
}

void turnConnectingLedOff() {
  digitalWrite(ledAlert, LOW);
}


// #########################################
// Leds effects 
// #########################################

void thunderstormEffect() {
  turnOffLeds();
  int nextLed = 0;
  int timeHigh = 200;
  int timeLow = 250;

  nextLed = getRandomLed();

  digitalWrite(leds[nextLed], HIGH);
  delay(timeHigh);

  digitalWrite(leds[nextLed], LOW);
  delay(timeLow);

  digitalWrite(leds[nextLed], HIGH);
  delay(timeHigh);

  digitalWrite(leds[nextLed], LOW);
  delay(1000);
}

void rainEffect() {
  turnOffLeds();
  
  int nextLed = getRandomLed();

  digitalWrite(leds[nextLed], HIGH);
  delay(200);

  digitalWrite(leds[nextLed], LOW);
  delay(2000);
}

void cloudsEffect() {
  turnOffLeds();
  
  digitalWrite(leds[2], HIGH);
  digitalWrite(leds[3], HIGH);
}

void clearDayEffect() {
  for(int i = 0; i < numberOfLeds; i++) {
    digitalWrite(leds[i], HIGH);
  }
}

void fewCloudsEffect() {
  turnOffLeds();
  
  digitalWrite(leds[0], HIGH);
  digitalWrite(leds[2], HIGH);
  digitalWrite(leds[4], HIGH);
}

// #########################################
// Weather Forecast 
// #########################################

void checkWeatherForecast() {
  Serial.println("Connecting to open weather...");
  turnConnectingLedOn();
  
  BearSSL::WiFiClientSecure client;
  
  client.setInsecure();
  
  int openWeatherCode = requestWeather(&client);

  if(openWeatherCode == 800) {
    openWeatherState = CLEAR;
  } else if(openWeatherCode == 801 || (openWeatherCode >= 701 && openWeatherCode <= 781) ) {
    openWeatherState = FEW_CLOUDS;
  } else if(openWeatherCode >= 200 && openWeatherCode <= 232) {
    openWeatherState = THUNDERSTORM;
  } else if( (openWeatherCode >= 500 && openWeatherCode <= 531) || (openWeatherCode >= 300 && openWeatherCode <= 321) ) {
    openWeatherState = RAIN;
  } else if(openWeatherCode >= 802 && openWeatherCode <= 804) {
    openWeatherState = CLOUDS;
  } else {
    openWeatherState = STATE_ERROR;
  }
  turnConnectingLedOff();
  Serial.println("Finishing connection with open weather");
}

int requestWeather(BearSSL::WiFiClientSecure *client) {
  Serial.printf("Trying: %s:443...\n", host);
  client->connect(host, port);
  if (!client->connected()) {
    Serial.printf("*** Can't connect. ***\n-------\n");
    return -1;
  }
  Serial.printf("Connected!\n-------\n");
  client->write("GET ");
  client->write(path);
  client->write(" HTTP/1.0\r\nHost: ");
  client->write(host);
  client->write("\r\nUser-Agent: ESP8266\r\n");
  client->write("\r\n");
  
  if (client->connected()) {
    String response = client->readString();
    int bodyPosition = response.indexOf("\r\n\r\n") + 4;
    response = response.substring(bodyPosition);
    Serial.println("Response: " + response);

    DynamicJsonBuffer jsonBuffer(1024);
    
    JsonObject& responseObject = jsonBuffer.parseObject(response);
    if (!responseObject.success()) {
      Serial.println("Parsing failed!");
      turnErrorLedOn();
      return -1;
    }

    Serial.println("Trying get code");
    int openWeatherCode = responseObject["weather"][0]["id"];
    
    Serial.print("Weather code: ");
    Serial.println(openWeatherCode);
    
    client->stop();

    return openWeatherCode;
  } else {
    turnErrorLedOn();
    
    client->stop();

    return 0;
  }
}

//Not used, but will serve to alarm
void setClock() {
  int timezone = -3;
  
  configTime(timezone * 3600, 0, "pool.ntp.org", "time.nist.gov");

  Serial.print("Waiting for NTP time sync: ");
  time_t now = time(nullptr);
  while (now < 8 * 3600 * 2) {
    delay(500);
    Serial.print(".");
    now = time(nullptr);
  }
  Serial.println("");
  struct tm timeinfo;
  gmtime_r(&now, &timeinfo);
  Serial.print("Current time: ");
  Serial.print(asctime(&timeinfo));
}
