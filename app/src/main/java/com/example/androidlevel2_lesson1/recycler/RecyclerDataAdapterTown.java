package com.example.androidlevel2_lesson1.recycler;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.EducationSource;
import com.example.androidlevel2_lesson1.model.Town;

import java.util.List;

public class RecyclerDataAdapterTown extends RecyclerView.Adapter<RecyclerDataAdapterTown.ViewHolderTown>{
    private IRVOnItemClick onItemClickCallback;
    private EducationSource educationSource;
    private Activity activity;
    private ViewHolderTown viewHolderTown;

    public RecyclerDataAdapterTown(EducationSource educationSource, Activity activity, IRVOnItemClick onItemClickCallback) {
        this.educationSource = educationSource;
        this.activity = activity;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolderTown onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,
                false);
        viewHolderTown = new ViewHolderTown(view);
        return viewHolderTown;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTown holder, int position) {
        List<Town> towns = educationSource.getTowns();
        Town town = towns.get(position);
        viewHolderTown = holder;
        holder.setTextToTextView(town.getTown());
        holder.setOnClickForItem(town.getTown());
    }

    @Override
    public int getItemCount() {
        int result = 0;
        if (educationSource.getTowns() != null)
            result = educationSource.getTowns().size();

        return result;
    }

    public EducationSource getEducationSource() {
        return educationSource;
    }

    public void setEducationSource(EducationSource educationSource) {
        this.educationSource = educationSource;
    }

    public void setSelectedTown() {
        viewHolderTown.setColorItem(Color.WHITE);
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

        void setColorItem(int color) {
            textView.setBackgroundColor(color);
        }
    }
}
