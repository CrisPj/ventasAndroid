package pythonteam.com.ventasapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.models.Latlong;
import pythonteam.com.ventasapp.models.Route;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener{


    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker marca;
    boolean bandGPS  = false;
    boolean bandRED = false;
    double latActual, lonActual, latMarca, lonMarca;
    Double latCustomer, longCustomer;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int routeID = 0;

    @BindView(R.id.fab_map_ok) FloatingActionButton fab_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        latCustomer = extras.getDouble("actualLat");
        longCustomer = extras.getDouble("actualLong");
        routeID = extras.getInt("routeID");

        if (checkLocationPermission()){

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if( routeID == 0){
                fab_ok.setOnClickListener(v -> {
                    DataApplication.tempLatlong = new Latlong();
                    DataApplication.tempLatlong.setX(latMarca);
                    DataApplication.tempLatlong.setY(lonMarca);
                    Toast.makeText(getApplicationContext(),"Ubicación modificada",Toast.LENGTH_SHORT).show();
                    finish();
                });
            }else{
                fab_ok.setOnClickListener(v -> finish());
            }

        }else{
            finish();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if( routeID == 0){
            if (latCustomer==0 && longCustomer==0){
                getGeoLocation();
                latMarca = latActual; lonMarca = lonActual;
            }else {
                latMarca = latCustomer; lonMarca = longCustomer;
            }
            LatLng myUbication = new LatLng( latMarca , lonMarca);
            marca = mMap.addMarker(new MarkerOptions().position(myUbication).title("Cliente"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myUbication,15));
        }else{



            Retrofits.get().getEmployeeRoute(SharedPreferencesManager.read(SharedPreferencesManager.USER_ID,-1)).enqueue(new Callback<ArrayList<Route>>() {
                @Override
                public void onResponse(Call<ArrayList<Route>> call, Response<ArrayList<Route>> response) {
                    if(response.isSuccessful()){

                        ArrayList<Route> rutas = response.body();
                        for( int i=0; i<rutas.size(); i++ ){
                            if (rutas.get(i).getIdRoute() == routeID ){
                                LatLng mark = new LatLng(
                                        rutas.get(i).getLatLong().getX(),
                                        rutas.get(i).getLatLong().getY());
                                marca = mMap.addMarker(new MarkerOptions().position(mark).title("Cliente"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark,13));
                            }
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Response error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ArrayList<Route>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

        if( routeID == 0){
            latMarca = latLng.latitude;
            lonMarca = latLng.longitude;

            if(marca != null) {
                marca.remove();
            }
            marca = mMap.addMarker(new MarkerOptions().position(latLng).title("Nueva ubicación"));
        }

    }

    private void getGeoLocation()
    {
        Location myUbication;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        bandGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        bandRED = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        try{
            if(bandGPS)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,10,this);
                myUbication = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latActual = myUbication.getLatitude();
                lonActual = myUbication.getLongitude();
                Toast.makeText(this,"Posición GPS exitosa "+latActual+":"+lonActual,Toast.LENGTH_SHORT).show();
            }else{
                if(bandRED)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,10,this);
                    myUbication = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latActual = myUbication.getLatitude();
                    lonActual = myUbication.getLongitude();
                    Toast.makeText(this,"Posición Red exitosa "+latActual+":"+lonActual, Toast.LENGTH_SHORT).show();
                }
            }

        }catch (SecurityException se){

        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permiso de localización")
                        .setMessage("Para continuar, necesitamos los permisos de localización de tu dispositivo")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}