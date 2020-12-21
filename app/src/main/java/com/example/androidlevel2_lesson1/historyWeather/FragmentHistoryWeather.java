package com.example.androidlevel2_lesson1.historyWeather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.recycler.IRVOnItemClick;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.EducationSource;
import com.example.androidlevel2_lesson1.recycler.RecyclerDataAdapterHistoryWeather;
import com.google.android.material.snackbar.Snackbar;

public class FragmentHistoryWeather extends Fragment implements IRVOnItemClick {
    private RecyclerView historyWeather;
    private String townSelected;
    private RecyclerDataAdapterHistoryWeather adapterHistoryWeather;
    private EducationSource educationSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_weather,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
    }

    @Override
    public void onItemClicked(String itemText) {
        educationSource.getHistoryWeathersSortTown();
        loadHistoryWeather();
    }

    private void setupRecyclerView(View view) {
        historyWeather = view.findViewById(R.id.listHistoryWeather);
        historyWeather.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyWeather.setLayoutManager(layoutManager);

        educationSource = App.getInstance().getEducationSource();
        adapterHistoryWeather = new RecyclerDataAdapterHistoryWeather(educationSource, requireActivity(),this);
        historyWeather.setAdapter(adapterHistoryWeather);
    }

    public void clearList() {
        educationSource.deleteAllHistorWeather();
    }

    public void clearFilter() {
        educationSource.loadHistoryweathers();
        loadHistoryWeather();
    }

    public void validate(final SearchView tv){
        String value = firstUpperCase(tv.getQuery().toString());
        educationSource.getHistoryWeatherByTown("%"+value+"%");

        if (educationSource.getHistoryWeathers().size() > 0) {
            loadHistoryWeather();
        } else {
            clearFilter();
            Snackbar.make(requireView(), "Такого города нет в вашей истории!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void loadHistoryWeather() {
        adapterHistoryWeather.setEducationSource(educationSource);
        historyWeather.setAdapter(adapterHistoryWeather);
    }

    public String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }

}
