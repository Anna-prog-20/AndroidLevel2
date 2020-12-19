package com.example.androidlevel2_lesson1.town;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.BuildConfig;
import com.example.androidlevel2_lesson1.data.DataContainer;
import com.example.androidlevel2_lesson1.recycler.IRVOnItemClick;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.data.OpenWeather;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.model.EducationSource;
import com.example.androidlevel2_lesson1.model.Town;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.example.androidlevel2_lesson1.recycler.RecyclerDataAdapterTown;
import com.example.androidlevel2_lesson1.weather.ActivityWeather;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private EducationSource educationSource;

    private Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                getCurrentData(getResources().getStringArray(R.array.listTown)[0]);
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
        onClick(itemText);
    }

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }

    private DataContainer getCurrentData(String itemText){
        DataContainer currentDataI=new DataContainer(itemText);
        if (currentData!=null) {
            currentDataI.setCheckPressure(currentData.isCheckPressure());
            currentDataI.setCheckWindSpeed(currentData.isCheckWindSpeed());
        }
        return currentDataI;
    }

    public void onClick(final String itemText){
        townSelected = itemText;
        showWeather(getCurrentData(itemText));
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

        educationSource = App.getInstance().getEducationSource();
        adapterTown = new RecyclerDataAdapterTown(educationSource, requireActivity(),this);
        town.setAdapter(adapterTown);
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
    }

    public void validate(final String tv){
        value = firstUpperCase(tv);

        educationSource.getTownByTown("%"+value+"%");
        if (educationSource.getTowns().size()>0) {
            adapterTown.setEducationSource(educationSource);
            town.setAdapter(adapterTown);
        }
        else {
            connection();
            educationSource.loadTowns();
            adapterTown.setEducationSource(educationSource);
            town.setAdapter(adapterTown);
            dlgBuilder.show(requireActivity().getSupportFragmentManager(),"dialogBuilder");
        }
    }

    private void addTown(final boolean connected, final String value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    Town lineTown = new Town(firstUpperCase(value));
                    educationSource.addTown(lineTown);
                    town.scrollToPosition((int) educationSource.getCountTown());
                    Snackbar.make(requireView(), "Город успешно добавлен!", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(requireView(), "Такого города не существует!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void connection(){
        initRetorfit();
        requestRetrofit(townSelected,apiKey);
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

            }
        });
    }

    @Override
    public void onDialogResult(int id) {
        if (id == R.string.add) {
            handler = new Handler();
            addTown(connected[0],value);
        }
    }
}
