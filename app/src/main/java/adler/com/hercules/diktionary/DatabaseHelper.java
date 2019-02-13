/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "eng_dictionary.db";
    private SQLiteDatabase myDatabase;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("path 1", DB_PATH);
    } // end method

    private void copyDatabase() throws IOException{
        InputStream input = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream output = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;

        while((length = input.read(buffer)) > 0){
            output.write(buffer, 0, length);
        } // end while

        output.flush();
        output.close();
        input.close();

        Log.i("copyDatabase", "Database copied");

    } // end method copyDatabase


    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();

        if (!dbExist){
            this.getReadableDatabase();

            try{
                copyDatabase();
            } catch (IOException e){
                throw new Error("Error copying database");
            }
        } // end if
    }

    public void openDatabase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if (myDatabase != null)
            myDatabase.close();
        super.close(); // from SQLiteOPenHelper
    }

    public boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e){

        }

        if (checkDB != null){
            checkDB.close();
        } // end if

        return checkDB != null ? true : false;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            this.getReadableDatabase();
            context.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public Cursor getMeaning(String text){
        Cursor cursor = myDatabase.rawQuery("SELECT en_definition, example, synonyms, antonyms FROM words WHERE en_word==UPPER('"+
                text + "')", null);

        return  cursor;
    } // end method

    public Cursor getSuggestions( String text){

        Cursor cursor = myDatabase.rawQuery("SELECT _id, en_word FROM words WHERE en_word LIKE '" + text + "%' LIMIT 40", null);

        return cursor;

    } // end method

    public void insertHistory(String text){
        myDatabase.execSQL("INSERT INTO history(word) VALUES(UPPER('"+ text +"'))");
    } // end method

    public Cursor getHistory(){
        Cursor cursor = myDatabase.rawQuery("SELECT DISTINCT word, en_definition FROM history h JOIN words w ON h.word==en_word ORDER BY h._id DESC",
                null);

        return  cursor;
    }

    public void deleteHistory(){
        myDatabase.execSQL("DELETE FROM history");
    }
} // end class
