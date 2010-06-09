package source.mdtn.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import source.mdtn.bundle.Bundle;

/**
 * ConvergenceLayerAdapter per trasportare bundle MDTN sopra connessioni TCP/IP.
 */
public class TcpAdapter {
	
	/** Socket per la comunicazione su TCP */
	private Socket socket;
	
	/** Stato della connessione del socket */
	private boolean connected;
	
	/** ObjectStream di ingresso (dati in entrata) */
	private ObjectInputStream ois;
	
	/** ObjectStream di uscita (dati in uscita) */
	private ObjectOutputStream oos;

	//Raw-streams
	private PrintWriter out;
	private BufferedReader in;
	

	/** Costruttore base, nessuna connessione automatica. */
	public TcpAdapter(){
		connected=false;
	}
	
	/** Costruttore avanzato che avvia subito la connessione utilizzando i parametri specificati. */
	public TcpAdapter(String ip, int port){
		connected=connect(ip,port);
	}
	
	/**
	 * Istanza una connessione TCP attraverso il socket.
	 * @param ip Indirizzo a cui collegarsi.
	 * @param port Porta da utilizzare per la comunicazione.
	 * @return true=connesso, false=disconnesso
	 */
	public boolean connect(String ip, int port){
		try {
			socket = new Socket(ip, port);
            //out = new PrintWriter(socket.getOutputStream(), true);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            /*
            oos.writeObject(new Bundle());
            oos.flush();
            */
            
            connected=true;
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
		}
		
		return connected;
	}
	
	
	/**
	 * Disconnessione forzata.
	 */
	public void disconnect(){
		try {
			send(null);
			connected=false;
			socket.close();
			//out.close();
			//in.close();
			ois.close();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Metodo a basso livello che invia un Bundle, come oggetto, attraverso lo stream del socket.
	 * Il metodo ha un'accesso sincronizzato allo stream, in modo da evitare interferenze.
	 * Si causa quindi un'inevitabile race-condition sul socket, ma è preferibile rispetto ai conflitti e 
	 * alla perdita di consistenza dei dati.
	 * 
	 * @param bundleToSend il bundle da inviare.
	 * @return true=bundle inviato<br>false=bundle non inviato
	 */
	public boolean send(Bundle bundleToSend){
		if(connected){
			try {
				synchronized (oos) { //Accesso sincronizzato allo stream, per evitare conflitti
					oos.writeObject(bundleToSend);
					oos.flush();	
				}
				Log.i("MDTN", "Scrittura su stream eseguita.");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Metodo di controllo che ritorna lo stato della connessione.
	 * @return true=connesso<br>false=non connesso
	 */
	public boolean isConnected(){
		return connected;
	}
	//TODO Fare i metodi di Invio e ricezione di Messaggi e di object(i bundle!). 
	//Serviranno classi Thread che tengono ricevono i dati e li inviano
	

}
