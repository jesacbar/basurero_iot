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
import aceves.jesus.basurero_utilidades.Utilidades;

import java.text.DecimalFormat;
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
	private HashMap<Basurero, String> estadosConexion = new HashMap<Basurero, String>();
	private HashMap<Basurero, String> estadosLlenado = new HashMap<Basurero, String>();
	
	
	public ManejadorNotificaciones(HashMap<Basurero, Lectura> ultimasLecturas) {
		super();
		this.ultimasLecturas = ultimasLecturas;
		
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
					mandarCorreo("El basurero #" + basurero.getId() + " se ha desconectado.");
					estadosConexion.put(basurero, "DESCONECTADO");
					System.out.println("NOTIFICACIÓN: El basurero #" + basurero.getId() + " se ha desconectado.");
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

		// Actualizar ultima lectura de ese bote de basura en el mapa de ultimas lecturas.
		ultimasLecturas.put(basureroLectura, lectura);
		
		if (estadosConexion.get(basureroLectura) == null || estadosConexion.get(basureroLectura).equalsIgnoreCase("DESCONECTADO")) {
			estadosConexion.put(basureroLectura, "CONECTADO");
			System.out.println("NOTIFICACIÓN: El basurero #" + basureroLectura.getId() + " se ha conectado.");
		}
		
		String estadoActual = estadosLlenado.get(basureroLectura);
		String estadoNuevo = Utilidades.calcularEstado(basureroLectura, lectura);
		// Cuando se reciba la primer lectura de un basurero
		if (estadoActual == null) {

		}
		// Cuando esté aprox. "lleno"
		else if (!estadoActual.equalsIgnoreCase("LLENO") && estadoNuevo.equals("LLENO")) {
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. tres cuartos
		else if (!estadoActual.equalsIgnoreCase("CASILLENO") && estadoNuevo.equals("CASILLENO")) {
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. a la mitad
		else if (!estadoActual.equalsIgnoreCase("MEDIO") && estadoNuevo.equals("MEDIO")) {
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
		}
		// Cuando esté aprox. un cuarto lleno
		else if (!estadoActual.equalsIgnoreCase("CASIVACIO") && estadoNuevo.equals("CASIVACIO")) {
			
		}
		// Cuando esté aprox. vacío
		else if (!estadoActual.equalsIgnoreCase("VACIO") && estadoNuevo.equals("VACIO")) {
			
		}
		System.out.println("NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
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
}
