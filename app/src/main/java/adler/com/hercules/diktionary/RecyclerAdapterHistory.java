/*
Created by: Ogor Anumbor
date      : 2/12/2019
 */

package adler.com.hercules.diktionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapterHistory extends RecyclerView.Adapter<RecyclerAdapterHistory.HistoryViewHolder>{
    private ArrayList<History> historyArrayList;
    private Context context;

    public RecyclerAdapterHistory(Context context, ArrayList<History> historyArrayList) {
        this.historyArrayList = historyArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_items, viewGroup, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int position) {
        historyViewHolder.tv_word.setText(historyArrayList.get(position).getWord());
        historyViewHolder.tv_defn.setText(historyArrayList.get(position).getDefinition());
    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tv_word;
        TextView tv_defn;

        // constructor
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_word = itemView.findViewById(R.id.tv_word);
            tv_defn = itemView.findViewById(R.id.tv_defn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();


                    String text = historyArrayList.get(position).getWord();

                    Intent intent = new Intent(context, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);


                }
            });
        }
    } // end class

} // end class
