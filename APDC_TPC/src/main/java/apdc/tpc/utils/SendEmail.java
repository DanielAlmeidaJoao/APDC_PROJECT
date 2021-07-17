package apdc.tpc.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	public SendEmail() {
		// TODO Auto-generated constructor stub
	}
	
    private static void sendEmail(String to, String msg) {
    	  String sub="Pinneapple Inc: VERIFICATION CODE";
    	  String from = "closingthegap.pinneappleinc@gmail.com"; //change accordingly 
    	  String password="jrbxaclvaflxbvxd";
  	      //Get properties object              
  	    Properties props = new Properties();    
  	    String host = "smtp.gmail.com";
  	    props.put("mail.smtp.starttls.enable", "true");
  	    props.put("mail.smtp.ssl.trust", host);
  	    props.put("mail.smtp.user", from);
  	    props.put("mail.smtp.password", password);
  	    props.put("mail.smtp.port", "587");//587
  	    props.put("mail.smtp.auth", "true");    
  	    
  	    //get Session   
  	    Session session = Session.getDefaultInstance(props);
          //compose message    
          try {    
           MimeMessage message = new MimeMessage(session);    
           message.setFrom(new InternetAddress(from));
           message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
           message.setSubject(sub);    
           //message.setText(msg);
           message.setContent(
                   "<div>"+msg+"</div>",
                  "text/html");
           //send message  
           //Transport.send(message);    
           
           Transport transport = session.getTransport("smtp");
           transport.connect(host, from, password);
           transport.sendMessage(message, message.getAllRecipients());
           transport.close();
           System.out.println("ENDEDD");
          } catch (MessagingException e) {
        	  //throw new RuntimeException(e);
          	System.out.println(e.getLocalizedMessage());
          }
      }
   
    public static boolean send(String to,String msg){  
        //Get properties object   
   	Thread thread = new Thread(() ->{
   		sendEmail(to, msg);
   	});
       thread.start();
       return true;
   } 
}
