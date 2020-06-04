package com.example.sewersar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.sewersar.LocationMarker;
//import uk.co.appoly.arcorelocation.LocationScene;
//import uk.co.appoly.arcorelocation.rendering.LocationNode;
//import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
//import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import com.example.sewersar.sensor.DeviceOrientation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import  android.app.Activity;
import  android.app.ActivityManager;
import 	android.os.Build;
import 	android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import 	android.widget.Toast;
import 	android.content.Context;

import com.example.sewersar.utils.ARLocationPermissionHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import  com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import  com.google.ar.sceneform.ux.ArFragment;
import 	android.net.Uri;
import 	android.view.Gravity;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean installRequested;
    private boolean hasFinishedLoading = false;


    private ArSceneView arSceneView;
    private ModelRenderable andyRenderable;

    private LocationScene locationScene;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ModelRenderable redSphereRenderable;
    private ModelRenderable pipe;

    private DeviceOrientation dOrientation;

    //18.5730080, 54.3517372
    //18.5729852, 54.3517485
    //18.5729322, 54.3517360
    float points[][] = {
            {18.57305f, 54.35175f},
            {18.57295f, 54.3517f},
            {18.57305f, 54.35165f}};
    /*float points[][] = {
            {18.5730080f, 54.3517372f},
            {18.5729852f, 54.3517485f},
            {18.5731256f, 54.3517114f}
    };*/
    @Override
    @SuppressWarnings({
            "AndroidApiChecker",
            "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!checkIsSupportedDeviceOrFinish(this)) {
            //return;
       // }
        dOrientation = new DeviceOrientation(this);
        dOrientation.resume();

        setContentView(R.layout.activity_sceneform);
        arSceneView = findViewById(R.id.ar_scene_view);


        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build();


        CompletableFuture.allOf(
                andy)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                //DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                andyRenderable = andy.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                //DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this,locationScene, andyRenderable, arSceneView);
        if(ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } else {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        }*/

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            redSphereRenderable =
                                    ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.00f, 0.0f), material); });



        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                // We know that here, the AR components have been initiated.
                                locationScene = new LocationScene(this, arSceneView);


                                // Adding a simple location marker of a 3D model


                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[0][0],
                                                points[0][1],
                                                getAndy()));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[1][0],
                                                points[1][1],
                                                getAndy()));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[2][0],
                                                points[2][1],
                                                getAndy()));

                                double newLon = (points[0][0] + points[2][0])/2;
                                double newLat = (points[0][1] + points[2][1])/2;
                                float newHeight = measure(points[0][1], points[0][0], points[2][1], points[2][0])/2;
                                float newX = points[0][0] - points[2][0];
                                float newY = points[0][1] - points[2][1];
                                float beta = (float)(Math.atan(newY/newX) * 180/Math.PI);


                                double lat1 = points[0][1];
                                double lat2 = points[2][1];
                                double dLon = Math.abs(points[0][0] - points[2][0]);
                                double y2 = Math.sin(dLon) * Math.cos(lat2);
                                double x2 = Math.cos(lat1)*Math.sin(lat2) -
                                        Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
                                double brng = Math.toDegrees(Math.atan2(y2, x2));
                                float o = locationScene.deviceOrientation.getOrientation();
                                double o2 = dOrientation.getOrientation();
                                float angle = (float)(o2+brng+90.0);
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                newLon,
                                                newLat,
                                                getPipe(newHeight, angle, locationScene)));
                                int x = 0;

                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            /*if (loadingMessageSnackbar != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                        hideLoadingMessage();
                                    }
                                }
                            }*/
                        });


        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);


    }


    private float measure(float lat1, float lon1, float lat2, float lon2){  // generally used geo measurement function
        float R = 6378.137f; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return (float)d * 1000; // meters
    }

    private Node getAndy() {
        Node base = new Node();
        base.setRenderable(redSphereRenderable );
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Węzeł sieci", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }

    private Node getPipe(float height, float angle, LocationScene o) {
        Node base = new Node();

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            pipe =
                                    ShapeFactory.makeCylinder(0.1f, height, new Vector3(0.0f, 0.00f, 0.0f), material);


                            base.setRenderable(pipe );
                            Quaternion lookRotation = Quaternion.eulerAngles (new Vector3(0.0f, angle,-90.0f));
                            base.setWorldRotation(lookRotation);
                            Context c = this;
                            base.setOnTapListener((v, event) -> {
                                Toast.makeText(
                                        c, "orientation: " + o.deviceOrientation.getOrientation(), Toast.LENGTH_LONG)
                                        .show();
                            });

                        });
        return base;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            //showLoadingMessage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
