/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        TextView tv_clearHistory = findViewById(R.id.tv_clearHistory);
        TextView tv_about = findViewById(R.id.tv_about);

        tv_clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper = new DatabaseHelper(SettingsActivity.this);
                try{
                    dbHelper.openDatabase();
                } catch (SQLException e){
                    e.printStackTrace();
                } // end catch

                showAlertDialog();
            } // end mathod onclick
        });

        // create click listener for about text
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.DialogAlertTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("All histories will be deleted.");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteHistory();
                    }
                });
        String negativeText = "No";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();

        // display dialog
        dialog.show();
    } // end method showAlertDialog

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
