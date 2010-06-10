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
	
	private BundleProtocol bp;
	//raw-stream
	//PrintWriter out;
	
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
		this.bp=new BundleProtocol();
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

		try{
			while ((datain = in.readObject()) != null) {
				
				Bundle newBundle = (Bundle)datain;
				
				System.out.println("Bundle ricevuto");
				myOwner.addLog("Bundle received(client id="+id+"): "+newBundle.getPrimary().getCreationTimestamp()+" "+newBundle.getPrimary().getCreationSequenceNumber() );
				EIDclient=newBundle.getPrimary().getSource();
				
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
		/*
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));

			String inputLine, outputLine;
			BundleProtocol kkp = new BundleProtocol();
			outputLine = kkp.processInput(null);
			out.println(outputLine);

			while ((inputLine = in.readLine()) != null) {
				myOwner.addLog("Received(id="+id+"): "+inputLine);
				System.out.println("Received(id="+id+"): "+inputLine);
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
				
				for(int i=0;i<other.size();i++){
					if(!(other.elementAt(i).equals(this)))
						other.elementAt(i).send(inputLine);
				}
				
				if (outputLine.equals("Bye"))
					break;
			}
			*/
		
			//Rimuovo il client dalla lista
			for(int i=0;i<other.size();i++){
				if(other.elementAt(i).equals(this) ){
					other.remove(i);
					myOwner.addLog("Client disconnected ("+id+") "+this);
					System.out.println("Client disconnected ("+id+") "+this);
				}
			}
			
			out.close();
			in.close();
			socket.close();
			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo che invia un bundle al client collegato.
	 * @param toSend il bundle da inviare.
	 */
	public boolean send(Bundle toSend){
		synchronized (out) {
			try {
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
	
	//Metodo raw per scrivere una linea sullo stream di uscita
//	public void send(String s){
//		out.println(s);
//	}
}
