package source.mdtn.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class RealResource implements Serializable {
	
	private String fileName;
	private byte[] data;
	
	public RealResource(String filename){
		
		try {
			File toRead = new File(filename);
			fileName = toRead.getName();
			data = Buffering.getBytesFromFile(toRead);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getName(){
		return fileName;
	}
	
	public byte[] getData(){
		return data;
	}
	

}
