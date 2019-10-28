package aceves.jesus.avanceiot;

import com.sun.mail.imap.protocol.MailboxInfo;

import com.sun.mail.smtp.SMTPTransport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * ManejadorNotificaciones.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Contiene métodos que sirven para verificar si es necesario mandar una o varias notificaciones tras
 * recibir una lectura de un bote de basura.
 */
public class ManejadorNotificaciones {
    
    private final String USERNAME = "basurer0i0t@gmail.com";
    private final String PASSWORD = "4Gf#I1P0VK#X";
    private final String TO = "jesusgace@gmail.com";
    private final String SUBJECT = "Bote de basura: Alerta de estado";

    /**
     * Verifica si se cumple alguna o varias de las condiciones para mandar notificaciones y regresa un arreglo de cadenas
     * con los estados que hayan cambiado.
     * @param lectura Lectura que se quiere verificar.
     * @param basureroLectura Basurero del que proviene la lectura que se quiere verificar.
     * @return Regresa un arreglo de cadenas con los estados que hayan cambiado según las condiciones que se hayan cumplido.
     */
	public ArrayList<String> verificar(Lectura lectura, Basurero basureroLectura) {
		
		double porcentajeLlenado = ((basureroLectura.getAlturaMax() - lectura.getAltura()) * 100) / basureroLectura.getAlturaMax();
		DecimalFormat df = new DecimalFormat("#.00");
		String porcentajeForm = df.format(porcentajeLlenado);
		ArrayList<String> estados = new ArrayList<String>();

		// Cuando esté aprox. "lleno"
		if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("LLENO") && porcentajeLlenado > 90) {
			System.out.println("NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("LLENO");
		}
		// Cuando esté aprox. tres cuartos
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("CASILLENO") && porcentajeLlenado <= 90 && porcentajeLlenado >= 60) {
			System.out.println("NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("CASILLENO");
		}
		// Cuando esté aprox. a la mitad
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("MEDIO") && porcentajeLlenado < 60 && porcentajeLlenado > 40) {
			System.out.println("NOTIFICACIÓN: El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			mandarCorreo("El basurero #" + lectura.getIdBasurero() + " está " + porcentajeForm + "% lleno.");
			estados.add("MEDIO");
		}
		// Cuando esté aprox. un cuarto lleno
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("CASIVACIO") && porcentajeLlenado <= 40 && porcentajeLlenado >= 10) {
			estados.add("CASIVACIO");
		}
		// Cuando esté aprox. vacío
		else if (!basureroLectura.getEstadoLlenado().equalsIgnoreCase("VACIO") && porcentajeLlenado < 10) {
			estados.add("VACIO");
		}
		// Cuando le quedara poca carga a un sensor
		if (!basureroLectura.getEstadoCarga().equalsIgnoreCase("BAJA") && lectura.getCarga() <= 10) {
			System.out.println("NOTIFICACIÓN: Al sensor del basurero #" + lectura.getIdBasurero() + " le queda poca carga.");
			mandarCorreo("Al sensor del basurero #" + lectura.getIdBasurero() + " le queda " + lectura.getCarga() + "% de carga.");
			estados.add("BAJA");
		}
		// Cuando la pila no esté muy baja
		else if (!basureroLectura.getEstadoCarga().equalsIgnoreCase("ALTA") && lectura.getCarga() > 10) {
			estados.add("ALTA");
		}

		return estados;
	}
	
	/** 
	 * Envía un correo electrónico a la dirección definida en el atributo TO de la clase con
	 * el cuerpo siendo la cadena que se le pase como parámetro.
	 * @param cuerpoMensaje Cuerpo que irá en el mensaje que se quiere mandar.
	 * @return Verdadero si se pudo mandar el correo, y falso en caso contrario.
	 */
	public boolean mandarCorreo(String cuerpoMensaje) {
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
}
