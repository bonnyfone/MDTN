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
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

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
	private SimpleAdapter adapterLocal;
	private SimpleAdapter adapterRemote;
	private SimpleAdapter adapterPublic;

	/** Numero che indica quale lista è attualmente in uso <br> 0=local, 1=remote, 2=public*/
	private int selector;

	/** Componenti grafici */
	private ListView myList;
	private TextView myLabelList;
	private EditText myURL;

	public FileActivity(){}
	public FileActivity(BundleNode refNode){this.refNode = refNode;}

	/** Metodo che aggiorna la lista delle risorse locali */
	private void updateLocalRes(){
		/* ----------------- CODICE DI TESTING --------------------*/
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

			resourceMap.put("image", R.drawable.androidok); // per la chiave image, inseriamo la risorsa dell immagine
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

			resourceMap.put("image", R.drawable.androiddownload); // per la chiave image, inseriamo la risorsa dell immagine
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

	}

	/** Metodo che aggiorna tutte le liste */
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


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout_files);
		refNode = MainActivity.getServiceBundleNode();
		//bind oggetti grafici
		myList = (ListView)findViewById(R.id.mylist);
		myLabelList = (TextView)findViewById(R.id.labellist);
		myURL = (EditText)findViewById(R.id.address);

		//Imposta, di default, la visualizzazione della lista locale.
		selector=0;

		//Inizializzo le liste
		localRes = new ArrayList<GenericResource>();
		remoteRes = new ArrayList<GenericResource>();
		publicRes = new ArrayList<GenericResource>();
		updateAllRes();
		//updateUI();

		//Pulsante per l'invio di richieste
		Button sendRequest = (Button)findViewById(R.id.request);
		sendRequest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("MDTN", "click!!!!");
				URL toDownload;
				try {
					toDownload = new URL(myURL.getText().toString());
					GenericResource newRequest = new GenericResource(toDownload);
					
					//TODO: modifica per testare public
					newRequest.setAsPublic();

					if(refNode.getMyAgent().isConnected()){//Se sono connesso..
						refNode.getMyAgent().sendRequestForResource(newRequest);
					}

				} catch (MalformedURLException e) {
					Log.i("MDTN","URL non valido");
				}

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
				//progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				//progres.setIcon(R.drawable.ic_tab_artists_white);
				//progres.setTitle("Connessione");
				progres.show();
				
				Thread synchro =new Thread(){
					public void run(){
						refNode.getMyAgent().requestList();
						
						while(!refNode.isResourceUpdated()){
							try {
								sleep(1500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
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
				selector=1;
				updateUI();
			}
		});





		/*OnItemClickListener listlistener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View arg1, int position,long arg3) {
				//Toast.makeText(getApplicationContext(), “You have clicked on ” + ((Order)parent.getItemAtPosition(position)).getOrderName(), Toast.LENGTH_SHORT).show();
				Log.i("MDTN", "Cliccato su "+position);
				//myList.setSelection(position);
			}
		};
		myList.setOnItemClickListener(listlistener);*/

		//Listener per catturare il long-click in modalità touch
		myList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if(selector==0) handleLocal(arg2);
				else if(selector==1) handleRemote(arg2);
				else if(selector==2) handlePublic(arg2);

				/*
				final CharSequence ele[] = {"Download","Elimina"};
				Log.i("MDTN", "Cliccato LUNGO su "+arg2 + " "+arg3+"item collegato-> DA SISTEMARE");

				final CharSequence[] items = {"Scarica", "Elimina", "Aggiorna"};

				AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
				builder.setTitle("Cosa vuoi fare?");
				builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				        dialog.cancel();
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				 */
				return false;
			}
		});

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
						sel.setInfo(refNode.getMyAgent().getMyIp());
						Log.i("MDTN", refNode.getMyAgent().getMyIp());
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
										// TODO Auto-generated catch block
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
				else if(item==1){//Elimina
					if(refNode.getMyAgent().isConnected()){
						refNode.getMyAgent().deleteResource(sel);
						//TODO eliminare i file remoti
					}
					else
						Toast.makeText(getApplicationContext(), "Non sei connesso al servizo MDTN.\nControlla lo stato della connessione su Status.", Toast.LENGTH_SHORT).show();
				}
				else if(item==2){//Aggiorna
					updateLocalRes();		
					updateUI();
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

	//TODO
	public void handlePublic(int id){

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
		Intent notificationIntent = new Intent(this, MainActivity.class);
		
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
//		notificationManager.notify(toastCounter, notification);
//		toastCounter++;
	}
}


