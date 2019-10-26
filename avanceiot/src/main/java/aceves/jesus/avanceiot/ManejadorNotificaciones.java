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
import java.util.Date;
import java.util.Properties;
public class ManejadorNotificaciones {
    
    private static final String UserName = "basurer0i0t@gmail.com";
    private static final String PassWord = "4Gf#I1P0VK#X";
    private static final String To = "jesusgace@gmail.com";
    private static final String Subject = "Bote de basura: Alerta de estado";
    
	public ManejadorNotificaciones() {
		
	}

	
	public boolean mandarCorreo(String cuerpoMensaje) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session sesion = Session.getInstance(props, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(UserName, PassWord);
			}
			
		});
		
		try {
		Message mensaje = new MimeMessage(sesion);
		mensaje.setFrom(new InternetAddress(UserName));
		mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To));
		mensaje.setSubject(Subject);
		mensaje.setText(cuerpoMensaje);
		
		Transport.send(mensaje);
		System.out.println("Mensaje enviado extosamente.");
		return true;
		
		}catch(MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}
}
