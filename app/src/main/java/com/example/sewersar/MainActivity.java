package com.example.sewersar;

import com.example.sewersar.database.SewersARViewModel;
import com.example.sewersar.database.SewersNode;
import com.example.sewersar.database.SewersPipe;
import com.example.sewersar.sensor.DeviceOrientation;
import com.example.sewersar.utils.ARLocationPermissionHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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
    List<SewersNode> selectedSewersNodes;
    List<SewersPipe> sewersPipes;
    List<SewersPipe> selectedSewersPipes;

    private static TextView myCoordsTextView;

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
        myCoordsTextView = findViewById(R.id.textView);

        selectedSewersNodes = new ArrayList<>();
        selectedSewersPipes = new ArrayList<>();

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
                                for(int i = 0; i < sewersPipes.size(); i++) {
                                    if(sewersNodes.get(sewersPipes.get(i).endNodeIndex).type.equals("Sieć wodociągowa") && sewersNodes.get(sewersPipes.get(i).startNodeIndex).type.equals("Sieć wodociągowa")) {
                                        selectedSewersPipes.add(sewersPipes.get(i));
                                    }
                                }
                                locationScene = new LocationScene(this, arSceneView, selectedSewersPipes);

                                // Adding a simple location marker of a 3D model
                                SewersPipe sp = sewersPipes.get(0);
                                //increaseAccuracy();

                                for (int i = 0; i < sewersNodes.size(); i++) {
                                    if(sewersNodes.get(i).type.equals("Sieć wodociągowa")) {
                                        selectedSewersNodes.add(sewersNodes.get(i));
                                    }
                                }
                                for (int i = 0; i < selectedSewersNodes.size(); i++) {
                                    locationScene.mLocationMarkers.add(
                                            new LocationMarker(
                                                    selectedSewersNodes.get(i).lon,
                                                    selectedSewersNodes.get(i).lat,
                                                    getSewersNode(new Color(android.graphics.Color.parseColor(selectedSewersNodes.get(i).color)), selectedSewersNodes.get(i).lon, selectedSewersNodes.get(i).lat)));

                                }
                                /*for(int i = 0; i < sewersPipes.size(); i++) {
                                    double lat1, lat2, lon1, lon2;
                                    lat1 = sewersNodes.get(sewersPipes.get(i).startNodeIndex).lat;
                                    lon1 = sewersNodes.get(sewersPipes.get(i).startNodeIndex).lon;
                                    lat2 = sewersNodes.get(sewersPipes.get(i).endNodeIndex).lat;
                                    lon2 = sewersNodes.get(sewersPipes.get(i).endNodeIndex).lon;
                                    addPipe(lat1, lon1, lat2, lon2, new Color(android.graphics.Color.RED));
                                }*/
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

    public void selectSewersType(View view) {
        Intent intent = new Intent(this, SelectSewersTypeActivity.class);

        startActivity(intent);
    }

    public static void setMyCoordsTextView(String text) {
        if(MainActivity.myCoordsTextView != null) {
            MainActivity.myCoordsTextView.setText(text);
        }
    }

    private void increaseAccuracy() {
        for (int i = 0; i < sewersPipes.size(); i++) {
            SewersNode sewersNode1 = sewersNodes.get(sewersPipes.get(i).startNodeIndex);
            SewersNode sewersNode2 = sewersNodes.get(sewersPipes.get(i).endNodeIndex);
            double distance = measureDistance(sewersNode1.lat, sewersNode2.lat, sewersNode1.lon, sewersNode2.lon);
            if(distance > 2.0) {
                int pipeIndex = 0;
                for (int j = 0; j < sewersPipes.size(); j++) {
                    if(sewersPipes.get(j).startNodeIndex == sewersPipes.get(j).startNodeIndex && sewersPipes.get(j).endNodeIndex == sewersPipes.get(j).endNodeIndex) {
                        pipeIndex = j;
                    }
                }
                sewersPipes.remove(pipeIndex);
                addPipes(sewersNode1.index, sewersNode2.index);
            }

        }
    }

    private void addPipes(int sewersNode1Index, int sewersNode2Index) {
        double distance = measureDistance(sewersNodes.get(sewersNode1Index).lat, sewersNodes.get(sewersNode2Index).lat, sewersNodes.get(sewersNode1Index).lon, sewersNodes.get(sewersNode2Index).lon);
        double lon1,lon2,lat1,lat2;
        if(distance > 2.0) {
            SewersNode newSewersNode = new SewersNode();
            int newSewersNodeIndex = sewersNodes.size();
            newSewersNode.index = newSewersNodeIndex;

            double dLon = Math.toRadians(sewersNodes.get(sewersNode2Index).lon - sewersNodes.get(sewersNode1Index).lon);
            lat1 = Math.toRadians(sewersNodes.get(sewersNode1Index).lat);
            lat2 = Math.toRadians(sewersNodes.get(sewersNode2Index).lat);
            lon1 = Math.toRadians(sewersNodes.get(sewersNode1Index).lon);

            double Bx = Math.cos(lat2) * Math.cos(dLon);
            double By = Math.cos(lat2) * Math.sin(dLon);
            double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
            double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

            newSewersNode.lon = Math.toDegrees(lon3);
            newSewersNode.lat = Math.toDegrees(lat3);
            newSewersNode.color = "";
            sewersNodes.add(newSewersNode);

            addPipes(sewersNode1Index, newSewersNodeIndex);
            addPipes(newSewersNodeIndex, sewersNode2Index);
        } else {
            SewersPipe newSewersPipe = new SewersPipe();
            newSewersPipe.index = sewersPipes.size();
            newSewersPipe.startNodeIndex = sewersNode1Index;
            newSewersPipe.endNodeIndex = sewersNode2Index;
            newSewersPipe.color = "";
            sewersPipes.add(newSewersPipe);
            return;
        }
    }

    private void addPipe(double lat1, double lon1, double lat2, double lon2, Color pipeColor) {
        double newLon = (lon1 + lon2)/2;
        double newLat = (lat1 + lat2)/2;
        float newDistance = measureDistance(lat1, lat2, lon1, lon2);
        double bearing = countBearing(lat1, lat2, lon1, lon2);
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

    private double countBearing(double lat1, double lat2, double lon1, double lon2) {
        double dLon = Math.abs(lon1 - lon2);
        double y2 = Math.sin(dLon) * Math.cos(lat2);
        double x2 = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y2, x2));
        return bearing;
    }


    private float measureDistance(double lat1, double lat2, double lon1, double lon2){  // generally used geo measurement function
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

    private Node getSewersNode(Color nodeColor, double lon, double lat) {
        Node base = new Node();
        MaterialFactory.makeOpaqueWithColor(this, nodeColor)
                .thenAccept(
                        material -> {
                            sewersNode =
                                    ShapeFactory.makeSphere(0.2f, new Vector3(0.0f, 0.00f, 0.0f), material);


                            base.setRenderable(sewersNode );
                            Context c = this;
                            base.setOnTapListener((v, event) -> {
                                Toast.makeText(
                                        c, "Węzeł sieci \n Lat: " + lat + " Lon: " + lon, Toast.LENGTH_LONG)
                                        .show();
                            });

                        });
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
