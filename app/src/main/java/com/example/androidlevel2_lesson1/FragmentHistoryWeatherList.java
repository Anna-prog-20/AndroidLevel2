package com.example.androidlevel2_lesson1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentHistoryWeatherList extends Fragment implements IRVOnItemClick{
    private RecyclerView town;
    private String townSelected;
    private ArrayList<String> arrayListHistoryWeather;
    private RecyclerDataAdapterTown adapterTown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_weather,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    @Override
    public void onItemClicked(String itemText) {

    }

    private void initViews(View view) {
        town = view.findViewById(R.id.listHistoryWeather);
        arrayListHistoryWeather = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.listHistoryWeather)));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapterTown = new RecyclerDataAdapterTown(arrayListHistoryWeather, this);
        town.setLayoutManager(layoutManager);
        town.setAdapter(adapterTown);
    }

    public void clearList() {
        adapterTown.clearItems();
    }

    public void commitFragment() {

    }

    public void validate(final SearchView tv){
        String value = firstUpperCase(tv.getQuery().toString());
        boolean check = searchToArray(arrayListHistoryWeather, value);

        if(check == false){
            Snackbar.make(requireView(), "Такого города нет в вашей истории!", Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean searchToArray(ArrayList<String> arrayListHistoryWeather, String value) {
        boolean check = false;
        for (String string : arrayListHistoryWeather) {
            if(string.equalsIgnoreCase(value)) {
                value=string;
                check = true;
                break;
            }
            else{
                check = false;
            }
        }
        return check;
    }

    public String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }

}
