package prashanth.wesync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import prashanth.wesync.models.EventList;

public class SignInAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private Context context;

    private ProgressDialog dialog;

    public SignInAsyncTask(Activity a){
        dialog = new ProgressDialog(a);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Syncing data, please wait.");
        dialog.show();
    }

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        context = params[0].first;
        String name = params[0].second;
        String email = params[1].second;

        try {
            // Set up the request
            URL url = new URL("https://wesync-164907.appspot.com/hello");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Build name data request params
            Map<String, String> nameValuePairs = new HashMap<>();
            nameValuePairs.put("authCode", name);
            nameValuePairs.put("email",email);
            String postParams = buildPostDataString(nameValuePairs);

            // Execute HTTP Post
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(postParams);
            writer.flush();
            writer.close();
            outputStream.close();
            connection.connect();

            // Read response
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonAnswer = connection.getHeaderField("event");
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return response.toString();
            }
            return "Error: " + responseCode + " " + connection.getResponseMessage();

        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String buildPostDataString(Map<String, String> params) throws UnsupportedEncodingException, UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        Gson gson = new Gson();
        JsonReader r = new JsonReader(new StringReader(result));
        r.setLenient(true);
        EventList events = gson.fromJson(r, EventList.class);
        ((GlobalClass) context.getApplicationContext()).setEventList(events);
        //EventList eventstest = ((GlobalClass) context.getApplicationContext()).getEventList();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
