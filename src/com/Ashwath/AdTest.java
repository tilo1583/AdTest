package com.Ashwath;

import java.util.regex.*;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.test.db.JokesDatabaseHelper;
import com.test.db.JokesDatabaseHelper.Sources;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AdTest extends Activity {
    private static final String MY_AD_UNIT_ID = "a14e25e167bba07";
    private static final String DISPLAY_TEXT_STR = "DisplayText";
    
    //connection to DB
    JokesDatabaseHelper dbHelper = null;
    
    private static int jokes_count;
    private static int insults_count;
    private static Sources db_source;
    private static String displayText;
    
    public void onSaveInstanceState (Bundle outState)
    {
    	 // Save UI state changes to the savedInstanceState.
    	 // This bundle will be passed to onCreate if the process is
    	 // killed and restarted.
    	 outState.putString(DISPLAY_TEXT_STR, displayText);
    	 super.onSaveInstanceState(outState);
    }
    
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

		if(savedInstanceState == null)
		{
			displayJokeorInsult(Sources.JOKES_DB);
		}
		else
		{
			displayText = savedInstanceState.getString(DISPLAY_TEXT_STR);
			display(displayText);
		}
        
        final Button button1 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	displayJokeorInsult(Sources.JOKES_DB);               
            }
        });
        
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.incrementProgressBy(1);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mid = seekBar.getMax() / 2;
				if(seekBar.getProgress() <= mid)
				{
					seekBar.setProgress(0);
					db_source = Sources.INSULTS_DB;
				}
				else
				{
					seekBar.setProgress(seekBar.getMax());
					db_source = Sources.JOKES_DB;
				}
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				int mid = seekBar.getMax() / 2;
				if(seekBar.getProgress() <= mid)
				{
					seekBar.setProgress(0);
					db_source = Sources.INSULTS_DB;
				}
				else
				{
					seekBar.setProgress(seekBar.getMax());
					db_source = Sources.JOKES_DB;
				}
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	private void displayJokeorInsult(Sources _source)
    {
    	Sources source = db_source;
    	String curJokeOrInsult = dbHelper.fetchJokeorInsult(getRandomNumber(source),source);
    	//String curJokeOrInsult = dbHelper.fetchJokeorInsult(1988 ,source);
    	displayText = massageDataWithNewLineChars(curJokeOrInsult);
    	display(displayText);
    }

	private void display(String text) {
    	TextView textView = (TextView)findViewById(R.id.serif);
    	textView.scrollTo(0, 0);
    	textView.setMovementMethod(new ScrollingMovementMethod());
    	textView.setText(text);
	}
   
    public static String massageDataWithNewLineChars(String data)
	{	
    	// The db has '\n' characters. When retrieving from the db, the getString
    	// thinks that it needs to retain the '\' and so adds an additional escape 
    	// char to it, thus making it '\\n'. So we have *two* '\' to escape when
    	// parsing the string in the code here.
		data = data.replaceAll("\\\\n"," ");	
		// Below logic found online at 
		// http://www.exampledepot.com/egs/java.util.regex/RemDupSpace.html
		// Original DB has arbitrary \n's which means arbitrary spaces. So, we need
		// to remove duplicate spaces.
        String patternStr = "\\s+";
        String replaceStr = " ";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(data);
        data = matcher.replaceAll(replaceStr);
        return data;
	}
    
    private int getRandomNumber(Sources source)
    {
    	double rand = Math.random();
    	int randInt = (int) (rand * ((source==Sources.JOKES_DB)?jokes_count:insults_count));
    	return randInt;
    }
}