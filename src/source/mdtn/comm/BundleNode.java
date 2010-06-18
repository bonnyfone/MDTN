package source.mdtn.comm;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.Buffer;
import java.util.Vector;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.GenericResource;
import source.mdtn.util.Message;
import source.mdtn.util.RealResource;
import source.mdtn.util.Timing;
import android.R.bool;
import android.os.Environment;
import android.util.Log;

public class BundleNode {

	/** BPAgent del nodo */
	private BPAgent myBpAgent;

	/** URI del EID del nodo */
	private URI myEID;

	/** Log eventi */
	private Vector<String> nodeLog;

	/** Mirror delle risorse remote */
	private Vector<GenericResource> remoteRes;
	
	/** Mirror delle risorse pubbliche */
	private Vector<GenericResource> publicRes;
	

	private boolean isResUpdated;
	
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
		remoteRes = new Vector<GenericResource>();
		publicRes = new Vector<GenericResource>();
		addLog("BundleNode istanziato, pronto ad accedere a MDTN.");

		Thread t= new Thread(){
			public void run(){
				try {
				while(true){
					sleep(10000);
					Log.i("MDTN", "MAX Memory: "+Runtime.getRuntime().maxMemory()/1024/1024+" mb, TOTAL disp: "+ Runtime.getRuntime().totalMemory()/1024 +" kb, FREE: "+Runtime.getRuntime().freeMemory()/1024+" kb");
					Runtime.getRuntime().runFinalization();
					Runtime.getRuntime().gc();
				}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
			}
		};
		t.start();

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
	
	public Vector<GenericResource> getRemoteRes(){
		return remoteRes;
	}

	public Vector<GenericResource> getPublicRes(){
		return publicRes;
	}

	public boolean isResourceUpdated(){
		return isResUpdated;
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

		
		public long getDataReceived(){
			return myTcpConn.getDataReceived();
		}
		
		public boolean getDataFinished(){
			return myTcpConn.getFinished();
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
		
		public String getMyIp(){
			return myTcpConn.getIpAddress();
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
		 * Metodo che invia una richiesta di download di una generica risorsa.
		 * @param myReq la risorsa richiesta.
		 * @return true=richiesta inoltrata, false=errore inoltro richiesta
		 */
		public boolean sendRequestForResource(GenericResource myReq){
			
			Bundle toSend = new Bundle();
			
			//Imposta il payload del bundle
			toSend.getPayload().setType("REQUEST");
			
			//Imposta il payload del bundle
			byte rawdata[] = Buffering.toBytes(myReq);
			toSend.getPayload().setPayloadData(rawdata);
			
			//Imposto eventuali flag??
			//TODO impostare flag necessari...
			
			//Invio il bundle contenente la richiesta.
			boolean esit = sendBundle(toSend);
			
			if(esit)addLog("Richiesta inoltrata.");
			else addLog("Errore inoltro richiesta.");

			return esit;
		}


		/**
		 * Metodo che invia la richiesta di download di una risorsa.
		 * Attiva uno stato di attesa sul client, che è pronto a ricevere
		 * risorse appena il server è disponibile.
		 * @param toDownload la risorsa che si vuole scaricare.
		 * @return true=richiesta inoltrata, false=errore invio richiesta
		 */
		public boolean downloadResource(GenericResource toDownload){

			toDownload.setInfo(getMyIp());
			
			Bundle toSend = new Bundle();
			
			//Imposta il payload del bundle
			toSend.getPayload().setType("DOWNLOAD");
			
			//Imposta il payload del bundle
			byte rawdata[] = Buffering.toBytes(toDownload);
			toSend.getPayload().setPayloadData(rawdata);
			
			//Invio il bundle contenente la richiesta.
			File SDCardRoot = Environment.getExternalStorageDirectory();  
			final String path = SDCardRoot+"/MDTN_data/"+toDownload.getName();
			addLog("In attesa del file "+toDownload.getName());
			
			Thread listen = new Thread(){
				public void run(){
					myTcpConn.activateDataTransfering(path);		
				}
			};
			listen.start();
			
			boolean esit = sendBundle(toSend);
			
			if(esit)addLog("Richiesta inoltrata.");
			else addLog("Errore inoltro richiesta.");

			return esit;
			
		}
		
		/**
		 * Metodo che invia la richiesta di cancellazione di una risorsa remota.
		 * @param toDelete la risorsa che si desidera eliminare.
		 * @return true=richiesta inoltrata, false=errore invio richiesta
		 */
		public boolean deleteResource(GenericResource toDelete){
			Bundle toSend = new Bundle();
			
			//Imposta il payload del bundle
			toSend.getPayload().setType("DELETE");
			
			toSend.getPayload().setPayloadData(Buffering.toBytes(toDelete));
			
			//Imposto eventuali flag??
			//TODO impostare flag necessari...
			
			//Invio il bundle contenente la richiesta.
			
			boolean esit = sendBundle(toSend);
			
			if(esit){
				addLog("Richiesta cancellazione risorsa.");
				
			}
			else addLog("Errore cancellazione risorsa.");

			return esit;
		}
		
		/**
		 * Metodo che richiede la lista risorse al server. 
		 * (La successiva risposta del server viene gestita automaticamente dai livelli inferiori)
		 * @return true=richiesta inviata, false=errore invio richiesta.
		 */
		public boolean requestList(){
			Bundle toSend = new Bundle();
			
			//Imposta il payload del bundle
			toSend.getPayload().setType("UPDATE_LIST");
			
			//Imposto eventuali flag??
			//TODO impostare flag necessari...
			
			//Invio il bundle contenente la richiesta.
			isResUpdated=false;
			boolean esit = sendBundle(toSend);
			
			if(esit){
				addLog("Aggiornamento risorse..");
				
			}
			else addLog("Errore agg. risorse.");

			return esit;
		}

		/**
		 * Metodo che controlla lo stato della connessione al servizio MDTN.
		 * @return true=connesso<br>false=non connesso
		 */
		public boolean isConnected(){
			return myTcpConn.isConnected();
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
						
						//Gestione REPORT
						if(received.getPayload().getType().equals("REPORT")){
							Report newReport = ((Report)Buffering.toObject(received.getPayload().getPayloadData()));
							addLog("REPORT: " + newReport.getMessage() + "\n\t\t\t(operazione effettuata alle "+newReport.getComplementTime() + ")");
						}
						//Gestione aggiornamento liste risorse
						else if(received.getPayload().getType().equals("UPDATE_LIST")){
							
							Vector<Vector<GenericResource>> newList= (Vector<Vector<GenericResource>>)Buffering.toObject(received.getPayload().getPayloadData());
							remoteRes = newList.elementAt(0);
							publicRes = newList.elementAt(1);
							
							addLog("Aggiornamento lista risorse completato.");
							isResUpdated=true;
						}
						else if(received.getPayload().getType().equals("DOWNLOAD")){
							
							RealResource realFile = (RealResource)Buffering.toObject(received.getPayload().getPayloadData());
							
							File SDCardRoot = Environment.getExternalStorageDirectory();  
							Buffering.writeBytesToFile(SDCardRoot+"/MDTN_data/"+realFile.getName(), realFile.getData());
							addLog("Ricevuto file "+realFile.getName());
							
							realFile=null;
						}
						
						/* Platform-independent operation */
						Bundle myRisp = bp.processBundle(received);
						
						/*Invia eventuali risposte*/
						if(!(myRisp==null)){
							if(sendBundle(myRisp))
								addLog("Risposta inviata.");
							else
								addLog("Errore invio risposta.");
						}
						
						//Force memory-free
						datain=null;
						received=null;
						Runtime.getRuntime().runFinalization();
						Runtime.getRuntime().gc();
					
					}
				}
				catch (IOException e) {
					myTcpConn.disconnect();
					Log.i("MDTN", "err: disconnected");
				} 
				catch (ClassNotFoundException e) {Log.i("MDTN", "err:class not found exc");}
			}
		}
	}

}
