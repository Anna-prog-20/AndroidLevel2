package com.example.weather.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.model.dataTransfer.DataContainer;
import com.example.weather.historyWeather.ActivityHistoryWeather;
import com.example.weather.town.MainActivity;
import com.example.weather.R;

import static com.example.weather.weather.FragmentWeather.dataKey;

public class ActivitySettings extends AppCompatActivity {
    public static String[] KEYS = {"isCheckPressure", "isCheckWindSpeed"};
    private CheckBox windSpeed, pressure;
    private DataContainer currentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.itemSetting);
        setContentView(R.layout.activity_settings);
        init();
        loadPreferences(MainActivity.sharedPreferences);
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle saveInstanceState) {
        assert saveInstanceState != null;
        saveInstanceState.putSerializable(dataKey, currentData);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        assert savedInstanceState != null;
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        savePreferences(MainActivity.sharedPreferences);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemSetting).setVisible(false);
        menu.findItem(R.id.itemInfoTown).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home: {
                Intent intent = new Intent(ActivitySettings.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.itemHistoryWeather: {
                Intent intent = new Intent(this, ActivityHistoryWeather.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void init() {
        windSpeed = findViewById(R.id.windSpeed);
        pressure = findViewById(R.id.pressure);
        currentData = getDataCurrent();
    }

    private void check(boolean b, CheckBox checkBox) {
        if (b)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
    }

    public DataContainer getDataCurrent() {
        return (DataContainer) this.getIntent().getSerializableExtra(dataKey);
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        String keyCheckPressure = ActivitySettings.KEYS[0];
        String keyCheckWindSpeed = ActivitySettings.KEYS[1];
        boolean checkPressure = sharedPref.getBoolean(keyCheckPressure, true);
        boolean checkWindSpeed = sharedPref.getBoolean(keyCheckWindSpeed, true);

        check(checkWindSpeed, windSpeed);
        check(checkPressure, pressure);
    }

    private void savePreferences(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEYS[0], pressure.isChecked());
        editor.putBoolean(KEYS[1], windSpeed.isChecked());
        editor.commit();
        Log.i("TAG", String.valueOf(pressure.isChecked()));
        Log.i("TAG", String.valueOf(windSpeed.isChecked()));
    }

}