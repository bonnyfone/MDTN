package source.mdtn.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

import org.htmlparser.parserapplications.SiteCapturer;

import source.mdtn.bundle.Bundle;
import source.mdtn.util.Buffering;
import source.mdtn.util.GenericResource;
import source.mdtn.util.PolledInputStream;
import source.mdtn.util.RealResource;


public class Service {

	/**
	 * key per "autenticazione" minimale allo script remoto.
	 */
	private static final int key = 166;

	/**
	 * Metodo statico per l'invio di una Mail. Le email sono inviate dal webserver www.mdtn.altervista.org.
	 * @param from email mittente.
	 * @param to email destinatario.
	 * @param subject oggetto del messaggio.
	 * @param message corpo del messaggio.
	 * @return la riposta dello script di invio mail.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String SendMail(String from, String to, String subject, String message, boolean cc) throws IOException{

		String sendCc="&cc=";
		if(cc)sendCc += from;
		else sendCc += "null";
		
		String result="";
		

		//Crea la query string NON quotata
		String composeUrl = "//www.mdtn.altervista.org/mailservice.php?k=" + key +
		"&from="+from+
		"&to="+to+
		"&sub="+subject+
		sendCc+
		"&message="+message;

		/* Codifica in querystring quoted */
		URLEncoder.encode(composeUrl, "UTF-8"); //prima, tutto in UTF-8
		URI tmpUri=null;
		try {
			tmpUri = new URI("http",composeUrl,null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "URI creation err";
		} //creo URI per quotare i caratteri speciali
		composeUrl = tmpUri.toString(); //estraggo come stringa

		//System.out.println("\nQuery now: \n"+composeUrl);

		URL url = new URL(composeUrl); //creo URL da aprire
		//InputStream is = url.openStream();

		//Apro connessione
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);

		//Leggo l'esito dello script
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						connection.getInputStream()));

		String decodedString;

		while ((decodedString = in.readLine()) != null) {
			result = result+decodedString+"\n";
			//System.out.println(decodedString);
		}
		in.close();


		//Stampa esito script
		/*
		System.out.println("\n#### Script output ####");
		try {
		  int b;
		  while((b = is.read()) != -1 ){
			  System.out.print((char)b);
		  }

		} finally {
			System.out.println("\n#### end output ####");
		  is.close();
		}
		 */
		//System.out.println(result.trim());
		return result.trim();
	}


	/***
	 * Metodo che ritorna la lista dei file disponibili per un determinato EID.
	 * @param source EID del client.
	 * @return un bundle contenente le informazioni.
	 */
	public static Bundle updateListBundle(URI source){

		//Individuo la certella dedicata a questo EID
		File dir=new File(Server.getDataPath() + source.getHost());

		//Se la cartella esiste, ottengo la lista dei files
		String[] children = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return true;
			}
		});

		//TODO Raccoglie info sui file del client
		Vector<GenericResource> remote = new Vector<GenericResource>();
		if(children != null){
			for(int i=0; i<children.length; i++){
				GenericResource newRes = new GenericResource(Server.getServerEID().toString(), children[i]);
				newRes.autoGetSize(dir+"/"+children[i]);
				remote.add(newRes);		
			}
		}

		//TODO Raccoglie info bacheca pubblica
		Vector<GenericResource> publics = Service.getPublicResourceList();

		//Vector contenitore, racchiude le informazioni sui file propri del client e sui file pubblici.
		Vector<Vector<GenericResource>> data = new Vector<Vector<GenericResource>>();
		data.add(remote);
		data.add(publics);

		Bundle ris = new Bundle();
		ris.getPayload().setType("UPDATE_LIST");
		ris.getPayload().setPayloadData(Buffering.toBytes(data));

		return ris;
	}


	/**
	 * Rimuove un elemento dalla lista delle risorse pubbliche.
	 * @param removePath il path da rimuovere.
	 */
	public static void removePublicResource(String removePath){
		//Leggo il file public.list
		File list = new File(Server.getDataPath()+"public.list");
		if(list.exists()){

			FileReader input;
			try {
				input = new FileReader(list);
				BufferedReader bufRead = new BufferedReader(input);

				String line; 
				int count = 0; 
				String buff="";
				//legge linea per linea
				while ((line = bufRead.readLine()) != null){

					if(!line.equals(removePath))
						buff+=(line+"\n");

					count++;
				}
				bufRead.close();

				//Riscrive il file aggiornato
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(list, false));
					bw.write(buff);
					bw.flush();
					bw.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Aggiunge un path alla lista delle risorse pubbliche
	 * @param newPath
	 */
	public static void  addPublicResource(String newPath){
		//Leggo il file public.list
		File list = new File(Server.getDataPath()+"public.list");
		removePublicResource(newPath);

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(list, true));
			bw.write(newPath);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}


	/**
	 * Metodo che ritorna la lista delle risorse pubbliche.
	 * @return
	 */
	public static Vector<GenericResource> getPublicResourceList(){
		//Vector di ritorno
		Vector<GenericResource> ris= new Vector<GenericResource>();

		//Leggo il file public.list
		File list = new File(Server.getDataPath()+"public.list");
		if(list.exists()){

			FileReader input;
			try {
				input = new FileReader(list);
				BufferedReader bufRead = new BufferedReader(input);

				String line; 
				int count = 0; 

				//legge linea per linea
				while ((line = bufRead.readLine()) != null){

					File newFile = new File(Server.getDataPath()+line);
					//URL dtnUrl = new URL("file://"+line);
					int lim=line.lastIndexOf("/");
					line.substring(0, lim);
					GenericResource newRes = new GenericResource("dtn://"+line.substring(0, lim),line.substring(lim+1, line.length()));
					newRes.autoGetSize(Server.getDataPath()+line);
					newRes.setAsPublic();

					System.out.println(newRes.getAddress()+ " size: "+newRes.getSize());
					ris.add(newRes);

					count++;
				}

				bufRead.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		return ris;
	}

	/**
	 * 
	 * Metodo che cattura un intero sito web e lo rende fruibile localmente.<br>
	 * <b>ANCORA NON FUNZIONANTE!!</b>
	 * @param address indirizzo del sito web.
	 */
	public static void captureSite(final String address){
		SiteCapturer s = new SiteCapturer();

		s.setSource(address);
		s.setTarget(address);

		s.setCaptureResources(true);

		s.capture();
	}

	/**
	 * Metodo che ritorna un bundle contenente la risorsa richiesta.
	 * @param req risorsa richiesta.
	 * @param EID EID del richiedente.
	 * @return un bundle contenente la risorsa.
	 */
	public static Bundle returnResource(GenericResource req, String EID){
		//Cerco il file
		String path="";

		if(req.isPublic()){
			//TODO ritorna una risorsa pubblica..
		}
		else{
			path = Server.getDataPath() + EID + "/" + req.getName();
			System.out.println("DOWNLOAD: path-> "+path);
		}

		Bundle risp = new Bundle();

		risp.getPayload().setType("DOWNLOAD");
		RealResource myFile = new RealResource(path);
		risp.getPayload().setData(Buffering.toBytes(myFile));

		return risp;
	}

	/**
	 * Metodo interno del server per inviare dati al client attraverso uno stream dedicato.
	 * @param toBeProcessed bundle con i parametri per il trasferimento.
	 */
	public static void uploadFile(Bundle toBeProcessed){
		Socket s;
		try {
			//Individua il file
			GenericResource res = (GenericResource)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
			String EID = toBeProcessed.getPrimary().getSource().getHost();
			System.out.println("Ris pubblic: "+res.getAddress());
			String ip = res.getInfo();
			
			String path="";
			
			if(res.isPublic()){
				URI x = new URI(res.getAddress());
				path=Server.getDataPath() + x.getHost() + "/" +res.getName();
			
			}
			else{
				path = Server.getDataPath() + EID + "/" + res.getName();
			}
			
			System.out.println("PATH:" +path);
			System.out.println("IP: " +ip);
			

			s = new Socket(ip, 44444);
			System.out.println("Client connected. Starting dump.");

			FileInputStream fis = new FileInputStream(path);

			//InputStream in = s.getInputStream();
			OutputStream o = s.getOutputStream();

			byte[] buf = new byte[4096];
			int read;
			while( (read=fis.read(buf)) != -1 ){
				o.write(buf, 0, read);
				o.flush();
			}
			o.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Metodo che scarica un determinato file da internet.
	 * @param savingPath percorso in cui salvare il file.
	 * @param url URL della risorsa da scaricare.
	 */
	public static String downloadFile(String savingPath,URL url)
	{
		String path = url.toString();
		try
		{
			System.out.println("Opening connection to " + path + "...");
			HttpURLConnection urlC = (HttpURLConnection) url.openConnection();
			int estimatedSize=urlC.getContentLength();
			
			long max = Server.fileSizeLimit();
			if(max <=0) max= 1024*1024*1024;
			if(estimatedSize> max){
				String tooMuch = "Il file richiesto è troppo grosso (~"+estimatedSize/1024/1024+" mb,"
								+" max "+Server.fileSizeLimit()/1024/1024+" mb)";
				
				return tooMuch;
			}
			
			System.out.println("Size "+estimatedSize);
			urlC.setReadTimeout(1000*10);
			urlC.setConnectTimeout(1000*10);

			final PolledInputStream is = new PolledInputStream(url.openStream(),10000,estimatedSize);

			System.out.print("Copying resource (type: " +
					urlC.getContentType());
			Date date=new Date(urlC.getLastModified());
			System.out.println(", modified on: " +
					date.toLocaleString() + ")...");
			System.out.flush();
			FileOutputStream fos=null;

			String name = "";
			if(url.getFile().equals(null))
				name="index.html";
			else 
				name = url.getFile();

			fos = new FileOutputStream(savingPath + name);

			byte[] buf = new byte[4096];
			int size = 0;
			int count=0;

			while((size = is.read(buf)) >0) {
				fos.write(buf, 0, size);
				count+=size;
			}
			
			/*i
			int oneChar, count=0;
			while ((oneChar=is.read()) != -1)
			{
				fos.write(oneChar);
				count++;
			}
			 */
			is.close();
			fos.close();
			System.out.println(count + " byte(s) copied");
			return "ok";
	}
	catch(FileNotFoundException fe){
		return "La risorsa richiesta non esiste.";
	}
	catch(SocketTimeoutException se){
		System.err.println(se.toString()); return "error: Errore Timeout in connessione/lettura.";
	}
	catch (MalformedURLException e)
	{ System.err.println(e.toString()); return "L'indirizzo non è valido.";}
	catch (IOException e)
	{ System.err.println(e.toString()); return "error: Errore durante il download.";}
}



public static void removeResource(Bundle toProcess){
	//EID client, per individuare la cartella
	String EID = toProcess.getPrimary().getSource().getHost().toString();
	//Risorsa da eliminare
	GenericResource toDelete = (GenericResource)Buffering.toObject((toProcess.getPayload().getPayloadData()));
	System.out.println(EID);
	File f = new File(Server.getDataPath()+EID+"/"+toDelete.getName());
	f.delete();

	Service.removePublicResource(EID+"/"+toDelete.getName());

}


public static void main(String args[]){
	String a[]={""};
	//a[0]="http://mdtn.altervista.org";
	a[0]="http://mdtn.altervista.org/tesi_final.pdf";
	
	try {
		System.out.println(downloadFile("./",new URL(a[0])));
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//a[0]="http://pimpmyearns.altervista.org/";
	//a[0]="http://news.google.it/";
	//captureSite(a[0]);
}
}



