package ru.gorbulevsv.androidyandexmapjs;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import ru.gorbulevsv.androidyandexmapjs.Models.IMyLocationListener;

public class MyLocationListener implements LocationListener {
    IMyLocationListener myLocationListener;

    public MyLocationListener(IMyLocationListener myLocationListener){
        this.myLocationListener = myLocationListener;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocationListener.onLocationChanged(location);
    }
}
