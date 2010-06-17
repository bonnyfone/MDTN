package source.mdtn.util;

import java.io.Serializable;
import java.net.URL;

/**
 * Classe di supporto che rappresenta ed astrae il concetto della risorsa.
 */
public class GenericResource implements Serializable{

	/** Indirizzo della risorsa (usato a seconda delle esigenze) */
	private String resAddress;
	
	/** Nominativo della risorsa */
	private String name;
	
	/** Informazione aggiuntiva sulla risorsa (usato a seconda delle esigenze)*/
	private String overInfo;
	
	/** Determina se la risorsa è pubblica.*/
	private boolean isPublic;
	
	
	private int iconType;
	
	public GenericResource(String dir, String addr){
		resAddress = dir+"/"+addr;
		name = addr;
		overInfo ="";
	}
	
	public GenericResource(URL addr){
		resAddress = addr.toString();
		name = addr.getFile();
		overInfo = "";
		isPublic=false;
	}
	
	
	/*  Set & Get */
	
	public String getAddress(){
		return resAddress;
	}
	
	public String getName(){
		return name;
	}
	
	public String getInfo(){
		return overInfo;
	}
	
	public boolean isPublic(){
		return isPublic;
	}
	
	public void setInfo(String myInfo){
		overInfo=myInfo;
	}
	
}
