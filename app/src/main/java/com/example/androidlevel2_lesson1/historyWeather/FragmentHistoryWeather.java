package com.example.androidlevel2_lesson1.historyWeather;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
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
import com.example.androidlevel2_lesson1.BatteryReceiver;
import com.example.androidlevel2_lesson1.IRVOnItemClick;
import com.example.androidlevel2_lesson1.NetworkReceiver;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.model.db.HistoryWeather;
import com.example.androidlevel2_lesson1.model.db.WeatherSource;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FragmentHistoryWeather extends Fragment implements IRVOnItemClick, OnFragmentDialogListener {
    private RecyclerView historyWeather;
    private String townSelected;
    private RecyclerDataAdapterHistoryWeather adapterHistoryWeather;
    private WeatherSource weatherSource;
    private Handler handler;

    private NetworkReceiver networkReceiver = App.getInstance().getNetworkReceiver();
    private BatteryReceiver batteryReceiver = App.getInstance().getBatteryReceiver();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        return inflater.inflate(R.layout.fragment_history_weather,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
    }

    @Override
    public void onItemClicked(String itemText) {
        weatherSource.getHistoryWeathersSortTown();
        loadHistoryWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible()) {
            initReceiver();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver.getDebugUnregister()) {
            requireActivity().unregisterReceiver(batteryReceiver);
        }
        if (networkReceiver.getDebugUnregister()) {
            requireActivity().unregisterReceiver(networkReceiver);
        }
    }

    private void initReceiver() {
        batteryReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
        networkReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
    }

    private void setupRecyclerView(View view) {
        historyWeather = view.findViewById(R.id.listHistoryWeather);
        historyWeather.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyWeather.setLayoutManager(layoutManager);

        weatherSource = App.getInstance().getWeatherSource();
        adapterHistoryWeather = new RecyclerDataAdapterHistoryWeather(weatherSource, requireActivity(),this);
        historyWeather.setAdapter(adapterHistoryWeather);
    }

    public void clearList() {
        weatherSource.deleteAllHistorWeather();
    }

    public void clearFilter() {
        weatherSource.loadHistoryweathers();
        loadHistoryWeather();
    }

    public void validate(final SearchView tv){
        final String value = firstUpperCase(tv.getQuery().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (weatherSource.getHistoryWeatherByTown("%"+value+"%").size() > 0) {
                    loadHistoryWeather();
                } else {
                    clearFilter();
                    Snackbar.make(requireView(), "Такого города нет в вашей истории!", Snackbar.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void loadHistoryWeather() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapterHistoryWeather.setWeatherSource(weatherSource);
                historyWeather.setAdapter(adapterHistoryWeather);
            }
        });

    }

    public String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }

    @Override
    public void onDialogResult(int id) {
        if (id == R.string.exit) {
            requireActivity().finishAffinity();
        }
    }
}
