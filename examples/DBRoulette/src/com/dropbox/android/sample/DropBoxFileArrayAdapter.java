package com.dropbox.android.sample;



import java.io.File;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 


public class DropBoxFileArrayAdapter extends ArrayAdapter<String> {
	
	private final Context context;
	private final String[] values;
	private String mPath ;
	DropboxAPI<?> mApi ;
 
	public DropBoxFileArrayAdapter(Context context, 
			                       DropboxAPI<?> api,
			                       String[] values , 
			                       String path ) {
		super(context, R.layout.file_list, values);
		
		mApi = api;
		this.context = context;
		this.values = values;
		mPath = path ;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.file_list, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.textView2);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
		textView.setText( values[position]);
 
		// Change icon based on name
		String s = values[position];
 
		System.out.println(s);
		
		Entry entry = null ;
		
		try {
			entry = mApi.metadata( mPath + s, 0, null, false, null) ;
		} catch ( Exception e ) {
			entry = null ;
		}
		
		
		if  ( s.equals( "Previous Folder" ) )  {
			imageView.setImageResource(R.drawable.up1 );
		}
		else if ( s.equals( "Select this folder" ) ) {
			imageView.setImageResource(R.drawable.right1 );
		}
		else if  ( entry != null && entry.isDir ) {
			imageView.setImageResource(R.drawable.folder1);
		}  
		else {
			imageView.setImageResource(R.drawable.file1);
		}
		
		
 
		return rowView;
	}
}
