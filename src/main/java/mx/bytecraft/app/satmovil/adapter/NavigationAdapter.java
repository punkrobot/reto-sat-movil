package mx.bytecraft.app.satmovil.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import mx.bytecraft.app.satmovil.R;

public class NavigationAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] navList;
    private final int rowResourceId;

    public NavigationAdapter(Context context, int textViewResourceId, String[] navList) {
        super(context, textViewResourceId, navList);

        this.navList = navList;
        this.context = context;
        rowResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(rowResourceId, parent, false);
        TextView textView = (TextView)rowView.findViewById(R.id.navigation_item_text);
        textView.setText(navList[position]);

        Drawable icon = null;
        switch (position){
            case 0:
                icon = context.getResources().getDrawable(R.drawable.ic_action_home);
                break;
            case 1:
                icon = context.getResources().getDrawable(R.drawable.ic_action_bookmark);
                break;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        return rowView;
    }

}