package prashanth.wesync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by asbhat3 on 4/19/2017.
 */

public class CustomContactAdapter extends BaseAdapter{

    String[] userNamesLocal;
    String[] userPhoneLocal;
    String[] userEmailLocal;
    Context context;

    private static LayoutInflater inflater=null;
    public CustomContactAdapter(UsersListActivity usersListActivity, String[] userNames, String[] userPhone, String[] userEmail) {
        // TODO Auto-generated constructor stub
        userNamesLocal =userNames;
        userPhoneLocal = userPhone;
        userEmailLocal = userEmail;
        context=usersListActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return userNamesLocal.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvName;
        TextView tvEmail;
        TextView tvPhone;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.contact, null);
        holder.tvName = (TextView) rowView.findViewById(R.id.textViewName);
        holder.tvEmail = (TextView) rowView.findViewById(R.id.textViewEmail);
        holder.tvPhone =(TextView) rowView.findViewById(R.id.textViewPhone);
        holder.tvName.setText(userNamesLocal[position]);
        holder.tvEmail.setText(userPhoneLocal[position]);
        holder.tvPhone.setText(userEmailLocal[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+userNamesLocal[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}
