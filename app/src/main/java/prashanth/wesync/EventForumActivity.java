package prashanth.wesync;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;
import java.util.List;

import prashanth.wesync.models.MessageModel;
import prashanth.wesync.models.UserInfo;

public class EventForumActivity extends AppCompatActivity {

    UserInfo currentUser;
    EditText msgText;
    DatabaseReference mDatabase;
    String eventName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_forum);
        Button sendBtn = (Button) findViewById(R.id.sendButtonEvent);
        msgText = (EditText) findViewById(R.id.editTextMsgEvent);
        ListView msgList = (ListView) findViewById(R.id.listViewMsgEvent);

        eventName = getIntent().getStringExtra("eventName");
        currentUser = ((GlobalClass) EventForumActivity.this.getApplication()).getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("chats"+eventName);

        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MessageModel model = new MessageModel(msgText.getText().toString(), currentUser.getName());
                mDatabase.push().setValue(model);
                msgText.setText("");


            }
        });
        msgText.setFocusable(true);
        msgText.setFocusableInTouchMode(true);



        final List<MessageModel> messages = new LinkedList<>();
        final ArrayAdapter<MessageModel> adapter = new ArrayAdapter<MessageModel>(
                this, android.R.layout.two_line_list_item, messages
        ) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                    view = getLayoutInflater().inflate(android.R.layout.two_line_list_item, parent, false);

                }
                MessageModel chatMessage = messages.get(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(chatMessage.getUser());
                ((TextView) view.findViewById(android.R.id.text2)).setText(chatMessage.getMsg());
                return view;
            }
        };
        msgList.setAdapter(adapter);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageModel chatmsg = dataSnapshot.getValue(MessageModel.class);
                messages.add(chatmsg);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }
}