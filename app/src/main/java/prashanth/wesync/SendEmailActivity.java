package prashanth.wesync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendEmailActivity extends AppCompatActivity {

    EditText subject;
    EditText content;
    Button send;
    String email = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        email = getIntent().getStringExtra("email");

        subject = (EditText)findViewById(R.id.subject);
        content = (EditText)findViewById(R.id.content);
        send = (Button)findViewById(R.id.sendmail);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = email;
                String sub = subject.getText().toString();
                String msg = content.getText().toString();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                i.putExtra(Intent.EXTRA_SUBJECT, sub);
                i.putExtra(Intent.EXTRA_TEXT, msg);
                i.setType("message/rfc822");
                startActivity(Intent.createChooser(i, "Email sent"));
            }
        });
    }
}
