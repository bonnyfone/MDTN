package source.mdtn.comm;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.Message;

public class BundleProtocol {
   
	public BundleProtocol(){
		
	}
	
	
	public String processBundle(Bundle toBeProcessed){
		
		String type=toBeProcessed.getPayload().getType();
		
		if(type.equals("EMAIL")){
			//invia mail
			Message newMessage = (Message)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
			System.out.println("Inviata mail a " +newMessage.getTo());
			
			//Simula perdita di tempo, da cancellare ovviamente..
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return "Inviata mail a " +newMessage.getTo();
		}
		else{
			//TODO popolare il protocollo
			//altro....
		}
		
		return "error";
	}
}
