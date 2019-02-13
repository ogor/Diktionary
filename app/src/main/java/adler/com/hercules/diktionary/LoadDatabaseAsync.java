/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;

public class LoadDatabaseAsync extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper dbHelper;

    public LoadDatabaseAsync(Context context){
        this.context = context;
    } // end constructor

    @Override
    protected Boolean doInBackground(Void... voids) {
        dbHelper = new DatabaseHelper(context);

        try{
            dbHelper.createDatabase();
        } catch (IOException e){
            throw new Error("Database was not created");
        }

        dbHelper.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        alertDialog.dismiss();
        MainActivity.openDatabase();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder d = new AlertDialog.Builder(context, R.style.DialogAlertTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.database_copying, null);

        d.setTitle("Loading Database... Please wait.");
        d.setView(dialogView);
        alertDialog = d.create();

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
} // end class
