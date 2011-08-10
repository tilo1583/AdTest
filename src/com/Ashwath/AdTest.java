package com.Ashwath;

import java.util.ArrayList;
import java.util.List;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.test.db.JokesDatabaseHelper;
import com.test.db.JokesDatabaseHelper.Sources;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdTest extends Activity {
    private static final String MY_AD_UNIT_ID = "a14e25e167bba07";
    
    //connection to DB
    JokesDatabaseHelper dbHelper = null;
    
    private static int jokes_count;
    private static int insults_count;
     
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);        
	    
		//set the current context to helper class
		dbHelper = new JokesDatabaseHelper(this);
		//open the connection and load jokes
		dbHelper.open();
		
		setContentView(R.layout.main);
        
		//get the total number of jokes/insults in the database
		jokes_count= dbHelper.countRows(Sources.JOKES_DB);
		insults_count= dbHelper.countRows(Sources.INSULTS_DB);
        initializeAd();
        displayJokeorInsult(Sources.JOKES_DB);        
        
        final Button button1 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	displayJokeorInsult(Sources.JOKES_DB);               
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.button4);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	displayJokeorInsult(Sources.INSULTS_DB);               
            }
        });
    }

    private void displayJokeorInsult(Sources source)
    {
    	TextView textView = (TextView)findViewById(R.id.serif);    	    	
    	textView.setText(massageDataWithNewLineChars(dbHelper.fetchJokeorInsult(getRandomNumber(source),source)));
    }
   
    public static String massageDataWithNewLineChars(String data)
	{
		data = data.replaceAll("\\\\r","");
		data = data.replaceAll("\\\\n","\n");
				
		return data;
	}
    
    private int getRandomNumber(Sources source)
    {
    	double rand = Math.random();
    	int randInt = (int) (rand * ((source==Sources.JOKES_DB)?jokes_count:insults_count));
    	return randInt;
    }
    
	private void initializeAd() {
		AdView adView = new AdView(this, AdSize.BANNER, "a14e25e167bba07");
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        // Add the adView to it
        layout.addView(adView);
        // Initiate a generic request to load it with an ad
     		
        adView.loadAd(new AdRequest());
	}	
}