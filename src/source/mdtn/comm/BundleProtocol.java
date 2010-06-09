package source.mdtn.comm;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.Message;

public class BundleProtocol {
   
	public BundleProtocol(){
		
	}
	
	
	public boolean processBundle(Bundle toBeProcessed){
		
		String type=toBeProcessed.getPayload().getType();
		
		if(type.equals("EMAIL")){
			//invia mail
			Message newMessage = (Message)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
			System.out.println("Inviata mail a " +newMessage.getTo());
			
			return true;
		}
		else{
			//TODO popolare il protocollo
			//altro....
		}
		
		return false;
	}
}
