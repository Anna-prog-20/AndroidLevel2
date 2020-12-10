package com.example.androidlevel2_lesson1.weather;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlevel2_lesson1.Connection;
import com.example.androidlevel2_lesson1.DataContainer;
import com.example.androidlevel2_lesson1.ThermometerView;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.recycler.RecyclerDataAdapter;
import com.example.androidlevel2_lesson1.model.WeatherRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentWeather extends Fragment implements OnFragmentDialogListener {
    public static final String dataKey = "dataKey";
    private TextView date;
    private TextView time;
    private TextView temperature;
    private TextView town;
    private TextView windSpeed;
    private TextView pressure;
    private RecyclerView listWeather;
    private ArrayList<String> arrayListWeek;
    private ArrayList<String> arrayListTemperature;
    private DataContainer currentData;
    private int t=0;
    private DialogBuilderFragment dlgBuilder;
    private Connection connection;
    private ThermometerView thermometerView;

    public static FragmentWeather create(DataContainer currentData){
        FragmentWeather f=new FragmentWeather();
        Bundle args=new Bundle();
        args.putSerializable(dataKey,currentData);
        f.setArguments(args);
        return f;
    }

    public DataContainer getDataCurrent(){
        if (getArguments() != null)
            return (DataContainer) getArguments().getSerializable(dataKey);
        else
            return (DataContainer) requireActivity().getIntent().getSerializableExtra(dataKey);
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
        initDialog();
        outputData();
        try {
            final URL uri = new URL(getString(R.string.weatherURL,town.getText(), "80efcfee52d4195b8ef83e2e5b69a707"));
            final Handler handler = new Handler();
            Connection connection = new Connection(uri,handler, (FragmentWeather) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMainWeather));
            connection.start();
            connection.join();
            if (connection.getConnect() == false) {
                dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
            }
        } catch (MalformedURLException | InterruptedException e) {
            e.printStackTrace();
        }
        super.onViewCreated(view, savedInstanceState);
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

    public void displayWeather(WeatherRequest weatherRequest) throws ParseException {
        Date dateCurrent=new Date();
        setDate(dateText(dateCurrent,weatherRequest));
        setTime(timeText(dateCurrent));
        String temperatureValue = getString(R.string.temperature, Math.round(weatherRequest.getList()[t].getMain().getTemp()));
        thermometerView.setLevel(Math.round(weatherRequest.getList()[t].getMain().getTemp()));
        temperature.setText(temperatureValue);
        String pressureText = getString(R.string.txtPressure, weatherRequest.getList()[t].getMain().getPressure());
        pressure.setText(pressureText);
        currentData.setPressure(weatherRequest.getList()[t].getMain().getPressure());
        String windSpeedStr = getString(R.string.txtWindSpeed, Math.round(weatherRequest.getList()[t].getWind().getSpeed()));
        windSpeed.setText(windSpeedStr);
        currentData.setWindSpeed(Math.round(weatherRequest.getList()[t].getWind().getSpeed()));
        setArrayList(weatherRequest);
        setupRecyclerView();
    }

    private String dateText(Date dateCurrent,WeatherRequest weatherRequest) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        for(int i=0;i<weatherRequest.getList().length;i++){
            @SuppressLint("SimpleDateFormat") String dateString0=new SimpleDateFormat("dd.MM.yyyy HH:mm").format(weatherRequest.getList()[i].getDt()*1000);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy");
            Date dateSearch= format.parse(dateString0);
            if(dateCurrent.after(dateSearch)){
                t=i;
                break;
            }
        }
        return dateFormat.format(weatherRequest.getList()[t].getDt()*1000);
    }

    private String timeText(Date dateCurrent){
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(dateCurrent);
    }

    private void setArrayList(WeatherRequest weatherRequest){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        arrayListWeek=new ArrayList<>();
        arrayListTemperature=new ArrayList<>();
        for(int i=t+1;i<weatherRequest.getList().length-1;i++){
            if(!dateFormat.format(weatherRequest.getList()[i].getDt() * 1000).equals(dateFormat.format(weatherRequest.getList()[i + 1].getDt() * 1000))&!dateFormat.format(weatherRequest.getList()[i].getDt() * 1000).equals(dateFormat.format(weatherRequest.getList()[t].getDt() * 1000))) {
                    arrayListWeek.add(dateFormat.format(weatherRequest.getList()[i].getDt() * 1000));
                    arrayListTemperature.add(getString(R.string.temperature, Math.round(weatherRequest.getList()[i].getMain().getTemp())));
            }
        }
    }
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        RecyclerDataAdapter adapter = new RecyclerDataAdapter(arrayListWeek, arrayListTemperature);
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
