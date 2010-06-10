package source.mdtn.comm;

import source.mdtn.bundle.Bundle;
import source.mdtn.server.Server;

public class BundleProtocol {
   
	public BundleProtocol(){
		
	}
	
	
	public Bundle processBundle(Bundle toBeProcessed){
		
		//Se è un pacchetto informativo, non serve salvare nulla
		if(toBeProcessed.getPayload().getType().equals("DISCOVERY")){
			//EIDclient=newBundle.getPrimary().getSource();
		}
		else{//Altrimenti, salvataggio persistente del bundle su disco (RFC5050).
			if(!toBeProcessed.store(Server.getBundlePath()))System.out.println("Errore di storage");	
		}
		
		return null;
	}
}
