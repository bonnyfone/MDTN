package source.mdtn.android;

import source.mdtn.comm.BundleNode;
import source.mdtn.util.Message;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MailActivity extends Activity {

	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;

	/** Componenti grafici **/
	private EditText _from;
	private EditText _to;
	private EditText _subject;
	private EditText _message;
	private Button _send;

	public MailActivity(){

	}

	public MailActivity(BundleNode refNode){
		this.refNode = refNode;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout_mail);
		refNode = MainActivity.getServiceBundleNode();

		_from = (EditText)findViewById(R.id.from);
		_to = (EditText)findViewById(R.id.to);
		_subject = (EditText)findViewById(R.id.subj);
		_message = (EditText)findViewById(R.id.mess);
		_send = (Button)findViewById(R.id.send);

		
		
		//Listener pulsante disconnessione
		_send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(refNode.getMyAgent().isConnected()){//Se sono connesso..
					String raw[]={_from.getText().toString(),_to.getText().toString(),
							      _subject.getText().toString(),_message.getText().toString()};
					final Message myMessage = new Message(raw[0],raw[1],raw[2],raw[3]);
					
					Thread worker = new Thread(){
						public void run(){
							if(refNode.getMyAgent().sendEmail(myMessage)){
								
							}
							else{
								
							}
						}
					};
					worker.start();
					
				}
			}
		});
			
	}
}
