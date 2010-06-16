package source.mdtn.comm;

import source.mdtn.bundle.Bundle;
import source.mdtn.server.Server;
import source.mdtn.server.Service;

public class BundleProtocol {

	/** modalità operativa (0=server, 1=client*/
	private int operativeMode;

	/**
	 * Costruttore del protocollo.
	 * @param operativeMode modalità operativa da usare. <br>0 = server <br>1 = client
	 */
	public BundleProtocol(int operativeMode){
		this.operativeMode=operativeMode;
	}



	public Bundle processBundle(Bundle toBeProcessed){

		if(operativeMode==0){
			return serverProcessBundle(toBeProcessed);
		}
		else if(operativeMode==1){
			return clientProcessBundle(toBeProcessed);
		}
		else{
			return null;
		}
	}

	
	/**
	 * Metodo interno che processa il bundle in modalità <b>client</b>.
	 * @param toBeProcessed il bundle da processare.
	 * @return un bundle di risposta.
	 */
	private Bundle clientProcessBundle(Bundle toBeProcessed){

		
		return null;
	}

	
	/**
	 * Metodo interno che processa il bundle in modalità <b>server</b>.
	 * @param toBeProcessed il bundle da processare.
	 * @return un bundle di risposta.
	 */
	private Bundle serverProcessBundle(Bundle toBeProcessed){
		
		//Se è un pacchetto informativo, non serve salvare nulla
		if(toBeProcessed.getPayload().getType().equals("DISCOVERY")){
			//EIDclient=newBundle.getPrimary().getSource();
		}
		else if(toBeProcessed.getPayload().getType().equals("UPDATE_LIST")){
			//TODO Creare un bundle con dentro la lista dei file del client+bacheca
			return Service.updateListBundle(toBeProcessed.getPrimary().getSource());
		}
		else{//Altrimenti, salvataggio persistente del bundle su disco (RFC5050).
			if(!toBeProcessed.store(Server.getBundlePath()))System.out.println("Errore di storage");	
		}

		return null;
	}
	
	

}
