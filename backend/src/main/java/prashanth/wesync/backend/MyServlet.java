/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package prashanth.wesync.backend;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.repackaged.com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import prashanth.wesync.backend.dao.Dao;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String authId = req.getParameter("authCode");
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        new JacksonFactory(),
                        "https://www.googleapis.com/oauth2/v4/token",
                        "413210436387-sqlv300j9f5u0f2a238o012lhhgpa7de.apps.googleusercontent.com",
                        "RoCAC2YYXawRNspv2H-R46uV",
                        authId,
                        "")  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();

        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String userId = payload.getSubject();  // Use this value as a key to identify a user.
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        Dao.INSTANCE.createUser(name,"sdf",email,accessToken,refreshToken);
        resp.setContentType("text/plain");
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        Calendar cal = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("WeSync").build();
        Directory dir = new Directory.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("WeSync").build();
        Events events = cal.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            resp.getWriter().println("No events");
        } else {
            for(Event item : items){
                JsonObject res = Dao.INSTANCE.createEvent(item.getSummary(),email,item.getLocation());
                resp.getWriter().println(res.toString());
            }
        }

//        Channel channel = new Channel();
//        channel.setId(UUID.randomUUID().toString());
//        channel.setType("web_hook");
//        channel.setAddress("https://wesync-164907.appspot.com/notification");
//        Channel c = cal.events().watch("primary",channel).execute();

    }
}
