package prashanth.wesync;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import prashanth.wesync.models.UserInfo;

import static prashanth.wesync.AppConstants.PERMISSION_REQUEST_LOCATION;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    String userLatitude = "";
    String userLongitude = "";
    //String userId = "";

    private LocationRequest mLocationRequest;

    Context context;
    Location mLastLocation;
    String userId = "";

    UserInfo userGlobal;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(!(getIntent().getStringExtra("email").equals("")) && getIntent().getStringExtra("email") != null){
            userId = getIntent().getStringExtra("email").replaceAll("\\.","");
        }

        requestAllPermissionsLocation();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        //userId = mDatabase.push().getKey();


        // creating user object
        ArrayList<String> interests = new ArrayList<>();
        interests.add("Movies");
        interests.add("Sports");
        UserInfo user = new UserInfo("Asb","luke@gmail.com","1234567890",33.4190996, -111.934596,interests);

        //mDatabase.setValue(user);
        mDatabase.child("users").child(userId).setValue(user);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users/"+userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userGlobal = dataSnapshot.getValue(UserInfo.class);
                ((GlobalClass) MenuActivity.this.getApplication()).setCurrentUser(userGlobal);

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

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

                } else {
                    Toast.makeText(this,"Location Permission required for app to run", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void requestAllPermissionsLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);

        }

    }




    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    public void editProfile(View v){

        Intent intent = new Intent(MenuActivity.this, EditProfileActivity.class);
        startActivity(intent);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                userLatitude = String.valueOf(mLastLocation.getLatitude());
                userLongitude = String.valueOf(mLastLocation.getLongitude());
                Log.d("Location Handler ", "User Location is" + userLongitude + "  " + userLatitude);
                // Begin polling for new location updates.
                startLocationUpdates();
            }
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60000)
                .setFastestInterval(60000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

    }

    public void findFriendsByDest(View v){
        Intent destIntent = new Intent(MenuActivity.this,DestinationMapsActivity.class);
        startActivity(destIntent);
    }

    public void findFriendsByInterest(View v){
        Intent interestIntent = new Intent(MenuActivity.this,InterestsMapsActivity.class);
        startActivity(interestIntent);
    }

    public void getAllUsers(View v){
        Intent userIntent = new Intent(MenuActivity.this,UsersListActivity.class);
        startActivity(userIntent);
    }

    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Map<String, Object> userLocationUpdates = new HashMap<String, Object>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users/"+userId);
        DatabaseReference latRef = usersRef.child("latitude");
        DatabaseReference longRef = usersRef.child("longitude");
        latRef.setValue(location.getLatitude());
        longRef.setValue(location.getLongitude());
        /*userLocationUpdates.put(userId, new UserInfo(null, null, location.getLatitude(), location.getLongitude(),null));
        usersRef.updateChildren(userLocationUpdates);*/
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}