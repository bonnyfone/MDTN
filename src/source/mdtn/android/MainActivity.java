package source.mdtn.android;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;


public class MainActivity extends TabActivity {
		
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    
	    //TODO Creare per primo un oggetto DNTclient, da passare alle varie attività delle tab
	    
	    //mettere sempre il costruttore di Default nelle activity
	    
	    // Create an Intent to launch an Activity for the tab (to be reused)
	   
	    /** InfoActivity */
	    InfoActivity info = new InfoActivity();
	    intent = new Intent().setClass(this,info.getClass());
	    spec = tabHost.newTabSpec("info").setIndicator("Info",
                res.getDrawable(R.drawable.ic_tab_status)) 
            .setContent(intent);  

	    tabHost.addTab(spec);
	    
	    StatusActivity a = new StatusActivity(1111);
	    intent = new Intent().setClass(this,a.getClass());
	    
	    	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("status").setIndicator("Status",
	                      res.getDrawable(R.drawable.ic_tab_status)) 
	                  .setContent(intent);  
	      
	    tabHost.addTab(spec);
	    a.setN(32);
 
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, MailActivity.class);
	    spec = tabHost.newTabSpec("mail").setIndicator("E-mail",
	    				res.getDrawable(R.drawable.ic_tab_albums)) //res.getDrawable(R.drawable.ic_tab_albums))
	                  .setContent(intent);
	    tabHost.addTab(spec); 

	    intent = new Intent().setClass(this, FileActivity.class);
	    spec = tabHost.newTabSpec("files").setIndicator("Files",
	    				res.getDrawable(R.drawable.ic_tab_songs)) //res.getDrawable(R.drawable.ic_tab_songs))
	                  .setContent(intent);
	    tabHost.addTab(spec); 

//	    tabHost.setCurrentTab(0);
	   // tabHost.setCurrentTabByTag("Status");  

	}
	
//	public void onContentChanged(){
//        Context context = getApplicationContext();
//        CharSequence text = "Tab switched";
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//	}

}