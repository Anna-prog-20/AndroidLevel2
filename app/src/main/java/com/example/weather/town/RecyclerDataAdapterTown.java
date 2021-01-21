package com.example.weather.town;

import android.app.Activity;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.db.WeatherSource;
import com.example.weather.model.db.Town;
import com.example.weather.IRVOnItemClick;

import java.util.List;

public class RecyclerDataAdapterTown extends RecyclerView.Adapter<RecyclerDataAdapterTown.ViewHolderTown> {
    private IRVOnItemClick onItemClickCallback;
    private WeatherSource weatherSource;
    private Activity activity;
    private ViewHolderTown viewHolderTown;
    private String selectTown;
    private int menuPosition;

    public int getPosition() {
        return menuPosition;
    }

    public void setPosition(int position) {
        this.menuPosition = position;
    }

    public String getSelectTown() {
        return selectTown;
    }

    public RecyclerDataAdapterTown(WeatherSource weatherSource, Activity activity, IRVOnItemClick onItemClickCallback) {
        this.weatherSource = weatherSource;
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
    public void onBindViewHolder(@NonNull final ViewHolderTown holder, final int position) {
        List<Town> towns = weatherSource.getTowns();
        Town town = towns.get(position);
        viewHolderTown = holder;
        holder.setTextToTextView(town.getTown());
        holder.setOnClickForItem(town.getTown());

        holder.getTextView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectTown = holder.getTextView().getText().toString();
                setPosition(position);
                return false;
            }
        });
        if (activity != null) {
            activity.registerForContextMenu(holder.getTextView());
        }
    }

    @Override
    public int getItemCount() {
        int result = 0;
        if (weatherSource.getTowns() != null)
            result = weatherSource.getTowns().size();

        return result;
    }

    public WeatherSource getWeatherSource() {
        return weatherSource;
    }

    public void setWeatherSource(WeatherSource weatherSource) {
        this.weatherSource = weatherSource;
    }

    public void setSelectedTown() {
        viewHolderTown.setColorItem(Color.WHITE);
    }

    class ViewHolderTown extends RecyclerView.ViewHolder {
        private TextView textView;

        public TextView getTextView() {
            return textView;
        }

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
                    if (onItemClickCallback != null) {
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
