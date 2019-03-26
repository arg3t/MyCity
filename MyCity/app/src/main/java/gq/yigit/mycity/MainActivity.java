package gq.yigit.mycity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.*;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import gq.yigit.mycity.tools.*;
import gq.yigit.mycity.tools.WebRequest.responseListener;
import gq.yigit.mycity.voteFragment.VoteFragment;
import gq.yigit.mycity.votesFragment.VotesContent;
import gq.yigit.mycity.votesFragment.VotesFragment;
import gq.yigit.mycity.votesFragment.VotesFragment.OnListFragmentInteractionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static gq.yigit.mycity.tools.ImageDownload.*;
import static gq.yigit.mycity.voteFragment.VoteFragment.*;

public class MainActivity extends AppCompatActivity
		implements
		OnNavigationItemSelectedListener,
		OnListFragmentInteractionListener,
		MainFragment.OnFragmentInteractionListener,
		OnFragmentInteractionListener,
		responseListener,
		imageListener {

	private Context cntxt;
	private static JSONObject userData;
	public static Bitmap userAvatar;
	private String url;
	private ImageView avatarView;
	private TextView userName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		cntxt = getApplicationContext();
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(cntxt,"server.config").trim();
		HashMap<String,String> request = new HashMap<>();
		request.put("username","efe");
		request.put("password","12345");
		WebRequest login_manager = new WebRequest(url + "/login/",false,request);
		login_manager.addListener(this);
		login_manager.execute();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		MainFragment fragment = new MainFragment();

		avatarView = findViewById(R.id.avatar);
		userName = findViewById(R.id.uname);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.app_bar_main, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Set new server");
			alert.setMessage("Server URL:");
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					FileActions file_manager = new FileActions();
					file_manager.writeToFile("https://" + input.getText().toString()+":5000",cntxt,"server.config");
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (id == R.id.voting) {
			VotesFragment fragment = new VotesFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);

		} else if (id == R.id.parking) {

		} else if (id == R.id.transit) {

		} else if (id == R.id.navigation) {

		} else if (id == R.id.rating) {

		} else if (id == R.id.utilities) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void onListFragmentInteraction(VotesContent.VoteItem vote){
		VoteFragment fragment = newInstance(vote.id);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.app_bar_main, fragment);
		transaction.commit();
		transaction.addToBackStack(null);
	}

	public void onFragmentInteraction(Uri uri){

	}

	public void receivedResponse(boolean success,String response){
		if(success) {
			try {
				JSONArray receivedData = new JSONArray(response);
				if(!(boolean)receivedData.get(0)){
					Toast.makeText(cntxt,"Please login again!",Toast.LENGTH_LONG).show();
				}else{

					userData = new JSONObject(receivedData.get(1).toString());
					Log.i("[INFO]",userData.toString());

					ImageDownload avatar_downloader = new ImageDownload();
					avatar_downloader.addListener(this);
					avatar_downloader.execute(url + userData.get("avatar"));

					userName.setText(userData.getString("realname"));
				}
			}catch (Exception e){
				Log.e("[ERROR]","Cannot receive userdata");
			}
		}
	}
	public void imageDownloaded(Bitmap img){
		try {
			userAvatar = img;
			avatarView.setImageBitmap(img);
		}catch(Exception e){
			Log.e("[ERROR]","Cannot set avatar!");
		}
	}
}
