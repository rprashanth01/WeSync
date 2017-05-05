package prashanth.wesync;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;

import static prashanth.wesync.AppConstants.PERMISSION_READ_CONTACTS;
import static prashanth.wesync.AppConstants.PERMISSION_REQUEST_LOCATION;

/**
 * Created by Abhi on 4/21/2017.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_DELAY = 3000;
    ArrayList<ContactList> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestAllPermissionsLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }

    private void requestAllPermissionsLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);

        }

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
                    requestAllPermissionsContact();
                } else {
                    Toast.makeText(this,"Location Permission required for app to run", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
    private void requestAllPermissionsContact() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS);

        }else{
        }

    }



}
