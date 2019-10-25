package aceves.jesus.simulador_basurero;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * SimuladorBasurero.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Este programa genera lecturas simuladas de un sensor y los manda como mensajes a un servidor MQTT.
 */
public class SimuladorBasurero {
	public static void main(String[] args) {
		MqttClient client;
		try {
			client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
			client.connect();

			Scanner sc = new Scanner(System.in);
			
			System.out.println("Ingrese el ID del basurero a simular:");
			int idSensor = sc.nextInt();
			
			System.out.println("Ingrese la carga de la batería inicial:");
			int bateria = sc.nextInt();
			
			while (bateria > 0) {
				MqttMessage message = new MqttMessage();
				double min = 35;
				double max = 40;
				double altura = min + Math.random() * (max - min);
				DecimalFormat df = new DecimalFormat("#.00");
			    String alturaForm = df.format(altura);
			    Instant i = Instant.now();
			    String mensaje = idSensor + "," + i + "," +  bateria + "," + alturaForm;
				message.setPayload(mensaje.getBytes());

				client.publish("lecturas_basureros", message);
				
				System.out.println("Mensaje enviado");
				System.out.println(mensaje);
				bateria = bateria - 1;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			client.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}