package wp.core;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest {

	public static void main (String [] args) throws Exception {
		sendMail("testing", "This is a test email", "jeffrey_sanchez@hotmail.com", "feedback@webpredict.net");
	}
	
	public static void	sendMail (String subject, String msgText, String from, String to) throws Exception {
		Properties props = System.getProperties();
	      // -- Attaching to default Session, or we could start a new one --
	      props.put("mail.smtp.host", "k2smtpout.secureserver.net");
	      Session session = Session.getDefaultInstance(props, null);
	      // -- Create a new message --
	      Message msg = new MimeMessage(session);
	      // -- Set the FROM and TO fields --
	      if (from != null)
	    	  msg.setFrom(new InternetAddress(from));
	      msg.setRecipients(Message.RecipientType.TO,
	        InternetAddress.parse(to, false));
	      // -- We could include CC recipients too --
	      // if (cc != null) 
	      // msg.setRecipients(Message.RecipientType.CC
	      // ,InternetAddress.parse(cc, false));
	      // -- Set the subject and body text --
	      msg.setSubject(subject);
	      msg.setText(msgText);
	      // -- Set some other header information --
	     // msg.setHeader("X-Mailer", "LOTONtechEmail");
	      msg.setSentDate(new Date());
	      // -- Send the message --
	      Transport.send(msg);
	}
}
