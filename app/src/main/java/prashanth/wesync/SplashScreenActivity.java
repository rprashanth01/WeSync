package prashanth.wesync;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import prashanth.wesync.models.ContactList;

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
        LocationBackgroundTask locationBackgroundTask = new LocationBackgroundTask();
        locationBackgroundTask.execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }

    class LocationBackgroundTask extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... params) {
            {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                while (cursor.moveToNext()) {

                    String email = "";
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor dataCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{ id },null);
                    while(dataCursor.moveToNext()){
                        ContactList contact = new ContactList();
                        String phoneNumber = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                        contact.setContactName(name);
                        if(phoneNumber.length() > 10){
                            contact.setContactNo(phoneNumber.substring(phoneNumber.length() - 10));
                        }else{
                            contact.setContactNo(phoneNumber);
                        }
                        contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[] { id }, null);
                        Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);

                        while (emails.moveToNext()) {
                            email = emails.getString(emails
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            break;
                        }
                        emails.close();
                        contact.setEmail(email);
                        contacts.add(contact);
                        break;
                    }
                    dataCursor.close();

                }
                cursor.close();
            }
            ((GlobalClass) SplashScreenActivity.this.getApplication()).setContactList(contacts);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}
