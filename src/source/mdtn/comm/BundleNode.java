package source.mdtn.comm;

import java.io.IOException;
import java.net.URI;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.Message;
import source.mdtn.util.Timing;
import android.util.Log;

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
						sleep(10000);	
						Bundle.resetSequenceNumber();
					}
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		};
		//resetSequenceNumber.start();
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
		
		/** Componente che gestisce i dati in entrata. */
		private Receiver ric;

		/** Interprete del BundleProtol */
		private BundleProtocol bp;
		
		/** Crea un BPAgent */
		public BPAgent(){
			myTcpConn = new TcpAdapter();
			bp = new BundleProtocol(1);
		}

		/** Effettua connessione al servizio MDTN. */
		public boolean connectToService(String ip){
			//addLog("Tentativo di connessione a MDTN...");
			boolean esito = myTcpConn.connect(ip, 3339);

			if(esito){
				addLog("Connessione stabilita.");

				//Attivo il thread di ricezione dati.
				ric = new Receiver();
				ric.start();
				
				//Invio del discovery-bundle
	            Bundle discovery = new Bundle();
	            discovery.getPayload().setType("DISCOVERY");
	            sendBundle(discovery);
			}
			else addLog("Errore di connessione.");

			return esito;
		}
		
		/**
		 * Classe thread interna che gestisce i dati in entrata.
		 */
		private class Receiver extends Thread{
			public Receiver(){

			}
			public void run(){
				try{
					Object datain;
					while((datain = myTcpConn.revice()) != null){
						addLog("Nuovo bundle ricevuto.");
						
						/*Platform-depended operation*/
						Bundle received = (Bundle)datain;
						if(received.getPayload().getType().equals("REPORT")){
							Report newReport = ((Report)Buffering.toObject(received.getPayload().getPayloadData()));
							addLog("REPORT: " + newReport.getMessage() + "\n\t\t\t(operazione effettuata alle "+newReport.getComplementTime() + ")");
						}
						
						/*Platform-independent operation*/
						Bundle myRisp = bp.processBundle((Bundle)datain);
						
						/*Invia eventuali risposte*/
						if(!(myRisp==null)){
							if(sendBundle(myRisp))
								addLog("Risposta inviata.");
							else
								addLog("Errore invio risposta.");
						}
					}
				}
				catch (IOException e) {
					myTcpConn.disconnect();
					Log.i("MDTN", "err: disconnected");
				} 
				catch (ClassNotFoundException e) {Log.i("MDTN", "err:class not found exc");}
			}
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
			//Assicura che la sorgente sia il bundleNode corrente
			myBundle.getPrimary().setSource(myEID);
			myBundle.getPrimary().setReportTo(myEID);
			
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
