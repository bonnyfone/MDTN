package source.mdtn.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
}
