package com.example.androidlevel2_lesson1.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.EditText;

import com.example.androidlevel2_lesson1.App;
import com.example.androidlevel2_lesson1.BuildConfig;
import com.example.androidlevel2_lesson1.R;
import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;
import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;
import com.example.androidlevel2_lesson1.model.WeatherRequest;
import com.example.androidlevel2_lesson1.model.connection.OpenWeather;
import com.example.androidlevel2_lesson1.model.dataTransfer.DataContainer;
import com.example.androidlevel2_lesson1.model.dataTransfer.PutData;
import com.example.androidlevel2_lesson1.town.MainActivity;
import com.example.androidlevel2_lesson1.weather.FragmentWeather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.androidlevel2_lesson1.weather.FragmentWeather.dataKey;

public class ActivityMap extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private DataContainer currentData;

    private GoogleMap mMap;
    private Marker currentMarker;

    private OpenWeather openWeather;
    private String apiKey;

    private List<Marker> markers = new ArrayList<Marker>();
    private DialogBuilderFragment dlgBuilder;

    private String townByCoords = "Чита";
    private int currentTemp;
    private volatile double[] coords = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestPemissions();

        initDialog();
        initPreferences();
        initRetorfit();
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
        dlgBuilder.setVisibleAddButton(false);
    }

    // Запрашиваем Permission’ы
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                requestRetrofitCoord(latLng,apiKey);
            }
        });

    }

    // Добавление меток на карту
    private void addMarker(LatLng location, String title){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        markers.add(marker);
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

                    final double lat = location.getLatitude(); // Широта
                    final double lng = location.getLongitude(); // Долгота
                    String accuracy = Float.toString(location.getAccuracy());   // Точность
                    final LatLng currentPosition = new LatLng(lat, lng);
                    currentMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentPosition));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 12));
                    if (getAddress(currentPosition).get(0).getLocality() != null) {
                        townByCoords = getAddress(currentPosition).get(0).getLocality();
                    }
                    else {
                        townByCoords = getAddress(currentPosition).get(0).getAdminArea();
                    }
                    tempRetrofitCoord(currentPosition,apiKey);
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


    // Результат запроса Permission’а у пользователя:
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

    private void tempRetrofitCoord(final LatLng latLng, final String keyApi) {
        openWeather.loadWeather((float) latLng.latitude, (float) latLng.longitude, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null) {
                    if (response.body().getList()[0].getMain() != null) {
                        currentTemp = (int) (response.body().getList()[0].getMain().getTemp() - 273.15);
                        currentMarker.setTitle(String.format("%s, %d\u2103",townByCoords, currentTemp));
                    }
                }
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
                //showMessageNotNetwork();
            }
        });
    }

    private void requestRetrofitCoord(final LatLng latLng, String keyApi) {
        openWeather.loadWeather((float) latLng.latitude, (float) latLng.longitude, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null) {
                    if (getAddress(latLng).get(0).getLocality() != null) {
                        addMarker(latLng, String.format("%s, %d\u2103", getAddress(latLng).get(0).getLocality(), (int) (response.body().getList()[0].getMain().getTemp() - 273.15)));
                    }
                    else {
                        addMarker(latLng, String.format("%s, %d\u2103", getAddress(latLng).get(0).getAdminArea(), (int) (response.body().getList()[0].getMain().getTemp() - 273.15)));
                    }
                }
                else {
                    dlgBuilder.show(getSupportFragmentManager(),"dialogBuilder");
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.i("TAG", String.valueOf(t));
                //showMessageNotNetwork();
            }
        });

    }

    // Получаем адрес по координатам
    private List<Address> getAddress(LatLng latLng){
        final Geocoder geocoder = new Geocoder(this);
        try {
            return geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}