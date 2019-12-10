#include <HCSR04.h>
#include <WiFi.h>
#include <PubSubClient.h>

#define PIN_TRIGGER 2
#define PIN_ECHO 5

const char* ssid = "ACEVESS";
const char* password = "54j^Fi%&Azuf";
const char* mqttServer = "test.mosquitto.org";
const int mqttPort = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

int i;
const unsigned int BAUD_RATE = 9600;
UltraSonicDistanceSensor distanceSensor(PIN_TRIGGER, PIN_ECHO);

void setup() {
  Serial.begin(BAUD_RATE);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }

  Serial.println("Connected to the WiFi network");

  client.setServer(mqttServer, mqttPort);

  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");

    if (client.connect("ESP32Client")) {

      Serial.println("connected");

    } else {

      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);

    }
  }

}

void loop() {
  delay(10000);

  // Medición de la distancia
  double distancia = distanceSensor.measureDistanceCm();
  Serial.print("Distancia: ");
  Serial.print(distancia);
  Serial.println(" cm");

  char copia[100];
  char valor[100] = "1,";
  String distanciaString = String(distancia);
  distanciaString.toCharArray(copia, distanciaString.length()+1);
  Serial.print(copia);
  strcat(valor, copia);

  client.publish("basurero-iot", valor);
}
