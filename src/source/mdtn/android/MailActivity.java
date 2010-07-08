package source.mdtn.android;

import source.mdtn.comm.BundleNode;
import source.mdtn.util.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MailActivity extends Activity {

	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;

	/** Componenti grafici **/
	private EditText _from;
	private EditText _to;
	private EditText _subject;
	private EditText _message;
	private Button _send;
	private CheckBox _cc;

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
		_cc = (CheckBox)findViewById(R.id.check);
		
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("MDTN", 0);
		
		String oldFrom = settings.getString("from","mia@mail.it");
		_from.setText(oldFrom);
		
		String oldTo = settings.getString("to","mobiledtn@gmail.com");
		_to.setText(oldTo);
		
		String oldObj = settings.getString("obj","My message!");
		_subject.setText(oldObj);
		
		String oldMess = settings.getString("mess","Hi, this is my message!");
		_message.setText(oldMess);
		
		
		
		//Crea i dialog personalizzati
		AlertDialog.Builder builder;

		/* Messaggio OK */
		//Context myCont = getParent().getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root));
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Email inoltrata con successo.");
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.androidok);

		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               
	           }});
		final AlertDialog alertOk = builder.create();
		
		/* Messaggio ERRORE */
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.custom_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root));
		text = (TextView) layout.findViewById(R.id.text);
		text.setText("Errore inoltro email.");
		image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.androiderr);

		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               
	           }});
		final AlertDialog alertErr = builder.create();	
		/*
		//Vari alertDialog per segnalare l'esito delle richieste di invio mail
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("  Email inoltrata con successo.  ")
		       .setCancelable(true)
		       .setIcon(R.drawable.androidok)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               
		           }
		       });
		
		final AlertDialog alertOk = builder.create(); //Messaggio OK
		
		builder.setMessage(" Errore durante inoltro email.  ")
	       .setCancelable(true)
	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               
	           }
	       });
		
		final AlertDialog alertErr = builder.create(); //Messaggio Errore
		*/
		
		/* Handler che riceve le indicazione sul tipo di messaggio da visualizzare */
		final Handler handler = new Handler() {  
			public void handleMessage(android.os.Message msg) {  
				if(msg.what == 1)
						alertOk.show();
				else
						alertErr.show();
			   }
			}; 

		
		//Listener per controllare la validità dell'email
		_from.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if(Message.checkEmail(_from.getText().toString()))
					_from.setBackgroundResource(android.R.drawable.editbox_background);
				else
					_from.setBackgroundColor(0xFFFF0000);//rosso

			}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		}); 
		
		_to.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if(Message.checkEmail(_to.getText().toString()))
					_to.setBackgroundResource(android.R.drawable.editbox_background);
				else
					_to.setBackgroundColor(0xFFFF0000);//rosso

			}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		}); 

		//Listener pulsante invio
		_send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(refNode.getMyAgent().isConnected()){//Se sono connesso..
					
					//Disabilita momentaneamente pulsante di invio email.
					Runnable disableSend = new Runnable(){
						public void run() {
							_send.setEnabled(false);
						}
					};
					runOnUiThread(disableSend);
					
					
					String raw[]={_from.getText().toString(),_to.getText().toString(),
							_subject.getText().toString(),_message.getText().toString()};
					
					
					final Message myMessage = new Message(raw[0],raw[1],raw[2],raw[3],_cc.isChecked());
			
					//Invio l'email, tramite thread dedicato. (mostro i messaggi di notifica tramite handler)
					Thread worker = new Thread(){
						public void run(){
							if(refNode.getMyAgent().sendEmail(myMessage)){
								handler.sendEmptyMessage(1);
							}
							else{
								handler.sendEmptyMessage(0);
							}
							//Riabilita pulsante di invio email.
							Runnable enableSend = new Runnable(){
								public void run() {
									_send.setEnabled(true);
								}
							};
							runOnUiThread(enableSend);
							
						}
					};

					worker.start();

				}
			}
		});

	}
	
    @Override
    protected void onStop(){
       super.onStop();

      //Salvataggio preferenze
      SharedPreferences settings = getSharedPreferences("MDTN", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString("from",_from.getText().toString());
      editor.putString("to",_to.getText().toString());
      editor.putString("obj",_subject.getText().toString());
      editor.putString("mess",_message.getText().toString());
      
      editor.commit();
    }
}
