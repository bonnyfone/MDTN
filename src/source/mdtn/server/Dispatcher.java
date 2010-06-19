package source.mdtn.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.comm.BundleProtocol;
import source.mdtn.comm.Report;
import source.mdtn.util.Buffering;
import source.mdtn.util.GenericResource;
import source.mdtn.util.Message;
import source.mdtn.util.Timing;

public class Dispatcher extends Thread {

	/** Oggetto di sincronizzazione. */
	private Object connLock;

	/** Riferimento al server. */
	private Server refServer;

	/** Protocollo per interpretare i bundle. */
	private BundleProtocol protocol;

	/** Numero massimo di operazioni da eseguire in parallelo. */
	private int maxOperation;

	/** Lista dei bundle da processare. */
	private Vector<Bundle> jobList;

	/** Lista dei file letti. */
	private Vector<String> fileReaded;

	/** Lista delle ricevute da inviare. */
	private Vector<Bundle> recepitList;

	/** Agente che si occupa di applicare il protocollo ed interpretare i bundle. */
	private Executor agent;

	/** Demone che si occupa di ripulire lo storage dai bundle processati. */
	private Cleaner mygc;

	public Dispatcher(Server myServer,Object sentinel){
		refServer = myServer;
		connLock = sentinel;
		protocol = new BundleProtocol(0);
		maxOperation = 5;
		jobList = new Vector<Bundle>();
		recepitList = new Vector<Bundle>();
		fileReaded = new Vector<String>();
		agent = new Executor();
		mygc = new Cleaner();
		mygc.start();
	}

	public void run(){
		//Avvia un thread per il monitoraggio del Bundle-storage che aggiorna la lista dei lavori.
		//new Refresher().start();
		try {
			synchronized (connLock) {
				while(true){
					connLock.wait(); //In attesa della connettività internet.
					agent.wakeup(); //Risveglia l'agente
					//System.out.println("Notify internet");
				}
			}
		} catch (InterruptedException e) {e.printStackTrace();}

	}

	/**
	 * Classe interna che si occupa dell'esecuzione dei task necessari.
	 * Esegue contemporaneamente n task, dove n è il numero massimo di operazioni
	 * simultanee impostato nel dispatcher.
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

		public synchronized void wakeup(){
			this.notifyAll();
		}

		public void process(){
			while(true){
				synchronized (this) {
					while(currentOperation >= maxOperation){ //Attende la disponibilità di eseguire nuove op
						try {this.wait();} 
						catch (InterruptedException e) {e.printStackTrace();}
					}

					refreshJobs();
					if(refServer.gotInternetAccess()){

						//TODO Scegliere: quale bundle processare????
						int id = jobList.size()-1;
						if(id>=0) {
							currentOperation++;
							final Bundle ref = jobList.elementAt(id);
							removeJob(ref);

							//Lancio un thread dedicato all'operazione
							Thread newOperation = new Thread(){
								public void run(){
									String esit="";
									esit = executeJob(ref); //process...
									refServer.addLog(esit);

									if(esit.startsWith("error")){
										System.out.println("\nERR!\n");
										/*TODO riaccoda il lavoro se ho fallito! Aspetto qualche istante e riaccodo*/
										ref.store(Server.getBundlePath());//ripristino lo storage!
										try {sleep(Timing.randomNumber(1000, 2000));} catch (InterruptedException e) {}
										jobList.add(ref);
									}
									else{//tutto ok, preparo la ricevuta
										Report newReport = new Report(esit);
										Bundle newReportBundle = new Bundle(); //TODO sistemare i vari campi del bundle...

										//destinatario della ricevuta
										newReportBundle.getPrimary().setSource(Server.getServerEID());
										newReportBundle.getPrimary().setReportTo(ref.getPrimary().getReportTo());
										newReportBundle.getPrimary().setDestination(ref.getPrimary().getReportTo());
										//appendo il messaggio 
										newReportBundle.getPayload().setPayloadData(Buffering.toBytes(newReport));
										//imposto il type
										newReportBundle.getPayload().setType("REPORT");

										//Store della ricevuta, ci penserà il demone Reporter ad inviarla..
										newReportBundle.store(Server.getBundlePath());
									}

									currentOperation--;
									synchronized (Executor.this) {
										Executor.this.notifyAll();
									}
								}
							};
							newOperation.start();
						}	
					}
				}
			}
		}

		/**
		 * Metodo che esegue effettivamente le attività richieste dal bundle.
		 * @param toBeProcessed il bundle contenente le informazioni sulle operazioni da eseguire.
		 * @return una stringa con l'esito dell'operazione.
		 */
		public String executeJob(Bundle toBeProcessed){

			String type=toBeProcessed.getPayload().getType();

			if(type.equals("EMAIL")){
				//invia mail
				Message newMessage = (Message)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
				String ris="Inviata mail a " +newMessage.getTo();

				try {
					String es=Service.SendMail(newMessage.getFrom(), newMessage.getTo(), newMessage.getSubject(), newMessage.getMessage());
					if(es.startsWith("error"))ris="error: Email non inviata correttamente, riprovo.";

					System.out.println("Inviata mail a " +newMessage.getTo());
				} catch (IOException e1) {
					e1.printStackTrace();
					return "error: Connessione interrotta. (Rescheduled)";
				}


				return ris;
			}
			else if(type.equals("REQUEST")){
				GenericResource newRequest = (GenericResource)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
				String ris;

				try {
					URL req = new URL(newRequest.getAddress());

					File newDir = new File(Server.getDataPath()+toBeProcessed.getPrimary().getSource().getHost().toString());
					newDir.mkdir();
					refServer.addLog("Download di "+newRequest.getAddress()+" in corso..");
					ris=Service.downloadFile(newDir.toString(), req);
					if(ris.equals("ok")){
						ris="Disponibile il file " +newRequest.getName();
						if(newRequest.isPublic()){
							Service.addPublicResource(toBeProcessed.getPrimary().getSource().getHost().toString()+newRequest.getName());
						}
					}

					//System.out.println("Inviata mail a " +newMessage.getTo());
				} catch (IOException e1) {
					e1.printStackTrace();
					return "error: Connessione interrotta. (Rescheduled)";
				}

				return ris;
			}
			else{
				//TODO popolare il protocollo
				//altro....
			}

			return "error";
		}

		/**
		 * Rimuovo il bundle da liste e disco, in quanto in fase di processing.
		 * @param toRemove il bundle da rimuovere.
		 */
		private void removeJob(Bundle toRemove){
			if(toRemove==null)return;

			/*System.out.println(Server.getBundlePath()+toRemove.getFilePath());
			File f= new File(Server.getBundlePath()+toRemove.getFilePath());
			if(f.delete())System.out.println("Cancellato!");
			else System.out.println("NON Cancellato!");
			 */
			//Richiede al BundleGarbage collector di occuparsi di questo bundle, per cancellarlo.
			mygc.addFileToDelete(Server.getBundlePath()+toRemove.getFilePath());

			for(int i=0;i<jobList.size();i++){
				if(jobList.elementAt(i).equals(toRemove)){
					jobList.remove(i);
					return;
				}
			}
		}

		/**
		 * Metodo interno che aggiorna la lista dei lavori da fare.
		 */
		private void refreshJobs(){
			File dir = new File(Server.getBundlePath()); 
			//Ottengo la lista dei file, filtrata opportunamente per estensione.
			String[] children = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".bundle");
				}
			});


			if (!(children == null)) { 
				for (int i=0; i<children.length; i++)
				{ 
					if(!alreadyReaden(children[i])){//Controllo se per caso ho già letto questo file.
						//Segno il file come letto e carico il bundle
						Bundle toAdd = Bundle.retrive(Server.getBundlePath()+children[i]);
						if(!(toAdd==null)){
							fileReaded.add(children[i]);
							jobList.add(toAdd);
							System.out.println("JobList: Added new bundle");	
						}
					}
				}
			} 
		}


		/**
		 * Metodo interno di supporto che verifica se un file è già stato letto dal dispatcher.
		 * @param file il file da controllare.
		 * @return true=già letto, false=non letto
		 */
		private boolean alreadyReaden(String file){
			for(int i=0; i<fileReaded.size(); i++){
				if(fileReaded.elementAt(i).equals(file))return true;
			}

			return false;
		}


	}


	/**
	 * Classe-thread demone interna che funge da Bundle-garbage Collector.
	 */
	private class Cleaner extends Thread{

		private Vector<File> delList;

		public Cleaner(){
			setDaemon(true);
			delList = new Vector<File>();
		}

		public void addFileToDelete(String filepath){
			File f = new File(filepath);
			if(f.exists())delList.addElement(f);
			else System.out.println("NON ESISTE!");
		}

		public void run(){
			while(true){
				try {
					for(int i=0;i<delList.size();i++){
						if(delList.elementAt(i).delete()){
							delList.remove(i);
							i--;
						}
					}
					//System.out.println("Cleaner: restano "+delList.size());
					sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * Classe thread interna per l'aggiornamento della joblist. Semplicemente, legge eventuali nuovi bundle
	 * dal bundle storage, o recupera l'intero gruppo di bundle in seguito ad un riavvio/crash.
	 */
	/*
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

	}*/


}
