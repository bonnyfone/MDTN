package source.mdtn.android;

import source.mdtn.comm.BundleNode;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FileActivity extends Activity {
	
	/** Componente fondamentale che rappresenta un nodo di comunicazione DTN */
	private BundleNode refNode;
	
	public FileActivity(){

	}

	public FileActivity(BundleNode refNode){
		this.refNode = refNode;
	}
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        TextView textview = new TextView(this);
	        textview.setText("This is the FileGet tab");
	        setContentView(textview);
	    }
}
