package source.mdtn.android;

import source.mdtn.comm.BundleNode;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MailActivity extends Activity {
	
	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;
	
	public MailActivity(){

	}

	public MailActivity(BundleNode refNode){
		this.refNode = refNode;
	}
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        refNode = MainActivity.getServiceBundleNode();
	        
	        TextView textview = new TextView(this);
	        textview.setText("This is the MAIL tab");
	        setContentView(textview);
	    }
}
