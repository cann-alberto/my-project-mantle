package com.project.mantle_v1.notification_home;

import java.io.File;
import java.util.Date;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.AddFriend;
import com.project.mantle_v1.database.AddService;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.database.Team;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.dropbox.Sharing;
import com.project.mantle_v1.dropbox.Uploader;
import com.project.mantle_v1.fileNavigator.FileDetailActivity;
import com.project.mantle_v1.fileNavigator.FileDetailFragment;
import com.project.mantle_v1.fileNavigator.FileListActivity;
import com.project.mantle_v1.gmail.ReaderTask;
import com.project.mantle_v1.parser.MantleMessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

/*
 * An activity representing a list of Notifications. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NotificationDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NotificationListFragment} and the item details (if present) is a
 * {@link NotificationDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NotificationListFragment.Callbacks} interface to listen for item
 * selections.
 */

/*
 *  Questa è la prima activity lanciata. Nella quale risiedono le due liste:
 *  - NotificationListFragment
 *  - NotificationDetailFragment
 *  nel caso in cui l'applicazione venga usata su tablet. 
 *  
 *  Se usato invece su dispositivi con display più piccoli, viene visualizzata la lista
 *  delle notifiche, cliccando sulle quali vengono visualizzati i dettagli nella 
 *  NotificationDetailActivity 
 */

public class NotificationListActivity extends FragmentActivity implements
		NotificationListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private MyHandler handler;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_list);

		handler = new MyHandler(getApplicationContext());

		SharedPreferences userDetails = getSharedPreferences(User.USER_DETAILS_PREF,
				0);

		ReaderTask rt = new ReaderTask(handler, userDetails.getString("email",
				" "), userDetails.getString("emailpswd", " "));

		mContext = this;
		if (!rt.isAlive())
			rt.start();

		setTitle("Salve " + userDetails.getString("username", " "));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (findViewById(R.id.notification_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((NotificationListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.notification_list))
					.setActivateOnItemClick(true);

		}

		// TODO: If exposing deep links into your app, handle intents here.

	}

	/**
	 * Callback method from {@link NotificationListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(NotificationDetailFragment.ARG_ITEM_ID, id);
			NotificationDetailFragment fragment = new NotificationDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.notification_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Notifica n = MyHandler.NOTIFICA_MAP.get(id);
			
			if (n.getNotificationType().equals(
							MantleMessage.SHARING_PHOTO)) {
				
				Log.v("Notification List Activity", "FileDetailActivity");
				
				MioDatabaseHelper db = new MioDatabaseHelper(getApplicationContext());
				
				long ID = db.insertFile(n.getmFile().getFileName(),
						n.getmFile().getLinkFile(), n.getmFile().getLinkThumb(), n.getmFile().getLinkComment(), "",
						n.getmFile().getObjectType(), MantleFile.NOT_OWN_FILE);
				
				n.getmFile().setIdFile(String.valueOf(ID));
				
				int ID_User = db.getId(n.getmFile().getSender_email());
				db.insertHistory((int) ID, ID_User,
						new Date(System.currentTimeMillis()).toString());
				db.close();
				Intent detailIntent = new Intent(this, FileDetailActivity.class);
				
				/* 
				 *  Passo alla File Detail Activity l'id del file che mi è stato condiviso
				 */
				
				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						String.valueOf(ID));
				
				
				startActivity(detailIntent);
			
			}
			else if(n.getNotificationType().equals(MantleMessage.NOTE)){
				
				/* 
				 *  Passo alla File Detail Activity l'id del file che mi è stato condiviso
				 */

				MioDatabaseHelper db = new MioDatabaseHelper(getApplicationContext());
				String fileUrl = db.getLinkfromLinkComment(n.getNote()
						.getCommentLink());
				String idFile = String.valueOf(db.getIdFile(fileUrl));
				Intent detailIntent = new Intent(this, FileDetailActivity.class);
				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						idFile);
				detailIntent.putExtra("Comment", n.getNote());
				startActivity(detailIntent);
				}
				
			else{
				Log.v("Notification List Activity", "NotificationDetailFragment");
				
				Intent detailIntent = new Intent(this,
						NotificationDetailActivity.class);
				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						id);
				startActivity(detailIntent);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		
		MenuItem menu1 = menu.add(0,0,0,"Amici");
	    {
	    	menu1.setIcon(R.drawable.ic_action_friend);
	    	menu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    }
	    menu1.setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this,
								FriendsList.class);
						intent.putExtra("flag", 1);
						startActivity(intent);
						return true;
					}
				});
	    
	    MenuItem menu2 = menu.add(1,1,1,"Condividi");
	    {
	    	menu2.setIcon(R.drawable.ic_action_share);
	    	menu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    }
	    menu2.setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this, Sharing.class);
						startActivity(intent);
						return true;
					}
				});
	    
	    menu.add("File").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this,
								FileListActivity.class);
						startActivity(intent);
						return true;
					}
				});
		
	    menu.add("Nuovo Servizio").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this, AddService.class);
						startActivity(intent);
						return true;
					}
				});

		menu.add("Sincronizza").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						MioDatabaseHelper db = new MioDatabaseHelper(
								getApplicationContext());

						db.exportDB();
						db.close();
						DropboxAuth dropbox = new DropboxAuth(
								getApplicationContext());

						new Uploader(mContext, dropbox.getAPI(),
								"/StoredFile/", new File(Environment
										.getExternalStorageDirectory()
										+ "/Mantle/db/Mantle")).execute();
						return true;
					}
				});

		menu.add("Cerchie").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this, Team.class);
						startActivity(intent);
						return true;
					}
				});

		return true;
	}
}