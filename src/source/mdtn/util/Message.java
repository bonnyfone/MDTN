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
	
	/** Invia notifica in cc*/
	private boolean cc;
	
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
	public Message(String from, String to, String subject, String message, boolean cc){
		this.cc=cc;
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
	
		
	
	/**
	 * Ritorna il mittente del messaggio.
	 * 
	 * @return una stringa contenente il mittente.
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Imposta il mittende del messaggio.
	 * @param from una stringa contenente il mittente.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Ritorna il destinatario del messaggio.
	 * @return una stringa con il destinatario.
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Imposta il destinatario del messaggio.
	 * @param to una stringa contenente il destinatario.
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Ritorna l'oggetto del messaggio.
	 * @return una stringa con l'oggetto del messaggio.
	 */
	public String getSubject() {
		return subject;
	}

	
	/**
	 * Imposta l'oggetto del messaggio. 
	 * @param subject una stringa con l'oggetto del messaggio.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Ritorna il messaggio.
	 * @return una stringa con il messaggio.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Ritorna l'opzione di invio copia in CC al mittente.
	 * @return un booleano con lo stato dell'opzione.
	 */
	public boolean getCC() {
		return cc;
	}

	/**
	 * Imposta il messaggio. 
	 * @param message una stringa con il messaggio.
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	
}
