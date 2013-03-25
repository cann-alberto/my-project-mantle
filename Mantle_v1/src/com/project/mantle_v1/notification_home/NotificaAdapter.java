package com.project.mantle_v1.notification_home;

import java.util.List;

import com.project.mantle_v1.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotificaAdapter extends ArrayAdapter<Notifica>{

	 public NotificaAdapter(Context context, int textViewResourceId,
			List<Notifica> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getViewOptimize(position, convertView, parent);
	    }

	    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
	        ViewHolder viewHolder = null;
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) getContext()
	                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.note_layout, null);
	            viewHolder = new ViewHolder();
	            viewHolder.title = (TextView)convertView.findViewById(R.id.textViewUser);
	            viewHolder.data = (TextView)convertView.findViewById(R.id.textViewNote);
	            convertView.setTag(viewHolder);
	        } else {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        Notifica note = getItem(position);
	        viewHolder.title.setText(note.getTitle());
	        viewHolder.data.setText(note.getData());
	        return convertView;
	    }

	    private class ViewHolder {
	        public TextView title;
	        public TextView data;
	    }

}