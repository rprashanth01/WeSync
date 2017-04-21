package prashanth.wesync;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;
import prashanth.wesync.models.UserInfo;

public class EventsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String eventName = "";
    String eventEmails = "";

    ArrayList<ContactList> UsersFromPhone;
    ArrayList<UserInfo> dbUsersFromPhone = new ArrayList<>();
    ArrayList<String> matchedUserNames = new ArrayList<>();

    ArrayList<UserInfo> userList = new ArrayList<>();

    ArrayList<String> phoneNo = new ArrayList<>();
    ArrayList<String> emailIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        eventEmails = getIntent().getStringExtra("eventEmails");
        eventName = getIntent().getStringExtra("eventName");
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

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //userLocal = ((GlobalClass) this.getApplication()).getCurrentUser();
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
     


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }
}
