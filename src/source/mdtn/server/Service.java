package source.mdtn.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
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
	public static String SendMail(String from, String to, String subject, String message) throws IOException{

		String result="";

		//Crea la query string NON quotata
		String composeUrl = "//www.mdtn.altervista.org/mailservice.php?k=" + key +
		"&from="+from+
		"&to="+to+
		"&sub="+subject+
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
				remote.add(new GenericResource(Server.getServerEID().toString(), children[i]));		
			}
		}
		
		//TODO Raccoglie info bacheca pubblica
		Vector<GenericResource> publics = new Vector<GenericResource>();
		publics.add(new GenericResource("./", "public.zip"));

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
	 * 
	 * Metodo che cattura un intero sito web e lo rende fruibile localmente.<br>
	 * <b>ANCORA NON FUNZIONANTE!!</b>
	 * @param address indirizzo del sito web.
	 */
	public static void captureSite(final String address){
		SiteCapturer s = new SiteCapturer();
		
		s.setSource(address);
		s.setTarget(address);
		/*NodeFilter filter = new NodeFilter() {
			
			@Override
			public boolean accept(Node arg0) {
				// TODO Auto-generated method stub
				if(arg0.getPage().getUrl().equals(address))
					return true;
				
				return false;
			}
		};
		s.setFilter(filter);
		*/
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
	
	public static void uploadFile(Bundle toBeProcessed){
        Socket s;
		try {
	        //Individua il file
	        GenericResource res = (GenericResource)Buffering.toObject(toBeProcessed.getPayload().getPayloadData());
	        String EID = toBeProcessed.getPrimary().getSource().getHost();
	        String ip = res.getInfo();
	        String path = Server.getDataPath() + EID + "/" + res.getName();
	     
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
		}
	}
	

	/**
	 * Metodo che scarica un determinato file da internet.
	 * @param savingPath percorso in cui salvare il file.
	 * @param url URL della risorsa da scaricare.
	 */
	public static boolean downloadFile(String savingPath,URL url)
	{
		String path = url.toString();
		
		/*
		if (args.length < 1)
		{
			System.err.println
			("usage: java copyURL URL [LocalFile]");
			System.exit(1);
		}
		*/
		try
		{
			System.out.println("Opening connection to " + path + "...");
			URLConnection urlC = url.openConnection();
			// Copy resource to local file, use remote file
			// if no local file name specified
			InputStream is = url.openStream();
			// Print info about resource
			System.out.print("Copying resource (type: " +
					urlC.getContentType());
			Date date=new Date(urlC.getLastModified());
			System.out.println(", modified on: " +
					date.toLocaleString() + ")...");
			System.out.flush();
			FileOutputStream fos=null;
			/*if (args.length < 2)
			{
				String localFile=null;
				// Get only file name
				StringTokenizer st=new StringTokenizer(url.getFile(), "/");
				while (st.hasMoreTokens())
					localFile=st.nextToken();
				fos = new FileOutputStream(localFile);
			}
			else*/
			String name = "";
			if(url.getFile().equals(null))
				name="index.html";
			else 
				name = url.getFile();
			
			fos = new FileOutputStream(savingPath + name);
			
			byte[] buf = new byte[4096];
			int size = 0;
			int count=0;
			while((size = is.read(buf)) > 0) {
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
			return true;
		}
		catch (MalformedURLException e)
		{ System.err.println(e.toString()); return false;}
		catch (IOException e)
		{ System.err.println(e.toString()); return false;}
	}
	
	public static void main(String args[]){
		String a[]={""};
		//a[0]="http://mdtn.altervista.org";
		a[0]="http://www.google.it/index.html";
		try {
			downloadFile("./",new URL(a[0]));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//a[0]="http://pimpmyearns.altervista.org/";
		//a[0]="http://news.google.it/";
		//captureSite(a[0]);
	}
}



