package source.mdtn.util;

import java.io.Serializable;
import java.util.regex.*;

/**
 * Classe generica che rappresenta un messaggio email.
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 815263407379141701L;

	/** Stringa rappresentante il mittente */
	private String from;
	
	/** Stringa rappresentante il destinatario */
	private String to;
	
	/** Oggetto del messaggio */
	private String subject;
	
	/** Corpo del messaggio */
	private String message;
	
	/**
	 * Costruttore di default.
	 */
	public Message(){
		from=to=subject=message="";
	}
	
	/**
	 * Costruttore avanzato del messaggio. 
	 * @param from Stringa rappresentante il mittente.
	 * @param to Stringa rappresentante il destinatario.
	 * @param subject Oggetto del messaggio.
	 * @param message Corpo del messaggio.
	 */
	public Message(String from, String to, String subject, String message){
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.message = message;
	}


	/**
	 * Metodo che controlla la validità di un indirizzo email.
	 * @param email l'email da verificare.
	 * @return true=email corretta, false=email non corretta
	 */
	public static boolean checkEmail(String email){
		 //Pattern
	      Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

	      //Match
	      Matcher m = p.matcher(email);

	      //check
	      boolean matchFound = m.matches();

	      if (matchFound)
	        return true;
	      else
	        return false;
	}
	
		
	
	/* Auto Set&Get */
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	
}
