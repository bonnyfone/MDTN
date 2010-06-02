package source.mdtn.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * Classe principale che rappresenta un server MDTN.
 * La classe è un thread, è quindi possibile avviare più istanze parallele del server, in ascolto su porte diverse.
 */
public class Server extends Thread {

	/** Socket del server, per accogliere le connessioni dei client. */
	ServerSocket serverSocket;

	/** Stato del server. Se true, il server è in ascolto per accettare nuove connessioni. */
	boolean listening = true;

	/** Vettore che rappresenta la lista dei client collegati al server.  */
	Vector<CommunicationThread> clients = new Vector<CommunicationThread>();
	
	/** Contatore dei client che si sono collegati a partire dallo startup del server. */
	int numClients=0;
	
	public Server(int listeningPort){

		try {
			serverSocket = new ServerSocket(listeningPort);
			System.out.println("In ascolto (porta "+listeningPort+")...");
		} catch (IOException e) {
			System.err.println("Could not listen on port: "+listeningPort+".");
			System.exit(-1);
		}
	}

	/**
	 * @Override
	 * Metodo che esegue il lavoro effettivo del server. Rimane in attesa ed accetta nuove connessioni.
	 */
	public void run(){

		//Mentre il server è in ascolto, accetta nuove connessioni
		while (listening){
			try{
				numClients++;
				System.out.println("Pre-accettazione di client "+numClients); //Son
				CommunicationThread a = new CommunicationThread(clients,numClients,serverSocket.accept()); 
				clients.addElement(a);
				a.start();
				System.out.println("Connessione accettata!");
			}
			catch(IOException ioe){ioe.printStackTrace();}
		}

		//Chiudo socket prima di uscire.
		try {
			serverSocket.close();
		} catch (IOException e) {e.printStackTrace();}
	}


	public static void main(String[] args){
		Server myServer = new Server(3339);
		myServer.start();
	}
}
