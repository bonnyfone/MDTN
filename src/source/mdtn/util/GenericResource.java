package source.mdtn.util;

import java.io.Serializable;
import java.net.URL;

public class GenericResource implements Serializable{

	private String resAddress;
	
	private String name;
	
	private String type;
	
	private boolean available;
	
	private int iconType;
	
	public GenericResource(String dir, String addr){
		resAddress = dir+"/"+addr;
		name = addr;
		type ="boh";
	}
	
	public GenericResource(URL addr){
		resAddress = addr.toString();
		name = addr.getFile();
		type = "boh";
		available=false;
	}
	
	public String getAddress(){
		return resAddress;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
}
