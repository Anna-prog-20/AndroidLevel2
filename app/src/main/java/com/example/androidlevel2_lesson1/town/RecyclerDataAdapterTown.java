package com.example.androidlevel2_lesson1.town;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.IRVOnItemClick;
import com.example.androidlevel2_lesson1.R;

import java.util.ArrayList;

public class RecyclerDataAdapterTown extends RecyclerView.Adapter<RecyclerDataAdapterTown.ViewHolderTown> {
    private IRVOnItemClick onItemClickCallback;
    private ArrayList<String> dataText;

    public RecyclerDataAdapterTown(ArrayList<String> dataText, IRVOnItemClick onItemClickCallback) {
        this.dataText=dataText;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolderTown onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,
                false);
        return new ViewHolderTown(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTown holder, int position) {
        holder.setTextToTextView(dataText.get(position));
        holder.setOnClickForItem(dataText.get(position));
    }

    @Override
    public int getItemCount() {
        return dataText == null ? 0 : dataText.size();
    }

    public void clearItems() {
        dataText.clear();
        notifyDataSetChanged();
    }

    class ViewHolderTown extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolderTown(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTextView);
        }

        void setTextToTextView(String text) {
            textView.setText(text);
        }

        void setOnClickForItem(final String text) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(text);
                    }
                }
            });
        }
    }
}
