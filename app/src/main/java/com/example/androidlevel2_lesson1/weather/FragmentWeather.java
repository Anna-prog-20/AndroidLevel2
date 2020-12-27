package com.example.androidlevel2_lesson1.weather;

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
import com.example.androidlevel2_lesson1.BuildConfig;
import com.example.androidlevel2_lesson1.data.DataContainer;
import com.example.androidlevel2_lesson1.data.FragmentOfData;
import com.example.androidlevel2_lesson1.data.InputDataContainer;
import com.example.androidlevel2_lesson1.data.OpenWeather;
import com.example.androidlevel2_lesson1.data.PutData;
import com.example.androidlevel2_lesson1.ThermometerView;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
       super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        initArrayImageId();
        initDialog();
        outputData();
        handler = new Handler();
        initPreferences();
        initRetorfit();
        requestRetrofit(String.valueOf(town.getText()),apiKey);
        outputImage();
        super.onViewCreated(view, savedInstanceState);
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
                }
                else {
                    dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
            }
        });
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
        visible(windSpeed,currentData.isCheckWindSpeed());
        visible(pressure,currentData.isCheckPressure());
        thermometerView = view.findViewById(R.id.thermometerView);
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
        thermometerView.setLevel(inputDataContainer.levelThermometer);
        thermometerView.invalidate();
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
    }
}
