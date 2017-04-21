package prashanth.wesync;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import prashanth.wesync.models.ContactList;
import prashanth.wesync.models.UserInfo;

import static prashanth.wesync.AppConstants.PERMISSION_REQUEST_LOCATION;


public class DestinationMapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocation;
    LocationRequest mLocationRequest;
    public double destLatitude ;
    public double destLongitude;

    ArrayList<UserInfo> dbUsersFromPhone = new ArrayList<>();
    ArrayList<ContactList> UsersFromPhone;
    Address address;

    ArrayList<String> matchedUserNames = new ArrayList<>();

    ArrayList<UserInfo> userList = new ArrayList<>();

    ArrayList<String> phoneNo = new ArrayList<>();
    ArrayList<String> emailIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     *
     * @param view
     */
    public void onSearch(View view) {


        EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Destination")).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            CameraUpdateFactory.newLatLngZoom(latLng, 17);

            destLatitude = address.getLatitude();
            destLongitude = address.getLongitude();


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot topSnapshot) {

                    for (DataSnapshot snapshot : topSnapshot.getChildren()) {
                        UserInfo user = snapshot.getValue(UserInfo.class);
                        phoneNo.add(user.getPhoneNumber());
                        emailIds.add(user.getEmail());
                        userList.add(user);
                    }
                    getMatchingContacts();

                    for(UserInfo user:dbUsersFromPhone){
                        float[] results = new float[1];
                        Location.distanceBetween(address.getLatitude(), address.getLongitude(), user.getLatitude(), user.getLongitude(), results);
                        float distanceInMeters = results[0];
                        boolean isWithin10km = distanceInMeters < 10000;
                        if(isWithin10km){
                            LatLng latLngFriends = new LatLng(user.getLatitude(), user.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLngFriends).title(user.getName())).showInfoWindow();
                        }
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });




        }

    }

    private void getMatchingContacts() {
        UsersFromPhone = ((GlobalClass) this.getApplication()).getContactList();
        for (ContactList contact : UsersFromPhone) {
            if (phoneNo.contains(contact.getContactNo())) {
                if (!matchedUserNames.contains(contact.getContactName())) {
                    matchedUserNames.add(contact.getContactName());
                    dbUsersFromPhone.add(userList.get(phoneNo.indexOf(contact.getContactNo())));
                }
            } else if (emailIds.contains(contact.getEmail())) {
                if (!matchedUserNames.contains(contact.getContactName())) {
                    matchedUserNames.add(contact.getContactName());
                    dbUsersFromPhone.add(userList.get(emailIds.indexOf(contact.getEmail())));

                }
            }
        }
    }

    public void getDirections(View v){
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+address.getLatitude()+","+address.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
            mGoogleApiClient.connect();

            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.d(this.getClass().getSimpleName(), "Place: " + place.getName());
                }

                @Override
                public void onError(Status status) {

                }

            });

        }

    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this,"Permission required for app to run", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            mMap.clear();
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("You!!");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocation = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera( CameraUpdateFactory.zoomTo(11.0f ) );
            mCurrLocation.showInfoWindow();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //5 seconds
        mLocationRequest.setFastestInterval(10000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
