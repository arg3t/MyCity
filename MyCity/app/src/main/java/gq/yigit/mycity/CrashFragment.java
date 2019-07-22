package gq.yigit.mycity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class CrashFragment extends Fragment implements WebRequest.responseListener {


	private OnFragmentInteractionListener mListener;
	private Uri mImageUri;
	private final int CAMERA_REQUEST = 100;
	private ImageButton img_button;
	private Bitmap img = null;
	private String url;
	private EditText input_msg;
	private ImageView img_show;

	public CrashFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView  = inflater.inflate(R.layout.fragment_crash, container, false);
		final CrashFragment activity = this;
		img_button = rootView.findViewById(R.id.denunciation_take_photo);
		Button submit = rootView.findViewById(R.id.denunciation_submit);
		url = FileActions.readFromFile(getContext(),"server.config").trim();
		img_show = rootView.findViewById(R.id.denunciation_photo);
		input_msg = rootView.findViewById(R.id.denunciation_text);


		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String,String> params = new HashMap<>();
				if(input_msg.getText().toString().length() < 10){
					Toast.makeText(getContext(),"Message should be longer than 10 characters!",Toast.LENGTH_LONG).show();
					return;
				}
				params.put("message",input_msg.getText().toString());
				if(img == null){
					Toast.makeText(getContext(),"You must add an image to the form!",Toast.LENGTH_LONG).show();
					return;
				}
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byte[] byteArray = byteArrayOutputStream.toByteArray();
				String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
				params.put("img",encoded);
				try{
					params.put("id",MainActivity.userData.getString("id"));
				}catch (JSONException e){}

				WebRequest webRequester = new WebRequest(url + "/crash_submit",false,params,0);
				webRequester.addListener(activity);
				webRequester.execute();
			}
		});


		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
		}
		img_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				File photo;
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				try
				{
					// place where to store camera taken picture
					photo = createTemporaryFile("picture", ".jpg");
					photo.delete();
					Uri mImageUri = Uri.fromFile(photo);
				}
				catch(Exception e)
				{
					Log.v("[ERROR]", "Can't create file to take picture!");
					Toast.makeText(getContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG);
				}
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
				//start camera intent
				activity.startActivityForResult(intent, CAMERA_REQUEST);
			}
		});

		return rootView;
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}

	@Override
	public void receivedResponse(boolean status, String data,int index){
		if(status){
			Toast.makeText(getContext(),"Crash sent succesfully!",Toast.LENGTH_SHORT).show();
		}
	}

	private File createTemporaryFile(String part, String ext) throws Exception
	{
		File tempDir= Environment.getExternalStorageDirectory();
		tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
		if(!tempDir.exists())
		{
			tempDir.mkdirs();
		}
		return File.createTempFile(part, ext, tempDir);
	}

	//called after camera intent finished
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//MenuShootImage is user defined menu option to shoot image
		if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK)
		{
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			img = photo;
			img_show.setImageBitmap(img);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 0) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				img_button.setEnabled(true);
			}
		}
	}
}
