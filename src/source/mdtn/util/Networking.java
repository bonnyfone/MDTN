package source.mdtn.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Classe di utilità che fornisce metodi statici per ottenere informazioni sulle periferiche e sugli indirizzi di rete.
 */
public class Networking {

	/**
	 * Metodo che rileva i dispositivi di rete disponibili (e i relativi ip) 
	 * 
	 * @return un vettore di stringhe contenente gli ip e i relativi dispositivi associati
	 * presenti sulla macchina in uso.
	 * @throws SocketException
	 */
	public static Vector<String> getNetworkAddresses() throws SocketException {
		Vector<String> networkAddresses = new Vector<String>();
		for (Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces(); netInterfaces.hasMoreElements();) {
			NetworkInterface netInterface = netInterfaces.nextElement();
			for (Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); addresses.hasMoreElements();) {
				InetAddress address = addresses.nextElement();
			    if (address instanceof Inet4Address) {
			    	//netInterface.getName()
			    	networkAddresses.add("  ("+netInterface.getName()+")  "+address.getHostAddress()+"  ");//salva gli indirizzi di tutte le interfacce di rete
			    }
			}
		}
		return networkAddresses;
	}
	
	
	/**
	 * Metodo che estrae l'ip da una stringa complessa rappresentante Dispositivo+Ip 
	 * 
	 * @param complexAddress è una stringa contenente un'indirizzo ip e altri caratteri superflui.
	 * @return una stringa contenente l'indirizzo ip puro.
	 */
	public static String extractIp(String complexAddress){
		if(complexAddress.equals(""))return "Error";
		
		int myFlag=-1;
		for(int i = 0; i< complexAddress.length(); i++){
			if(complexAddress.charAt(i) == ')'){
				myFlag=i;
				break;
			}		
		}
		String myResult = complexAddress.substring(myFlag+1);
		//Elimino caratteri di spaziatura inutili
		return myResult.trim();
	}
	
	
	
	/**
	 * Metodo che controlla la disponibilità della connessione Internet.
	 * @return true=connesso ad Internet<br>false=non connesso ad Internet.
	 */
	public static boolean checkInternetConnection(){
		try {
			InetAddress myInet = InetAddress.getByName("google.com"); 
			//System.out.println(myInet.getHostAddress()+ "    "+myInet.isReachable(1000));
			return myInet.isReachable(1000);
			
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
}
