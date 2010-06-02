package source.mdtn.server;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Classe principale che rappresenta un <b><i>Server MDTN</i></b>.
 * La classe è un thread, è quindi possibile avviare più istanze parallele del server, in ascolto su porte diverse.
 */
public class Server extends Thread {

	/** Socket del server, ha lo scopo di accogliere le connessioni dei client. */
	ServerSocket serverSocket;

	/** Stato del server. Se true, il server è in ascolto per accettare nuove connessioni. */
	boolean listening = true;

	/** Vettore che rappresenta la lista dei client collegati al server.  */
	Vector<CommunicationThread> clients = new Vector<CommunicationThread>();

	/** Contatore dei client che si sono collegati a partire dallo startup del server. */
	int numClients=0;
	
	/** Interfaccia grafica del server */
	ServerGui myGui;


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
				addLog("Pre-accettazione di client "+numClients);
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

	
	public void addLog(String myMessage){
		myGui.txtLog.insert(myMessage+"\n", 0);
	}
	

	/**
	 * Classe interna di supporto grafico che fornisce una semplice interfaccia grafica al server.
	 */
	public class ServerGui extends JFrame{

		private static final long serialVersionUID = 6197952227066657715L;
		
		JTextArea txtLog;
		JTextField txtIp;

		public ServerGui(){
			setSize(500,500);
			setLocation(750, 250);
			
			txtLog = new JTextArea();
			txtIp = new JTextField();
			JScrollPane scrollingLog = new JScrollPane(txtLog);
			scrollingLog.setBorder(BorderFactory.createTitledBorder("Logs"));
			
			add(txtIp, BorderLayout.NORTH);
			add(scrollingLog,BorderLayout.CENTER);
			
			validate();
			setVisible(true);

			//WindowListener per la corretta terminazione del programma
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent arg0) {
					//TODO Disconnettere in modo opportuno i client qui!
					System.exit(0);
				}
			});
			
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
