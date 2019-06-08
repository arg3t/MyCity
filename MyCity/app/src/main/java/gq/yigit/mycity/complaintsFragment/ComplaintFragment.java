package gq.yigit.mycity.complaintsFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import gq.yigit.mycity.MainActivity;
import gq.yigit.mycity.R;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class ComplaintFragment extends Fragment implements WebRequest.responseListener {

	private OnComplaintsClicked mListener;
	private ImageView complaint_image;
	private ComplaintFragment activity;
	private Uri mImageUri;
	private Bitmap img;
	private String img_b64 = "";
	private Button submit_button;
	private EditText text_in;
	private String url;

	public ComplaintFragment() {
		// Required empty public constructor
	}

	public static ComplaintFragment newInstance(String param1, String param2) {
		ComplaintFragment fragment = new ComplaintFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_complaint, container, false);
		activity = this;

		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(getContext(),"server.config").trim();

		complaint_image = rootView.findViewById(R.id.complaint_image);
		submit_button = rootView.findViewById(R.id.compaint_submit);
		text_in = rootView.findViewById(R.id.complaint_text);

		rootView.findViewById(R.id.complaints_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.ComplaintsClicked(null);
			}
		});
		submit_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				HashMap<String,String> params = new HashMap<>();

				try {
					Location curloc = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					params.put("lat",String.valueOf(curloc.getLatitude()));
					params.put("lng",String.valueOf(curloc.getLongitude()));
				}catch (SecurityException e){
					Log.e("[ERROR]", "An error occured with location permissions");

				}
				if(img_b64.isEmpty()){
					Toast.makeText(getContext(),"Please take a photo of the complaint!",Toast.LENGTH_LONG).show();
					return;
				}

				if(text_in.getText().toString().length() < 10){
					Toast.makeText(getContext(),"Complaint should be minimum 10 characters",Toast.LENGTH_LONG).show();
					return;
				}

				params.put("img",img_b64);
				params.put("content",text_in.getText().toString());
				try {
					params.put("id", MainActivity.userData.getString("id"));
				}catch (JSONException e){
					Log.e("[ERROR]","Cannot get id");
				}

				WebRequest request = new WebRequest(url+"/complaint",false, params,0);
				request.addListener(activity);
				request.execute();

			}

		});

		complaint_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
				activity.startActivityForResult(intent, 100);
			}
		});



		return rootView;
	}



	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnComplaintsClicked) {
			mListener = (OnComplaintsClicked) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnComplaintsClicked");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	public interface OnComplaintsClicked {
		void ComplaintsClicked(@Nullable ComplaintsContent.ComplaintItem item);
	}


	//called after camera intent finished
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==100 && resultCode==RESULT_OK)
		{
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			img = photo;
			complaint_image.setImageBitmap(img);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			img_b64= Base64.encodeToString(byteArray, Base64.DEFAULT);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void receivedResponse(boolean success, String response,int id){
		if(success){
			Toast.makeText(getContext(),"Complaint send successfully!",Toast.LENGTH_SHORT).show();
		}
	}
}
