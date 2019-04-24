package gq.yigit.mycity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import gq.yigit.mycity.navigation.TransitFragment;
import gq.yigit.mycity.tools.*;
import gq.yigit.mycity.tools.WebRequest.responseListener;
import gq.yigit.mycity.utility.UtilityMain;
import gq.yigit.mycity.votesFragment.VoteFragment;
import gq.yigit.mycity.votesFragment.VotesContent;
import gq.yigit.mycity.votesFragment.VotesFragment;
import gq.yigit.mycity.votesFragment.VotesFragment.OnListFragmentInteractionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static gq.yigit.mycity.tools.ImageDownload.*;
import static gq.yigit.mycity.votesFragment.VoteFragment.*;

public class MainActivity extends AppCompatActivity
		implements
		OnNavigationItemSelectedListener,
		OnListFragmentInteractionListener,
		MainFragment.OnFragmentInteractionListener,
		RateFragment.OnFragmentInteractionListener,
		UtilityMain.OnFragmentInteractionListener,
		TransitFragment.OnFragmentInteractionListener,
		OnFragmentInteractionListener,
		responseListener,
		imageListener {

	public static Context cntxt;
	public static JSONObject userData;
	public static Bitmap userAvatar;
	private String url;
	private ImageView avatarView;
	private TextView userName;
	public static Activity mainActivity;
	public static String apikey = "AIzaSyBuOC03IHPA_6TPnfk18b0SAgD1uge4-dk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("[BOOKMARK]","Started creating activity");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		cntxt = getApplicationContext();

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
		WebRequest login_manager = new WebRequest(url + "/login/",false,request,0);
		login_manager.addListener(this);
		Log.d("[BOOKMARK]","Before executing login");
		login_manager.execute();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		MainFragment fragment = new MainFragment();
		View header = navigationView.getHeaderView(0);

		avatarView = header.findViewById(R.id.avatar);
		userName = header.findViewById(R.id.uname);
		mainActivity = this;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.app_bar_main, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		Log.d("[BOOKMARK]","Done with main");
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
			TransitFragment fragment = new TransitFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);
		}  else if (id == R.id.rating) {
			RateFragment fragment = new RateFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);
		} else if (id == R.id.utilities) {
			UtilityMain fragment = new UtilityMain();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);
		}else if(id == R.id.denunciation){
			DenunciationFragment fragment = new DenunciationFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);
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

	public void receivedResponse(boolean success,String response,int reqid){
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
					avatar_downloader.execute(url +"/img/"+ userData.get("id")+".png");

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
			if(userAvatar != null) {
				avatarView.setImageBitmap(img);
			}
		}catch(Exception e){
			Log.e("[ERROR]","Cannot set avatar!");
		}
	}
}
