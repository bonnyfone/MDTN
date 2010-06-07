package source.mdtn.android;


import source.mdtn.comm.BundleNode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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
	private EditText _txtIp;
	private ProgressDialog progres;

	/** Costruttore di default */
	public StatusActivity(){}

	/** Costruttore avanzato che costruisce l'attività associandola al servizio MDTN.
	 * @param refNode riferimento al BundleNode che rappresenta il nodo della comunicazione MDTN.
	 */
	public StatusActivity(BundleNode refNode){
		this.refNode = refNode;

		if(this.refNode == null)
			Log.i("MDTN", "Check costruttore: refNode NULLO");
		else
			Log.i("MDTN", "Check costruttore: refNode ESISTE");

		Log.i("MDTN", "Indirizzo"+this);
	}

	public void setBundleNode(BundleNode myNode){
		this.refNode=myNode;
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
		_txtIp = (EditText) findViewById(R.id.ip);

		refNode = MainActivity.getServiceBundleNode();


		//Listener pulsante connessione
		_buttonConnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("MDTN", "Indirizzo dal listener"+this);
				
				if(refNode == null){
					Log.i("MDTN", "Bundle node è nullo!");
					return;
				}
				if(!refNode.getMyAgent().isConnected()){//Se non sono già connesso..
					refNode.addLog("Tentativo di connessione a MDTN...");
					progres = ProgressDialog.show(StatusActivity.this, "", 
							"Connecting...", true);
					progres.show();

					String ip = _txtIp.getEditableText().toString();
					if(ip.equals(""))ip="10.0.2.2";

					Log.i("MDTN", "Avvio Thread di connessione...");

					refNode.getMyAgent().connectToService(ip);

					progres.cancel();
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
					refNode.getMyAgent().sendBundle(newBundle);
					refNode.addLog("Bundle inviato ("+newBundle.getPrimary().getCreationTimestamp()+")");
				}
			}
		});


		Thread checkStatus = new Thread(){
			public void run(){
				while(true){
					try {
						Runnable updateStat = new Runnable(){
							public void run() {
								boolean stat=refNode.getMyAgent().isConnected();

								if(stat){
									_labelStat.setTextColor(0xFF00FF00);
									_labelStat.setText("CONNECTED");
								}
								else{
									_labelStat.setTextColor(0xFFFF0000);
									_labelStat.setText("DISCONNECTED");
								}
								
								String logList="";
								for(int i=0;i<refNode.getLogs().size();i++){
									logList=refNode.getLogs().elementAt(i) + "\n" + logList;
								}
								
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
