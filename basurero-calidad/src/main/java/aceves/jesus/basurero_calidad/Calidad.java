package aceves.jesus.basurero_calidad;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import aceves.jesus.basurero_entidades.Basurero;
import aceves.jesus.basurero_entidades.Lectura;
import aceves.jesus.basurero_ma.ManejadorAlmacenaje;
import aceves.jesus.basurero_mn.ManejadorNotificaciones;

/**
 * Calidad.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Se encarga de recibir los mensajes del servidor MQTT mandados por los sensores, verificar si son de calidad, 
 * y en caso de serlos los manda al manejador de almacenaje para su almacenamiento y al de manejador
 * de notificaciones para ver si es necesario mandar notificaciones.
 */
public class Calidad implements MqttCallback{
		
	private static ManejadorAlmacenaje ma = new ManejadorAlmacenaje();
	private static ManejadorNotificaciones mn;
	
	public static void main(String[] args) {
		MqttClient client;
		
		mn = new ManejadorNotificaciones(ma.obtenerUltimasLecturas());
		
		try {
			client = new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId());
			client.setCallback(new Calidad());
			client.connect();
			System.out.println("--- Se estableció la conexión con el broker MQTT ---");
			client.subscribe("basurero-iot");
			
			// Se registran los basureros de prueba si no estaban ya registrados
			// Nota: Las alturas de los basureros deben de registrarse en centimetros y
			// y los volúmenes en centimetros cúbicos.
			if (ma.obtenerBasurero(1) == null) {
				Basurero basurero1 = new Basurero(1, 100, 100);
				ma.insertarBasurero(basurero1);
				Basurero basurero2 = new Basurero(2, 100, 100);
				ma.insertarBasurero(basurero2);
				Basurero basurero3 = new Basurero(3, 100, 100);
				ma.insertarBasurero(basurero3);
				System.out.println("< Se registraron los basureros de prueba >");
			}			
			
			mn.start();
			
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Se ejecuta cuando se pierde la conexión con el servidor MQTT.
	 */
	public void connectionLost(Throwable cause) {
		
	}

	/**
	 * Se ejecuta cuando se recibe un correo del servidor MQTT.
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String[] partesLectura = message.toString().split(",");
		System.out.println(Arrays.toString(partesLectura));
		Lectura lectura = new Lectura(Integer.parseInt(partesLectura[0]), 
				Date.from(Instant.now()), 
				Double.parseDouble(partesLectura[1]));
		System.out.println("---------------LECTURA RECIBIDA---------------");
		if (revisarCalidad(lectura)) {
			System.out.println("CALIDAD: Se pasaron las pruebas de calidad.");
			ma.insertarLectura(lectura);
			Basurero basureroLectura = ma.obtenerBasurero(lectura.getIdBasurero());
			mn.verificar(lectura, basureroLectura);
		}
	}

	/**
	 * Se ejecuta cuando se completa la entrega de un mensaje al servidor MQTT.
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {

		
	}
	
	/**
	 * Revisa si la lectura que se le pasa como parámetro cumple con los lineamientos de calidad establecidos.
	 * @param lectura Lectura que se quiere revisar.
	 * @return Verdadero si cumple con los lineamientos de calidad, falso en caso contrario.
	 */
	private boolean revisarCalidad(Lectura lectura) {
		// Revisar cuando se recibe un mensaje de un bote que no está registrado.
		if (ma.obtenerBasurero(lectura.getIdBasurero()) == null) {
			System.out.println("CALIDAD: Se recibió una lectura de un basurero que no está registrado.");
			return false;
		}
		// Revisar cuando se obtiene una lectura de altura negativa.
		if (lectura.getAltura() < 0) {
			System.out.println("CALIDAD: Se recibió una lectura de altura negativa.");
			return false;
		}
		// Revisar cuando se obtiene una altura que supera la altura máxima registrada del basurero.
		if (lectura.getAltura() > (ma.obtenerBasurero(lectura.getIdBasurero()).getAltura())) {
			System.out.println("CALIDAD: Se recibió una lectura de altura superior a la altura del basurero.");
			return false;
		}
		return true;
	}
	
	
}
