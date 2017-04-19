package prashanth.wesync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;
import prashanth.wesync.models.UserInfo;

public class UsersListActivity extends AppCompatActivity {

    ArrayList<ContactList> contacts = new ArrayList<>();
    ArrayList<UserInfo> userList = new ArrayList<>();

    ArrayList<String> phoneNo = new ArrayList<>();
    ArrayList<String> emailIds = new ArrayList<>();

    ArrayList<String> matchedUserNames = new ArrayList<>();
    ArrayList<String> matchedUserEmails = new ArrayList<>();
    ArrayList<String> matchedUserPhones = new ArrayList<>();

    ArrayList<ContactList> matchedUsers = new ArrayList<>();

    ListView contactsListView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        context=this;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot topSnapshot) {

                for (DataSnapshot snapshot : topSnapshot.getChildren()) {
                    UserInfo user = snapshot.getValue(UserInfo.class);
                    phoneNo.add(user.getPhoneNumber());
                    emailIds.add(user.getEmail());
                    userList.add(user);
                }
                getContacts();
                getMatchingContacts();


                contactsListView=(ListView) findViewById(R.id.listViewContact);
                contactsListView.setAdapter(new CustomContactAdapter(UsersListActivity.this, matchedUserNames.toArray(new String[matchedUserNames.size()]), matchedUserEmails.toArray(new String[matchedUserEmails.size()]), matchedUserPhones.toArray(new String[matchedUserPhones.size()])));
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



    }


    private void getMatchingContacts() {
        for(ContactList contact:contacts){
            if(phoneNo.contains(contact.getContactNo())){
                if(!matchedUserNames.contains(contact.getContactName())) {
                    matchedUsers.add(contact);
                    matchedUserNames.add(contact.getContactName());
                    matchedUserEmails.add(contact.getEmail());
                    matchedUserPhones.add(contact.getContactNo());
                }
            }else if(emailIds.contains(contact.getEmail())){
                if(!matchedUserNames.contains(contact.getContactName())){
                    matchedUsers.add(contact);
                    matchedUserNames.add(contact.getContactName());
                    matchedUserEmails.add(contact.getEmail());
                    matchedUserPhones.add(contact.getContactNo());
                }
            }
        }
    }

    public void getContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {


            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor dataCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{ id },null);
            while(dataCursor.moveToNext()){
                ContactList contact = new ContactList();
                String phoneNumber = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String email = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                contact.setContactName(name);
                if(phoneNumber.length() > 10){
                    contact.setContactNo(phoneNumber.substring(phoneNumber.length() - 10));
                }else{
                    contact.setContactNo(phoneNumber);
                }
                contact.setEmail(email);
                contacts.add(contact);
            }


        }
    }
}
