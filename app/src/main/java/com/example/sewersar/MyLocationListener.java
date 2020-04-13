package com.example.sewersar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

public class MyLocationListener implements LocationListener {

    private ModelRenderable andyRenderable;
    private LocationScene locationScene;
    private MainActivity mainActivity;

    private ArSceneView arSceneView;
    private Frame arFrame;

    MyLocationListener(MainActivity mainActivity, LocationScene locationScene, ModelRenderable andyRenderable, ArSceneView arSceneView) {
        this.mainActivity = mainActivity;
        this.locationScene = locationScene;
        this.andyRenderable = andyRenderable;

        this.arSceneView = arSceneView;
        this.arFrame = arFrame;
    }

    @Override
    public void onLocationChanged(Location loc) {
        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();

        /*
        if (locationScene == null) {
            locationScene = new LocationScene(mainActivity, arSceneView);
            LocationMarker locationMarker = new LocationMarker(loc.getLongitude(), loc.getLatitude(), getAndy());
            locationScene.mLocationMarkers.add(locationMarker);
            locationMarker.anchorNode.setEnabled(true);

            locationScene.refreshAnchors();
        }

        if (locationScene != null) {
            locationScene.processFrame(arFrame);
        }*/


    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private Node getAndy() {
        Node base = new Node();
        base.setRenderable(andyRenderable );
        Context c = mainActivity;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Andy touched.", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }
}