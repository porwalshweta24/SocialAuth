package app.com.socialAuth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.com.socialAuth.dropbox.DropBoxActivity;
import app.com.socialAuth.facebook.FacebookActivity;
import app.com.socialAuth.insta.Instagram.InstaActivity;
import app.com.socialAuth.twitter.TwitterActivity;

/**
 * Created by Lenovo on 8/2/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnInsta, btnTwitter, btnDropBox, btnFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            btnInsta = (Button) findViewById(R.id.btnInsta);
            btnTwitter = (Button) findViewById(R.id.btnTwitter);
            btnDropBox = (Button) findViewById(R.id.btnDropBox);
            btnFacebook = (Button) findViewById(R.id.btnFacebook);
            btnTwitter.setOnClickListener(this);
            btnInsta.setOnClickListener(this);
            btnDropBox.setOnClickListener(this);
            btnFacebook.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnInsta) {
            startActivity(new Intent(MainActivity.this, InstaActivity.class));
        } else if (view == btnTwitter) {
            startActivity(new Intent(MainActivity.this, TwitterActivity.class));
        } else if (view == btnDropBox) {
            startActivity(new Intent(MainActivity.this, DropBoxActivity.class));
        } else if (view == btnFacebook) {
            startActivity(new Intent(MainActivity.this, FacebookActivity.class));
        }
    }
}
