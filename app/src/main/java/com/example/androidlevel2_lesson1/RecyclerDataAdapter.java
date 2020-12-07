package com.example.androidlevel2_lesson1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.R;

import java.util.ArrayList;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.ViewHolder> {
    private ArrayList<String> dataHeading,dataText;

    public RecyclerDataAdapter(ArrayList<String> dataHeading, ArrayList<String> dataText) {
        this.dataHeading = dataHeading;
        this.dataText = dataText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(dataText!=null)
            holder.setTextToTextView(dataHeading.get(position),dataText.get(position));
        else if (dataHeading!=null)
            holder.setTextToTextView(dataHeading.get(position));
    }

    @Override
    public int getItemCount() {
        return dataHeading == null ? 0 : dataHeading.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemHeading;
        private TextView itemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHeading = itemView.findViewById(R.id.itemHeading);
            itemText = itemView.findViewById(R.id.itemText);
        }

        void setTextToTextView(String textHeading,String text) {
            itemHeading.setText(textHeading);
            itemText.setText(text);
        }

        void setTextToTextView(String textHeading) {
            itemHeading.setText(textHeading);
        }
    }

}
