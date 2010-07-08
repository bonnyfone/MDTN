package source.mdtn.android;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import source.mdtn.comm.BundleNode;
import source.mdtn.util.GenericResource;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Classe grafica che rappresenta l'attività di gestione delle risorse (richiesta nuove risorse, download 
 * e gestione). Viene richiamata dalla MainActivity all'interno di una TabView.
 *
 */
public class FileActivity extends Activity {

	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;

	/** Liste delle risorse locali (modello)*/
	private ArrayList<GenericResource> localRes;

	/** Lista delle risorse remote (modello)*/
	private ArrayList<GenericResource> remoteRes;

	/** Lista delle risorse pubbliche (modello)*/
	private ArrayList<GenericResource> publicRes;

	/** Adapters per le liste */
	
	/**Adapters per la lista delle risorse locali.*/
	private SimpleAdapter adapterLocal;
	
	/**Adapters per la lista delle risorse remote.*/
	private SimpleAdapter adapterRemote;
	
	/**Adapters per la lista delle risorse pubbliche.*/
	private SimpleAdapter adapterPublic;

	/** Numero che indica quale lista è attualmente in uso <br> 0=local, 1=remote, 2=public*/
	private int selector;

	/** Componenti grafici */
	
	/** ListView contentente le varie liste delle risorse.*/
	private ListView myList;
	
	/** Etichetta che contiene il nome della lista visualizzata.*/
	private TextView myLabelList;
	
	/** Stringa contenente l'ultima richiesta inoltrata (preferenze) */
	private String oldRequest;

	//Costruttori java-like
	public FileActivity(){}
	public FileActivity(BundleNode refNode){this.refNode = refNode;}

	/** Metodo che aggiorna la lista delle risorse locali */
	private void updateLocalRes(){
		
		localRes.clear();
		File SDCardRoot = Environment.getExternalStorageDirectory(); 
		File dir = new File(SDCardRoot+"/MDTN_data/"); 

		//Ottengo la lista dei file, filtrata opportunamente per estensione.
		String[] children = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.toLowerCase().endsWith(".tmp");
			}
		});

		//carico i file nel modello
		for(int i=0; i<children.length; i++){
			GenericResource gr = new GenericResource(dir.toString(),children[i]);
			gr.autoGetSize(dir.toString()+"/"+children[i]);
			localRes.add(gr);
		}


		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<localRes.size();i++){
			GenericResource newRes=localRes.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> resourceMap=new HashMap<String, Object>();//creiamo una mappa di valori

			resourceMap.put("image", R.drawable.file); // per la chiave image, inseriamo la risorsa dell immagine
			resourceMap.put("name", newRes.getName()); // per la chiave name,l'informazine sul nome
			resourceMap.put("surname", newRes.getAddress() + "\n("+newRes.getSize()/1024+" kb)");// per la chiave surnaname, l'informazione sul cognome
			data.add(resourceMap);  //aggiungiamo la mappa di valori alla sorgente dati

		}

		String[] from={"image","name","surname"}; //dai valori contenuti in queste chiavi
		int[] to={R.id.personImage,R.id.personName,R.id.personSurname};//agli id delle view

		//costruzione dell adapter
		adapterLocal=new SimpleAdapter(
				getApplicationContext(),
				data,//sorgente dati
				R.layout.row, //layout contenente gli id di "to"
				from,
				to);
	}

	/** Metodo che aggiorna la lista delle risorse remote */
	private void updateRemoteRes(){

		remoteRes.clear();

		for(int i=0; i<refNode.getRemoteRes().size(); i++)
			remoteRes.add(refNode.getRemoteRes().elementAt(i));


		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<remoteRes.size();i++){
			GenericResource newRes=remoteRes.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> resourceMap=new HashMap<String, Object>();//creiamo una mappa di valori

			resourceMap.put("image", R.drawable.download); // per la chiave image, inseriamo la risorsa dell immagine
			resourceMap.put("name", newRes.getName()); // per la chiave name,l'informazine sul nome
			resourceMap.put("surname", newRes.getAddress()+"\n("+newRes.getSize()/1024+" kb)");// per la chiave surnaname, l'informazione sul cognome
			data.add(resourceMap);  //aggiungiamo la mappa di valori alla sorgente dati

		}

		String[] from={"image","name","surname"}; //dai valori contenuti in queste chiavi
		int[] to={R.id.personImage,R.id.personName,R.id.personSurname};//agli id delle view

		//costruzione dell adapter
		adapterRemote=new SimpleAdapter(
				getApplicationContext(),
				data,//sorgente dati
				R.layout.row, //layout contenente gli id di "to"
				from,
				to);
	}

	/** Metodo che aggiorna la lista delle risorse pubbliche */
	private void updatePublicRes(){

		publicRes.clear();

		for(int i=0; i<refNode.getPublicRes().size(); i++)
			publicRes.add(refNode.getPublicRes().elementAt(i));


		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<publicRes.size();i++){
			GenericResource newRes=publicRes.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> resourceMap=new HashMap<String, Object>();//creiamo una mappa di valori

			resourceMap.put("image", R.drawable.download_public); // per la chiave image, inseriamo la risorsa dell immagine
			resourceMap.put("name", newRes.getName()); // per la chiave name,l'informazine sul nome
			resourceMap.put("surname", newRes.getAddress()+"\n("+newRes.getSize()/1024+" kb)");// per la chiave surnaname, l'informazione sul cognome
			data.add(resourceMap);  //aggiungiamo la mappa di valori alla sorgente dati

		}

		String[] from={"image","name","surname"}; //dai valori contenuti in queste chiavi
		int[] to={R.id.personImage,R.id.personName,R.id.personSurname};//agli id delle view

		//costruzione dell adapter
		adapterPublic=new SimpleAdapter(
				getApplicationContext(),
				data,//sorgente dati
				R.layout.row, //layout contenente gli id di "to"
				from,
				to);
	}

	/** Metodo che aggiorna tutte le liste delle risorse.*/
	private void updateAllRes(){
		updateLocalRes();
		updateRemoteRes();
		updatePublicRes();
	}

	/** Metodo che aggiorna la visualizzazione grafica in base alle impostazioni del selector.*/
	public void updateUI(){
		switch(selector){
		case(0): 
			myList.setAdapter(adapterLocal);
		myLabelList.setText("Risorse locali");
		adapterLocal.notifyDataSetChanged();
		break;

		case(1):
			myList.setAdapter(adapterRemote);
		myLabelList.setText("Risorse remote");	
		adapterRemote.notifyDataSetChanged();
		break;

		case(2):
			myList.setAdapter(adapterPublic);
		myLabelList.setText("Bacheca pubblica");
		adapterPublic.notifyDataSetChanged();
		break;
		}
	}

	/**
	 * Metodo che richiama il box di richiesta per una nuova risorsa.
	 */
	public void askNewAddress(){
		
		//Oggetti grafici e layout
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.request_dialog,
				(ViewGroup) findViewById(R.id.layout_root));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.androiddownload);

		final EditText input = (EditText)layout.findViewById(R.id.text);
		input.setText(oldRequest);
		
		//Messaggio di conferma:pubblico o no?
		final AlertDialog.Builder publicOrNo = new AlertDialog.Builder(this);
		publicOrNo.setTitle("Bacheca pubblica");
		publicOrNo.setMessage("Vuoi rendere pubblica questa risorsa?");
		
		publicOrNo.setPositiveButton(" Si ", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				String value = input.getText().toString();  
				URL toDownload;
				try {
					toDownload = new URL(value);
					GenericResource newRequest = new GenericResource(toDownload);
					newRequest.setAsPublic();
					oldRequest = value;
					if(refNode.getMyAgent().isConnected()){//Se sono connesso..
						if(refNode.getMyAgent().sendRequestForResource(newRequest))
							Toast.makeText(getApplicationContext(), "La richiesta è stata inoltrata.\nVerrai notificato non appena la risorsa sarà disponibile.", Toast.LENGTH_LONG).show();
						else
							Toast.makeText(getApplicationContext(), "Impossibile inoltrare la richiesta.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();
						return;
					}

				} catch (MalformedURLException e) {
					Log.i("MDTN","URL non valido");
				}

			}  
		});

		publicOrNo.setNegativeButton("No", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				String value = input.getText().toString();  
				URL toDownload;
				try {
					oldRequest = value;
					toDownload = new URL(value);
					GenericResource newRequest = new GenericResource(toDownload);
					if(refNode.getMyAgent().isConnected()){//Se sono connesso..
						if(refNode.getMyAgent().sendRequestForResource(newRequest))
							Toast.makeText(getApplicationContext(), "Richiesta inoltrata.\nVerrai notificato non appena il file sarà disponibile.", Toast.LENGTH_LONG).show();
						else
							Toast.makeText(getApplicationContext(), "Impossibile inoltrare la richiesta.\nControlla lo stato della connessione su Status.", Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();
						return;
					}

				} catch (MalformedURLException e) {
					Log.i("MDTN","URL non valido");
				}

			}  
		});
		
		//Messaggio di richiesta indirizzo risorsa
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);  
		alert.setTitle("Richiedi risorsa");  
		alert.setMessage("Inserisci l'url della risorsa:");  

		alert.setView(layout);

		alert.setPositiveButton("Invia", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
					publicOrNo.show();
			}  
		});

		alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				return;
			}  
		});  

		alert.show(); 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout_files);
		refNode = MainActivity.getServiceBundleNode();
		//bind oggetti grafici
		myList = (ListView)findViewById(R.id.mylist);
		myLabelList = (TextView)findViewById(R.id.labellist);
		//myURL = (EditText)findViewById(R.id.address);
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("MDTN", 0);
		oldRequest = settings.getString("resource","http://mdtn.altervista.org/ex.pdf");
		
		//Imposta, di default, la visualizzazione della lista locale.
		selector=0;

		//Inizializzo le liste
		localRes = new ArrayList<GenericResource>();
		remoteRes = new ArrayList<GenericResource>();
		publicRes = new ArrayList<GenericResource>();
		updateAllRes();
		//updateUI();


		//Pulsante per l'invio di richieste
		ImageButton sendRequest = (ImageButton)findViewById(R.id.request);
		sendRequest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				askNewAddress();

			}
		});

		/* Pulsanti per lo switching da una lista all'altra */
		final ImageButton switchToLocal  = (ImageButton)findViewById(R.id.local);
		final ImageButton switchToRemote = (ImageButton)findViewById(R.id.remote);
		final ImageButton switchToPublic = (ImageButton)findViewById(R.id.bacheca);
		final ImageButton refresh = (ImageButton)findViewById(R.id.refresh);

		refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!refNode.getMyAgent().isConnected()){
					Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();
					return;
				}
				final ProgressDialog 	progres = ProgressDialog.show(FileActivity.this, "", 
						"Sincronizzazione...", true);
				progres.setCancelable(true);
				progres.show();

				//Avvio thread di monitoraggio
				Thread synchro =new Thread(){
					public void run(){
						refNode.getMyAgent().requestList();

						while(!refNode.isResourceUpdated()){
							try {
								sleep(1500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						progres.cancel();
						updateAllRes();
						Runnable update = new Runnable() {
							public void run() {updateUI();	}
						};
						runOnUiThread(update);

					}

				};
				synchro.start();
			}
		});

		switchToLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateLocalRes();
				selector=0;
				updateUI(); 
			}
		});

		switchToRemote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateRemoteRes();
				selector=1;
				updateUI();
			}
		});


		switchToPublic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updatePublicRes();
				selector=2;
				updateUI();
			}
		});


		//Listener per catturare il long-click in modalità touch
		myList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if(selector==0) handleLocal(arg2);
				else if(selector==1) handleRemote(arg2);
				else if(selector==2) handlePublic(arg2);

				return false;
			}
		});

		updateUI();
	}
	
    @Override
    protected void onStop(){
       super.onStop();

      //Salvataggio preferenze
      SharedPreferences settings = getSharedPreferences("MDTN", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString("resource",oldRequest);

      
      editor.commit();
    }

	/**
	 * Metodo interno per la gestione delle operazioni sulle risorse locali
	 * @param id indice della risorsa selezionata.
	 */
	private void handleLocal(int id){
		//Safe check
		if(id > localRes.size())return;
		final GenericResource sel= localRes.get(id);

		Log.i("MDTN", "Cliccato LUNGO su "+id + " ,item collegato-> "+sel.getName());
		final CharSequence[] items = {"Elimina","Aggiorna"};

		AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
		builder.setTitle(sel.getName());
		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

				if(item==0){//Elimina il file
					File del= new File(sel.getAddress());
					if(del.delete())
						Toast.makeText(getApplicationContext(), "Il file " +sel.getName()+ " è stato eliminato.", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(getApplicationContext(), "ERRORE: Impossibile eliminare il file "+sel.getName(), Toast.LENGTH_SHORT).show();
				}
				else if(item==1){//Aggiorna
					updateLocalRes();		
					updateUI();
				}

				Log.i("MDTN","Selezionato: "+item);
				dialog.cancel();

				//Aggiorna la lista
				updateLocalRes();		
				updateUI();

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


	/**
	 * Metodo interno per la gestione delle operazioni sulle risorse Remote.
	 * @param id indice della risorsa selezionata.
	 */
	public void handleRemote(int id){
		//Safe check
		if(id > remoteRes.size())return;
		final GenericResource sel= remoteRes.get(id);

		Log.i("MDTN", "Cliccato LUNGO su "+id + " ,item collegato-> "+sel.getName());
		final CharSequence[] items = {"Download","Elimina","Aggiorna"};

		//Creo il menu contestuale
		AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
		builder.setTitle(sel.getName());
		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if(item==0){//Download
					if(refNode.getMyAgent().isConnected()){
						if(!refNode.getMyAgent().getDataFinished()){
							Toast.makeText(getApplicationContext(), "Stai già scaricando un file.\nAttendi il termine del download prima di richiedere un nuovo file.", Toast.LENGTH_LONG).show();
							return;
						}
						refNode.getMyAgent().downloadResource(sel);
						Toast.makeText(getApplicationContext(), "Download del file in corso...", Toast.LENGTH_SHORT).show();

						//Monitora il download attraverso uno status message.
						Thread monitor = new Thread(){
							public void run(){
								while(!refNode.getMyAgent().getDataFinished()){
									long actual=refNode.getMyAgent().getDataReceived();
									long perc = actual * 100/ sel.getSize(); 
									addToast("MDTN downloading..."+perc+"%","Download: "+actual/1024 +" kb ("+perc+" %)", false, false, false);
									try {
										sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								addToast("MDTN download completato","Scaricato : "+sel.getName() +" ("+refNode.getMyAgent().getDataReceived()/1024+" kb)", true, true, true);
							}
						};
						monitor.start();
					}
					else
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_LONG).show();

				}
				else if(item==1){//Elimina
					if(refNode.getMyAgent().isConnected()){
						if(refNode.getMyAgent().deleteResource(sel))
							Toast.makeText(getApplicationContext(), "Richiesta di eliminazione inoltrata.\nRiesegui la sincronia per verificare i cambiamenti.", Toast.LENGTH_LONG).show();
						else
							Toast.makeText(getApplicationContext(), "Impossibile inoltrare la richiesta.\nControlla lo stato della connessione su Status.", Toast.LENGTH_LONG).show();
						//TODO eliminare i file remoti
					}
					else
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_LONG).show();
				}
				else if(item==2){//Aggiorna
					/*
					 * updateRemoteRes();
					 * updateUI();		
					 */
				}

				Log.i("MDTN","Selezionato: "+item);
				dialog.cancel();

				//Aggiorna la lista
				updateRemoteRes();		
				updateUI();

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Metodo interno per la gestione delle operazioni sulle risorse Pubbliche.
	 * @param id indice della risorsa selezionata.
	 */
	public void handlePublic(int id){
		//Safe check
		if(id > publicRes.size())return;
		final GenericResource sel= publicRes.get(id);

		Log.i("MDTN", "Cliccato LUNGO su "+id + " ,item collegato-> "+sel.getName());
		final CharSequence[] items = {"Download","Aggiorna"};

		//Creo il menu contestuale
		AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
		builder.setTitle(sel.getName());
		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if(item==0){//Download
					if(refNode.getMyAgent().isConnected()){
						if(!refNode.getMyAgent().getDataFinished()){
							Toast.makeText(getApplicationContext(), "Stai già scaricando un file.\nAttendi il termine del download prima di richiedere un nuovo file.", Toast.LENGTH_LONG).show();
							return;
						}
						refNode.getMyAgent().downloadResource(sel);
						Toast.makeText(getApplicationContext(), "Download del file in corso...", Toast.LENGTH_SHORT).show();

						//Monitora il download attraverso uno status message.
						Thread monitor = new Thread(){
							public void run(){
								while(!refNode.getMyAgent().getDataFinished()){
									long actual=refNode.getMyAgent().getDataReceived();
									long perc = actual * 100/ sel.getSize(); 
									addToast("MDTN downloading..."+perc+"%","Download: "+actual/1024 +" kb ("+perc+" %)", false, false, false);
									try {
										sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								addToast("MDTN download completato","Scaricato : "+sel.getName() +" ("+refNode.getMyAgent().getDataReceived()/1024+" kb)", true, true, true);
								
							}
						};
						monitor.start();
					}
					else
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();

				}

				else if(item==1){//Aggiorna
					/*
					 * updatePublicRes();
					 * updateUI();
					 * (fatte comunque in chiusura)		
					 */
				}

				Log.i("MDTN","Selezionato: "+item);
				dialog.cancel();

				//Aggiorna la lista
				updatePublicRes();		
				updateUI();

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


	/**
	 * Metodo grafico che aggiunge una messaggio di notifica alla barra delle notifiche di Android.
	 * @param title titolo del messaggio.
	 * @param message corpo del messaggio.
	 * @param vibration abilita vibrazione.
	 * @param light abilita segnale luminoso.
	 * @param sound abilita suono.
	 */
	private void addToast(String title, String message, boolean vibration, boolean light, boolean sound){
		//Ottengo il notification manager
		//Intent notificationIntent = new Intent(this, MainActivity.class);

		//Cliccando sulla notidica, mi riporta all'istanza del programma precedentemente avviata.
		//Volendo, si può ottenere un altro comportamento.
		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, this.getParent().getIntent(), 0);
		//		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		CharSequence tickerText = title;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		if(sound)
			notification.defaults |= Notification.DEFAULT_SOUND;

		if(light){
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}

		if(vibration){
			//notification.defaults |= Notification.DEFAULT_VIBRATE;
			long[] vibrate = {0,100,200,300};
			notification.vibrate = vibrate;
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Context context = getApplicationContext();
		CharSequence contentTitle = title;
		CharSequence contentText = message;

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		notificationManager.notify(99, notification);

	}
}


