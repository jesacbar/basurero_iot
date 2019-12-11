package aceves.jesus.basurero_mn;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import aceves.jesus.basurero_entidades.Basurero;
import aceves.jesus.basurero_entidades.Lectura;
import aceves.jesus.basurero_utilidades.Utilidades;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * ManejadorNotificaciones.java Autores: Karla Castro, María Germán, Jesús
 * Aceves Contiene métodos que sirven para verificar si es necesario mandar una
 * o varias notificaciones tras recibir una lectura de un bote de basura.
 */
public class ManejadorNotificaciones extends Thread implements MqttCallback {

	private final String USERNAME = "basurer0i0t@gmail.com";
	private final String PASSWORD = "4Gf#I1P0VK#X";
	private String TO = "jesusgace@gmail.com"; // Valor por defecto
	private final String SUBJECT = "Bote de basura: Alerta de estado";
	
	private String porcNotificacion = null;
	
	// Si este boolean está en false no se mandaran los correos
	// (usado para pruebas).
	private final Boolean MANDARCORREOS = true;
	
	private MqttClient client;
	
	private HashMap<Basurero, Lectura> ultimasLecturas = new HashMap<Basurero, Lectura>();
	private HashMap<Basurero, String> estadosConexion = new HashMap<Basurero, String>();
	private HashMap<Basurero, String> estadosLlenado = new HashMap<Basurero, String>();
	
	public ManejadorNotificaciones(HashMap<Basurero, Lectura> ultimasLecturas) {
		super();
		
		this.ultimasLecturas = ultimasLecturas;
		
		JSONObject jsonConfiguracion = leerConfiguracion();

		TO = (String)jsonConfiguracion.get("correoDestinatario");
		porcNotificacion = (String)jsonConfiguracion.get("porcentaje");

		for (Basurero basurero : ultimasLecturas.keySet()) {
			
			// Llenado del mapa de estados de conexión de los basureros.
			Date ahora = new Date();
			if (ahora.getTime() - ultimasLecturas.get(basurero).getFechahora().getTime() >= 1 * 60 * 1000) {
				estadosConexion.put(basurero, "DESCONECTADO");
				System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getId() + " se ha desconectado.");
			} else {
				estadosConexion.put(basurero, "CONECTADO");
				System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getId() + " se ha conectado.");
			}
			
			// Llenado del mapa de estados de llenado de los basureros.
			estadosLlenado.put(basurero, Utilidades.calcularEstado(basurero, ultimasLecturas.get(basurero)));

		}
		
		try {
			client = new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId());
			client.setCallback(this);
			client.connect();
			System.out.println("--- Se estableció la conexión con el broker MQTT (Notificaciones) ---");
			client.subscribe("basurero-iot-configuracion");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("SE RECIBIÓ LA SIGUIENTE CONFIGURACIÓN: " + message.toString());
		String[] partesMensaje = message.toString().split(",");
		JSONObject configuracion = leerConfiguracion();
		configuracion.put(partesMensaje[0], partesMensaje[1]);
		
		Files.write(Paths.get("../config.json"), configuracion.toJSONString().getBytes());
		
		if (partesMensaje[0].equalsIgnoreCase("porcentaje")) {
			porcNotificacion = partesMensaje[1];
		} else {
			TO = partesMensaje[1];
		}
	}
	
	/**
	 * Este hilo revisa cada minuto si ha pasado un minuto o más desde que se
	 * recibió la última lectura de cada basurero, y si sí, registra el estado de
	 * llenado y carga del basurero como "DESCONECTADO".
	 */
	public void run() {
		// Cantidad de segundos que se quiere esperar antes de volver a revisar si estan
		// conectados los basureros.
		int SEGUNDOS = 60;

		while (true) {
			Date ahora = new Date();
			// Revisar si ha pasado el tiempo establecido desde que se recibió la última lectura de cada
			// basurero.
			System.out.println("< Revisión de basureros desconectados iniciada >");
			for (Basurero basurero : ultimasLecturas.keySet()) {
				Lectura ultimaLectura = ultimasLecturas.get(basurero);
				if (estadosConexion.get(basurero).equalsIgnoreCase("CONECTADO") && 
						ahora.getTime() - ultimaLectura.getFechahora().getTime() >= 1 * SEGUNDOS * 1000) {
					estadosConexion.put(basurero, "DESCONECTADO");
					System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getId() + " se ha desconectado.");
					notificar("El basurero #" + basurero.getId() + " se ha desconectado.");
				}
			}
			System.out.println("< Revisión de basureros desconectados terminada >");
			try {
				Thread.sleep(SEGUNDOS * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<Basurero, Lectura> getUltimasLecturas() {
		return ultimasLecturas;
	}

	public void setUltimasLecturas(HashMap<Basurero, Lectura> ultimasLecturas) {
		this.ultimasLecturas = ultimasLecturas;
	}

	/**
	 * Verifica si se cumple alguna o varias de las condiciones para mandar
	 * notificaciones y regresa un arreglo de cadenas con los estados que hayan
	 * cambiado.
	 * 
	 * @param lectura         Lectura que se quiere verificar.
	 * @param basureroLectura Basurero del que proviene la lectura que se quiere
	 *                        verificar.
	 * @return Regresa un arreglo de cadenas con los estados que hayan cambiado
	 *         según las condiciones que se hayan cumplido.
	 */
	public void verificar(Lectura lectura, Basurero basureroLectura) {

		double porcentajeLlenado = Utilidades.calcularPorcentajeLlenado(basureroLectura, lectura);
		DecimalFormat df = new DecimalFormat("#0.00");
		String porcentajeForm = df.format(porcentajeLlenado);

		// Si se detectó un cambio de porcentajes de llenado, mandar un mensaje
		// al servidor MQTT.
		if (ultimasLecturas.get(basureroLectura).getAltura() != lectura.getAltura()) {
			mandarNotificacionMqtt("llenado," + basureroLectura.getId() + "," + Utilidades.calcularPorcentajeLlenado(basureroLectura, lectura));
			System.out.println("llenado," + basureroLectura.getId() + ","+ Utilidades.calcularPorcentajeLlenado(basureroLectura, lectura));
		}
		
		// Actualizar ultima lectura de ese bote de basura en el mapa de ultimas lecturas.
		ultimasLecturas.put(basureroLectura, lectura);
		
		if (estadosConexion.get(basureroLectura) == null || estadosConexion.get(basureroLectura).equalsIgnoreCase("DESCONECTADO")) {
			estadosConexion.put(basureroLectura, "CONECTADO");
			System.out.println("notificacion, " + "" + "El basurero #" + basureroLectura.getId() + " se ha conectado.");
			mandarNotificacionMqtt("notificacion,El basurero #" + basureroLectura.getId() + " se ha conectado.");
		}
		
		String estadoActual = estadosLlenado.get(basureroLectura);
		String estadoNuevo = Utilidades.calcularEstado(basureroLectura, lectura);
		
		if (Utilidades.calcularPorcentajeLlenado(basureroLectura, lectura) >= Double.parseDouble(porcNotificacion)) {
			notificar("N. personalizada: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		
		// Cuando se reciba la primer lectura de un basurero
		if (estadoActual == null) {

		}
		// Cuando esté aprox. "lleno"
		else if (!estadoActual.equalsIgnoreCase("LLENO") && estadoNuevo.equals("LLENO")) {
			notificar("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. tres cuartos
		else if (!estadoActual.equalsIgnoreCase("CASILLENO") && estadoNuevo.equals("CASILLENO")) {
			notificar("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. a la mitad
		else if (!estadoActual.equalsIgnoreCase("MEDIO") && estadoNuevo.equals("MEDIO")) {
			notificar("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. un cuarto lleno
		else if (!estadoActual.equalsIgnoreCase("CASIVACIO") && estadoNuevo.equals("CASIVACIO")) {
			
		}
		// Cuando esté aprox. vacío
		else if (!estadoActual.equalsIgnoreCase("VACIO") && estadoNuevo.equals("VACIO")) {
			
		}
		System.out.println("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		estadosLlenado.put(basureroLectura, estadoNuevo);
	}

	/**
	 * Envía un correo electrónico a la dirección definida en el atributo TO de la
	 * clase con el cuerpo siendo la cadena que se le pase como parámetro.
	 * 
	 * @param cuerpoMensaje Cuerpo que irá en el mensaje que se quiere mandar.
	 * @return Verdadero si se pudo mandar el correo, y falso en caso contrario.
	 */
	public boolean mandarCorreo(String cuerpoMensaje) {
		if (MANDARCORREOS) {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session sesion = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME, PASSWORD);
				}

			});

			try {
				Message mensaje = new MimeMessage(sesion);
				mensaje.setFrom(new InternetAddress(USERNAME));
				mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
				mensaje.setSubject(SUBJECT);
				mensaje.setText(cuerpoMensaje);

				Transport.send(mensaje);

				return true;

			} catch (MessagingException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Manda una notificación al servidor MQTT.
	 * @param mensaje Cuerpo de la notificación.
	 * @return Verdadero si se pudo mandar el mensaje, falso en caso contrario.
	 */
	public boolean mandarNotificacionMqtt(String mensaje) {		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String fechahoraForm = dtf.format(now); 

		String mensajeConFecha = fechahoraForm + "," + mensaje;
		
		MqttMessage message = new MqttMessage();
	    message.setPayload(mensajeConFecha.getBytes());

		try {
			client.publish("basurero-iot-notificaciones", message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		System.out.println("---------------NOTIFICACION MANDADA---------------");
		return true;
	}
	
	public void notificar(String mensaje) {
		mandarCorreo(mensaje);
		mandarNotificacionMqtt("notificacion,"+mensaje);
	}
	
	public JSONObject leerConfiguracion() {
		// Ver si existe el archivo de configuración, y si no es así
		// crearlo.
		File temp = new File("../config.json");
		if (!temp.exists()) {
			System.out.println("No se encontró el JSON de configuración");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("correoDestinatario", "correo@email.com");
			jsonObject.put("porcentaje", "100");
			try {
				Files.write(Paths.get("../config.json"), jsonObject.toJSONString().getBytes());
				System.out.println(jsonObject.toJSONString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileReader reader;
		try {
			reader = new FileReader("../config.json");
			JSONParser jsonParser = new JSONParser();
			return (JSONObject) jsonParser.parse(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return null;
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	};
}
