package source.mdtn.util;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

/**
 * Classe di supporto che rappresenta ed astrae il concetto della risorsa.
 */
public class GenericResource implements Serializable{

	private static final long serialVersionUID = -3337316793966500378L;

	/** Indirizzo della risorsa (usato a seconda delle esigenze) */
	private String resAddress;
	
	/** Nominativo della risorsa */
	private String name;
	
	/** Informazione aggiuntiva sulla risorsa (usato a seconda delle esigenze)*/
	private String overInfo;
	
	/** Determina se la risorsa è pubblica.*/
	private boolean isPublic;
	
	/** Dimensioni della risorse (se note) in bytes.*/
	private long size;
	
	/**
	 * Costruttore di una risorsa.
	 * @param dir directory
	 * @param addr indirizzo
	 */
	public GenericResource(String dir, String addr){
		size=-1;
		resAddress = dir+"/"+addr;
		name = addr;
		overInfo ="";
		
	}
	
	/**
	 * Costruttore di una risorsa
	 * @param addr URL della risorsa.
	 */
	public GenericResource(URL addr){
		size=-1;
		resAddress = addr.toString();
		name = addr.getFile();
		overInfo = "";
		isPublic=false;
	}
	
	/**
	 * Calcola il filesize in base al percorso.
	 * @param path percorso del file.
	 */
	public void autoGetSize(String path){
		File f = new File(path);
		long newSize = f.length();
		
		if(newSize>0)size=newSize;
		else newSize=-1;
	}
	
	/*  Set & Get */
	
	/**
	 * Ritorna l'indirizzo della risorsa.
	 */
	public String getAddress(){
		return resAddress;
	}
	
	/**
	 * Ritorna il nome della risorsa.
	 * @return una stringa con il nome.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Ritorna le informazioni della risorsa.
	 * @return una stringa con le informazioni.
	 */
	public String getInfo(){
		return overInfo;
	}
	
	/**
	 * Metodo che indica se la risorsa è pubblica.
	 * @return un booleano che indica se la risorsa è pubblica.
	 */
	public boolean isPublic(){
		return isPublic;
	}
	
	/**
	 * Imposta le informazioni della risorsa.
	 * @param myInfo una stringa con le informazioni.
	 */
	public void setInfo(String myInfo){
		overInfo=myInfo;
	}
	
	/**
	 * Imposta le dimensioni della risorsa.
	 * @param s un long che rappresenta le dimensioni.
	 */
	public void setSize(long s){
		size=s;
	}
	
	/**
	 * Ritorna le dimensioni della risorsa.
	 * @return un long che rappresenta le dimensioni della risorsa.
	 */
	public long getSize(){
		return size;
	}
	
	/**
	 * Imposta la risorsa come pubblica.
	 */
	public void setAsPublic(){
		isPublic=true;
	}
}
