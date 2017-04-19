package prashanth.wesync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private String name = "";
    private String phone = "";
    private View view;
    private ArrayList<String> interests = new ArrayList<String>();
    boolean checked;

    private DatabaseReference mDatabase;

    CheckBox movieCB;
    CheckBox musicCB;
    CheckBox danceCB;
    CheckBox sportsCB;
    CheckBox dateCB;

    EditText phoneET;
    EditText nameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        view = findViewById(android.R.id.content);
        nameET = (EditText)findViewById(R.id.nameET);

        phoneET = (EditText)findViewById(R.id.phoneET);

        movieCB = (CheckBox)findViewById(R.id.checkMovies);
        musicCB = (CheckBox)findViewById(R.id.checkMusic);
        danceCB = (CheckBox)findViewById(R.id.checkDance);
        sportsCB = (CheckBox)findViewById(R.id.checkSports);
        dateCB = (CheckBox)findViewById(R.id.checkDate);


    }

    void storeProfileData(View view){

        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
        if(movieCB.isChecked()){
            interests.add(movieCB.getText().toString());
        }
        if(musicCB.isChecked()){
            interests.add(musicCB.getText().toString());
        }
        if(danceCB.isChecked()){
            interests.add(danceCB.getText().toString());
        }
        if(sportsCB.isChecked()){
            interests.add(sportsCB.getText().toString());
        }
        if(dateCB.isChecked()){
            interests.add(dateCB.getText().toString());
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        String userId = mDatabase.push().getKey();
        UserInfo user = new UserInfo(name,"abc@gmail.com",phone,12345,34567,interests);
        mDatabase.child(userId).setValue(user);


        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
