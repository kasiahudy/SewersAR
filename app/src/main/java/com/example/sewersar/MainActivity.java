package com.example.sewersar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

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
import 	android.widget.Toast;
import 	android.content.Context;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import  com.google.ar.sceneform.rendering.ModelRenderable;
import  com.google.ar.sceneform.ux.ArFragment;
import 	android.net.Uri;
import 	android.view.Gravity;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private final int REQUEST_PERMISSION_LOCATION=1;

    private ArFragment arFragment;
    private ModelRenderable lampPostRenderable;
    private ModelRenderable andyRenderable;

    private LocationScene locationScene;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    @SuppressWarnings({
            "AndroidApiChecker",
            "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }




        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        ArSceneView arSceneView = arFragment.getArSceneView();
        Frame arFrame = arSceneView.getArFrame();

        ModelRenderable.builder()
                .setSource(this, Uri.parse("Pipe.sfb"))
                .build()
                .thenAccept(renderable -> lampPostRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast =
                            Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });

        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build();


        CompletableFuture.allOf(andy)
                .handle(
                        (notUsed, throwable) ->
                        {
                            if (throwable != null) {
                                //DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                andyRenderable = andy.get();

                            } catch (InterruptedException | ExecutionException ex) {
                                //DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }
                            return null;
                        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this,locationScene, andyRenderable, arSceneView, arFrame);
        if(ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        }

        /*arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy2 = new TransformableNode(arFragment.getTransformationSystem());
                    andy2.setParent(anchorNode);
                    andy2.setRenderable(andyRenderable);
                    andy2.select();
                });
*/

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {

                            if (locationScene == null) {
                                locationScene = new LocationScene(this, arSceneView);
                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                18.5729,
                                                54.3516,
                                                getAndy()));
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(arFrame);
                            }

                        });

    }

    private Node getAndy() {
        Node base = new Node();
        base.setRenderable(andyRenderable );
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Andy touched.", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later",
                    Toast.LENGTH_LONG).show();
            activity.finish();
            return false;    }
        String openGlVersionString = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                .getDeviceConfigurationInfo()
                .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later",
                    Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
