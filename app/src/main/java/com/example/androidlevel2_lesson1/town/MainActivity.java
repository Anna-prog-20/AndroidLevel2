package com.example.androidlevel2_lesson1.town;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;
import com.example.androidlevel2_lesson1.maps.ActivityMap;
import com.example.androidlevel2_lesson1.model.dataTransfer.DataContainer;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.dialog.BottomDialogFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;
import com.example.androidlevel2_lesson1.historyWeather.ActivityHistoryWeather;
import com.example.androidlevel2_lesson1.info.ActivityInfo;
import com.example.androidlevel2_lesson1.setting.ActivitySettings;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int PERMISSION_REQUEST_CODE = 10;

    private DataContainer currentData;
    public static SharedPreferences sharedPreferences;

    private volatile double[] coords = new double[2];
    private String townByCoords = "Чита";

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

        App.getInstance().getWeatherSource().loadTowns();

        initGetToken();
        initNotificationChannel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchText = (SearchView) search.getActionView();

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                FragmentManager fm = getSupportFragmentManager();
                final FragmentTown fragment = (FragmentTown) fm.findFragmentById(R.id.fragmentTownList);
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
                if (currentData != null) {
                    currentData = (DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
                    intent.putExtra(FragmentWeather.dataKey, currentData);
                }
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

    @SuppressLint("ResourceType")
    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initGetToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                    }
                });
    }

    
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemInfo: {
                Intent intent = new Intent(this, ActivityInfo.class);
                startActivity(intent);
                break;
            }
            case R.id.itemWeatherCurrentTown: {
                weatherCurrentTown();
                break;
            }
            case R.id.itemWeatherMap: {
                Intent intent = new Intent(this, ActivityMap.class);
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
                if (currentData != null) {
                    currentData = (DataContainer) Objects.requireNonNull(getIntent().getExtras()).getSerializable(FragmentWeather.dataKey);
                    intent.putExtra(FragmentWeather.dataKey, currentData);
                }
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void weatherCurrentTown() {
        requestPemissions();

    }

    private void requestPemissions() {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у
        // пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем координаты
            requestLocation();
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        }
    }

    // Запрашиваем координаты
    private void requestLocation() {
        // Если Permission’а всё- таки нет, просто выходим: приложение не имеет
        // смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // Получаем менеджер геолокаций
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // Получаем наиболее подходящий провайдер геолокации по критериям.
        // Но определить, какой провайдер использовать, можно и самостоятельно.
        // В основном используются LocationManager.GPS_PROVIDER или
        // LocationManager.NETWORK_PROVIDER, но можно использовать и
        // LocationManager.PASSIVE_PROVIDER - для получения координат в
        // пассивном режиме
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            // Будем получать геоположение через каждые 10 секунд или каждые
            // 10 метров
            locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude(); // Широта
                    coords[0] = lat;
                    double lng = location.getLongitude(); // Долгота
                    coords[1] = lng;

                    if (getAddress(coords) == null) {
                        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
                        messageDialogFragment.setVisibleOkButton(true);
                        messageDialogFragment.setTextMessage(R.string.text_message_notnetwork);
                        if (getSupportFragmentManager() != null) {
                            if (!getSupportFragmentManager().isDestroyed()) {
                                messageDialogFragment.show(getSupportFragmentManager(), "MessageFragment");
                            }
                        }
                    }
                    else {
                        if (getAddress(coords).get(0).getLocality() != null) {
                            townByCoords = getAddress(coords).get(0).getLocality();
                        }
                        else {
                            townByCoords = getAddress(coords).get(0).getAdminArea();
                        }
                        showStartWeather(coords,townByCoords);

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    // Получаем адрес по координатам
    private List<Address> getAddress(final double[] coords){
        final Geocoder geocoder = new Geocoder(this);
        try {
            return geocoder.getFromLocation(coords[0], coords[1], 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            // Запрашиваем эти два Permission’а у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Запрошенный нами
            // Permission
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            }
        }

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
        String town = sharedPref.getString(keyTown,"Чита");

        showStartWeather(null, town);
    }

    private void showStartWeather(double[] coords, String town) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTown fragment = (FragmentTown) fm.findFragmentById(R.id.fragmentTownList);
        if (fragment != null) {
            fragment.onClick(coords, town);
        }
    }
}