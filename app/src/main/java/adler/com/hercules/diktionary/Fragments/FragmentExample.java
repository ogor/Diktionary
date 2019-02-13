/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import adler.com.hercules.diktionary.R;
import adler.com.hercules.diktionary.WordMeaningActivity;

public class FragmentExample extends Fragment {
    public FragmentExample(){

    } // end constructor

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        Context context = getActivity();
        TextView tv_defintion = view.findViewById(R.id.tv_definition);

        String example = ((WordMeaningActivity)context).example;

        tv_defintion.setText(example);

        if (example == null){
            tv_defintion.setText("No example(s) found!");
        } // end if

        return view;
    }
} // end class