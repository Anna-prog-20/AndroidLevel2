package com.example.androidlevel2_lesson1.setting;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidlevel2_lesson1.DataContainer;
import com.example.androidlevel2_lesson1.town.MainActivity;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.info.ActivityInfo;
import com.example.androidlevel2_lesson1.weather.ActivityWeather;

import static com.example.androidlevel2_lesson1.weather.FragmentWeather.dataKey;

public class ActivitySettings extends AppCompatActivity {
    private CheckBox windSpeed,pressure;
    private DataContainer currentData;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.itemSetting);
        setContentView(R.layout.activity_settings);
        init();
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle saveInstanceState) {
        assert saveInstanceState != null;
        saveInstanceState.putSerializable(dataKey,currentData);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        assert savedInstanceState != null;
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        currentData.setCheckPressure(pressure.isChecked());
        currentData.setCheckWindSpeed(windSpeed.isChecked());
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            intent=new Intent(this, MainActivity.class);
        }
        else {
            intent = new Intent(this, ActivityWeather.class);
        }
        intent.putExtra(dataKey,currentData);
        startActivity(intent);
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
            case android.R.id.home: {
                Intent intent = new Intent(ActivitySettings.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.itemInfo: {
                Intent intent = new Intent(this, ActivityInfo.class);
                startActivity(intent);
                return true;
            }
           default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void init(){
        windSpeed=findViewById(R.id.windSpeed);
        pressure=findViewById(R.id.pressure);
        currentData = getDataCurrent();
        if(currentData!=null) {
            check(currentData.isCheckWindSpeed(), windSpeed);
            check(currentData.isCheckPressure(), pressure);
        }
    }

    private void check(boolean b,CheckBox checkBox){
        if(b)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
    }

    public DataContainer getDataCurrent(){
        return (DataContainer) this.getIntent().getSerializableExtra(dataKey);
    }
}