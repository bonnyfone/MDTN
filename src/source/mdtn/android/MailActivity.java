package source.mdtn.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MailActivity extends Activity {
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        TextView textview = new TextView(this);
	        textview.setText("This is the MAIL tab");
	        setContentView(textview);
	    }
}
