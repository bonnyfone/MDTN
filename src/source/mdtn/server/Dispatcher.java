package source.mdtn.server;

import java.io.File;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.comm.BundleProtocol;

public class Dispatcher extends Thread {

	/** Oggetto di sincronizzazione */
	private Object connLock;

	/** Protocollo per interpretare i bundle */
	private BundleProtocol protocol;

	/** Numero massimo di operazioni da eseguire in parallelo */
	private int maxOperation;

	/** Lista dei bundle da processare */
	private Vector<Bundle> jobList;

	/** Lista dei file letti */
	private Vector<String> fileReaded;

	/** Lista delle ricevute da inviare */
	private Vector<Bundle> recepitList;

	/** Agente che si occupa di applicare il protocollo ed interpretare i bundle */
	private Executor agent;

	public Dispatcher(Object sentinel){
		connLock = sentinel;
		protocol = new BundleProtocol();
		maxOperation = 1;
		jobList = new Vector<Bundle>();
		recepitList = new Vector<Bundle>();
		fileReaded = new Vector<String>();
		agent = new Executor();
	}

	public void run(){

		//Avvia un thread per il monitoraggio del Bundle-storage che aggiorna la lista dei lavori.
		new Refresher().start();
		try {
			synchronized (connLock) {
				while(true){
					connLock.wait(); //In attesa della connettività internet.

					agent.wakeup(); //Risveglia l'agente

				}
			}
		} catch (InterruptedException e) {e.printStackTrace();}

	}

	/**
	 * Classe interna per l'esecuzione effettiva delle operazioni.
	 */
	private class Executor{
		private int currentOperation;

		public Executor(){
			currentOperation=0;

			Thread t = new Thread(){
				public void run(){
					process();
				}
			};
			t.start();
		}

		public void wakeup(){
			this.notifyAll();
		}

		public void process(){

			synchronized (this) {
				while(true){
					while(currentOperation >= maxOperation){ //Attende la disponibilità di eseguire nuove op
						try {this.wait();} 
						catch (InterruptedException e) {e.printStackTrace();}
					}
					//TODO Scegliere: quale bundle processare????
					final int id = jobList.size()-1;

					if(id>=0) {
						currentOperation++;

						Thread newOperation = new Thread(){
							public void run(){
								boolean esit=false;
								esit = protocol.processBundle(jobList.elementAt(id)); //process...
								currentOperation--;

								//Se ho completato il task, posso rimuovere il bundle
								if(esit)jobList.remove(id);
							}
						};
						newOperation.start();
					}	
				}
			}
		}


	}


	/**
	 * Classe thread interna per l'aggiornamento della joblist. Semplicemente, legge eventuali nuovi bundle
	 * dal bundle storage, o recupera l'intero gruppo di bundle in seguito ad un riavvio/crash.
	 */
	private class  Refresher extends Thread{
		public void run(){
			while(true){
				try {
					refreshJobs();
					System.out.println("Bundlelist: "+jobList.size());
					sleep(2000);
				} catch (InterruptedException e) {e.printStackTrace();}
			}}

		private void refreshJobs(){
			File dir = new File(Server.getBundlePath()); 
			String[] children = dir.list();
			if (!(children == null)) { 
				for (int i=0; i<children.length; i++)
				{ 
					if(!alreadyReaden(children[i])){//Controllo se per caso ho già letto questo file.

						//Segno il file come letto e carico il bundle
						fileReaded.add(children[i]);
						jobList.add(Bundle.retrive(Server.getBundlePath()+children[i]));

					}
				} 
			} 
		}

		private boolean alreadyReaden(String file){
			for(int i=0; i<fileReaded.size(); i++){
				if(fileReaded.elementAt(i).equals(file))return true;
			}

			return false;
		}

	}


}
