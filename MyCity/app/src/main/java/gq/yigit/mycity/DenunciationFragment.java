package gq.yigit.mycity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class DenunciationFragment extends Fragment implements WebRequest.responseListener {

	private OnFragmentInteractionListener mListener;
	private int reps = 0;
	private String url;
	private DenunciationFragment activity;
	private Spinner spinner;
	private String emergency;
	private Button submit;
	private EditText note;
	private Bitmap img;
	private ImageButton img_button;
	private static final int CAMERA_REQUEST = 1888;

	public DenunciationFragment() {
	}


	public static DenunciationFragment newInstance(String param1, String param2) {
		DenunciationFragment fragment = new DenunciationFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {



		View rootView = inflater.inflate(R.layout.fragment_denunciation, container, false);
		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(getContext(),"server.config").trim();
		activity=this;



		submit = rootView.findViewById(R.id.denunciation_submit);
		note = (EditText) rootView.findViewById(R.id.denunciation_text);
		img_button= rootView.findViewById(R.id.denunciation_photo);
		spinner = rootView.findViewById(R.id.denunciation_spinner);

		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			img_button.setEnabled(false);
			ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
		}

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				HashMap<String,String> args = new HashMap<>();
				try {
					args.put("id", MainActivity.userData.getString("id"));
				}catch (JSONException e){}
				args.put("emergency",emergency);

				args.put("note",note.getText().toString());
				args.put("accepted","false");

				if(img != null) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
					byte[] byteArray = byteArrayOutputStream.toByteArray();
					String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

					args.put("photo", encoded);
				}else{
					args.put("photo", "null");
				}

				WebRequest request = new WebRequest(url+"/denunciation",false,args);
				request.addListener(activity);
				request.execute();
			}
		});

		img_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);

			}
		});

		ArrayAdapter<String> adapter;
		final List<String> list = new ArrayList<String>();
		list.add("Please Select...");
		list.add("Ambulance");
		list.add("Police");
		list.add("Fire");
		adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Log.i("[INFO]",String.valueOf(position));
				if(position>0) {emergency = list.get(position);}			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
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

	public void receivedResponse(boolean success, String response) {
		try {
			JSONObject received = new JSONObject(response);
			if (reps % 2 == 0) {
				if ((boolean) received.get("success")) {
					Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
				} else {
					reps++;

					final Dialog dialog = new Dialog(getContext());
					dialog.setContentView(R.layout.popup_confirmation);
					Button dialogButton = (Button) dialog.findViewById(R.id.confirm_button);
					((TextView)dialog.findViewById(R.id.confirm_text)).setText("If this alert is wrong, you will receive a penalty of " + received.getString("penalty") +"$");
					dialogButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							HashMap<String,String> args = new HashMap<>();
							try {
								args.put("id", MainActivity.userData.getString("id"));
							}catch (JSONException e){}
							args.put("emergency",emergency);

							args.put("note",note.getText().toString());
							if(img != null) {
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
								byte[] byteArray = byteArrayOutputStream.toByteArray();
								String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

								args.put("photo", encoded);
							}else{
								args.put("photo", "null");
							}
							args.put("accepted","true");

							WebRequest requestConfirm = new WebRequest(url+"/denunciation",false,args);
							requestConfirm.addListener(activity);
							requestConfirm.execute();
						}
					});
					dialog.show();
				}
			}else{
				reps ++;
				if ((boolean) received.get("success")) {
					Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getContext(), "An error occured", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) { }
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
