package app.com.socialAuth.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.com.socialAuth.R;

public class FriendList extends Activity {

	private ListView lvFriendList;

	private ArrayList<HashMap<String, String>> friendList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fb_friend_list);

		lvFriendList = (ListView) findViewById(R.id.lvFriendList);

		displayFriendList();
	}

	@SuppressWarnings("unchecked")
	private void displayFriendList() {
		friendList = (ArrayList<HashMap<String, String>>) getIntent()
				.getSerializableExtra(FacebookActivity.FRIEND_LIST);

		FriendListAdapter adapter = new FriendListAdapter(this, friendList);
		lvFriendList.setAdapter(adapter);

		bindEventHandler();
	}

	private void bindEventHandler() {
		lvFriendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getFriendDetail(friendList.get(position).get(
						FacebookActivity.USER_ID));
			}
		});
	}

	private void getFriendDetail(final String userId) {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					Request request = new Request(session, userId);

					request.setCallback(new Request.Callback() {

						@Override
						public void onCompleted(Response response) {

							GraphObject graphObj = response.getGraphObject();

							HashMap<String, String> userHashmap = new HashMap<String, String>();
							userHashmap.put(FacebookActivity.USER_ID, userId);
							userHashmap.put(FacebookActivity.USER_NAME, graphObj
									.getProperty("name").toString());
							userHashmap.put(FacebookActivity.FIRST_NAME, graphObj
									.getProperty("first_name").toString());
							userHashmap.put(FacebookActivity.LAST_NAME, graphObj
									.getProperty("last_name").toString());
							userHashmap.put(FacebookActivity.GENDER,
									(String) graphObj.getProperty("gender"));

							Intent intent = new Intent(FriendList.this,
									UserInfo.class);
							intent.putExtra(FacebookActivity.USER_MAP, userHashmap);
							startActivity(intent);
						}
					});

					request.executeAsync();
				}
			}
		});
	}

}
