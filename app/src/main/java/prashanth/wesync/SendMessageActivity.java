package prashanth.wesync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SendMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
    }

    public void sendSMS(View view){
        String phoneNumber = getIntent().getStringExtra("phone");
        Intent intent = new Intent(SendMessageActivity.this, SendSMSActivity.class);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
    }

    public void sendEmail(View view){
        String email = getIntent().getStringExtra("email");
        Intent intent = new Intent(SendMessageActivity.this, SendEmailActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);

    }
}
