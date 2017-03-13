/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dropbox.android.sample;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import android.app.AlertDialog;
import android.app.Dialog;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;


public class DBRoulette1 extends Activity {
    private static final String TAG = "DBRoulette";

    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////

    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static public String APP_KEY = "bu595s0ewy5wbgg";
    final static public String APP_SECRET = "pbjmp6vtg97p0bj";

    // If you'd like to change the access type to the full Dropbox instead of
    // an app folder, change this value.
    final static public AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////
    
    
    final String[] ADDR_LIST = { "sdcard","dropbox","google driver" };


    // You don't need to change these, leave them alone.
    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";


    DropboxAPI<AndroidAuthSession> mApi;

    private boolean mLoggedIn;

    // Android widgets
    ImageView mImage ;
    
    private Button mLinkBtn;
    
    private Button mUploadBtn;
    private Button mDownloadBtn;
    private Button mCreateFolderBtn;
    
    private Button mSelSourceAddrBtn ;
    private Button mSelDestinationAddrBtn ;
    
    
    private EditText mSourceAddr ; 
    private EditText mDestinationAddr  ; 

    

    private final String PHOTO_DIR = "/Photos/";

    
    private String mCameraFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (savedInstanceState != null) {
            mCameraFileName = savedInstanceState.getString("mCameraFileName");
        }
        */
        
        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        // Basic Android widgets
        setContentView(R.layout.main1);

        checkAppKeySetup();
        
        mSourceAddr = (EditText)findViewById(R.id.sourceAddr);
        
        // mDropBoxAddr.setText("/") ;
        
        mDestinationAddr  = (EditText)findViewById(R.id.destinationAddr );
        
        // mSdcardAddr.setText("/sdcard/") ;

        mLinkBtn = (Button)findViewById(R.id.linkBtn);

        mLinkBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    mApi.getSession().startAuthentication(DBRoulette1.this);
                }
            }
        });

        

        // This is the button to take a photo
        mUploadBtn = (Button)findViewById(R.id.uploadBtn);

        mUploadBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	/*
                Intent intent = new Intent();
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);
                // File outFile = new File("/sdcard/out.txt");

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                */
            	File outFile = new File( mSourceAddr.getText().toString() );
            	
            	if ( !outFile.exists() ) showToast( "File not found!" ) ;
                
                try {
                	UploadPicture upload = new UploadPicture( DBRoulette1.this, 
                			                                  mApi, 
                			                                  mDestinationAddr.getText().toString(), 
                			                                  outFile );
                    upload.execute();
                    // startActivityForResult(intent, NEW_PICTURE);
                    
                    
                    
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        });


        // This is the button to take a photo
        mDownloadBtn = (Button)findViewById(R.id.downloadBtn);

        mDownloadBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	
                DownloadRandomPicture download = new DownloadRandomPicture(DBRoulette1.this, 
                		                                                   mApi, 
                		                                                   mSourceAddr.getText().toString(), 
                		                                                   mDestinationAddr.getText().toString(), 
                		                                                   mImage );
                
                
                download.execute();
            }
        });
        
        mCreateFolderBtn = (Button)findViewById(R.id.createFolderBtn);

        mCreateFolderBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	
                CreateFolder createFolder = new CreateFolder(DBRoulette1.this, 
                		                                     mApi, 
                		                                     mDestinationAddr.getText().toString() );
                
                
                createFolder.execute();
            }
        });
        
        mSelSourceAddrBtn = (Button)findViewById(R.id.sourceAddrBtn);

        mSelSourceAddrBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              ForPass.BTN_NUM = 1 ;
              ShowAlertDialogAndList() ;
            }
        });
        
        mSelDestinationAddrBtn = (Button)findViewById(R.id.destinationAddrBtn);

        mSelDestinationAddrBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              ForPass.BTN_NUM = 2 ;
              ShowAlertDialogAndList() ;
            }
        });
        
        
        
        

        // Display the proper UI state if logged in or not
        setLoggedIn(mApi.getSession().isLinked());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mCameraFileName", mCameraFileName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();
        
        mSourceAddr.setText( ForPass.SOURCE_PATH ) ;
        mDestinationAddr.setText( ForPass.DESTINATION_PATH ) ;

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(true);
                
                // mSdcardAddr.setText( ForPass.RETURN_SDCARD_PATH ) ;
                
                
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    // This is what gets called on finishing a media piece to import
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PICTURE) {
            // return from file upload
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                if (uri == null && mCameraFileName != null) {
                    uri = Uri.fromFile(new File(mCameraFileName));
                }
                File file = new File(mCameraFileName);

                if (uri != null) {
                    UploadPicture upload = new UploadPicture(this, mApi, PHOTO_DIR, file);
                    upload.execute();
                }
            } else {
                Log.w(TAG, "Unknown Activity Result from mediaImport: "
                        + resultCode);
            }
        }
        
        // debug message
        mPhoto.setText(mCameraFileName);
    }
    */

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		mLinkBtn.setText("Unlink from Dropbox");
            
    	} else {
    		mLinkBtn.setText("You're Link with Dropbox");
            
    	}
    }

    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
    
    
    
    private void ShowAlertDialogAndList() {

    Builder MyAlertDialog = new AlertDialog.Builder(this);

    MyAlertDialog.setTitle("select location...");

    //建立選擇的事件

    DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener(){

      public void onClick(DialogInterface dialog, int which) {
    	  ForPass.ROOT_PATH = "/sdcard/"  ;
    	  
    	  if ( ADDR_LIST[which].equals( "sdcard" ) ) { 
    		  ForPass.ROOT_PATH = "/sdcard/"  ;
    		  
    		  Intent newAct = new Intent();
    	      newAct.setClass( DBRoulette1.this, fileViewActivity.class );
    	      
    	      // 呼叫新的 Activity Class
    	      startActivity( newAct );
    	  }
    	  else if ( ADDR_LIST[which].equals( "dropbox" ) ) {
    		  ForPass.ROOT_PATH = "/"  ;
    		  
    		  Intent newAct = new Intent();
    	      newAct.setClass( DBRoulette1.this, DropBoxFileViewActivity.class );
    	      
    	      // 呼叫新的 Activity Class
    	      startActivity( newAct );
    	  }
    	  else if ( ADDR_LIST[which].equals( "google driver" ) ) {
    		  ForPass.ROOT_PATH = "/"  ;
    		  
    		  Intent newAct = new Intent();
    	      newAct.setClass( DBRoulette1.this, fileViewActivity.class );
    	      
    	      // 呼叫新的 Activity Class
    	      startActivity( newAct );
    	  }
    	  
			  
		 

      }

    };

    //建立按下取消什麼事情都不做的事件

    DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
    	  
      }
    
    }; 
    
    MyAlertDialog.setItems(ADDR_LIST, ListClick);
    
    MyAlertDialog.setNeutralButton("取消",OkClick );
    
    MyAlertDialog.show();
    
    } 
}
