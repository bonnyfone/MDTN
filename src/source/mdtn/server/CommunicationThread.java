package source.mdtn.server;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.comm.BundleProtocol;

/**
 * Classe-thread per la gestione di una singola connessione via socket.
 */
public class CommunicationThread extends Thread {

	/** Socket per la comunicazione TCP */
	private Socket socket;

	/** ID della connessione (ogni istanza di questa classe ha un'id differente) */
	private int id;

	/** Riferimento alla lista delle altre connessioni attive. */
	private Vector<CommunicationThread> other;

	/** Riferimento al server. */
	private Server myOwner;

	/** EID del client */
	private URI EIDclient;

	/** ObjectStream di uscita (dati in uscita)*/
	private ObjectOutputStream out;

	/** ObjectStream di entrata (dati in ingresso)*/
	private ObjectInputStream in;

	private String clientIP;

	private BundleProtocol bp;


	/**
	 * Costruttore specifico di un thread di comunicazione. 
	 * Istanzia e gestisce una connessione TCP via socket.
	 * @param myOwner riferimento al Server.
	 * @param altri riferimento alla lista delle altre connessioni attivie.
	 * @param n identificativo da assegnare alla connessione.
	 * @param socket socket da utilizzare per la comunicazione.
	 */
	public CommunicationThread(Server myOwner, Vector<CommunicationThread> altri,int n,Socket socket) {
		super("MDTN:CommunicationThread");
		setDaemon(true);
		this.socket = socket;
		this.id=n;
		this.other=altri;
		this.myOwner=myOwner;
		this.bp=new BundleProtocol(0);
	}

	/**
	 * Override del metodo Thread.run(), si occupa di gestire l'intera comunicazione via socket. 
	 */
	public void run() {

		try{
			Object datain;
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			clientIP = socket.getInetAddress().getHostAddress();
			System.out.println("IP client:"+clientIP);
			socket.setKeepAlive(true);
			boolean firstDatain=true;

			/* Communication Handle */
			try{
				while ((datain = in.readObject()) != null) {

					//Dati in arrivo
					Bundle newBundle = (Bundle)datain;
					System.out.println("Bundle ricevuto");
					myOwner.addLog("Bundle received(client id="+id+"): "+newBundle.getPrimary().getSource()+" ,"+newBundle.getPrimary().getCreationSequenceNumber() );
					EIDclient=newBundle.getPrimary().getSource();

					//Eventuale update delle connessioni precedenti del client
					if(firstDatain){updateOldReference(); firstDatain=false;}

					//Processo il bundle appena ricevuto.
					Bundle risp=bp.processBundle(newBundle);

					//Eventualmente, invio subito un bundle di risposta (non obbligatorio)
					if(!(risp==null))send(risp);

					/*
				//Se è un pacchetto informativo, non serve salvare nulla
				if(newBundle.getPayload().getType().equals("DISCOVERY")){
					EIDclient=newBundle.getPrimary().getSource();
				}
				else{//Altrimenti, salvataggio persistente del bundle su disco (RFC5050).
					newBundle.store(Server.getBundlePath());	
				}
					 */

					/* Il nuovo bundle verrà processato automaticamente dal Thread demone di bundle-processing
					 * del server. Il thread di comunicazione nel frattempo rimane libero per ricevere altri 
					 * bundle.
					 */
				}
			}
			catch(EOFException eofe){myOwner.addLog("Received EOF exception, client bad-disconnected.");}

			//Rimuovo il client dalla lista
			for(int i=0;i<other.size();i++){
				if(other.elementAt(i).equals(this) ){
					other.remove(i);
					myOwner.addLog("Client disconnected ("+id+") "+this);
					System.out.println("Client disconnected ("+id+") "+this);
				}
			}

			//Chiusura stream
			out.close();
			in.close();
			socket.close();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo interno che aggiorna eventuali precedenti connessioni del client, ancora pendenti.
	 * Le connessioni pendenti sono soggette al timeout dello stack TCP, dalle configurazioni del
	 * sistema operativo e della rete. Pertanto è necessario controllare, ad ogni nuova connessione,
	 * la provenienza del client per aggiornare, nell'eventualità, la lista delle connessioni.
	 */
	private void updateOldReference(){
		for(int i=0;i<other.size();i++){
			if(!other.elementAt(i).equals(this) && other.elementAt(i).getEID().equals(this.getEID()) ){
				other.remove(i);
				myOwner.addLog("Update reference to client "+ EIDclient +" ("+id+") ");
			}
		}

	}

	/**
	 * Metodo che invia un bundle al client collegato.
	 * @param toSend il bundle da inviare.
	 */
	public boolean send(Bundle toSend){
		synchronized (out) {
			try {
				toSend.getPrimary().setSource(Server.getServerEID());
				out.writeObject(toSend);
				out.flush();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Metodo che ritorna l'URI che identifica il client che utilizza questo thread di comunicazione.
	 * @return un URI contenente l'EID del client.
	 */
	public URI getEID(){
		if(EIDclient==null){
			try {return new URI("dtn://null");} 
			catch (URISyntaxException e) {e.printStackTrace();}
		}
		return EIDclient;
	}


}
