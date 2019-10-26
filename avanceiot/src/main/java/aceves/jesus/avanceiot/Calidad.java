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
	ManejadorNotificaciones mn = new ManejadorNotificaciones();
	
	public static void main(String[] args) {
		MqttClient client;
		
		try {
			client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
			client.setCallback(new Calidad());
			client.connect();
			System.out.println("Se estableció la conexión con el broker MQTT");
			client.subscribe("lecturas_basureros");
			ManejadorNotificaciones mn = new ManejadorNotificaciones();
			
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
			ma.insertarLectura(lectura);
			if (mandarNotificacion(lectura)) {
				System.out.println("Se mandó una notificación.");
			}
			
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean revisarCalidad(Lectura lectura) {
		System.out.println("Se inician las pruebas de calidad.");
		// Revisar cuando se recibe un mensaje de un bote que no está registrado.
		if (ma.obtenerBasurero(lectura.getIdBasurero()).equals(null)) {
			return false;
		}
		System.out.println("Se pasó la prueba 1.");
		// Revisar cuando se obtiene una lectura de altura negativa.
		if (lectura.getAltura() < 0) {
			return false;
		}
		System.out.println("Se pasó la prueba 1.");
		// Revisar cuando se obtiene una altura que supera la altura máxima registrada del basurero.
		if (lectura.getAltura() > (ma.obtenerBasurero(lectura.getIdBasurero()).getAlturaMax())) {
			return false;
		}
		System.out.println("Se pasaron las pruebas de calidad.");
		return true;
	}
	
	private boolean mandarNotificacion(Lectura lectura) {
		double porcentajeLlenado = lectura.getAltura() - lectura.getIdBasurero().getAlturaMax()/2;
		// Cuando esté "lleno"
		// Cuando esté tres cuartos
		// Cuando esté a la mitad
		if (lectura.getAltura() < ma.obtenerBasurero(lectura.getIdBasurero()).getAlturaMax()/2) {
			mn.mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " +  + "% lleno.");
		}
		// Cuando se le fuera a acabar la pila a un sensor
		if (lectura.getCarga() == 10) {
			mn.mandarCorreo("Al sensor del basurero #" + lectura.getIdBasurero() + " le queda poca carga.");
		}
		// Cuando se pierda conexión
		return false;
	}
	
	
	
}
