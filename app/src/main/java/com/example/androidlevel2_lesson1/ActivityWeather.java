package com.example.androidlevel2_lesson1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidlevel2_lesson1.R;

import java.util.Objects;

public class ActivityWeather extends AppCompatActivity {
    private String town;
    private DataContainer currentData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            Intent intent = new Intent(this, MainActivity.class);
            currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
            intent.putExtra(FragmentWeather.dataKey,currentData);
            startActivity(intent);
            finish();
            return;
        }
        if(savedInstanceState==null){
            FragmentWeather details = new FragmentWeather();
            details.setArguments(getIntent().getExtras());
            town= details.getDataCurrent().getTown();
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
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this, MainActivity.class);
        currentData=(DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
        intent.putExtra(FragmentWeather.dataKey,currentData);
        startActivity(intent);
    }
}
