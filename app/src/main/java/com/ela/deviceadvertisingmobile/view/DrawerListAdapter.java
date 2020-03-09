package com.ela.deviceadvertisingmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ela.deviceadvertisingmobile.R;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter
{
    /** ---------- Arguments ---------- */
    Context mContext;
    ArrayList<NavItem> mNavItems;

    /** ---------- Public functions -------- */
    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    /** ---------- Override functions -------- */
    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subTitle);
        ImageView iconView = view.findViewById(R.id.icon);

        titleView.setText( mNavItems.get(position).title);
        subtitleView.setText(mNavItems.get(position).subtitle);
        iconView.setImageResource(mNavItems.get(position).icon);

        return view;
    }
}
