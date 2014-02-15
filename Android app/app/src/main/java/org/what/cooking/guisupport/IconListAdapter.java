package org.what.cooking.guisupport;

/**
 * Created by marica on 02/01/14.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import org.what.cooking.R;

import java.util.ArrayList;


public class IconListAdapter extends BaseAdapter {
    private static ArrayList<String> items;

    private Integer[] imgid = {
            R.drawable.marker_free,
            R.drawable.marker_charged
    };

    private LayoutInflater l_Inflater;

    public IconListAdapter(Context context) {
        items = new ArrayList<String>();
        items.add(context.getResources().getString(R.string.free));
        items.add(context.getResources().getString(R.string.charged));
        l_Inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.freeorcharged_item_searchlist, null);
            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.searchFreeOrChargedLabel);
            ((TextView)convertView.findViewById(R.id.searchFreeOrChargedLabel)).setTypeface(Typeface.createFromAsset(convertView.getContext().getAssets(), "fonts/CANDARA.TTF"));
            holder.icon = (ImageView) convertView.findViewById(R.id.searchFreeOrChargedIcon);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.searchFreeOrChargedCheckbox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(items.get(position));
        holder.icon.setImageResource(imgid[position]);
        holder.checkBox.setChecked(true);

        return convertView;
    }

    static class ViewHolder {
        TextView label;
        ImageView icon;
        CheckBox checkBox;
    }
}
