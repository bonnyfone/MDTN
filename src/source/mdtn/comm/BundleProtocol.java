package source.mdtn.comm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import source.mdtn.bundle.Bundle;
import source.mdtn.server.Server;
import source.mdtn.server.Service;
import source.mdtn.util.Buffering;
import source.mdtn.util.GenericResource;

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
	private Bundle serverProcessBundle(final Bundle toBeProcessed){
		
		//Se è un pacchetto informativo, non serve salvare nulla
		if(toBeProcessed.getPayload().getType().equals("DISCOVERY")){
			//EIDclient=newBundle.getPrimary().getSource();
		}
		//Richiesta di aggiornamento lista
		else if(toBeProcessed.getPayload().getType().equals("UPDATE_LIST")){
			return Service.updateListBundle(toBeProcessed.getPrimary().getSource());
		}
		//Richiesta di eliminazione risorsa
		else if(toBeProcessed.getPayload().getType().equals("DELETE")){
			Service.removeResource(toBeProcessed);
			//return Service.updateListBundle(toBeProcessed.getPrimary().getSource());
		}
				
		//Richiesta di download
		else if(toBeProcessed.getPayload().getType().equals("DOWNLOAD")){
			
	        //TODO
			Thread handle= new Thread(){
				public void run(){
					Service.uploadFile(toBeProcessed);
				}
			};
			handle.start();
			/*
			GenericResource res = (GenericResource)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
			String EID = toBeProcessed.getPrimary().getSource().getHost();
			return Service.returnResource(res,EID);
			*/
		}
		else{//Altrimenti, salvataggio persistente del bundle su disco (RFC5050).
			if(!toBeProcessed.store(Server.getBundlePath()))System.out.println("Errore di storage");	
		}

		return null;
	}
	
	

}
