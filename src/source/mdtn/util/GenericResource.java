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
	
	private int iconType;
	
	public GenericResource(String dir, String addr){
		size=-1;
		resAddress = dir+"/"+addr;
		name = addr;
		overInfo ="";
		
	}
	
	public GenericResource(URL addr){
		size=-1;
		resAddress = addr.toString();
		name = addr.getFile();
		overInfo = "";
		isPublic=false;
	}
	
	public void autoGetSize(String path){
		File f = new File(path);
		long newSize = f.length();
		
		if(newSize>0)size=newSize;
		else newSize=-1;
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
	
	public void setSize(long s){
		size=s;
	}
	
	public long getSize(){
		return size;
	}
	
	public void setAsPublic(){
		isPublic=true;
	}
}
