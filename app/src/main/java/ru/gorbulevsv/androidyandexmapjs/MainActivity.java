package ru.gorbulevsv.androidyandexmapjs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;

import ru.gorbulevsv.androidyandexmapjs.Models.IMyLocationListener;
import ru.gorbulevsv.androidyandexmapjs.Models.MyLocation;
import ru.gorbulevsv.androidyandexmapjs.Models.User;

public class MainActivity extends AppCompatActivity implements IMyLocationListener {
    WebView webview;
    TextView textToolbarTitle;
    Button buttonToolbarLogout, buttonMapToCenter, buttonAddPlacemarks;
    int LOCATION_REFRESH_TIME = 5000; // 1 seconds to update
    int LOCATION_REFRESH_DISTANCE = 0; // 1 meters to update
    int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    FirebaseAuth auth;
    FirebaseDatabase database;
    LocationManager locationManager;
    MyLocationListener locationListener;
    SharedPreferences preferences;
    User user;
    boolean gettingLocationIsRun = false;
    HashMap<String, User> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        user = new User() {{
            isDriver = true;
            isOnline = true;
        }};
        gettingLocationIsRun = true;
        addAllUsersOnMap();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);
        requestLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, locationListener);
        }
    }

    void initControls() {
        webview = findViewById(R.id.webview);
        webview.addJavascriptInterface(new JavaScript(this), "android");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/index.html");

        textToolbarTitle = findViewById(R.id.toolbarTitle);
        buttonToolbarLogout = findViewById(R.id.buttonToolbarLogout);
        buttonToolbarLogout.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();
                DatabaseReference users = database.getReference().child("users").child(userId);
                users.child("isOnline").setValue(false);
            }
            auth.signOut();
            preferences.edit().putString("email", "").apply();
            preferences.edit().putString("password", "").apply();
            startActivity(new Intent(this, RegisterLoginActivity.class));
        });

        buttonMapToCenter = findViewById(R.id.buttonMapToCenter);
        buttonMapToCenter.setOnClickListener(view -> {
            webview.loadUrl("javascript:setCenter(" + user.myLocation.latitude + "," + user.myLocation.longitude + ")");
        });

        buttonAddPlacemarks = findViewById(R.id.buttonAddPlacemarks);
        buttonAddPlacemarks.setOnClickListener(view -> {
            addAllUsersOnMap();
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        webview.loadUrl("javascript:setCoord(" + location.getLatitude() + "," + location.getLongitude() + ")");

        if (FirebaseAuth.getInstance().getCurrentUser() != null && gettingLocationIsRun) {
            String userId = auth.getCurrentUser().getUid();

            user.myLocation = new MyLocation(location.getLatitude(), location.getLongitude());
            textToolbarTitle.setText(userId);
            DatabaseReference users = database.getReference().child("users").child(userId);
            users.setValue(user);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gettingLocationIsRun = true;
        //addAllUsersOnMap();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gettingLocationIsRun = false;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            DatabaseReference users = database.getReference().child("users").child(userId);
            users.child("isOnline").setValue(false);
            webview.loadUrl("javascript:removePlacemark()");
        }
    }

    class MyAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            return null;
        }
    }

    void addAllUsersOnMap() {
        GenericTypeIndicator<HashMap<String, User>> t = new GenericTypeIndicator<HashMap<String, User>>() {
        };
        database.getReference().child("users").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                users = task.getResult().getValue(t);
//                Collection<User> v = users.values();
//                Gson gson = new Gson();
//                webview.loadUrl("javascript:addPlacemarkCollection(" + gson.toJson(v) + ")");
//            }
            if (task.isSuccessful()) {
                users = task.getResult().getValue(t);
                Gson gson = new Gson();
                String s = gson.toJson(users);
                webview.loadUrl("javascript:addPlacemarkMap(" + gson.toJson(users) + ")");
            }
        });
    }
}