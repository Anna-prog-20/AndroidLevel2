package com.example.androidlevel2_lesson1.town;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.BatteryReceiver;
import com.example.androidlevel2_lesson1.BuildConfig;
import com.example.androidlevel2_lesson1.NetworkReceiver;
import com.example.androidlevel2_lesson1.model.dataTransfer.DataContainer;
import com.example.androidlevel2_lesson1.IRVOnItemClick;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.connection.OpenWeather;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.model.db.WeatherSource;
import com.example.androidlevel2_lesson1.model.db.Town;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.example.androidlevel2_lesson1.weather.ActivityWeather;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentTown extends Fragment implements IRVOnItemClick, OnFragmentDialogListener {
    private boolean isExistWeather=false;
    private DataContainer currentData;
    private RecyclerView town;
    private String townSelected;
    private String value;
    final boolean[] connected = {false};
    private DialogBuilderFragment dlgBuilder;
    private OpenWeather openWeather;
    private String apiKey;

    private RecyclerDataAdapterTown adapterTown;
    private WeatherSource weatherSource;

    private Handler handler;

    private NetworkReceiver networkReceiver = App.getInstance().getNetworkReceiver();
    private BatteryReceiver batteryReceiver = App.getInstance().getBatteryReceiver();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initReceiver() {
        batteryReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
        networkReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        return inflater.inflate(R.layout.fragment_town,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        initDialog();
        initPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible()) {
            initReceiver();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isExistWeather = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (getActivity().getIntent().getSerializableExtra(FragmentWeather.dataKey) != null) {
            currentData = (DataContainer) getActivity().getIntent().getSerializableExtra(FragmentWeather.dataKey);
        }
        else {
            if(savedInstanceState!=null)
                currentData=(DataContainer) savedInstanceState.getSerializable(FragmentWeather.dataKey);
            else {
                currentData = new DataContainer(getResources().getStringArray(R.array.listTown)[0]);
                getCurrentData(null, getResources().getStringArray(R.array.listTown)[0]);
            }
        }
       if (isExistWeather){
            showWeather(currentData);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(FragmentWeather.dataKey,currentData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(String itemText) {
        onClick(null, itemText);
    }

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }

    private DataContainer getCurrentData(double[] coords, String itemText){
        DataContainer currentDataI=new DataContainer(itemText);
        if (currentData!=null) {
            currentDataI.setCheckPressure(currentData.isCheckPressure());
            currentDataI.setCheckWindSpeed(currentData.isCheckWindSpeed());
            if (coords != null) {
                currentDataI.setLat((float) coords[0]);
                currentDataI.setLon((float) coords[1]);
            }
        }
        return currentDataI;
    }

    public void onClick(double[] coords, final String itemText){
        townSelected = itemText;
        showWeather(getCurrentData(coords, itemText));
    }

    private void showWeather(DataContainer currentData){
        if(isExistWeather) {
            FragmentWeather detail = (FragmentWeather) getChildFragmentManager().findFragmentById(R.id.fragmentMainWeather);
            if (detail == null|| !detail.getDataCurrent().getTown().equals(currentData.getTown())) {
                detail = FragmentWeather.create(currentData);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentMainWeather, detail);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("Some_Key");
                ft.commit();
            }
        }
            else {
                Intent intent=new Intent(getActivity(), ActivityWeather.class);
                intent.putExtra(FragmentWeather.dataKey,currentData);
                startActivity(intent);
            }
    }

    private void setupRecyclerView(View view) {
        town=view.findViewById(R.id.listTown);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        town.setLayoutManager(layoutManager);
        initTown();
    }

    private void initTown() {
        weatherSource = App.getInstance().getWeatherSource();
        adapterTown = new RecyclerDataAdapterTown(weatherSource, requireActivity(), this);
        town.setAdapter(adapterTown);
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
    }

    public void validate(final String tv){
        value = firstUpperCase(tv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (weatherSource.getTownByTown("%"+value+"%").size() > 0) {
                    loadTown();
                } else {
                    connection();
                    clearFilter();
                    dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
                }
            }
        }).start();
    }

    private void loadTown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapterTown.setWeatherSource(weatherSource);
                town.setAdapter(adapterTown);
            }
        });

    }

    public void clearFilter() {
        weatherSource.loadTowns();
        loadTown();
    }

    private void addTown(final boolean connected, final String value) {
        if (connected) {
            Town lineTown = new Town(firstUpperCase(value));
            weatherSource.addTown(lineTown);
            int countTown = 0;
            if (weatherSource.getTowns() != null) {
                countTown = weatherSource.getTowns().size();
            }
            else {
                countTown = (int) weatherSource.getCountTown();
            }
            town.scrollToPosition(countTown);
            Snackbar.make(requireView(), "Город успешно добавлен!", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(requireView(), "Такого города не существует!", Snackbar.LENGTH_LONG).show();
        }
    }

    public String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void connection(){
        initRetorfit();
        requestRetrofit(townSelected, apiKey);
    }

    private void initPreferences() {

        apiKey = BuildConfig.WEATHER_API_KEY;
    }
    
    private void initRetorfit() {
        Retrofit retrofit;

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.webService))
                .addConverterFactory(GsonConverterFactory.create(App.getInstance().gson()))
                .build();

        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() == null) {
                    connected[0] = false;
                }
                else {
                    connected[0] = true;
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
            }
        });
    }

    private void requestRetrofitCoord(float lat, float lon, String keyApi) {
        openWeather.loadWeather(lat, lon, keyApi).enqueue(new Callback<WeatherRequest>() {
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() == null) {
                    connected[0] = false;
                }
                else {
                    connected[0] = true;
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
            }
        });
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

    @Override
    public void onDialogResult(int id) {
        if (id == R.string.add) {
            handler = new Handler();
            addTown(connected[0],value);
        }
        if (id == R.string.exit) {
            requireActivity().finishAffinity();
        }
    }
}
