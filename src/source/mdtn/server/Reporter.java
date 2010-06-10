package source.mdtn.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import source.mdtn.bundle.Bundle;

/**
 * Classe-thread demone che si occupa del gestione dei report.
 */
public class Reporter extends Thread {

	/** Lista delle ricevute/report da consegnare. */
	private Vector<Bundle>recepitList;
	
	/** Lista dei file letti. */
	private Vector<String> fileReaded;
	
	/** Riferimento al server. */
	private Server refServer;
	
	private Vector<CommunicationThread> clients;
	
	public Reporter(Server myServer){
		refServer=myServer;
		clients=refServer.getClients();
		recepitList = new Vector<Bundle>();
		fileReaded = new Vector<String>();
	}
	
	public void run(){
		while(true){
			
			//Aggiorno la lista dei report da inviare
			refreshReports();
			System.out.println("Check Report!");
			for(int i=0; i<recepitList.size(); i++){
				Bundle refBundle = recepitList.elementAt(i);
				//Provo a consegnare la ricevuta. Cerco se il destinatario è collegato
				for(int j=0; j<clients.size(); j++){
					try{
						CommunicationThread clientRef = clients.elementAt(j);
						if(!(clientRef==null) && !(refBundle==null)){
							System.out.println(clientRef.getEID()+ " -> "+refBundle.getPrimary().getDestination());
							if(clientRef.getEID().equals(refBundle.getPrimary().getDestination())){
								//Se è il client che cerco, invio la ricevuta!
								boolean esit = clientRef.send(refBundle);
								if(esit){//Se l'invio è riuscito, posso cancellare il bundle
									refServer.addLog("Ricevuta inviata a "+clientRef.getEID());
									removeReport(refBundle);
									i--;
								}
							}
						}
					}
					catch(ArrayIndexOutOfBoundsException e){System.out.println("Err schivato..");}
				}
				
			}
			
			//Aspetto prima di effettuare un nuovo controllo.
			try {sleep(5000);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	
	QUUUUUUUUUUUAAAAAAAAAAAAAAAAA
	Cancellare bene la ricevuta dopo invio!
	Visualizzare i messaggi toast su android
	
	/**
	 * Rimuovo il bundle-report da liste e disco.
	 * @param toRemove il bundle da rimuovere.
	 */
	private void removeReport(Bundle toRemove){
		if(toRemove==null)return;

		/*System.out.println(Server.getBundlePath()+toRemove.getFilePath());
		File f= new File(Server.getBundlePath()+toRemove.getFilePath());
		if(f.delete())System.out.println("Cancellato!");
		else System.out.println("NON Cancellato!");
		 */
		
		//mygc.addFileToDelete(Server.getBundlePath()+toRemove.getFilePath());

		for(int i=0;i<recepitList.size();i++){
			if(recepitList.elementAt(i).equals(toRemove)){
				recepitList.remove(i);
				return;
			}
		}
	}

	/**
	 * Metodo interno che aggiorna automaticamente la lista dei report da inviare.
	 */
	private void refreshReports(){
		File dir = new File(Server.getBundlePath()); 
		//Filtra opportunamente i file
		String[] children = dir.list(new FilenameFilter() {
	           public boolean accept(File dir, String name) {
	                return name.toLowerCase().endsWith(".report");
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
						recepitList.add(toAdd);
						System.out.println("ReportLista: Added new report (tot: "+recepitList.size()+")");	
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
