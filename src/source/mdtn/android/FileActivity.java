package source.mdtn.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import source.mdtn.comm.BundleNode;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FileActivity extends Activity {

	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;

	/** Componenti grafici */
	private ListView myList;

	
	public FileActivity(){

	}

	public FileActivity(BundleNode refNode){
		this.refNode = refNode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout_files);

		Button b = (Button)findViewById(R.id.request);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Log.i("MDTN", "click!!!!");
			}
		});
		
		//lista delle persone che la listview visualizzerà
		final ArrayList<Person> personList=new ArrayList<Person>(); 


		final Person [] people={
				new Person("Anna","Falchi",R.drawable.androidok),
				new Person("Cameron", "Diaz", R.drawable.androiddeveloperminidroid),
				new Person("Jessica","Alba",R.drawable.androiddownload),
				new Person("Manuela","Arcuri",R.drawable.androiderr)};

		//riempimento casuale della lista delle persone
		Random r=new Random();
		for(int i=0;i<10;i++){
			personList.add(people[r.nextInt(people.length)]);
		}
		
		//Questa è la lista che rappresenta la sorgente dei dati della listview
		//ogni elemento è una mappa(chiave->valore)
		final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

		for(int i=0;i<personList.size();i++){
			Person p=personList.get(i);// per ogni persona all'inteno della ditta

			HashMap<String,Object> personMap=new HashMap<String, Object>();//creiamo una mappa di valori

			personMap.put("image", p.getPhotoRes()); // per la chiave image, inseriamo la risorsa dell immagine
			personMap.put("name", p.getName()); // per la chiave name,l'informazine sul nome
			personMap.put("surname", p.getSurname());// per la chiave surnaname, l'informazione sul cognome
			data.add(personMap);  //aggiungiamo la mappa di valori alla sorgente dati
			
		}


		String[] from={"image","name","surname"}; //dai valori contenuti in queste chiavi
		int[] to={R.id.personImage,R.id.personName,R.id.personSurname};//agli id delle view

		//costruzione dell adapter
		final SimpleAdapter adapter=new SimpleAdapter(
				getApplicationContext(),
				data,//sorgente dati
				R.layout.row, //layout contenente gli id di "to"
				from,
				to);

		//utilizzo dell'adapter
		myList = (ListView)findViewById(R.id.mylist);
		
		OnItemClickListener listlistener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView parent, View arg1, int position,long arg3) {
				//Toast.makeText(getApplicationContext(), “You have clicked on ” + ((Order)parent.getItemAtPosition(position)).getOrderName(), Toast.LENGTH_SHORT).show();
				Log.i("MDTN", "Cliccato su "+position);
				myList.setSelection(position);
				
			}
		};
		myList.setOnItemClickListener(listlistener);
		myList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.i("MDTN", "Cliccato LUNGO su "+arg2 + " "+arg3+"item collegato-> "+personList.get(arg2).getSurname());
				return false;
			}
		});
		QUAAAAAAAAA
		
		//myList.setBackgroundColor(0xFF999999);
		//myList.setFocusable(true);
		//myList.setFocusableInTouchMode(true);
		//myList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//myList.setChoiceMode(ListView.CHOICE_MODE_NONE);
		myList.setAdapter(adapter);
		
		
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
		//t.start();
	}

	private class Person {
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
	}


}


