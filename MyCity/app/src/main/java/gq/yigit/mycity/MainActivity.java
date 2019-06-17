package gq.yigit.mycity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
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
import gq.yigit.mycity.complaintsFragment.ComplaintFragment;
import gq.yigit.mycity.complaintsFragment.ComplaintViewFragment;
import gq.yigit.mycity.complaintsFragment.ComplaintsContent;
import gq.yigit.mycity.complaintsFragment.ComplaintsFragment;
import gq.yigit.mycity.oldShit.DenunciationFragment;
import gq.yigit.mycity.oldShit.ParkFragment;
import gq.yigit.mycity.oldShit.RateFragment;
import gq.yigit.mycity.tools.*;
import gq.yigit.mycity.tools.WebRequest.responseListener;
import gq.yigit.mycity.oldShit.utility.UtilityMain;
import gq.yigit.mycity.oldShit.votesFragment.VoteFragment;
import gq.yigit.mycity.oldShit.votesFragment.VotesContent;
import gq.yigit.mycity.oldShit.votesFragment.VotesFragment;
import gq.yigit.mycity.oldShit.votesFragment.VotesFragment.OnListFragmentInteractionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static gq.yigit.mycity.tools.ImageDownload.*;
import static gq.yigit.mycity.oldShit.votesFragment.VoteFragment.*;

public class MainActivity extends AppCompatActivity
		implements
		OnNavigationItemSelectedListener,
		OnListFragmentInteractionListener,
		MainFragment.OnFragmentInteractionListener,
		RateFragment.OnFragmentInteractionListener,
		UtilityMain.OnFragmentInteractionListener,
		TransitFragment.OnFragmentInteractionListener,
		QRFragment.OnFragmentInteractionListener,
		OnFragmentInteractionListener,
		ParkFragment.OnFragmentInteractionListener,
		ComplaintFragment.OnComplaintsClicked,
		ComplaintsFragment.OnListFragmentInteractionListener,
		ComplaintViewFragment.OnFragmentInteractionListener,
		CrashFragment.OnFragmentInteractionListener,
		CrashMapFragment.OnFragmentInteractionListener,
		responseListener,
		imageListener {

	public static Context cntxt;
	public static JSONObject userData;
	public static Bitmap userAvatar;
	private String url;
	private ImageView avatarView;
	private TextView userName;
	public static Activity mainActivity;
	public static DisplayMetrics pix_density;
	public static String apikey = "AIzaSyBuOC03IHPA_6TPnfk18b0SAgD1uge4-dk";
	public boolean present = true;
	public MenuItem present_item;
	public static LocationManager locationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("[BOOKMARK]","Started creating activity");
		super.onCreate(savedInstanceState);
		pix_density = getApplicationContext().getResources().getDisplayMetrics();
		locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
					1 );
		}else if( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
					1 );

		}
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		try {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 1000, new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					Log.i("[INFO]", "Location changed to lat:" + location.getLatitude() + " lng:" + location.getLongitude());
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {

				}

				@Override
				public void onProviderEnabled(String provider) {
					Log.i("[INFO]", "Provider enabled: " + provider);
				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.i("[INFO]", "Provider disabled: " + provider);

				}
			});
		}catch (SecurityException e){
			Log.e("[ERROR]", "An error occured with location permissions");
		}
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
		Menu menu = navigationView.getMenu();
		present_item = menu.findItem(R.id.present_items);
		present_item.setVisible(!present);
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
		}else if(id == R.id.action_presentation){
			present = !present;
			present_item.setVisible(!present);
			Toast.makeText(getApplicationContext(),"Toggled presentation mode!",Toast.LENGTH_LONG).show();
		}else if(id == R.id.action_restart){
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
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
			ParkFragment fragment = new ParkFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			fragmentTransaction.addToBackStack(null);
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
		}else if (id == R.id.qr_code){
			QRFragment fragment= new QRFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
		}else if (id == R.id.complaint){
			ComplaintFragment fragment= new ComplaintFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
		}else if (id == R.id.crash){
			CrashFragment fragment= new CrashFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
		}else if (id == R.id.crash_map){
			CrashMapFragment fragment = new CrashMapFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
		}


		fragmentTransaction.addToBackStack(null);
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

	public void ComplaintsClicked(@Nullable ComplaintsContent.ComplaintItem item){

		if(item == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			ComplaintsFragment fragment = new ComplaintsFragment();
			fragmentTransaction.replace(R.id.app_bar_main, fragment);
			fragmentTransaction.commit();
			return;
		}


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

	@Override
	public void onListFragmentInteraction(ComplaintsContent.ComplaintItem item){
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		ComplaintViewFragment fragment = ComplaintViewFragment.newInstance(item.toString());
		fragmentTransaction.replace(R.id.app_bar_main, fragment);
		fragmentTransaction.commit();
	}


}
