package com.example.weather.weather;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.historyWeather.ActivityHistoryWeather;
import com.example.weather.setting.ActivitySettings;
import com.example.weather.model.dataTransfer.DataContainer;
import com.example.weather.town.MainActivity;
import com.example.weather.R;
import com.example.weather.info.ActivityInfo;

import java.util.Objects;

public class ActivityWeather extends AppCompatActivity {
    private String town;
    private DataContainer currentData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Intent intent = new Intent(this, MainActivity.class);
            currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
            intent.putExtra(FragmentWeather.dataKey,currentData);
            startActivity(intent);
            finish();
            return;
        }
        if(savedInstanceState==null){
            final FragmentWeather details = new FragmentWeather();
            details.setArguments(getIntent().getExtras());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (details.getDataCurrent().getTown() != null) {
                        town = details.getDataCurrent().getTown();
                    }
                }
            }).start();

        }
    }

    private boolean check(double d){
        return d != 0;
    }

    @Override
    protected void onPause() {
        currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
        super.onPause();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.itemInfoTown).setVisible(true);
        menu.findItem(R.id.itemSetting).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.itemChange).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemInfoTown: {
                Uri uri = Uri.parse("https://ru.wikipedia.org/wiki/"+town);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
            case R.id.itemChange: {
                onBackPressed();
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
            case R.id.itemHistoryWeather: {
                Intent intent = new Intent(this, ActivityHistoryWeather.class);
                startActivity(intent);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
//        Intent intent=new Intent(this, MainActivity.class);
//        currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
//        intent.putExtra(FragmentWeather.dataKey,currentData);
        //startActivity(intent);
        super.onBackPressed();
    }
}
