package com.test.db;

//Test

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
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

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_NAME = "JokesandInsults.db";
    private static final int DATABASE_VERSION = 2;
    
    private static final String JOKES_TABLE_NAME = "jokes";
    
    private static final String COL_JOKEID = "joke_id";
    private static final String COL_CATEGORY = "category";
    private static final String COL_JOKE = "joke";
    
    private static final String JOKES_SOURCE_LOC = "jokes.sql";
    private static final String INSULTS_SOURCE_LOC = "insults.sql";

	/**
	 * Jokes Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "CREATE TABLE "+JOKES_TABLE_NAME+" ("+
				COL_JOKEID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_CATEGORY+ " TEXT ," +
				COL_JOKE+ " TEXT)";

	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		enum Sources{
			JOKES_DB, INSULTS_DB
		}
		
		private final Context mCtx_inner;
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mCtx_inner=context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
			populateDB(db, Sources.JOKES_DB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+JOKES_TABLE_NAME);
			onCreate(db);
		}
		
		private void populateDB(SQLiteDatabase db, Sources source){
			
			final String SOURCE_LOC = (source==Sources.JOKES_DB)?JOKES_SOURCE_LOC:INSULTS_SOURCE_LOC;		
			Log.v("Reached****************************","*******************************");
			
			AssetManager am = mCtx_inner.getAssets();				
			try {
				InputStream is = am.open(SOURCE_LOC);
				DataInputStream in = new DataInputStream(is);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String line;

			    while ((line = br.readLine()) != null) {
			    	db.execSQL(line);
			    }
				Log.v("FInished****************************","*******************************");
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

	public Cursor fetchAllJokes() {
		return null;
	}
	
	public int JokesCount() throws SQLException {
		
		int count = 0;
		Cursor c = mDb.rawQuery("SELECT COUNT(1)" + " FROM " + JOKES_TABLE_NAME, null);
    
		if (c != null ) {
			c.moveToFirst();		          
			count = c.getInt(c.getColumnIndex(c.getColumnName(0)));         
		}
		return count;
	}

	public String fetchJoke(int random_id) throws SQLException {
		String joke = null;
		Cursor c = mDb.rawQuery("SELECT " + COL_JOKE + " FROM " +
                JOKES_TABLE_NAME +" where "+ COL_JOKEID +"="+random_id, null);
    
		if (c != null ) {
			c.moveToFirst();		          
			joke = c.getString(c.getColumnIndex(COL_JOKE));         
		}
		return joke;
	}
}