package com.test.db;

//Test

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class JokesDatabaseHelper {

	public enum Sources{
		JOKES_DB, INSULTS_DB
	}
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_NAME = "JokesandInsults.db";
    private static final int DATABASE_VERSION = 2;
    
    private static final String JOKES_TABLE_NAME = "jokes";
    private static final String INSULTS_TABLE_NAME = "insults";
    
    private static final String COL_JOKEID = "joke_id";
    private static final String COL_CATEGORY = "category";
    private static final String COL_JOKE = "joke";
    
    private static final String COL_INSULTID = "insult_id";    
    private static final String COL_INSULT = "insult";
    
    private static final String JOKES_SOURCE_LOC = "jokes.sql";
    private static final String INSULTS_SOURCE_LOC = "insults.sql";

	/**
	 * Jokes Database creation sql statement
	 */
	private static final String CREATE_TABLE_JOKES = "CREATE TABLE "+JOKES_TABLE_NAME+" ("+
				COL_JOKEID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_CATEGORY+ " TEXT ," +
				COL_JOKE+ " TEXT)";
	
	private static final String CREATE_TABLE_INSULTS = "CREATE TABLE "+INSULTS_TABLE_NAME+" ("+
				COL_INSULTID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +				
				COL_INSULT+ " TEXT)";

	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
			
		private final Context mCtx_inner;
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mCtx_inner=context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(CREATE_TABLE_JOKES);
			db.execSQL(CREATE_TABLE_INSULTS);
			populateDB(db, Sources.JOKES_DB);
			populateDB(db, Sources.INSULTS_DB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+JOKES_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+INSULTS_TABLE_NAME);
			onCreate(db);
		}
		
		private void populateDB(SQLiteDatabase db, Sources source){
			
			final String SOURCE_LOC = (source==Sources.JOKES_DB)?JOKES_SOURCE_LOC:INSULTS_SOURCE_LOC;		
			Log.v("Loading.."+SOURCE_LOC,"*******************************");
			
			AssetManager am = mCtx_inner.getAssets();				
			try {
				InputStream is = am.open(SOURCE_LOC);
				DataInputStream in = new DataInputStream(is);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String line;

			    while ((line = br.readLine()) != null) {
			    	db.execSQL(line);
			    }
				Log.v("Finished","*******************************");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public JokesDatabaseHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public JokesDatabaseHelper open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
	
	public int countRows(Sources source) throws SQLException {
		
		final String SOURCE_TABLE = (source==Sources.JOKES_DB)?JOKES_TABLE_NAME:INSULTS_TABLE_NAME;
		int count = 0;
		Cursor c = mDb.rawQuery("SELECT COUNT(1)" + " FROM " + SOURCE_TABLE, null);
    
		if (c != null ) {
			c.moveToFirst();		          
			count = c.getInt(c.getColumnIndex(c.getColumnName(0)));         
		}
		return count;
	}

	public String fetchJokeorInsult(int random_id, Sources source) throws SQLException {
		
		final String SOURCE_TABLE = (source==Sources.JOKES_DB)?JOKES_TABLE_NAME:INSULTS_TABLE_NAME;
		final String SELECT_COLUMN = (source==Sources.JOKES_DB)?COL_JOKE:COL_INSULT;
		final String WHERE_COLUMN = (source==Sources.JOKES_DB)?COL_JOKEID:COL_INSULTID;
		
		String result = null;
		
		Cursor c = mDb.rawQuery("SELECT " + SELECT_COLUMN + " FROM " +
				SOURCE_TABLE +" where "+ WHERE_COLUMN +"="+random_id, null);
    
		if (c != null ) {
			c.moveToFirst();		          
			result = c.getString(c.getColumnIndex(SELECT_COLUMN));         
		}
		return result;
	}
}