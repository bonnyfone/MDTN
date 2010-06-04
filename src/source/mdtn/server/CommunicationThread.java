package source.mdtn.server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import source.mdtn.bundle.Bundle;

public class CommunicationThread extends Thread {
	private Socket socket = null;
	private int id;
	private Vector<CommunicationThread> other;
	PrintWriter out;
	private Server myOwner;
	
	public CommunicationThread(Server myOwner, Vector<CommunicationThread> altri,int n,Socket socket) {
		super("MDTN:CommunicationThread");
		setDaemon(true);
		this.socket = socket;
		this.id=n;
		this.other=altri;
		this.myOwner=myOwner;
	}
	
	public void send(String s){
		out.println(s);
	}

	public void run() {

		try{
		Object datain;
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		while ((datain = in.readObject()) != null) {
			System.out.println("Obj ricevuto");
			myOwner.addLog("Received(id="+id+"): "+((Bundle)datain).getPrimary().getCreationTimestamp());
		
		}
		

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
}
