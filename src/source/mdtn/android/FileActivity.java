package source.mdtn.android;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import source.mdtn.comm.BundleNode;
import source.mdtn.server.Server;
import source.mdtn.util.GenericResource;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	                return true;
	                }
	           });
		
		
			for(int i=0; i<children.length; i++)
				localRes.add(new GenericResource(dir.toString(),children[i]));
			
			/*localRes.add(new GenericResource(new URL("http://mdtn.altervista.org/a.txr")));
			localRes.add(new GenericResource(new URL("http://google.it/fer.pdf")));
			localRes.add(new GenericResource(new URL("http://asasdas.asd/ca.cea")));*/
	

		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<localRes.size();i++){
			GenericResource newRes=localRes.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> resourceMap=new HashMap<String, Object>();//creiamo una mappa di valori

			resourceMap.put("image", R.drawable.androidok); // per la chiave image, inseriamo la risorsa dell immagine
			resourceMap.put("name", newRes.getName()); // per la chiave name,l'informazine sul nome
			resourceMap.put("surname", newRes.getAddress());// per la chiave surnaname, l'informazione sul cognome
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
		/* ----------------- CODICE DI TESTING --------------------*/
		remoteRes.clear();

//		try{
			for(int i=0; i<refNode.getRemoteRes().size(); i++)
				remoteRes.add(refNode.getRemoteRes().elementAt(i));
//			remoteRes.add(new GenericResource(new URL("http://asasdas.asd/ca.cea")));
//			remoteRes.add(new GenericResource(new URL("http://ffasas.asd/ver.txt")));
//			remoteRes.add(new GenericResource(new URL("http://asasdas.asd/Per.pdf")));
//			remoteRes.add(new GenericResource(new URL("http://asasdavs.asd/aaaaa.zip")));
//		}
//		catch (MalformedURLException e) {
//			Log.i("MDTN","URL mal formattato");
//		}

		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<remoteRes.size();i++){
			GenericResource newRes=remoteRes.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> resourceMap=new HashMap<String, Object>();//creiamo una mappa di valori

			resourceMap.put("image", R.drawable.androiddownload); // per la chiave image, inseriamo la risorsa dell immagine
			resourceMap.put("name", newRes.getName()); // per la chiave name,l'informazine sul nome
			resourceMap.put("surname", newRes.getAddress());// per la chiave surnaname, l'informazione sul cognome
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
				refNode.getMyAgent().requestList();
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
				return false;
			}
		});
		//QUAAAAAAAAA
		
		//myList.setBackgroundColor(0xFF999999);
		//myList.setFocusable(true);
		//myList.setFocusableInTouchMode(true);
		//myList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//myList.setChoiceMode(ListView.CHOICE_MODE_NONE);
		//myList.setAdapter(adapter);
		
		/*
		Thread t = new Thread(){
			public void run(){
				while(true){
					personList.add(people[1]);
					HashMap<String,Object> personMap=new HashMap<String, Object>();//creiamo una mappa di valori

					personMap.put("image", people[1].getPhotoRes()); // per la chiave image, inseriamo la risorsa dell immagine
					personMap.put("name", people[1].getName()); // per la chiave name,l'informazine sul nome
					personMap.put("surname", people[1].getSurname());// per la chiave surnaname, l'informazione sul cognome
					data.add(personMap);  //aggiungiamo la mappa di valori alla sorgente dati
					Log.i("MDTN", "aggiunto alla lista!");
					
					Runnable r = new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							Log.i("MDTN", "Selezionato ora: "+myList.getSelectedItemPosition());
						}
					};
					runOnUiThread(r);
					
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		*/
		//t.start();
	}

	/*private class Person {
		private String name;
		private String surname;
		private int photoRes;
		public Person(String name, String surname, int photoRes) {
			super();
			this.name = name;
			this.surname = surname;
			this.photoRes = photoRes;
		}
		public String getName() {
			return name;
		}
		public String getSurname() {
			return surname;
		}
		public int getPhotoRes() {
			return photoRes;
		}
	}*/


}


