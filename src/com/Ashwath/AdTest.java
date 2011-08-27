package com.Ashwath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.*;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.test.db.JokesDatabaseHelper;
import com.test.db.JokesDatabaseHelper.Sources;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class AdTest extends Activity {
	Facebook facebook = new Facebook("273107596035037");
	private static boolean isFBAuthorized = false;

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
    	 // [TODO] save the isFBauthorized as well?
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
        
		Configuration config = getResources().getConfiguration();
	    ImageView img = (ImageView)findViewById(R.id.imageView1);
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
	    {
			img.setVisibility(ImageView.INVISIBLE);
	    }
		else
		{
			img.setVisibility(ImageView.VISIBLE);
		}

		//get the total number of jokes/insults in the database
		jokes_count= dbHelper.countRows(Sources.JOKES_DB);
		insults_count= dbHelper.countRows(Sources.INSULTS_DB);
		db_source = Sources.INSULTS_DB;

		if(savedInstanceState == null)
		{
			displayJokeorInsult(db_source);
		}
		else
		{
			displayText = savedInstanceState.getString(DISPLAY_TEXT_STR);
			display(displayText);
		}
        
        final Button button1 = (Button) findViewById(R.id.nextButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	displayJokeorInsult(Sources.JOKES_DB);               
            }
        });
        
        final Button shareButton = (Button) findViewById(R.id.twitter_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent share = new Intent(Intent.ACTION_SEND);
            	share.setType("text/plain");
            	//share.putExtra(Intent.EXTRA_STREAM, displayText);
            	share.putExtra(Intent.EXTRA_TEXT, displayText + " via Rude Joker!");
            	startActivity(Intent.createChooser(share, "Share it!"));               
            }
        });
        
        final Button fbButton = (Button) findViewById(R.id.fb_button);
        fbButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	authorizeFB();
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
    
    private void authorizeFB()
    {
//    	 if(isFBAuthorized == false)
    	 if(!facebook.isSessionValid())
    	 {
	      	 facebook.authorize(this, new String[]{"publish_stream"}, new DialogListener() {
	             @Override
	             public void onComplete(Bundle values) {
	            	 //facebook.setAccessToken(values.getString(Facebook.TOKEN));
	            	 updateStatus(values.getString(Facebook.TOKEN));
	            	 Log.d("onComplete","");
	             }
	
	             @Override
	             public void onFacebookError(FacebookError error) {
	            	 Log.d("onFBError","");
	             }
	
	             @Override
	             public void onError(DialogError e) {
	            	 Log.d("onError","");
	             }
	
	             @Override
	             public void onCancel() {
	            	 Log.d("onCancel","");
	             }
	      	 });
//	      	 isFBAuthorized = true;
    	 }
    	 else
    	 {
    		 updateStatus(null);
    	 }
    }
    
    //updating FB Status
    public void updateStatus(String accessToken){
    	//try 
    	{
			Bundle bundle = new Bundle();
			//bundle.putString("method", "auth.expireSession"); 
			//'message' tells facebook that you're updating your status
			bundle.putString("message", displayText); 
			//bundle.putString(Facebook.TOKEN,accessToken);
			//tells facebook that you're performing this action on the authenticated users wall, thus 
//			it becomes an update. POST tells that the method being used is POST
			//String response = facebook.request("me/feed",bundle,"POST");
			//Toast.makeText(AdTest.this, "Posting to Your Wall...", Toast.LENGTH_SHORT).show();
			//Log.d("UPDATE RESPONSE",""+response);
			bundle.putString("link", "http://www.facebook.com/apps/application.php?id=273107596035037");
			bundle.putString("name", "Rude Joker!");
			bundle.putString("caption", displayText);
			facebook.dialog(this,"feed", bundle,

	        	      new DialogListener() {
	        	           @Override
	        	           public void onComplete(Bundle values) {
	        	        	   Toast.makeText(AdTest.this, "Posting to Your Wall...", Toast.LENGTH_SHORT).show();
	        	               Log.d("onComplete","");
	        	           }

	        	           @Override
	        	           public void onFacebookError(FacebookError error) {
	        	        	   Log.d("onFBError","");
	        	           }

	        	           @Override
	        	           public void onError(DialogError e) {
	        	        	   Log.d("onError","");
	        	           }

	        	           @Override
	        	           public void onCancel() {
	        	        	   Log.d("onCancel","");
	        	           }
	        	      }
	        	);
			Log.d("UPDATE RESPONSE","");			
		} 
    	/*catch (MalformedURLException e) {
			Log.e("MALFORMED URL",""+e.getMessage());
		} catch (IOException e) {
			Log.e("IOEX",""+e.getMessage());
		} */
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
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