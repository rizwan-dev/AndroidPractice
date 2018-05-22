package com.chai.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE=99;
    private EditText edtLocation;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSearch=(Button)findViewById(R.id.btnSearch);


    }

    public void onClick(View v)
    {
        if(v.getId()==R.id.btnSearch)
        {
            edtLocation=(EditText)findViewById(R.id.edtLocation);
            String location1= edtLocation.getText().toString();
            List<Address> addressList = null;
            if(!TextUtils.isEmpty(location1))
            {
                Geocoder geocoder= new Geocoder(this);
                MarkerOptions markerOptions=new MarkerOptions();
                try {
                    addressList=geocoder.getFromLocationName(location1,5);
                }catch (IOException io)
                {
                 io.printStackTrace();
                }
                for(int i=0;i<addressList.size();i++)
                {
                   Address address=addressList.get(i);
                   LatLng latLng =new LatLng(address.getLatitude(),address.getLongitude());
                    markerOptions.position(latLng);
                    markerOptions.title("your Search Result");
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }

            }

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

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            builtGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }

    }

    protected synchronized void builtGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

             lastLocation=location;
             if(currentLocationMarker!=null)
             {
                 currentLocationMarker.remove();
             }
             LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
             MarkerOptions markerOptions= new MarkerOptions();
             markerOptions.position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
             currentLocationMarker=mMap.addMarker(markerOptions);
             mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
             mMap.animateCamera(CameraUpdateFactory.zoomBy(100));
             if(googleApiClient!=null)
             { LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
             }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
  locationRequest =new LocationRequest();
  locationRequest.setInterval(1000);
  locationRequest.setFastestInterval(1000);
  locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
  if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
  }

    }
    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
                return  false;
            }
            return  false;


        }
        else
            return true;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
