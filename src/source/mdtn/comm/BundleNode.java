package source.mdtn.comm;

import java.net.URI;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.Message;
import source.mdtn.util.Timing;

public class BundleNode {

	/** BPAgent del nodo */
	private BPAgent myBpAgent;

	/** URI del EID del nodo */
	private URI myEID;

	/** Log eventi */
	private Vector<String> nodeLog;


	//TODO Qui vanno messi tutti i campi dati e le informazioni generali sul nodo! Le funzionalità
	//sono implementate attraverso metodi del BPAgent

	/**
	 * Costruttore del BundleNode.
	 * @param myEID URI che rappresenta l'EID del nodo.
	 */
	public BundleNode(URI myEID){
		this.myEID = myEID;
		myBpAgent = new BPAgent();
		nodeLog = new Vector<String>();
		addLog("BundleNode istanziato, pronto ad accedere a MDTN.");


		Thread resetSequenceNumber = new Thread(){
			public void run(){
				try {
					while(true){
						sleep(1000);	
						Bundle.resetSequenceNumber();
					}
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		};
		resetSequenceNumber.start();
	}

	/**
	 * Ritorna il BPAgent del nodo. Attraverso l'agente è possibile sfruttare i servizi MDTN.
	 * @return
	 */
	public BPAgent getMyAgent(){
		return myBpAgent;
	}

	/**
	 * Aggiunge un nuovo log alla lista degli eventi del nodo.
	 * @param newLog Stringa contenente il nuovo log.
	 */
	public void addLog(String newLog){
		nodeLog.add(Timing.getTime(2, ":") + "  " + newLog);
	}

	public Vector<String> getLogs(){
		return nodeLog;
	}


	/**----------------------------------------------------------------------------------------------*/
	/**----------------------------------        B P A        -------------------------------------- */
	/**----------------------------------------------------------------------------------------------*/

	/** Bundle Protocol Agent, ovvero il fornitore dei servizi MDTN*/
	public class BPAgent {

		/** ConvergenceLayerAdapter specifico per la comunicazione via TCP/IP */
		private TcpAdapter myTcpConn; 

		/** Crea un BPAgent */
		public BPAgent(){
			myTcpConn = new TcpAdapter();
		}

		/** Effettua connessione al servizio MDTN. */
		public boolean connectToService(String ip){
			//addLog("Tentativo di connessione a MDTN...");
			boolean esito = myTcpConn.connect(ip, 3339);

			if(esito)addLog("Connessione stabilita.");
			else addLog("Errore di connessione.");

			return esito;
		}

		
		/** Effettua connessione al servizio MDTN. */
		public void disconnectFromService(){
			addLog("Disconnesso.");
			myTcpConn.disconnect();
		}


		/**
		 * Invia un bundle al server MDTN.
		 * @param myBundle il bundle da inviare.
		 * @return true=inviato, false=non inviato
		 */
		public boolean sendBundle(Bundle myBundle){
			return myTcpConn.send(myBundle);
		}

		
		/**
		 * Invia un messaggio email. Costruisce automaticamente il bundle necessario e lo invia, 
		 * richiamando i livelli inferiori.
		 * @param myMessage il messaggio da inviare.
		 * @return true=inviato, false=non inviato
		 */
		public boolean sendEmail(Message myMessage){

			Bundle toSend = new Bundle();

			//Imposta il tipo di dati
			toSend.getPayload().setType("EMAIL");

			//Imposta il payload del bundle
			byte rawdata[] = Buffering.toBytes(myMessage);
			toSend.getPayload().setPayloadData(rawdata);

			//Imposto eventuali flag??
			//TODO impostare flag necessari...

			//Invio il bundle contenente il messaggio.
			boolean esit = sendBundle(toSend);
			
			if(esit)addLog("Email inoltrata.");
			else addLog("Errore inoltro email.");

			return esit;
		}


		/**
		 * Metodo che controlla lo stato della connessione al servizio MDTN.
		 * @return true=connesso<br>false=non connesso
		 */
		public boolean isConnected(){
			return myTcpConn.isConnected();
		}

	}

}
