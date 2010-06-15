package source.mdtn.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.parserapplications.SiteCapturer;


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
	
	////////////////////////////////////////////////////////////////////////////
	// Program: copyURL.java
	// Author: Anil Hemrajani (anil@patriot.net)
	// Purpose: Utility for copying files from the Internet to local disk
	// Example: 1. java copyURL http://www.patriot.net/users/anil/resume/resume.gif
	//	          2. java copyURL http://www.ibm.com/index.html abcd.html
	////////////////////////////////////////////////////////////////////////////

	public static void downloadFile(String savingPath,URL url)
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
			int oneChar, count=0;
			while ((oneChar=is.read()) != -1)
			{
				fos.write(oneChar);
				count++;
			}
			is.close();
			fos.close();
			System.out.println(count + " byte(s) copied");
		}
		catch (MalformedURLException e)
		{ System.err.println(e.toString()); }
		catch (IOException e)
		{ System.err.println(e.toString()); }
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



