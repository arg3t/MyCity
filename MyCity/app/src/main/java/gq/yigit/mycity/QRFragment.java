package gq.yigit.mycity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.google.zxing.WriterException;
import org.json.JSONException;

import gq.yigit.mycity.tools.QRCodeGenerator;

public class QRFragment extends Fragment {


	private OnFragmentInteractionListener mListener;
	private ImageView qr_view;
	private Bitmap qr_img;
	public QRFragment() {
	}



	public static QRFragment newInstance(String param1, String param2) {
		QRFragment fragment = new QRFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_qr, container, false);
		qr_view = rootView.findViewById(R.id.qr_image);

		try {
			qr_img = QRCodeGenerator.Generate(MainActivity.userData.getString("id"),1080,1080);
			qr_view.setImageBitmap(qr_img);
		}catch(JSONException e){
			Log.e("[ERROR]","JSON error occured while generating qr code!");
		}catch (WriterException e){
			Log.e("[ERROR]","An error occured while generating qr code!");
		}
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
					+ " must implement OnComplaintsClicked");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
