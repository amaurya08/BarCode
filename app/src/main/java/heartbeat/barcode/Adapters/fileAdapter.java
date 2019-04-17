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

import heartbeat.barcode.R;
import heartbeat.barcode.PojoClasses.filePojo;

public class fileAdapter extends ArrayAdapter {
    private  Context context;
    private int resource;
    private LayoutInflater layoutInflater;
    private ArrayList<filePojo> arrayList;
    public fileAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<filePojo> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.arrayList=objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view =  layoutInflater.inflate(resource,null);

        TextView filename= (TextView) view.findViewById(R.id.filename);


        TextView tittle=(TextView) view.findViewById(R.id.tittle);

        filePojo temp=arrayList.get(position);
        tittle.setText(temp.getFilename().toUpperCase().charAt(0)+"");
        filename.setText(temp.getFilename());
        return view;
    }
}
