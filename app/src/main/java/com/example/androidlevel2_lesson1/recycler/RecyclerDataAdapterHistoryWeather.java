package com.example.androidlevel2_lesson1.recycler;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.EducationSource;
import com.example.androidlevel2_lesson1.model.HistoryWeather;

import java.util.List;

public class RecyclerDataAdapterHistoryWeather extends RecyclerView.Adapter<RecyclerDataAdapterHistoryWeather.ViewHolderHistoryWeather> {

    private Activity activity;
    private IRVOnItemClick onItemClickCallback;

    private EducationSource educationSource;

    public RecyclerDataAdapterHistoryWeather(EducationSource educationSource, Activity activity, IRVOnItemClick onItemClickCallback) {
        this.educationSource = educationSource;
        this.activity = activity;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolderHistoryWeather onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_weather, parent,
                false);
        view.findViewById(R.id.itemTown).getLayoutParams().width = parent.getWidth()*4/9;
        view.findViewById(R.id.itemDate).getLayoutParams().width = parent.getWidth()/3;
        view.findViewById(R.id.itemTemp).getLayoutParams().width = parent.getWidth()*2/9;
        return new ViewHolderHistoryWeather(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHistoryWeather holder, int position) {
        List<HistoryWeather> historyWeathers = educationSource.getHistoryWeathers();
        HistoryWeather historyWeather = historyWeathers.get(position);
        holder.setTextToTextViews(historyWeather.getTown(),historyWeather.getDate(),historyWeather.getTemp());
        holder.setOnClickForItem(historyWeather.getTown());
    }

    @Override
    public int getItemCount() {
        return educationSource.getHistoryWeathers().size();
    }

    public EducationSource getEducationSource() {
        return educationSource;
    }

    public void setEducationSource(EducationSource educationSource) {
        this.educationSource = educationSource;
    }

    class ViewHolderHistoryWeather extends RecyclerView.ViewHolder {
        private TextView town;
        private TextView date;
        private TextView temp;

        public ViewHolderHistoryWeather(@NonNull View itemView) {
            super(itemView);
            town = itemView.findViewById(R.id.itemTown);
            date = itemView.findViewById(R.id.itemDate);
            temp = itemView.findViewById(R.id.itemTemp);
        }

        void setTextToTextViews(String town, String date, String temp) {
            getTown().setText(town);
            getDate().setText(date);
            getTemp().setText(temp);
        }

        void setOnClickForItem(final String text) {
            town.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(text);
                    }
                }
            });
        }

        public TextView getTown() {
            return town;
        }

        public void setTown(TextView town) {
            this.town = town;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }

        public TextView getTemp() {
            return temp;
        }

        public void setTemp(TextView temp) {
            this.temp = temp;
        }

    }
}
