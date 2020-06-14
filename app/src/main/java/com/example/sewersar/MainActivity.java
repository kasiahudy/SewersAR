package com.example.sewersar;

import com.example.sewersar.database.SewersARViewModel;
import com.example.sewersar.database.SewersNode;
import com.example.sewersar.database.SewersPipe;
import com.example.sewersar.sensor.DeviceOrientation;
import com.example.sewersar.utils.ARLocationPermissionHelper;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sewersar.utils.LocationUtils;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ArSceneView;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private boolean installRequested;
    private boolean hasFinishedLoading = false;


    private ArSceneView arSceneView;
    private ModelRenderable andyRenderable;

    private LocationScene locationScene;


    private ModelRenderable sewersNode;
    private ModelRenderable pipe;

    private DeviceOrientation dOrientation;

    private SewersARViewModel mSewersARViewModel;
    List<SewersNode> sewersNodes;
    List<SewersPipe> sewersPipes;

    //18.5730080, 54.3517372
    //18.5729852, 54.3517485
    //18.5729322, 54.3517360
    float points[][] = {
            {18.57302f, 54.35172f},
            {18.5725f, 54.35175f},
            {18.57303f, 54.351715f},

            {18.57305f, 54.35171f},
            {18.57315f, 54.3519f},

            {18.57292f, 54.3517f},
            {18.5729f, 54.35176f}};
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
        dOrientation = new DeviceOrientation(this);
        dOrientation.resume();

        setContentView(R.layout.activity_sceneform);
        arSceneView = findViewById(R.id.ar_scene_view);

        mSewersARViewModel = new ViewModelProvider(this).get(SewersARViewModel.class);
        mSewersARViewModel.getAllNodes().observe(this, new Observer<List<SewersNode>>() {
            @Override
            public void onChanged(@Nullable final List<SewersNode> newSewersNodes) {
                sewersNodes = newSewersNodes;
            }
        });

        mSewersARViewModel.getAllPipes().observe(this, new Observer<List<SewersPipe>>() {
            @Override
            public void onChanged(@Nullable final List<SewersPipe> newSewersPipes) {
                sewersPipes = newSewersPipes;
            }
        });

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

        /*MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            sewersNode =
                                    ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.00f, 0.0f), material); });*/

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
                                SewersPipe sp = sewersPipes.get(0);
                                for (int i = 0; i < sewersNodes.size(); i++) {
                                    locationScene.mLocationMarkers.add(
                                            new LocationMarker(
                                                    sewersNodes.get(i).lon,
                                                    sewersNodes.get(i).lat,
                                                    getSewersNode(new Color(android.graphics.Color.RED))));
                                }

                                //addPipe(sewersNodes.get(0).lat, sewersNodes.get(0).lon, sewersNodes.get(2).lat, sewersNodes.get(2).lon, new Color(android.graphics.Color.RED));
                                /*locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[0][0],
                                                points[0][1],
                                                getSewersNode(new Color(android.graphics.Color.RED))));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[1][0],
                                                points[1][1],
                                                getSewersNode(new Color(android.graphics.Color.RED))));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[2][0],
                                                points[2][1],
                                                getSewersNode(new Color(android.graphics.Color.RED))));




                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[3][0],
                                                points[3][1],
                                                getSewersNode(new Color(android.graphics.Color.BLUE))));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[4][0],
                                                points[4][1],
                                                getSewersNode(new Color(android.graphics.Color.BLUE))));

                                //addPipe(points[3][1], points[3][0], points[4][1], points[4][0], new Color(android.graphics.Color.BLUE));


                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[5][0],
                                                points[5][1],
                                                getSewersNode(new Color(android.graphics.Color.YELLOW))));

                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                points[6][0],
                                                points[6][1],
                                                getSewersNode(new Color(android.graphics.Color.YELLOW))));

                                //addPipe(points[5][1], points[5][0], points[6][1], points[6][0], new Color(android.graphics.Color.YELLOW));*/


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

                                /*final LocationMarker marker = locationScene.mLocationMarkers.get(0);
                                final LocationMarker marker2 = locationScene.mLocationMarkers.get(2);

                                marker.anchorNode.setParent(arSceneView.getScene());
                                Vector3 point1, point2;
                                point1 = marker.anchorNode.getWorldPosition();
                                point2 = marker2.anchorNode.getWorldPosition();

                                final Vector3 difference = Vector3.subtract(point1, point2);
                                final Vector3 directionFromTopToBottom = difference.normalized();
                                final Quaternion rotationFromAToB =
                                        Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
                                MaterialFactory.makeOpaqueWithColor(this, new Color(255, 0, 0))
                                        .thenAccept(
                                                material -> {
                                                    ModelRenderable model = ShapeFactory.makeCylinder(0.1f, difference.length(),
                                                            new Vector3(0f, 0f, 0f), material);
                                                    model.setShadowReceiver(false);
                                                    model.setShadowCaster(false);

                                                    // 3. make node
                                                    Node node = new Node();

                                                    node.setParent(marker.anchorNode);
                                                    node.setRenderable(model);

                                                    node.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                                                    // 4. set rotation
                                                    node.setWorldRotation(Quaternion.multiply(rotationFromAToB,
                                                            Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90)));
                                                }
                                        );*/
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

    private void addPipe(double lat1, double lon1, double lat2, double lon2, Color pipeColor) {
        double newLon = (lon1 + lon2)/2;
        double newLat = (lat1 + lat2)/2;
        float newDistance = measureDistance(lat1, lon1, lat2, lon2);
        double bearing = LocationUtils.bearing(lat1, lon1, lat2, lon2);
        double currentOrientation = dOrientation.getOrientation();
        if(bearing < 90.0) {
            bearing += 90.0;
        } else if(bearing > 270.0) {
            bearing -= 270.0;
        }
        float angle = (float)(currentOrientation + bearing);
        locationScene.mLocationMarkers.add(
                new LocationMarker(
                        newLon,
                        newLat,
                        getPipe(newDistance, angle, pipeColor, locationScene)));
    }

    private double countBearing(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.abs(lon1 - lon2);
        double y2 = Math.sin(dLon) * Math.cos(lat2);
        double x2 = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y2, x2));
        return bearing;
    }


    private float measureDistance(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
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

    private Node getSewersNode(Color nodeColor) {
        Node base = new Node();
        MaterialFactory.makeOpaqueWithColor(this, nodeColor)
                .thenAccept(
                        material -> {
                            sewersNode =
                                    ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.00f, 0.0f), material);


                            base.setRenderable(sewersNode );
                            Context c = this;
                            base.setOnTapListener((v, event) -> {
                                Toast.makeText(
                                        c, "Węzeł sieci", Toast.LENGTH_LONG)
                                        .show();
                            });

                        });

        /*base.setRenderable(sewersNode );
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Węzeł sieci", Toast.LENGTH_LONG)
                    .show();
        });*/
        return base;
    }

    private Node getPipe(float height, float angle, Color pipeColor, LocationScene o) {
        Node base = new Node();

        MaterialFactory.makeOpaqueWithColor(this, pipeColor)
                .thenAccept(
                        material -> {
                            pipe =
                                    ShapeFactory.makeCylinder(0.1f, height, new Vector3(0.0f, 0.00f, 0.0f), material);


                            base.setRenderable(pipe );
                            Quaternion lookRotation = Quaternion.eulerAngles (new Vector3(0.0f, angle,-90.0f));
                            base.setWorldRotation(lookRotation);
                            //base.setLocalPosition(new Vector3(0.0f, -3.00f, 0.0f));
                            Context c = this;
                            base.setOnTapListener((v, event) -> {
                                Toast.makeText(
                                        c, "orientation: " + o.deviceOrientation.getOrientation() + "\n dlugosc: " + height, Toast.LENGTH_LONG)
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
