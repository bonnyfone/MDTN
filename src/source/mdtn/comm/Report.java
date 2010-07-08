package source.mdtn.comm;

import java.io.Serializable;
import source.mdtn.util.Timing;

/**
 * Classe rappresentante un generico Report o ricevuta.
 */
public class Report implements Serializable {

	private static final long serialVersionUID = -2425822024675989447L;

	/** Messaggio del rapporto. */
	private String message;
	
	/** Data di completamento del task riferito dal report. */
	private String completeTime;
	
	/**
	 * Costruttore del report.
	 * @param messgeToReport messaggio da inserire nel report.
	 */
	public Report(String messgeToReport){
		message = messgeToReport;
		completeTime = Timing.getTime(2, ":");
	}
	
	/**
	 * Metodo che ritorna il messaggio contenuto nel rapporto.
	 * @return una stringa contenente il messaggio.
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Metodo che ritorna l'ora di completamento del task a cui il report fa riferimento.
	 * @return una stringa contenente l'ora di completamento.
	 */
	public String getComplementTime(){
		return completeTime;
	}
}
