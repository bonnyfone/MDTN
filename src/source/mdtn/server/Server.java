package source.mdtn.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import source.mdtn.util.Networking;
import source.mdtn.util.Timing;

/**
 * Classe principale che rappresenta un <b><i>Server MDTN</i></b>.
 * La classe è un thread, è quindi possibile avviare più istanze parallele del server, in ascolto su porte diverse.
 */
public class Server extends Thread {

	/** Socket del server, ha lo scopo di accogliere le connessioni dei client. */
	private ServerSocket serverSocket;

	/** Stato del server. Se true, il server è in ascolto per accettare nuove connessioni. */
	private boolean listening = true;

	/** Stato della connettività Internet */
	private boolean gotInternet = false;
	
	/** Oggetto di sincronizzazione per il dispatcher */
	private Object connectionLock;
	
	/** Dispatcher delle operazioni pendenti */
	private Dispatcher opDispatcher;
	
	/** Reporter che si occupa di inviare le ricevute. */
	private Reporter opReporter;

	/** Contatore dei client che si sono collegati a partire dallo startup del server. */
	private int numClients=0;

	/** Interfaccia grafica del server */
	private ServerGui myGui;

	/** Vettore che rappresenta la lista dei client collegati al server.  */
	private Vector<CommunicationThread> clients = new Vector<CommunicationThread>();

	/** Path per lettura/scrittura bundle (config.mdtn)*/
	private static String bundlePath;
	
	/** Path per lettura/scrittura files (config.mdtn)*/
	private static String dataPath;
	
	/** Dimensione massima consentita per il download di file.*/
	private static long fileSizeLimit;
	
	/** Numero massimo di operazioni che il server può eseguire contemporaneamente (dispatch)*/
	private static int maxParallelOperation;
	
	/** EID del server */
	private static URI serverEID;

	/**
	 * Costruttore del <b>Server</b>. Crea un server in ascolto sulla porta specificata.
	 * @param listeningPort la porta su cui mettersi in ascolto.
	 */
	public Server(int listeningPort, String path, String data, long dataSize, int maxParallelOp){

		try {
			setDaemon(true);
			try {
				serverEID = new URI("dtn://server");
			} catch (URISyntaxException e) {e.printStackTrace();}
			
			connectionLock = new Object();
			myGui = new ServerGui();
			bundlePath = path;
			dataPath = data;
			fileSizeLimit=dataSize;
			maxParallelOperation=maxParallelOp;
			serverSocket = new ServerSocket(listeningPort);
			
			//Daemons
			opDispatcher = new Dispatcher(this,connectionLock);
			opReporter = new Reporter(this);
			opDispatcher.start();
			opReporter.start();
			
			System.out.println("In ascolto (porta "+listeningPort+")...");

			addLog("Reading configuration from config.mdtn...");
			addLog("Bundle-storage in \""+bundlePath+"\"");
			addLog("Data-storage in \""+dataPath+"\"");
			addLog("Max resource size allowed: "+fileSizeLimit/1024/1024 +" mb");
			addLog("Max number of parallel operation: "+maxParallelOperation);
			addLog("----------------------------SERVER CONFIG-----------------------------");
			
			addLog("In ascolto (porta "+listeningPort+")...\n");		
			
			Service.removePublicResource("saads.ads.it");
			//Service.addPublicResource("saads.ads.it");
			Service.getPublicResourceList();
			
			/* Test */
			/*Message x = new Message("fromasd","toasd","asd","asd");
			byte b[]=Buffering.toBytes(x);
			Message y = (Message)Buffering.toObject(b);
			//Bundle y = (Bundle)Buffering.toObject(b);
			System.out.println(y.getFrom()+" "+y.getTo());
			 */

		} catch (IOException e) {
			addLog("Could not listen on port: "+listeningPort+".");
			System.err.println("Could not listen on port: "+listeningPort+".");
			JOptionPane.showMessageDialog(null, "Could not listen on port: "+listeningPort+".");
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
				addLog("Pre-accettazione di client "+numClients);
				CommunicationThread a = new CommunicationThread(this,clients,numClients,serverSocket.accept()); 
				clients.addElement(a);
				a.start();
				addLog("Connessione accettata!");
			}
			catch(IOException ioe){ioe.printStackTrace();}
		}

		//Chiudo socket prima di uscire.
		try {
			serverSocket.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Metodo specifico per aggiungere un nuovo log alla lista log del server.
	 * @param myMessage Un stringa contenente il nuovo log da aggiungere.
	 */
	public void addLog(String myMessage){
		myGui.txtLog.insert(Timing.getTime(2, ":")+ "\t " +myMessage+"\n", 0);
	}

	/**
	 * Metodo statico che ritorna il path di salvataggio dei bundle.
	 * @return una stringa con il path di salvataggio dei bundle.
	 */
	public static String getBundlePath(){
		return bundlePath;
	}
	
	/**
	 * Metodo statico che ritorna il path di salvataggio dei file.
	 * @return una stringa con il path di salvataggio dei file.
	 */
	public static String getDataPath(){
		return dataPath;
	}
	
	/**
	 * Metodo statico che ritorna l'EID del server.
	 * @return un URI con l'EID del server.
	 */
	public static URI getServerEID(){
		return serverEID;
	}
	
	public static long fileSizeLimit(){
		return fileSizeLimit;
	}
	
	public static int maxParallelOp(){
		return maxParallelOperation;
	}
	
	/**
	 * Metodo che ritorna il riferimento alla lista dei clients del server.
	 * @return la lista dei thread di comunicazione dei clients.
	 */
	public Vector<CommunicationThread> getClients(){
		return clients;
	}


	/**
	 * Classe interna di supporto grafico che fornisce una semplice interfaccia grafica al server.
	 */
	public class ServerGui extends JFrame{

		private static final long serialVersionUID = 6197952227066657715L;

		JTextArea txtLog;
		JTextField txtIp;
		JLabel labelIp;
		JLabel labelConn;
		JLabel labelNumClients;

		public ServerGui(){
			setTitle("MDTN - Server");
			setSize(500,500);
			setLocation(750, 250);

			JPanel panelTop = new JPanel();
			GridLayout topLayout = new GridLayout(0,3);
			panelTop.setLayout(topLayout);
			try {
				//labelIp = new JLabel(InetAddress.getLocalHost().toString());
				labelIp = new JLabel(Networking.getNetworkAddresses().elementAt(0));
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			labelIp.setBorder(BorderFactory.createTitledBorder("Ip server"));



			labelConn = new JLabel("internet");
			labelConn.setBorder(BorderFactory.createTitledBorder("Internet"));

			labelNumClients = new JLabel("0");
			labelNumClients.setBorder(BorderFactory.createTitledBorder("N° clients"));

			panelTop.add(labelIp);
			panelTop.add(labelConn);
			panelTop.add(labelNumClients);

			txtLog = new JTextArea();
			txtLog.setTabSize(2);
			txtLog.setBackground(Color.black);
			txtLog.setForeground(Color.white);
			txtIp = new JTextField();

			JScrollPane scrollingLog = new JScrollPane(txtLog);
			scrollingLog.setBorder(BorderFactory.createTitledBorder("Logs"));

			add(panelTop, BorderLayout.NORTH);
			add(scrollingLog,BorderLayout.CENTER);

			validate();
			setVisible(true);
			updateNetworkInformation();


			//Demone per il monitoraggio dello stato della rete.
			Thread updater = new Thread(){
				public void run(){
					while(true){
						updateNetworkInformation();
						try {
							sleep(2000);
						} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			};
			updater.setDaemon(true);
			updater.start();

			//WindowListener per la corretta terminazione del programma
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent arg0) {
					//TODO Disconnettere in modo opportuno i client qui!
					System.exit(0);
				}
			});

		}

		/**
		 * Aggiorna lo stato di rete. In particolare, aggiorna lo stato della connessione Internet 
		 * e degli indirizzi ip.
		 */
		public void updateNetworkInformation(){
			//Connettività internet
			gotInternet = Networking.checkInternetConnection();
			
			//Eventualmente, sveglia il dispatcher se c'è connessione
			if(gotInternet){
				synchronized (connectionLock) {
					connectionLock.notifyAll();
				}
			}

			if(gotInternet){
				labelConn.setForeground(Color.green);
				labelConn.setText("ONLINE");
			}
			else{
				labelConn.setForeground(Color.red);
				labelConn.setText("OFFLINE");
			}

			//Client collegati
			labelNumClients.setText(clients.size()+"");
		}


	}

	/**
	 * Metodo che ritorna lo stato della connettività Internet del server.
	 * @return true=connesso, false=non connesso.
	 */
	public boolean gotInternetAccess(){
		return gotInternet;
	}

	/**
	 * Main di avvio del server.
	 */
	public static void main(String[] args){

		//Parametri necessari per l'avvio.
		int defaultPort=3339;
		String defaultPath="./";
		String dataPath="./";
		long dataSize=1024*1024*10;
		int maxParallelOp=5;

		//Processa il file di configurazione.
		try { 
			BufferedReader in = new BufferedReader(new FileReader("config.mdtn")); 
			String str; 
			int line=0;
			while ((str = in.readLine()) != null) 
			{ 
				if(line==0)
					defaultPort = Integer.parseInt(str);
				else if(line==1)
					defaultPath = str;
				else if(line==2)
					dataPath = str;
				else if(line==3)
					dataSize=Long.parseLong(str);
				else if(line==4)
					maxParallelOp=Integer.parseInt(str);					
				
				line++;
			} 
			in.close(); 
		}
		catch (IOException e) { JOptionPane.showMessageDialog(null, "Errore lettura file di configurazione.\nServer avviato con impostazioni di default."); } 

		//Avvio server
		Server myServer = new Server(defaultPort,defaultPath,dataPath,dataSize,maxParallelOp);
		myServer.start();
	}
}
