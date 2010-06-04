package source.mdtn.comm;

import java.net.URI;

import source.mdtn.bundle.Bundle;

public class BundleNode {
	
	/** BPAgent del nodo */
	private BPAgent myBpAgent;
	
	/** URI del EID del nodo */
	private URI myEID;
	
	//TODO Qui vanno messi tutti i campi dati e le informazioni generali sul nodo! Le funzionalità
	//sono implementate attraverso metodi del BPAgent
	
	/**
	 * Costruttore del BundleNode.
	 * @param myEID URI che rappresenta l'EID del nodo.
	 */
	public BundleNode(URI myEID){
		this.myEID = myEID;
		myBpAgent = new BPAgent();
	}
	
	/**
	 * Ritorna il BPAgent del nodo. Attraverso l'agente è possibile sfruttare i servizi MDTN.
	 * @return
	 */
	public BPAgent getMyAgent(){
		return myBpAgent;
	}
	
	
	
	
	
	/**----------------------------------------------------------------------------------------------*/
	/**----------------------------------        B P A        -------------------------------------- */
	/**----------------------------------------------------------------------------------------------*/
	
	
	public class BPAgent {
		
		/** ConvergenceLayerAdapter specifico per la comunicazione via TCP/IP */
		private TcpAdapter myTcpConn; 

		/** Crea un BPAgent */
		public BPAgent(){
			myTcpConn = new TcpAdapter();
		}
		
		/** Effettua connessione al servizio MDTN. */
		public boolean connectToService(String ip){
			return myTcpConn.connect(ip, 3339);
		}
		
		/** Effettua connessione al servizio MDTN. */
		public void disconnectFromService(){
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
		 * Metodo che controlla lo stato della connessione al servizio MDTN.
		 * @return true=connesso<br>false=non connesso
		 */
		public boolean isConnected(){
			return myTcpConn.isConnected();
		}
		
	}
	
}
