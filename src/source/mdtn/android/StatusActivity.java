package source.mdtn.android;


import source.mdtn.comm.BundleNode;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StatusActivity extends Activity {

	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;

	/** Componenti grafici **/
	private Button _buttonConnect;
	private Button _buttonDisconnect;
	private Button _buttonSend;
	private TextView _labelStat;
	private TextView _labelLogs;
	private TextView _wifistate;
	private EditText _txtIp;
	private ProgressDialog progres;

	/**Servizio wifi di sistema*/
	private WifiManager wifiManager;

	/** Contatore dei log*/
	int lastLog;

	/** Contatore dei toast-message */
	int toastCounter;

	private String ip;

	/** Costruttore di default */
	public StatusActivity(){lastLog=0;}

	/** Costruttore avanzato che costruisce l'attività associandola al servizio MDTN.
	 * @param refNode riferimento al BundleNode che rappresenta il nodo della comunicazione MDTN.
	 */
	public StatusActivity(BundleNode refNode){
		this.refNode = refNode;
		this.lastLog = 0;

		if(this.refNode == null)
			Log.i("MDTN", "Check costruttore: refNode NULLO");
		else
			Log.i("MDTN", "Check costruttore: refNode ESISTE");

		Log.i("MDTN", "Indirizzo"+this);
	}

	public void setBundleNode(BundleNode myNode){
		this.refNode=myNode;
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

		//HELLO_ID+1;
		notificationManager.notify(toastCounter, notification);
		toastCounter++;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout_status);
		//Get dei componenti grafici
		_buttonConnect = (Button) findViewById(R.id.connect);
		_buttonDisconnect = (Button) findViewById(R.id.disconnect);
		_buttonSend = (Button) findViewById(R.id.send);
		_labelStat = (TextView) findViewById(R.id.stat);
		_labelLogs = (TextView) findViewById(R.id.logs);
		_wifistate = (TextView) findViewById(R.id.wifistate);
		_txtIp = (EditText) findViewById(R.id.ip);

		refNode = MainActivity.getServiceBundleNode();

		wifiManager= (WifiManager) this.getSystemService(Context.WIFI_SERVICE);



		//Listener pulsante connessione
		_buttonConnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("MDTN", "Indirizzo dal listener"+this);

				if(refNode == null){
					Log.i("MDTN", "Bundle node è nullo!");
					return;
				}
				if(!refNode.getMyAgent().isConnected()){//Se non sono già connesso..

					//Avvia tentativo di connessione
					refNode.addLog("Tentativo di connessione a MDTN...");

					//Mostra barra di caricamento
					progres = ProgressDialog.show(StatusActivity.this, "", 
							"Connecting...", true);
					progres.setCancelable(true);
					//progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					//progres.setIcon(R.drawable.ic_tab_artists_white);
					//progres.setTitle("Connessione");
					progres.show();

					ip =  _txtIp.getEditableText().toString();
					if(ip.equals(""))ip="10.0.2.2";

					Log.i("MDTN", "Avvio Thread di connessione...");

					//Thread dedicato alla connessione
					Thread connector = new Thread(){
						public void run(){
							//Richiama il BPAgent per effettuare la connessione
							refNode.getMyAgent().connectToService(ip);
							try {sleep(900);} catch (InterruptedException e) {}
							progres.cancel();
						}
					};
					connector.start();

					/*try {

					} catch (IOException e) {
						e.printStackTrace();
					}
					 */

				}
			}
		});

		//Listener pulsante disconnessione
		_buttonDisconnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(refNode.getMyAgent().isConnected()){//Se sono connesso..
					refNode.getMyAgent().disconnectFromService();
				}
			}
		});

		//Listener pulsante invio
		_buttonSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(refNode.getMyAgent().isConnected()){//Se sono connesso..
					source.mdtn.bundle.Bundle newBundle = new source.mdtn.bundle.Bundle();
					newBundle.getPayload().setType("DISCOVERY");
					refNode.getMyAgent().sendBundle(newBundle);
					refNode.addLog("Bundle inviato ("+newBundle.getPrimary().getCreationTimestamp()+")");

					//WIFI on?
					//final String mytest = Settings.System.getString(getContentResolver(), Settings.System.WIFI_ON);

					//refNode.addLog(mytest);
				}
			}
		});



		/**
		 * Thread di supporto che aggiorna l'UI con messaggi di notifica e log.
		 * Effettua il monitoraggio dei log del BundleNode.
		 */
		Thread checkStatus = new Thread(){
			public void run(){

				while(true){
					try {
						Runnable updateStat = new Runnable(){
							public void run() {
								//Wifi check

								if(wifiManager.isWifiEnabled()){
									WifiInfo info=wifiManager.getConnectionInfo();

									if(info.getSSID()==null){
										_wifistate.setText("Attivo, non connesso.");
										_wifistate.setTextColor(0xFFFFFF00);
									}
									else if(info.getSSID()!=null) {
										_wifistate.setText("Connesso a "+info.getSSID());
										_wifistate.setTextColor(0xFF00FF00);
									}
											

								}else{
									_wifistate.setText("Spento.");
									_wifistate.setTextColor(0xFFFF0000);
								}



								boolean stat=refNode.getMyAgent().isConnected();

								//Stato della connessione
								if(stat){
									_labelStat.setTextColor(0xFF00FF00);
									_labelStat.setText("Connesso");
								}
								else{
									_labelStat.setTextColor(0xFFFF0000);
									_labelStat.setText("Disconnesso");
								}

								String logList = "";
								int limit = refNode.getLogs().size();
								for(int i=lastLog; i<limit ;i++){
									String newLog = refNode.getLogs().elementAt(i);
									logList=refNode.getLogs().elementAt(i) + "\n" + logList;

									//Se c'è un report, aggiungi notifica "toast"
									if(newLog.substring(8).trim().startsWith("REPORT")){

										addToast("MDTN: notifica", newLog.substring(17), true, true, true);	
									}


								}

								lastLog=limit;
								logList += _labelLogs.getText().toString();
								_labelLogs.setText(logList);
							}
						};
						runOnUiThread(updateStat);

						sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		};

		checkStatus.start();


	}

}
