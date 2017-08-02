package app.com.socialAuth.dropbox;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;

import java.io.File;
import java.util.Date;

import app.com.socialAuth.R;
import app.com.socialAuth.Utils;

public class DropBoxActivity extends Activity implements OnClickListener {
    private static final int TAKE_PHOTO = 1;
    private static final int TAKE_VIDEO = 2;
    private Button btnUpload, btnDownload;
    private final String DIR = "/";
    private File f;
    private boolean mLoggedIn, onResume;
    private DropboxAPI<AndroidAuthSession> mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_activity);
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        // checkAppKeySetup();
        setLoggedIn(false);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        btnUpload = (Button) findViewById(R.id.btnUploadPhoto);
        btnUpload.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        Utils.checkPermission(DropBoxActivity.this);
        Utils.checkPermissionCam(DropBoxActivity.this);
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(Constants.DROPBOX_APP_KEY,
                Constants.DROPBOX_APP_SECRET);
        AndroidAuthSession session;
        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, Constants.ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, Constants.ACCESS_TYPE);
        }
        return session;
    }

    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(
                Constants.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(Constants.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Constants.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnDownload) {
            startActivity(new Intent(DropBoxActivity.this, DropboxDownload.class));
        } else if (v == btnUpload) {
            if (Utils.checkPermission(DropBoxActivity.this) && Utils.checkPermissionCam(DropBoxActivity.this)) {
                createDir();
                if (mLoggedIn) {
                    logOut();
                }
                Utils.showAlertDialog(DropBoxActivity.this
                        , "Select Upload", getResources().getStringArray(R.array.filters_upload), onClickedStateListner);
            }
        }
    }

    DialogInterface.OnClickListener onClickedStateListner = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {

            switch (item) {
                case 0:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(Utils.getPathDropBox(), new Date().getTime() + ".jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, TAKE_PHOTO);
                    break;
                case 1:
                    Intent photoPickerIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f = new File(Utils.getPathDropBox(), new Date().getTime() + ".mp4");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(photoPickerIntent, TAKE_VIDEO);
                    break;

            }

        }
    };

    private void logOut() {
        mApi.getSession().unlink();

        clearKeys();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(
                Constants.ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private void createDir() {
        File dir = new File(Utils.getPathDropBox());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO || requestCode == TAKE_VIDEO) {
//				f = new File(Utils.getPath() + "/temp.jpg");
                if (Utils.isOnline(DropBoxActivity.this)) {
                    mApi.getSession().startAuthentication(DropBoxActivity.this);
                    onResume = true;
                } else {
                    Utils.showNetworkAlert(DropBoxActivity.this);
                }
            }
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            UploadFile upload = new UploadFile(DropBoxActivity.this, mApi, DIR, f);
            upload.execute();
            onResume = false;

        }
    }

    private void storeKeys(String key, String secret) {
        SharedPreferences prefs = getSharedPreferences(
                Constants.ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(Constants.ACCESS_KEY_NAME, key);
        edit.putString(Constants.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    @Override
    protected void onResume() {

        AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(onResume);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());
            }
        }
        super.onResume();
    }

//    if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
//        // Word document
//        intent1.setDataAndType(uri, "application/msword");
//    } else if (url.toString().contains(".pdf")) {
//        // PDF file
//        intent1.setDataAndType(uri, "application/pdf");
//    } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
//        // Powerpoint file
//        intent1.setDataAndType(uri, "application/vnd.ms-powerpoint");
//    } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
//        // Excel file
//        intent1.setDataAndType(uri, "application/vnd.ms-excel");
//    } else if(url.toString().contains(".zip") || url.toString().contains(".rar"))  {
//        // ZIP Files
//        intent1.setDataAndType(uri, "application/zip");
//    } else if (url.toString().contains(".rtf")) {
//        // RTF file
//        intent1.setDataAndType(uri, "application/rtf");
//    } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
//        // WAV audio file
//        intent1.setDataAndType(uri, "audio/x-wav");
//    } else if (url.toString().contains(".gif")) {
//        // GIF file
//        intent1.setDataAndType(uri, "image/gif");
//    } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
//        // JPG file
//        intent1.setDataAndType(uri, "image/jpeg");
//    } else if (url.toString().contains(".txt")) {
//        // Text file
//        intent1.setDataAndType(uri, "text/plain");
//    }
//
//                    else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
//            url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
//        // Video files
//        intent1.setDataAndType(uri, "video/*");
//    } else {
//        intent1.setDataAndType(uri, "*/*");
//    }
}
