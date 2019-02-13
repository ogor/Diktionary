/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static DatabaseHelper dbHelper;
    static boolean databaseOpened=false;
    SearchView searchView;

    android.support.v4.widget.SimpleCursorAdapter suggestionAdapter;

    // setup recycler view
    ArrayList<History> histories;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    // setup the layout
    RelativeLayout relativeLayout_emptyHistory;
    Cursor cursorHistory;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("Activity Main", "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Log.d("Activity Main", "onCreate: ");
        Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);


        searchView = findViewById(R.id.search_view);

        // click listener for searchview
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);

                // used to test UI
                //Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                //startActivity(intent);
            }
        });

        dbHelper = new DatabaseHelper(this);

        if (dbHelper.checkDatabase()){
            openDatabase();
        }
        else{
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }


        // code for suggestion
        final String[] from = new String[]{"en_word"};
        final int[] to = new int[] {R.id.tv_suggest};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from , to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.changeCursor(cursor);
            }
        };

        searchView.setSuggestionsAdapter(suggestionAdapter);


        // suggestion listener
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                // add clicked text to search box
                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);

                String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                searchView.setQuery(clicked_word, false);

                searchView.clearFocus();
                searchView.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle ();
                bundle.putString("en_word", clicked_word);
                intent.putExtras(bundle);

                startActivity(intent);


                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String text = searchView.getQuery().toString();

                // test for apostrophe or single quote to prevent improper SQL query termination
                Pattern pattern = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher matcher = pattern.matcher(text);

                if (matcher.matches()) {

                    Cursor cursor = dbHelper.getMeaning(text);

                    // incase suggestion or meanings are empty or none
                    if (cursor.getCount() == 0){
                        // means returned empty
                        showAlertDialog();
                    } // end if
                    else{
                        searchView.clearFocus(); // hide kb
                        searchView.setFocusable(false);

                        Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("en_word", text);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } // end else

                } // end if matches
                else{
                    showAlertDialog();
                }


                return false;
            } // end method onQueryTextSubmit

            @Override
            public boolean onQueryTextChange(String s) {
                searchView.setIconifiedByDefault(false); // add margins on suggestion list

                // test for apostrophe or single quote to prevent improper SQL query termination
                Pattern pattern = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher matcher = pattern.matcher(s);

                if (matcher.matches()) {
                    Cursor cursorSuggestion = dbHelper.getSuggestions(s);
                    suggestionAdapter.changeCursor(cursorSuggestion);
                }
                return false;

            }
        });

        // operation for relativeLayout, recycler view
        relativeLayout_emptyHistory = findViewById(R.id.relLayout_emptyHistory);

        recyclerView = findViewById(R.id.recyclerView_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        // connect recycler to layoutmanager
        recyclerView.setLayoutManager(layoutManager);

        fetch_history();




    } // end method oncreate

    private void fetch_history() {
        histories = new ArrayList<>();
        historyAdapter = new RecyclerAdapterHistory(this, histories);
        recyclerView.setAdapter(historyAdapter);

        History history;

        if (databaseOpened){
            cursorHistory = dbHelper.getHistory();

            if (cursorHistory.moveToFirst()){
                do {
                    history = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),
                            cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                    histories.add(history);
                }while (cursorHistory.moveToNext());
            } // end if

            historyAdapter.notifyDataSetChanged();

            if (historyAdapter.getItemCount() == 0){
                relativeLayout_emptyHistory.setVisibility(View.VISIBLE);
            } // end if
            else{
                relativeLayout_emptyHistory.setVisibility(View.GONE);
            }
        } // end if
    } // end method

    private void showAlertDialog(){
        searchView.setQuery("", false);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogAlertTheme );
        builder.setTitle("Word not found!\n");
        builder.setMessage("Please search again!");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing happens

                // next show dialog
            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                searchView.clearFocus(); // hide kboard
            }
        });

        AlertDialog dialog = builder.create();

        // display dialog
        dialog.show();
    }

    protected  static void openDatabase() {
        try{
            dbHelper.openDatabase();
            databaseOpened=true;
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    } // method

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle action item clicks
        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } // end if

        if (id == R.id.action_exit){
            System.exit(0);
            return true;
        } // end if
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch_history();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce)
            super.onBackPressed();

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        // run handler for 2secs
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    } // end method onBackPressed
} // end class
