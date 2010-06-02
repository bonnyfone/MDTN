package source.mdtn.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import source.mdtn.comm.BundleProtocol;

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
				System.out.println("Recived(id="+id+"): "+inputLine);
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
				
				for(int i=0;i<other.size();i++){
					if(!(other.elementAt(i).equals(this)))
						other.elementAt(i).send(inputLine);
				}
				
				if (outputLine.equals("Bye"))
					break;
			}
			
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
		}
	}
}
