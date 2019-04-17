package heartbeat.barcode.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import heartbeat.barcode.PojoClasses.PersonPojo;
import heartbeat.barcode.R;

public class AadharAdapter extends ArrayAdapter {

    private ArrayList arraylist;
    private Context context;
    private LayoutInflater inflater;
    public AadharAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<PersonPojo> objects) {
        super(context, resource, objects);
        arraylist =objects;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {

        View view= inflater.inflate(R.layout.list_item,null);
        TextView uid=(TextView)view.findViewById(R.id.uid);
        TextView name=(TextView)view.findViewById(R.id.name);

        TextView tittle=(TextView)view.findViewById(R.id.tittle);

        PersonPojo p= (PersonPojo) arraylist.get(position);

        tittle.setText(p.getName().toUpperCase().charAt(0)+"");

        uid.setText(p.getUid());
        name.setText(p.getName());
        return view;
    }
}
