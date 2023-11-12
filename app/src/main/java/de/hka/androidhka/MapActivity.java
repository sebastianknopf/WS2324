package de.hka.androidhka;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.hka.androidhka.network.EfaApiClient;
import de.hka.androidhka.objects.EfaCoordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        XYTileSource mapServer = new XYTileSource("MapServer",
                8,
                20,
                256,
                ".png",
                new String[]{"https://tileserver.svprod01.app/styles/default/"}
        );

        String authorizationString = this.getMapServerAuthorizationString(
                "ws2223@hka",
                "LeevwBfDi#2027"
        );

        Configuration
                .getInstance()
                .getAdditionalHttpRequestProperties()
                .put("Authorization", authorizationString);

        this.mapView = this.findViewById(R.id.mapView);
        this.mapView.setTileSource(mapServer);

        GeoPoint startPoint = new GeoPoint(49.0069, 8.4037);

        IMapController mapController = mapView.getController();
        mapController.setZoom(14.0);
        mapController.setCenter(startPoint);

        this.mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                loadClosestStops(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                setupLocationListener();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });

        Log.i("MainActivity", "onResume");
    }


    @SuppressLint("MissingPermission")
    private void setupLocationListener()
    {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                GeoPoint startPoint = new GeoPoint(latitude, longitude);

                IMapController mapController = mapView.getController();
                mapController.setCenter(startPoint);
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
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE
        );

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                locationListener
        );

    }

    private String getMapServerAuthorizationString(String username, String password)
    {
        String authorizationString = String.format("%s:%s", username, password);
        return "Basic " + Base64.encodeToString(authorizationString.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    private void loadClosestStops(double latitude, double longitude) {
         Call<EfaCoordResponse> efaCall = EfaApiClient
                .getInstance()
                .getClient()
                .loadStopsWithinRadius(
                        EfaApiClient
                                .getInstance()
                                .createCoordinateString(
                                        latitude,
                                        longitude
                                ),
                        10000
                );

         efaCall.enqueue(new Callback<EfaCoordResponse>() {
             @Override
             public void onResponse(Call<EfaCoordResponse> call, Response<EfaCoordResponse> response) {
                 Log.d("MapActivity", String.format("Response %d Locations", response.body().getLocations().size()));
             }

             @Override
             public void onFailure(Call<EfaCoordResponse> call, Throwable t) {
                 Log.d("MapActivity", "Failure");
             }
         });
    }
}