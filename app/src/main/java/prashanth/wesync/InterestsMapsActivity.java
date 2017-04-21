package prashanth.wesync;


import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;
import prashanth.wesync.models.UserInfo;

public class InterestsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<ContactList> UsersFromPhone;
    ArrayList<UserInfo> dbUsersFromPhone = new ArrayList<>();
    ArrayList<String> matchedUserNames = new ArrayList<>();

    ArrayList<UserInfo> userList = new ArrayList<>();

    ArrayList<String> phoneNo = new ArrayList<>();
    ArrayList<String> emailIds = new ArrayList<>();

    UserInfo userLocal  = new UserInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        userLocal = ((GlobalClass) this.getApplication()).getCurrentUser();
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
                    ArrayList<String> userInterests = userLocal.getInterests();
                    for(String interest: userInterests){
                        if(user.getInterests().contains(interest)) {
                            float[] results = new float[1];
                            Location.distanceBetween(userLocal.getLatitude(), userLocal.getLongitude(), user.getLatitude(), user.getLongitude(), results);
                            float distanceInMeters = results[0];
                            boolean isWithin10km = distanceInMeters < 10000;
                            if (isWithin10km) {
                                LatLng latLngFriends = new LatLng(user.getLatitude(), user.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLngFriends).title(user.getName()+" Interests: "+interest)).showInfoWindow();
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngFriends));
                                //CameraUpdateFactory.newLatLngZoom(latLngFriends, 11);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngFriends));
                                mMap.animateCamera( CameraUpdateFactory.zoomTo(11.0f ) );
                                //mMap.animateCamera( CameraUpdateFactory.zoomTo(11.0f ) );

                            }

                        }
                    }
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

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
}
