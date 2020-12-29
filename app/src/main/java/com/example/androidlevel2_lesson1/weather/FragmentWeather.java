package com.example.androidlevel2_lesson1.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.BatteryReceiver;
import com.example.androidlevel2_lesson1.BuildConfig;
import com.example.androidlevel2_lesson1.NetworkReceiver;
import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;
import com.example.androidlevel2_lesson1.model.dataTransfer.DataContainer;
import com.example.androidlevel2_lesson1.model.dataTransfer.FragmentOfData;
import com.example.androidlevel2_lesson1.model.dataTransfer.InputDataContainer;
import com.example.androidlevel2_lesson1.model.connection.OpenWeather;
import com.example.androidlevel2_lesson1.model.dataTransfer.PutData;
import com.example.androidlevel2_lesson1.ThermometerView;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.db.HistoryWeather;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.example.androidlevel2_lesson1.setting.ActivitySettings;
import com.example.androidlevel2_lesson1.town.MainActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentWeather extends Fragment implements OnFragmentDialogListener, FragmentOfData {
    public static final String dataKey = "dataKey";
    private TextView date;
    private TextView time;
    private TextView temperature;
    private TextView town;
    private TextView windSpeed;
    private TextView pressure;
    private RecyclerView listWeather;
    private ImageView imageTemperature;
    private ArrayList arrayImageId;
    private DataContainer currentData;
    private int t=0;
    private OpenWeather openWeather;
    private String apiKey;

    private DialogBuilderFragment dlgBuilder;
    private ThermometerView thermometerView;
    private static Handler handler;

    private NetworkReceiver networkReceiver = App.getInstance().getNetworkReceiver();
    private BatteryReceiver batteryReceiver = App.getInstance().getBatteryReceiver();

    public static FragmentWeather create(DataContainer currentData){
        FragmentWeather f=new FragmentWeather();
        Bundle args=new Bundle();
        args.putSerializable(dataKey,currentData);
        f.setArguments(args);
        return f;
    }

    public DataContainer getDataCurrent(){
        if (getArguments() != null) {
            return (DataContainer) getArguments().getSerializable(dataKey);
        }
        else {
            return (DataContainer) requireActivity().getIntent().getSerializableExtra(dataKey);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        currentData.setCheckWindSpeed(check(windSpeed));
        currentData.setCheckPressure(check(pressure));
        outState.putSerializable(dataKey,currentData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        currentData.setCheckWindSpeed(check(windSpeed));
        currentData.setCheckPressure(check(pressure));
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean check(TextView textView){
        return textView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        initReceiver();
        super.onCreate(savedInstanceState);

    }

    private void initReceiver() {
        batteryReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
        networkReceiver.setCurrentFragmentManager(requireActivity().getSupportFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume");
        if (MainActivity.sharedPreferences!=null) {
            loadPreferences(MainActivity.sharedPreferences);
        }
        if (this.isVisible()) {
            initReceiver();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        Log.i("TAG", "onViewCreated");
        if (MainActivity.sharedPreferences!=null) {
            loadPreferences(MainActivity.sharedPreferences);
        }

        initArrayImageId();
        initDialog();
        outputData();
        handler = new Handler();
        initPreferences();
        initRetorfit();
        if (currentData.getLat() == 0) {
            requestRetrofit(String.valueOf(town.getText()),apiKey);
        }
        else
            requestRetrofitCoord(currentData.getLat(),currentData.getLon(),apiKey);

        outputImage();

        super.onViewCreated(view, savedInstanceState);
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

    private void outputImage() {
        int index = (int) Math.round(Math.random()*(arrayImageId.size()-1));
        Picasso.get()
                .load((Integer) arrayImageId.get(index))
                .into(imageTemperature);
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
                if (response.body() != null) {
                    handler.post(new PutData((FragmentWeather) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMainWeather), response.body()));
                    savePreferences(MainActivity.sharedPreferences);
                    Log.i("TAG","onStart");
                }
                else {
                    dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
                showMessageNotNetwork();
            }
        });

    }

    private void requestRetrofitCoord(float lat, float lon, String keyApi) {
        openWeather.loadWeather(lat, lon, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null) {
                    handler.post(new PutData((FragmentWeather) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMainWeather), response.body()));
                    savePreferences(MainActivity.sharedPreferences);
                    Log.i("TAG","onStart");
                }
                else {
                    dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
                //showMessageNotNetwork();
            }
        });

    }

    private void showMessageNotNetwork() {
        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setVisibleOkButton(false);
        messageDialogFragment.setTextMessage(R.string.text_message_notnetwork);
        if (!requireActivity().isDestroyed()) {
            if (!requireActivity().getSupportFragmentManager().isDestroyed()) {
                messageDialogFragment.show(requireActivity().getSupportFragmentManager(), "MessageFragment");
            }
        }
    }

    private void initArrayImageId() {
        arrayImageId = new ArrayList();
        arrayImageId.add(R.drawable.sun);
        arrayImageId.add(R.drawable.moon);
        arrayImageId.add(R.drawable.rain);
        arrayImageId.add(R.drawable.wind);
    }

    private void initViews(View view) {
        currentData = getDataCurrent();
        date=view.findViewById(R.id.dateCurrent);
        time=view.findViewById(R.id.timeCurrent);
        temperature=view.findViewById(R.id.temperatureCurrent);
        town=view.findViewById(R.id.townCurrent);
        windSpeed=view.findViewById(R.id.windSpeed);
        pressure = view.findViewById(R.id.pressure);
        listWeather = view.findViewById(R.id.listWeather);
        imageTemperature = view.findViewById(R.id.imageTemperature);
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
        dlgBuilder.setVisibleAddButton(false);
    }

    private void visible(TextView textView,boolean b){
        if (b) {
            textView.setVisibility(View.VISIBLE);
        }
        else
            textView.setVisibility(View.GONE);
    }

    private void setDate(String dateCurrent) {
        date.setText(dateCurrent);
    }

    private void setTime(String timeCurrent) {
        time.setText(timeCurrent);
    }

    private void setTemperature(String temperatureCurrent) {
        temperature.setText(temperatureCurrent);
    }

    private void setTown(String townCurrent) {
        town.setText(townCurrent);
    }

    private void setPressure(String pressureCurrent) {
        pressure.setText(pressureCurrent);
    }

    private void setWindSpeed(String windSpeedCurrent) {
        windSpeed.setText(windSpeedCurrent);
    }

    public TextView getTown() {
        return town;
    }

    private void outputData(){
        if (currentData!=null) {
            String txtTown = currentData.getTown();
            setTown(txtTown);
        }
    }

    private void inputData(InputDataContainer inputDataContainer) {
        setDate(inputDataContainer.date);
        setTime(inputDataContainer.time);
        setTemperature(inputDataContainer.temperature);
        setPressure(inputDataContainer.pressure);
        setWindSpeed(inputDataContainer.windSpeed);
        setupRecyclerView(inputDataContainer);
    }

    @Override
    public void displayWeather(final InputDataContainer inputDataContainer) throws ParseException {
        inputData(inputDataContainer);

        thermometerView = getView().findViewById(R.id.thermometerView);
        thermometerView.setLevel(inputDataContainer.levelThermometer);
        thermometerView.invalidate();

        HistoryWeather historyWeather = new HistoryWeather(town.getText().toString(),inputDataContainer.date,inputDataContainer.temperature);
        App.getInstance().getWeatherSource().addHistoryWeather(historyWeather);
        Log.i("TAG", "displayWeather");
    }

    private void setupRecyclerView(InputDataContainer inputDataContainer) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        RecyclerDataAdapter adapter = new RecyclerDataAdapter(inputDataContainer.arrayListWeek, inputDataContainer.arrayListTemperature);
        listWeather.setLayoutManager(layoutManager);
        listWeather.setAdapter(adapter);
    }

    @Override
    public void onDialogResult(int id) {
        if (id == R.string.cancel) {
            requireActivity().finish();
        }
        if (id == R.string.exit) {
            requireActivity().finishAffinity();
        }
    }

    private void savePreferences(SharedPreferences sharedPref){
        String key = "town";
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, town.getText().toString());
            editor.commit();
        }
        Log.i("TAG", town.getText().toString());
    }

    private void loadPreferences(SharedPreferences sharedPref){
        String keyCheckPressure = ActivitySettings.KEYS[0];
        String keyCheckWindSpeed = ActivitySettings.KEYS[1];
        boolean checkPressure = sharedPref.getBoolean(keyCheckPressure,true);
        boolean checkWindSpeed = sharedPref.getBoolean(keyCheckWindSpeed,true);
        visible(windSpeed,checkWindSpeed);
        visible(pressure,checkPressure);
    }
}
