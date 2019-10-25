package aceves.jesus.avanceiot;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Calidad.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 */
public class Calidad implements MqttCallback{
	ManejadorAlmacenaje ma = new ManejadorAlmacenaje();
	
	public static void main(String[] args) {
		MqttClient client;
		
		try {
			client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
			client.setCallback(new Calidad());
			client.connect();
			System.out.println("Se estableció la conexión con el broker MQTT");
			client.subscribe("lecturas_basureros");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String[] partesLectura = message.toString().split(",");
		Lectura lectura = new Lectura(Integer.parseInt(partesLectura[0]), Date.from(Instant.parse(partesLectura[1])), 
				Integer.parseInt(partesLectura[2]), Double.parseDouble(partesLectura[3]));
		if (revisarCalidad(lectura)) {
			// Aquí poner condiciones para las notificaciones
			ma.insertarLectura(lectura);
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean revisarCalidad(Lectura lectura) {
		// Falta:
		// - Revisar cuando se recibe un mensaje de un bote que no está registrado
		
		if (lectura.getAltura() < 0) {
			return false;
		}
		if (lectura.getAltura() > (ma.obtenerBasurero(lectura.getIdSensor()).getAlturaMax())) {
			return false;
		}
		return true;
	}
}
