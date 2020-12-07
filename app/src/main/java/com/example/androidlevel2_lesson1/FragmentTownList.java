package com.example.androidlevel2_lesson1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class FragmentTownList extends Fragment implements IRVOnItemClick{
    private boolean isExistWeather=false;
    private DataContainer currentData;
    private RecyclerView town;
    private String townSelected;
    private String searchText;
    private ArrayList<String> arrayListTown;
    private String value;

    public void setTownSelected(String townSelected) {
        this.townSelected = townSelected;
    }
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
        initViews(view);
        setupRecyclerView();
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

    private DataContainer getCurrentData(String itemText){
        DataContainer currentDataI=new DataContainer(itemText);
        if (currentData!=null) {
            currentDataI.setCheckPressure(currentData.isCheckPressure());
            currentDataI.setCheckWindSpeed(currentData.isCheckWindSpeed());
        }
        return currentDataI;
    }

    private void onClick(final String itemText){
        townSelected = itemText;
        showWeather(getCurrentData(itemText));
    }

    private void initViews(View view) {
        town=view.findViewById(R.id.listTown);
        arrayListTown=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.listTown)));
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

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerDataAdapterTown adapterTown = new RecyclerDataAdapterTown(arrayListTown, this);
        town.setLayoutManager(layoutManager);
        town.setAdapter(adapterTown);
    }

    private void checkTown() {
    }

    public void validate(final SearchView tv){
        value = firstUpperCase(tv.getQuery().toString());
        boolean check = searchToArray(arrayListTown, value);

        final boolean[] connected = {false};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connected[0] = connection();
            }
        });
        thread.start();

        if(check){
            onClick(value);
        } else {
            Snackbar.make(tv, "В вашем списке не найден введенный город!", Snackbar.LENGTH_LONG)
                    .setAction("Добавить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connected[0]){
                                arrayListTown.add(value);
                                setupRecyclerView();
                                town.scrollToPosition(arrayListTown.size()-1);
                                Snackbar.make(requireView(), "Город успешно добавлен!", Snackbar.LENGTH_LONG).show();
                            }
                            else{
                                Snackbar.make(requireView(), "Такого города не существует!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).show();
        }
    }

    public boolean searchToArray(ArrayList<String> arrayListTown, String value) {
        boolean check = false;
        for (String string : arrayListTown) {
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

    private boolean connection(){
        try {
            URL uri = new URL(getString(R.string.weatherURL,townSelected, "80efcfee52d4195b8ef83e2e5b69a707"));

            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) uri.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } catch (Exception e) {
                return false;
            }
            finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return true;
    }

}
