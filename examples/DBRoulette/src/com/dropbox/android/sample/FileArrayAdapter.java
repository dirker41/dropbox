package com.dropbox.android.sample;



import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 


public class FileArrayAdapter extends ArrayAdapter<String> {
	
	private final Context context;
	private final String[] values;
	private String mPath ;
 
	public FileArrayAdapter(Context context, String[] values , String path ) {
		super(context, R.layout.file_list, values);
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
		
		
		
		File file = new File( (mPath + s) ) ;
 
		
		if  ( !file.exists() && s.equals( "Previous Folder" ) )  {
			imageView.setImageResource(R.drawable.up1 );
		}
		else if ( !file.exists() && s.equals( "Select this folder" ) ) {
			imageView.setImageResource(R.drawable.right1 );
		}
		else if  ( file.exists() && !file.isFile() ) {
			imageView.setImageResource(R.drawable.folder1);
		}  
		else {
			imageView.setImageResource(R.drawable.file1);
		}
 
		return rowView;
	}
}
