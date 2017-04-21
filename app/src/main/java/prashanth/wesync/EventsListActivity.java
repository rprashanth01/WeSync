package prashanth.wesync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import prashanth.wesync.models.Event;
import prashanth.wesync.models.EventList;

public class EventsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        ListView msgList = (ListView) findViewById(R.id.eventList);

        final List<Event> eventLists;
        EventList eventList = ((GlobalClass) EventsListActivity.this.getApplication()).getEventList();
        eventLists = eventList.getEvents();
        final ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(
                this,android.R.layout.two_line_list_item,eventLists
        ){
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if(view == null){
                    view = getLayoutInflater().inflate(android.R.layout.two_line_list_item,parent,false);

                }
                Event event = eventLists.get(position);
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getEventName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(event.getEmails());
                return view;
            }
        };
        msgList.setAdapter(adapter);
    }
}
