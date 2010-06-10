package source.mdtn.server;

import java.io.File;
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
	
	public Reporter(){
		recepitList = new Vector<Bundle>();
	}
	
	public void run(){
		
	}
	
	
	
	/**
	 * Rimuovo il bundle del report da liste e disco.
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

	private void refreshReports(){
		File dir = new File(Server.getBundlePath()); 
		String[] children = dir.list();
		if (!(children == null)) { 
			for (int i=0; i<children.length; i++)
			{ 
				if(!alreadyReaden(children[i])){//Controllo se per caso ho già letto questo file.
					//Segno il file come letto e carico il bundle
					Bundle toAdd = Bundle.retrive(Server.getBundlePath()+children[i]);
					if(!(toAdd==null)){
						fileReaded.add(children[i]);
						recepitList.add(toAdd);
						System.out.println("JobList: Added new bundle");	
					}
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
