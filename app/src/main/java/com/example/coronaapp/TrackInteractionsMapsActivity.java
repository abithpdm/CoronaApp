package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class TrackInteractionsMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location lastLocation;
    private  LatLng UsercurrentLocation;
    private  LatLng requestetinguserLocation;
    private int radius=10000;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    GeoQuery geoQuery;
    private String founduserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_interactions_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {



        if (getApplicationContext()!=null)
        {
            lastLocation=location;

            LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


            String userID= currentUser.getUid();
            DatabaseReference Useravailability = FirebaseDatabase.getInstance().getReference().child("User RealtimeLocation");
            GeoFire geoFire = new GeoFire(Useravailability);

            Searchingpeoplearoundyou();



            geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener(){
                @Override
                public void onComplete(String key, DatabaseError error) {
                }
            });


        }


    }

    private void Searchingpeoplearoundyou() {

        String userID =FirebaseAuth.getInstance().getCurrentUser().getUid();

        founduserID =userID;

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("FindpeopleAround Requests");
        GeoFire geofire =new GeoFire(ref);
        geofire.setLocation(userID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener(){
            @Override
            public void onComplete(String key, DatabaseError error) {
                Getclosestpersontoyou();

            }
        });




        UsercurrentLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(UsercurrentLocation).title("My location"));



    }

    private void Getclosestpersontoyou() {


        DatabaseReference PeopleAvailable=FirebaseDatabase.getInstance().getReference().child("User RealtimeLocation");
        GeoFire geoFire=new GeoFire(PeopleAvailable);
        geoQuery=geoFire.queryAtLocation(new GeoLocation(UsercurrentLocation.latitude,UsercurrentLocation.longitude),radius);
        geoQuery.removeAllListeners();



        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {



                String currentuserID=FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference DriversRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID).child("myinteractions");

                    HashMap drivermap=new HashMap();
                    drivermap.put("interacts", founduserID);
                    DriversRef.updateChildren(drivermap);

                    GetOtherUserLocation();


//                    getDriverInfo();





            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady()
            {
                    radius++;

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }

    private ValueEventListener TheotheruserLocationRefListener;
    private DatabaseReference  otherUserLocationRef;

    private void GetOtherUserLocation() {


        otherUserLocationRef=FirebaseDatabase.getInstance().getReference().child("User RealtimeLocation").child(founduserID).child("l");
        otherUserLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {if (dataSnapshot.exists())
            {
                List<Object> driverLocationmap=(List<Object>)dataSnapshot.getValue();
                double LocationLat=0;
                double LocationLng=0;

                if (driverLocationmap.get(0) !=null)
                {
                    LocationLat=Double.parseDouble(driverLocationmap.get(0).toString());

                } if (driverLocationmap.get(1) !=null)

            {
                LocationLng=Double.parseDouble(driverLocationmap.get(1).toString());
            }

                LatLng driverLatLng=new LatLng(LocationLat,LocationLng);

                Location location1=new Location("");
                location1.setLatitude(UsercurrentLocation.latitude);
                location1.setLongitude(UsercurrentLocation.longitude);


                Location location2=new Location("");
                location2.setLatitude(driverLatLng.latitude);
                location2.setLongitude(driverLatLng.longitude);

                float Distance =location1.distanceTo(location2);




                if (Distance<20)
                {
                }else

                {


                }


            }else {
                Toast.makeText(TrackInteractionsMapsActivity.this, "No person around", Toast.LENGTH_SHORT).show();
            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }








    protected synchronized void buildGoogleApiClient()

    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }



}
