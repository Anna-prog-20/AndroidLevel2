package com.example.androidlevel2_lesson1.town;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.data.DataContainer;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.data.OpenWeather;
import com.example.androidlevel2_lesson1.dialog.BottomDialogFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.historyWeather.ActivityHistoryWeather;
import com.example.androidlevel2_lesson1.info.ActivityInfo;
import com.example.androidlevel2_lesson1.model.WeatherList;
import com.example.androidlevel2_lesson1.setting.ActivitySettings;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DataContainer currentData;
    public static SharedPreferences sharedPreferences;

    private OnFragmentDialogListener onFragmentDialogListener = new OnFragmentDialogListener() {
        @Override
        public void onDialogResult(int id) {
            switch (id) {
                case R.id.btnAdd: {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTown fragment = (FragmentTown) fm.findFragmentById(R.id.fragmentTownList);
                    if (fragment != null) {
                        fragment.setTownSelected(dialogFragment.getText());
                        fragment.validate(dialogFragment.getText());
                    }
                }
                case R.id.btnCancel:
                    break;
            }
        }
    };
    private BottomDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setTitle(R.string.app_name);
        }
        else {
            setTitle(R.string.itemTown);
        }
        Toolbar toolbarMain = initToolbar();
        initDrawer(toolbarMain);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        loadPreferences(sharedPreferences);
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
                FragmentTown fragment = (FragmentTown) fm.findFragmentById(R.id.fragmentTownList);
                if (fragment != null) {
                    fragment.setTownSelected(query);
                    fragment.validate(query);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE)
            menu.findItem(R.id.itemSetting).setVisible(false);
        menu.findItem(R.id.itemInfoTown).setVisible(false);
        menu.findItem(R.id.itemAddTown).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAddTown: {
                dialogFragment = BottomDialogFragment.newInstance();
                dialogFragment.setOnFragmentDialogListener(onFragmentDialogListener);
                dialogFragment.show(getSupportFragmentManager(), "dialogFragment");
                return true;
            }
            case R.id.itemHistoryWeather: {
                Intent intent = new Intent(this, ActivityHistoryWeather.class);
                startActivity(intent);
                return true;
            }
            case R.id.itemInfo: {
                Intent intent = new Intent(this, ActivityInfo.class);
                startActivity(intent);
                return true;
            }
            case R.id.itemSetting: {
                Intent intent = new Intent(this, ActivitySettings.class);
                currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
                intent.putExtra(FragmentWeather.dataKey,currentData);
                startActivity(intent);
                return true;
            }
           default: {
               return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==88){
            recreate();
        }
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle saveInstanceState) {
        assert saveInstanceState != null;
        saveInstanceState.putSerializable(FragmentWeather.dataKey,currentData);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        assert savedInstanceState != null;
        super.onRestoreInstanceState(savedInstanceState);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemInfo: {
                Intent intent = new Intent(this, ActivityInfo.class);
                startActivity(intent);
                break;
            }
            case R.id.itemHistoryWeather: {
                Intent intent = new Intent(this, ActivityHistoryWeather.class);
                startActivity(intent);
                break;
            }
            case R.id.itemSetting: {
                Intent intent = new Intent(this, ActivitySettings.class);
                currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
                intent.putExtra(FragmentWeather.dataKey,currentData);
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadPreferences(SharedPreferences sharedPref){
        String keyTown = "town";
        String keyCheckPressure = ActivitySettings.KEYS[0];
        String keyCheckWindSpeed = ActivitySettings.KEYS[1];
        String town = sharedPref.getString(keyTown,"Чита");
        boolean checkPressure = sharedPref.getBoolean(keyCheckPressure,true);
        boolean checkWindSpeed = sharedPref.getBoolean(keyCheckWindSpeed,true);

        showStartWeather(town);
    }

    private void showStartWeather(String town) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTown fragment = (FragmentTown) fm.findFragmentById(R.id.fragmentTownList);
        if (fragment != null) {
            fragment.onClick(town);
        }
    }

}