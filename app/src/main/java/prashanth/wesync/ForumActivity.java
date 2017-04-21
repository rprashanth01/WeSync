package prashanth.wesync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import prashanth.wesync.models.MessageModel;
import prashanth.wesync.models.UserInfo;

public class ForumActivity extends AppCompatActivity {

    UserInfo currentUser;
    EditText msgText;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        Button sendBtn = (Button) findViewById(R.id.sendButton);
        msgText = (EditText) findViewById(R.id.editTextMsg);
        ListView msgList = (ListView) findViewById(R.id.listViewMsg);

        currentUser = ((GlobalClass) ForumActivity.this.getApplication()).getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("chats");

        sendBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MessageModel model = new MessageModel(currentUser.getName(),msgText.getText().toString());
                mDatabase.push().setValue(model);
                msgText.setText("");


            }
        });


    }
}
