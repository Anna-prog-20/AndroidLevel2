package com.example.androidlevel2_lesson1;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class ActivityHistoryWeather extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DataContainer currentData;
    private FragmentHistoryWeatherList fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.itemHistoryWeather);
        setContentView(R.layout.activity_history_weather);
        initToolbar();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemSetting).setVisible(false);
        menu.findItem(R.id.itemInfo).setVisible(false);
        menu.findItem(R.id.itemHistoryWeather).setVisible(false);
        menu.findItem(R.id.itemClear).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarHistoryWeather);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int countOfFragmentInManager = getSupportFragmentManager().getBackStackEntryCount();
        if(countOfFragmentInManager > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchText = (SearchView) search.getActionView();

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentManager fm = getSupportFragmentManager();
                fragment = (FragmentHistoryWeatherList) fm.findFragmentById(R.id.fragmentHistoryWeatherList);
                if (fragment != null) {
                    fragment.setTownSelected(query);
                    fragment.validate(searchText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        } );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemClear: {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                fragment = (FragmentHistoryWeatherList) fm.findFragmentById(R.id.fragmentHistoryWeatherList);
                if (fragment != null) {
                    fragment.clearList();
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}