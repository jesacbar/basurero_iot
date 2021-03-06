package aceves.jesus.basurero_simulador;

import java.text.DecimalFormat;
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
		Scanner sc = new Scanner(System.in);
		
		try {
			client = new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId());
			client.connect();
			
			while (true) {
				System.out.println("Ingrese el ID del basurero:");
				int idBasurero = sc.nextInt();
				
				System.out.println("Ingrese la altura leída:");
				double altura = sc.nextDouble();
				DecimalFormat df = new DecimalFormat("#.00");
			    String alturaForm = df.format(altura);
			    
			    String mensaje = idBasurero + "," + alturaForm;
			    
			    MqttMessage message = new MqttMessage();
			    message.setPayload(mensaje.getBytes());

				client.publish("basurero-iot", message);
				
				System.out.println("---------------LECTURA MANDADA---------------");
				
			}
			
		} catch (MqttException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
}