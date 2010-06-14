package source.mdtn.android;

import java.net.URI;
import java.net.URISyntaxException;

import source.mdtn.comm.BundleNode;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends TabActivity {
	
	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private static BundleNode myNode;
	
	public static BundleNode getServiceBundleNode(){
		return myNode;
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    //----------------------CODICE DI TESTING ---------------
	    URI x=null;
		try {
			TelephonyManager tele = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
			String imei = tele.getDeviceId();
			
			x = new URI("dtn://"+imei);
			myNode = new BundleNode(x);
			
			/*
			if(myNode.getMyAgent().connectToService("10.0.2.2"))
				Log.i("CONN", "COLLEGATO!");
			else
				Log.i("CONN", "ERRORE DI COLLEGAMENTO!");
			*/
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //----------------------FINE CODICE DI TESTING ---------------
		
		/* Tutte le attività condividono un unico componente comune, ovvero un oggetto di tipo BundleNode,
		 * il quale incapsula l'intero funzionamento del servizio MDTN al suo interno. 
		 * In questo modo, la componente logica è totalmente indipendende da quella grafica e dalla piattaforma
		 * Android, il che permette lo sviluppo futuro di svariati client su diverse piattaforme usando questo
		 * core comune.
		 */
		
	    /** InfoActivity: informazioni generali */
	    InfoActivity info = new InfoActivity();
	    intent = new Intent().setClass(this,info.getClass());
	    spec = tabHost.newTabSpec("info").setIndicator("Info",
                res.getDrawable(R.drawable.ic_tab_status)) 
            .setContent(intent);  

	    tabHost.addTab(spec);
	    
	    /** StatusActivity: gestione della connessione al servizio */
	    //StatusActivity status = new StatusActivity(myNode);
	    StatusActivity status = new StatusActivity();
	    intent = new Intent().setClass(this,status.getClass());
	    spec = tabHost.newTabSpec("status").setIndicator("Status",
	                      res.getDrawable(R.drawable.ic_tab_status)) 
	                  .setContent(intent);  
	    tabHost.addTab(spec);
 
	    /** MailActivity: gestione servizio mail */
	    MailActivity mail = new MailActivity(myNode);
	    intent = new Intent().setClass(this,mail.getClass());
	    spec = tabHost.newTabSpec("mail").setIndicator("E-mail",
	    				res.getDrawable(R.drawable.ic_tab_albums)) //res.getDrawable(R.drawable.ic_tab_albums))
	                  .setContent(intent);
	    
	    tabHost.addTab(spec); 

	    /** FileActivity: gestione servizio richiesta file e risorse */
	    FileActivity file = new FileActivity(myNode);
	    intent = new Intent().setClass(this,file.getClass());
	    spec = tabHost.newTabSpec("files").setIndicator("Files",
	    				res.getDrawable(R.drawable.ic_tab_songs)) //res.getDrawable(R.drawable.ic_tab_songs))
	                  .setContent(intent);

	    tabHost.addTab(spec); 

	    tabHost.setCurrentTab(1);
	    
	  //tabHost.setCurrentTabByTag("Status");  

	}
	
//	public void onContentChanged(){
//        Context context = getApplicationContext();
//        CharSequence text = "Tab switched";
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//	}
	
	
	
	/*  Gestione MENU */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, 0, 0, "Exit")
		.setIcon(R.drawable.icon);

		return result;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0: //Mostra messaggio: Vuoi uscire? SI,NO
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to exit?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MainActivity.this.finish();
					myNode.getMyAgent().disconnectFromService();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}