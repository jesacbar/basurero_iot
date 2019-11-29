package aceves.jesus.basurero_mn;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import aceves.jesus.basurero_entidades.Basurero;
import aceves.jesus.basurero_entidades.Lectura;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * ManejadorNotificaciones.java Autores: Karla Castro, María Germán, Jesús
 * Aceves Contiene métodos que sirven para verificar si es necesario mandar una
 * o varias notificaciones tras recibir una lectura de un bote de basura.
 */
public class ManejadorNotificaciones extends Thread {

	private final String USERNAME = "basurer0i0t@gmail.com";
	private final String PASSWORD = "4Gf#I1P0VK#X";
	private final String TO = "jesusgace@gmail.com";
	private final String SUBJECT = "Bote de basura: Alerta de estado";

	// Si este boolean está en false no se mandaran los correos
	// (usado para pruebas).
	private final Boolean MANDARCORREOS = false;
	
	private HashMap<Basurero, Lectura> ultimasLecturas = new HashMap<Basurero, Lectura>();
	private HashMap<Basurero, String> estadosBasureros = new HashMap<Basurero, String>();
	
	
	public ManejadorNotificaciones(HashMap<Basurero, Lectura> ultimasLecturas) {
		super();
		this.ultimasLecturas = ultimasLecturas;
		
		for (Basurero basurero : ultimasLecturas.keySet()) {
			Date ahora = new Date();
			if (ahora.getTime() - ultimasLecturas.get(basurero).getFechahora().getTime() >= 1 * 60 * 1000) {
				estadosBasureros.put(basurero, "DESCONECTADO");
				System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getIdBasurero() + " se ha desconectado.");
			} else {
				estadosBasureros.put(basurero, "CONECTADO");
				System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getIdBasurero() + " se ha conectado.");
			}
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
				if (estadosBasureros.get(basurero).equalsIgnoreCase("CONECTADO") && 
						ahora.getTime() - ultimaLectura.getFechahora().getTime() >= 1 * SEGUNDOS * 1000) {
					mandarCorreo("El basurero #" + basurero.getIdBasurero() + " se ha desconectado.");
					estadosBasureros.put(basurero, "DESCONECTADO");
					System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getIdBasurero() + " se ha desconectado.");
				}
			}
			System.out.println("< Revisión de basureros desconectados terminada >");
			try {
				Thread.sleep(SEGUNDOS * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
	public ArrayList<String> verificar(Lectura lectura, Basurero basureroLectura) {

		double porcentajeLlenado = ((basureroLectura.getAlturaMax() - lectura.getAltura()) * 100)
				/ basureroLectura.getAlturaMax();
		DecimalFormat df = new DecimalFormat("#.00");
		String porcentajeForm = df.format(porcentajeLlenado);
		ArrayList<String> estados = new ArrayList<String>();

		// Actualizar ultima lectura de ese bote de basura en el mapa de ultimas lecturas.
		ultimasLecturas.put(basureroLectura, lectura);
		if (estadosBasureros.get(basureroLectura).equalsIgnoreCase("DESCONECTADO")) {
			estadosBasureros.put(basureroLectura, "CONECTADO");
			System.out.println("NOTIFICACIÓN: El basurero #" + basureroLectura.getIdBasurero() + " se ha conectado.");
		}
		
		// Cuando esté aprox. "lleno"
		if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("LLENO") && porcentajeLlenado > 90) {
			System.out.println(
					"NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("LLENO");
		}
		// Cuando esté aprox. tres cuartos
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("CASILLENO") && porcentajeLlenado <= 90
				&& porcentajeLlenado >= 60) {
			System.out.println(
					"NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("CASILLENO");
		}
		// Cuando esté aprox. a la mitad
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("MEDIO") && porcentajeLlenado < 60
				&& porcentajeLlenado > 40) {
			System.out.println(
					"NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("MEDIO");
		}
		// Cuando esté aprox. un cuarto lleno
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("CASIVACIO") && porcentajeLlenado <= 40
				&& porcentajeLlenado >= 10) {
			estados.add("CASIVACIO");
		}
		// Cuando esté aprox. vacío
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("VACIO") && porcentajeLlenado < 10) {
			estados.add("VACIO");
		}
		// Cuando le quedara poca carga a un sensor
		if (!basureroLectura.getEstadoCarga().equalsIgnoreCase("BAJA") && lectura.getCarga() <= 10) {
			System.out.println(
					"NOTIFICACIÓN: Al sensor del basurero #" + lectura.getIdBasurero() + " le queda poca carga.");
			mandarCorreo("Al sensor del basurero #" + lectura.getIdBasurero() + " le queda " + lectura.getCarga()
					+ "% de carga.");
			estados.add("BAJA");
		}
		// Cuando la pila no esté muy baja
		else if (!basureroLectura.getEstadoCarga().equalsIgnoreCase("ALTA") && lectura.getCarga() > 10) {
			estados.add("ALTA");
		}

		return estados;
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
}
