package com.example.androidlevel2_lesson1.weather;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import com.example.androidlevel2_lesson1.data.DataContainer;
import com.example.androidlevel2_lesson1.data.FragmentOfData;
import com.example.androidlevel2_lesson1.data.InputDataContainer;
import com.example.androidlevel2_lesson1.data.ProcessingData;
import com.example.androidlevel2_lesson1.data.ServiceConnectionWeather;
import com.example.androidlevel2_lesson1.data.ServiceConnectionWeather.ServiceBinder;
import com.example.androidlevel2_lesson1.ThermometerView;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.recycler.RecyclerDataAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import static android.content.Context.BIND_AUTO_CREATE;

public class FragmentWeather extends Fragment implements OnFragmentDialogListener, FragmentOfData {
    static final String BROADCAST_ACTION_SERVICEFINISHED = "com.example.androidlevel2_lesson1.town.servicefinished";
    public static final String dataKey = "dataKey";
    private TextView date;
    private TextView time;
    private TextView temperature;
    private TextView town;
    private TextView windSpeed;
    private TextView pressure;
    private RecyclerView listWeather;
    private DataContainer currentData;
    private int t=0;
    private DialogBuilderFragment dlgBuilder;
    private ThermometerView thermometerView;
    private boolean isBound = false;
    private ServiceBinder boundService;
    private static Handler handler;
    // Обработка соединения с сервисом
    private ServiceConnection boundServiceConnection = new ServiceConnection() {

        // При соединении с сервисом
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            final boolean[] b = {false};
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boundService = (ServiceBinder) service;
                        isBound = boundService != null;
                    }
                });
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!boundService.getConnection().getConnect()) {
                        dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
                    }
                    if (boundService.getConnection().getObtainedData() != null) {
                        ProcessingData processingData = new ProcessingData(handler, (FragmentWeather) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMainWeather), boundService.getObtainedData());
                        processingData.start();
                    }
                }
            }).start();
        }

        // При разрыве соединения с сервисом
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG","ServiceConnection onServiceDisconnected");
            isBound = false;
            boundService = null;
        }
    };

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
        if (isBound){
            requireActivity().unbindService(boundServiceConnection);
        }

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
            handler = new Handler();
            final Activity activity = requireActivity();
            Intent intent = new Intent(requireActivity(), ServiceConnectionWeather.class);
            intent.putExtra("uri", uri);
            activity.startService(intent);
            requireActivity().bindService(intent,
                    boundServiceConnection,
                    BIND_AUTO_CREATE);
        } catch (MalformedURLException e) {
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
