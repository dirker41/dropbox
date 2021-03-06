package com.dropbox.android.sample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class DropBoxFileViewActivity extends ListActivity {
 

	
	// private static final String MEDIA_PATH = new String("/sdcard/");
	private List<String> songs = new ArrayList<String>();
	private String mPath ;
	
	DropboxAPI<AndroidAuthSession> mApi;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        
 
		mPath = ForPass.ROOT_PATH ;
	    
	    
		String[] fileArr ;
	    
	    
		songs.add( "Previous Folder" ) ;
		songs.add( "Select this folder" ) ;
		
		
			
			
		try {
			DropboxAPI.Entry entry = mApi.metadata( mPath, 0, null, true, null);
			
			List<DropboxAPI.Entry> childEntryList = entry.contents ;
			
			try {
			for ( int i = 0 ; i < childEntryList.size() ; i ++ ) 	
			  songs.add( childEntryList.get(i).fileName() ) ;
			} catch ( Exception e ) {
				;
			}
			
			
			
			
		} catch ( Exception e) {
			Log.e("DbExampleLog", "Something went wrong while getting metadata.");
			showToast( "Erordsddsfdsdfddsddddddddddd" ) ;
		}
			
		fileArr = new String[ songs.size() ] ;
		for ( int i = 0 ; i < songs.size(); i++ ) {
			fileArr[i] = songs.get(i) ;
		}
		  
		
		 
		setListAdapter(new DropBoxFileArrayAdapter(this, mApi , fileArr, mPath ));
	  }
 
		// setListAdapter(new ArrayAdapter<String>(this, R.layout.f_v ,songs));
	
	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
 
		//get selected items
		  String selectedValue = (String) getListAdapter().getItem(position);
		  // Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
		  
		  String path = mPath + selectedValue ;
		  
		  if( path.endsWith( "/Previous Folder" ) ) {
			   String oriPath = path.substring(0,path.lastIndexOf("/") )  ;
				  if ( oriPath.length() > 1 )
				    ForPass.ROOT_PATH = path.substring(0,oriPath.lastIndexOf("/")+1 )  ;
				  
				  Intent newAct = new Intent();
		          newAct.setClass( DropBoxFileViewActivity.this, DropBoxFileViewActivity.class );
		      
		          // 呼叫新的 Activity Class
		          startActivity( newAct );
		      
		          // 不結束原先的 Activity Class
		          // 這樣按返回鍵時, 就可以回到這個 Activity 了
		          DropBoxFileViewActivity.this.finish();
		   }
		   else if( path.endsWith( "/Select this folder" ) ) {
			   editTextDialog( path ) ;
		   }
		   else {
		   try{
		     DropboxAPI.Entry entry = mApi.metadata( path, 100, null, false, null);
		   
		     if ( entry != null && !entry.isDir ) {
			   
				  if ( ForPass.BTN_NUM == 1 ) 
				    ForPass.SOURCE_PATH = path  ;
				  else if ( ForPass.BTN_NUM == 2 ) 
					ForPass.DESTINATION_PATH = path  ;
				  
		          DropBoxFileViewActivity.this.finish();
			  }
		      else if ( entry != null && entry.isDir ) {
				 
		    	ForPass.ROOT_PATH = path + "/" ;
				Intent newAct = new Intent();
		        newAct.setClass( DropBoxFileViewActivity.this, DropBoxFileViewActivity.class );
		      
		          // 呼叫新的 Activity Class
		        startActivity( newAct );
		      
		          // 不結束原先的 Activity Class
		          // 這樣按返回鍵時, 就可以回到這個 Activity 了
		        DropBoxFileViewActivity.this.finish();
			   }
		    } catch ( Exception e ) {
			  ;
		    }
		  } // else 
		  
		  
	    }
	  
	  @Override
	    protected void onResume() {
	        super.onResume();
	        
	        
	        
	        AndroidAuthSession session = mApi.getSession();
	        

	        // The next part must be inserted in the onResume() method of the
	        // activity from which session.startAuthentication() was called, so
	        // that Dropbox authentication completes properly.
	        if (session.authenticationSuccessful()) {
	            try {
	                // Mandatory call to complete the auth
	                session.finishAuthentication();

	                // Store it locally in our app for later use
	                // TokenPair tokens = session.getAccessTokenPair();
	                
	                
	                // mSdcardAddr.setText( ForPass.RETURN_SDCARD_PATH ) ;
	                
	                
	            } catch (IllegalStateException e) {
	                
	            }
	        }
	    }
	  
	    private AndroidAuthSession buildSession() {
	        AppKeyPair appKeyPair = new AppKeyPair( DBRoulette1.APP_KEY, DBRoulette1.APP_SECRET);
	        AndroidAuthSession session;

	        String[] stored = getKeys();
	        if (stored != null) {
	            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
	            session = new AndroidAuthSession(appKeyPair, DBRoulette1.ACCESS_TYPE, accessToken);
	        } else {
	            session = new AndroidAuthSession(appKeyPair, DBRoulette1.ACCESS_TYPE);
	        }

	        return session;
	    }
	    
	    private String[] getKeys() {
	        SharedPreferences prefs = getSharedPreferences(DBRoulette1.ACCOUNT_PREFS_NAME, 0);
	        String key = prefs.getString(DBRoulette1.ACCESS_KEY_NAME, null);
	        String secret = prefs.getString(DBRoulette1.ACCESS_SECRET_NAME, null);
	        if (key != null && secret != null) {
	        	String[] ret = new String[2];
	        	ret[0] = key;
	        	ret[1] = secret;
	        	return ret;
	        } else {
	        	return null;
	        }
	    }

	    private void showToast(String msg) {
	        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
	        error.show();
	    }
	    
	    private void editTextDialog( final String path ) {
	  	  
				// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.edit_text, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			    alertDialogBuilder.setTitle("Enter file name...");
			    
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);

				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
						// edit text
					    	
					    	  String fileName = userInput.getText().toString() ;
					    	
					          String oriPath = path.substring(0,path.lastIndexOf("Select this folder") )  ;
							  
							  
							  
							  
							  if ( ForPass.BTN_NUM == 1 ) 
								ForPass.SOURCE_PATH = oriPath + fileName ;
							  else if ( ForPass.BTN_NUM == 2 ) 
								ForPass.DESTINATION_PATH = oriPath + fileName ;
							  
					          DropBoxFileViewActivity.this.finish();
						
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

	}
}

