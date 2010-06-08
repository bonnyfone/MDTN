package source.mdtn.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.Message;
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

	/** Contatore dei client che si sono collegati a partire dallo startup del server. */
	private int numClients=0;
	
	/** Interfaccia grafica del server */
	private ServerGui myGui;
	
	/** Vettore che rappresenta la lista dei client collegati al server.  */
	private Vector<CommunicationThread> clients = new Vector<CommunicationThread>();


	/**
	 * Costruttore del <b>Server</b>. Crea un server in ascolto sulla porta specificata.
	 * @param listeningPort la porta su cui mettersi in ascolto.
	 */
	public Server(int listeningPort){

		try {
			setDaemon(true);
			myGui = new ServerGui();
			serverSocket = new ServerSocket(listeningPort);
			System.out.println("In ascolto (porta "+listeningPort+")...");
			addLog("In ascolto (porta "+listeningPort+")...");
			
			/* Test */
			Message x = new Message("fromasd","toasd","asd","asd");
			byte b[]=Buffering.toBytes(x);
			Message y = (Message)Buffering.toObject(b);
			//Bundle y = (Bundle)Buffering.toObject(b);
			System.out.println(y.getFrom()+" "+y.getTo());
			
		} catch (IOException e) {
			addLog("Could not listen on port: "+listeningPort+".");
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
	 * Main di avvio del server.
	 * @param args <b>args[0]</b> = Unico parametro accettato: porta del server (Intero da 1-65535).
	 */
	public static void main(String[] args){

		int defaultPort=3339;

		if(args.length==1)defaultPort=Integer.parseInt(args[0]);

		Server myServer = new Server(defaultPort);
		myServer.start();
	}
}
