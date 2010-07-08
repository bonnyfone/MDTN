package source.mdtn.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Classe che rappresenta una risorsa fisica.
 */
public class RealResource implements Serializable {
	
	private static final long serialVersionUID = 530665556780976340L;

	/** Nome del file */
	private String fileName;
	
	/**Dati contenuti*/
	private byte[] data;
	
	/**
	 * Costruttore. 
	 * @param filename percorso del file.
	 */
	public RealResource(String filename){
		
		try {
			File toRead = new File(filename);
			fileName = toRead.getName();
			data = Buffering.getBytesFromFile(toRead);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Ritorna il nome della risorsa. 
	 * @return una stringa con il nome.
	 */
	public String getName(){
		return fileName;
	}
	
	/**
	 * Ritorna i dati del file.
	 * @return un array di byte.
	 */
	public byte[] getData(){
		return data;
	}
	

}
