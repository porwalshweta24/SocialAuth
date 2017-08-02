package app.com.socialAuth.dropbox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dropbox.client2.session.Session.AccessType;

public class Constants {

    public static final String OVERRIDEMSG = "File name with this name already exists.Do you want to replace this file?";
    final static public String DROPBOX_APP_KEY = "ENTER_YOUR_KEY";
    final static public String DROPBOX_APP_SECRET = "ENTER_YOUR_KEY";
    public static boolean mLoggedIn = false;

    final static public AccessType ACCESS_TYPE = AccessType.DROPBOX;

    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = DROPBOX_APP_KEY;//"ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = DROPBOX_APP_SECRET;//"ACCESS_SECRET";
//	public static final int BOOKMARKS = 4;
//	public static final int OPENFILE = 5;


}
