package source.mdtn.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StatusActivity extends Activity {
	
	int n;
	
	public StatusActivity(){

	}

	public StatusActivity(int x){
		setN(x);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView textview = new TextView(this);
		textview.setText("This is the Status tab "+n);
		setContentView(textview);
		//setContentView(R.layout.tab_statuts);
		
	}


	public void setN(int x){
		n=x;
		Log.d("MYTAB", "Valore di n="+n);
	}

}
