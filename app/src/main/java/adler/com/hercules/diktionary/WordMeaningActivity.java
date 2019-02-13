/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adler.com.hercules.diktionary.Fragments.FragmentAntonyms;
import adler.com.hercules.diktionary.Fragments.FragmentDefinition;
import adler.com.hercules.diktionary.Fragments.FragmentExample;
import adler.com.hercules.diktionary.Fragments.FragmentSynonyms;

public class WordMeaningActivity extends AppCompatActivity {

    private ViewPager viewPager;

    String enWord;
    DatabaseHelper dbHelper;
    Cursor cursor = null;

    public String enDefinition;
    public String example;
    public String synonyms;
    public String antonyms;


    TextToSpeech textToSpeech;

    boolean startedFromShare = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        // received values from mainActivity
        Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString("en_word");

        dbHelper = new DatabaseHelper(this);

        try{
            dbHelper.openDatabase();
        }catch (SQLException sqle){
            throw sqle;
        } // end catch

        cursor = dbHelper.getMeaning(enWord);

        if (cursor.moveToFirst()) {// means cursor will move to first result, which means db is not empty
            enDefinition = cursor.getString(cursor.getColumnIndex("en_definition"));
            example = cursor.getString(cursor.getColumnIndex("example"));
            synonyms = cursor.getString(cursor.getColumnIndex("synonyms"));
            antonyms = cursor.getString(cursor.getColumnIndex("antonyms"));



        } // end if

        //****************************************
        // insert searched word into history table
        dbHelper.insertHistory(enWord);


        // add btn for speaking word out
        ImageButton imgBtn_speak = findViewById(R.id.imgBtn_speaker);

        // setup on click listener for button
        imgBtn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS){
                            int result = textToSpeech.setLanguage(Locale.getDefault());

                            if (result == textToSpeech.LANG_MISSING_DATA || result == textToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("error", "This language is not supported");
                            } else{
                                // using api 19 minimum
                                textToSpeech.speak(enWord, TextToSpeech.QUEUE_FLUSH, null);//.speak(enWord, TextToSpeech.QUEUE_FLUSH, null,)

                                // if api 21 minimum use textToSpeech.speak(characterSequence, queue, null, null)
                                // if api 19 minimum use textToSpeech.speak(characterSequence, queue, null)
                            } // end else
                        } // end if
                        else{
                            Log.e("error", "Initialization failed! Sorry about that.");
                        }
                    } // end method init
                });
            } // end method onClick
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(enWord);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        viewPager = findViewById(R.id.tabViewPager);

        if (viewPager != null){
            setupViewPager(viewPager);
        } // end if

        // tablayout
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        // addontablistener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            /*if (startedFromShare){
                Intent intent  = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } // end if
            else {
            }*/

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();



        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFrag(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }


    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefinition(), "Definitions");
        adapter.addFrag(new FragmentSynonyms(), "Synonyms");
        adapter.addFrag(new FragmentAntonyms(), "Antonyms");
        adapter.addFrag(new FragmentExample(), "Examples");
        viewPager.setAdapter(adapter);
    }



} // end class
