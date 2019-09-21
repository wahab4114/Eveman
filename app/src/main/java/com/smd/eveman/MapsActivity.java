package com.smd.eveman;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private Button mSubmitBtn;

    Location mLastLocation;
    Location mTappedLocation;
    Marker mCurrLocationMarker;
    Marker mTappedLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String mPlaceName;


    Location startLocation;
    Location endLocation;

    AutocompleteSupportFragment autocompleteFragment;

    boolean viewsingle_location;

    boolean viewsingle_location_u;


    private ProgressDialog mRegProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("---------->", "In On create");


        viewsingle_location = false;
        viewsingle_location_u = false;

        setContentView(R.layout.new_maps);
        mRegProgressDialog = new ProgressDialog(this);

        mSubmitBtn = (Button) findViewById(R.id.selectEvent);

        mTappedLocation = new Location("");


        mPlaceName = "";

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getSerializableExtra("location")!=null)
        {
            viewsingle_location=true;
        }
        else if(getIntent().getSerializableExtra("locationU")!=null)
        {
            viewsingle_location_u = true;
        }


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyD6rVSBO2oIxAgzFmq8DOPbP8o_W5ny-R4");
        }

        // Initialize the AutocompleteSupportFragment.
         autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("----->", "Place: " + place.getName() + ", " + place.getId());
                //fab.setVisibility(View.VISIBLE);
                mPlaceName = place.getName();
                LatLng latlong= place.getLatLng();
                mTappedLocation.setLatitude(latlong.latitude);
                mTappedLocation.setLongitude(latlong.longitude);
                placeMarker(mTappedLocation);
                Toast.makeText(MapsActivity.this, "place:"+place.getName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Status status) {
                Log.i("----->", "An error occurred: " + status);

            }
        });




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(getApplicationContext(),"Tap anywhere to select event location", Toast.LENGTH_LONG).show();

        mMap = googleMap;

        view_single_location();
        view_single_locationU();

        Log.e("---------->", "OnMapReady");
        // check if the device is greater than equal to marshmallow

        if(viewsingle_location == false)
        {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // check if location permissions are already granted?
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.e("---------->", "Permissions already granted");

                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    } else {

                        Log.e("---------->", "Request for the permission made");

                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_CODE);

                    }
                } else {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }



            if(viewsingle_location_u==false)
            {
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        mTappedLocation.setLatitude(latLng.latitude);
                        mTappedLocation.setLongitude(latLng.longitude);
                        placeMarker(mTappedLocation);


                    }
                });
            }


        }





        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addeventintent = new Intent(MapsActivity.this,AddEventActivity.class);
                addeventintent.putExtra("name",mPlaceName);
                addeventintent.putExtra("lat",mTappedLocationMarker.getPosition().latitude);
                addeventintent.putExtra("long",mTappedLocationMarker.getPosition().longitude);
                addeventintent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(addeventintent);
                finish();

            }
        });


    }

    // connect api client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("---------->", "In OnLocationChanged");

        mRegProgressDialog.dismiss();

        mLastLocation = location;
        endLocation = location;

        if(viewsingle_location_u==false)
        placeMarker(mLastLocation);

    }

    public void placeMarker(Location location)
    {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            if(mTappedLocationMarker != null)
            {
                mTappedLocationMarker.remove();
            }
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(latLng);
            markerOptions2.title("Tapped Position");
            markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mTappedLocationMarker = mMap.addMarker(markerOptions2);


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) MapsActivity.this);
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("---------->", "In On connected");


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3 * 1000);
        mLocationRequest.setFastestInterval(3 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mRegProgressDialog.setTitle("Fetching current location");
            mRegProgressDialog.setMessage("Please wait...");
            mRegProgressDialog.setCanceledOnTouchOutside(false);
            mRegProgressDialog.show();

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) MapsActivity.this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e("------->","connection ended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("---------->","in OnRequest Permission result");

        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {


            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)

            {

                try {

                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);

                } catch (SecurityException e) {

                }



            }


        }
    }


    void view_single_location()
    {
        Event location = (Event) getIntent().getSerializableExtra("location");
        if(viewsingle_location==true) {

            Log.e("---------->", "In View single loc");
            autocompleteFragment.setUserVisibleHint(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);

            findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
            findViewById(R.id.selectEvent).setVisibility(View.GONE);

            Location fakelocation = new Location("");
            fakelocation.setLatitude(location.Latitude);
            fakelocation.setLongitude(location.Longitude);
            placeMarker(fakelocation);
        }
    }

    void view_single_locationU()
    {
        Event location = (Event) getIntent().getSerializableExtra("locationU");
        if(viewsingle_location_u==true) {

            Log.e("---------->", "In View single locUUUU");
            autocompleteFragment.setUserVisibleHint(false);
/*
            mMap.getUiSettings().setAllGesturesEnabled(false);
*/

            findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
            findViewById(R.id.selectEvent).setVisibility(View.GONE);

            Location fakelocation = new Location("");
            fakelocation.setLatitude(location.Latitude);
            fakelocation.setLongitude(location.Longitude);
            startLocation = fakelocation;
            placeMarker(fakelocation);
        }
    }



}
