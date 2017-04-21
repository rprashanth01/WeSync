package prashanth.wesync;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static prashanth.wesync.AppConstants.PERMISSION_SEND_SMS;

public class SendSMSActivity extends AppCompatActivity {

    Button sendSMSButton;
    EditText messageText;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        requestPermissionsSMS();
        phoneNumber = getIntent().getStringExtra("phone");
        sendSMSButton = (Button)findViewById(R.id.send);
        messageText = (EditText)findViewById(R.id.msg);

        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageText.getText().toString();
                sendmsg(phoneNumber, msg);
            }
        });
    }

    private void requestPermissionsSMS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);

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
            case PERMISSION_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                    }

                } else {
                    Toast.makeText(this,"SMS Permission required for app to run", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }
    private void sendmsg(String num, String msg) {
        String Sent = "Message sent";
        String Deliver = "Message delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(Sent), 0);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0, new Intent(Deliver), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(Sent));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message delivered", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "Sms not delivered", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(Deliver));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(num, null, msg, sentPI, deliverPI);
    }
}
