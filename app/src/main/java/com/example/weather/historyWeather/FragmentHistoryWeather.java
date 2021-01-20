package com.example.weather.historyWeather;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.App;
import com.example.weather.BatteryReceiver;
import com.example.weather.IRVOnItemClick;
import com.example.weather.NetworkReceiver;
import com.example.weather.R;
import com.example.weather.dialog.OnFragmentDialogListener;
import com.example.weather.model.db.WeatherSource;
import com.google.android.material.snackbar.Snackbar;

public class FragmentHistoryWeather extends Fragment implements IRVOnItemClick, OnFragmentDialogListener {
    private RecyclerView historyWeather;
    private String townSelected;
    private RecyclerDataAdapterHistoryWeather adapterHistoryWeather;
    private WeatherSource weatherSource;

    private Handler handler;
    private Handler handler1;
    private HandlerThread handlerThread;

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
        handlerThread = new HandlerThread("handlerThreadHistoryWeather");
        handlerThread.start();
        handler1 = new Handler(handlerThread.getLooper());
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
        if (requireActivity() != null) {
            if (batteryReceiver.getDebugUnregister()) {
                requireActivity().unregisterReceiver(batteryReceiver);
            }
            if (networkReceiver.getDebugUnregister()) {
                requireActivity().unregisterReceiver(networkReceiver);
            }
            handlerThread.quitSafely();
        }
    }

    private void initReceiver() {
        if (requireActivity() != null) {
            batteryReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
            networkReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
        }
    }

    private void setupRecyclerView(View view) {
        historyWeather = view.findViewById(R.id.listHistoryWeather);
        historyWeather.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyWeather.setLayoutManager(layoutManager);

        weatherSource = App.getInstance().getWeatherSource();
        if (requireActivity() != null) {
            adapterHistoryWeather = new RecyclerDataAdapterHistoryWeather(weatherSource, requireActivity(), this);
        }
        historyWeather.setAdapter(adapterHistoryWeather);
    }

    public void clearList() {
        weatherSource.deleteAllHistorWeather();
    }

    public void clearFilter() {
       handler1.post(new Runnable() {
            @Override
            public void run() {
                weatherSource.loadHistoryweathers();
            }
       });
       loadHistoryWeather();
    }

    public void validate(final SearchView tv){
        final String value = firstUpperCase(tv.getQuery().toString());

        handler1.post(new Runnable() {
            @Override
            public void run() {
                if (weatherSource.getHistoryWeatherByTown("%"+value+"%").size() > 0) {
                    loadHistoryWeather();
                } else {
                    clearFilter();
                    Snackbar.make(requireView(), "Такого города нет в вашей истории!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
            if (requireActivity() != null) {
                requireActivity().finishAffinity();
            }
        }
    }

}
