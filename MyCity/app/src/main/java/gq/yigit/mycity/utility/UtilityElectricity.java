package gq.yigit.mycity.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.zxing.WriterException;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import gq.yigit.mycity.MainActivity;
import gq.yigit.mycity.R;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.QRCodeGenerator;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class UtilityElectricity extends Fragment implements WebRequest.responseListener {

	private JSONObject electricityUsage;
	private GraphView graph;

	private TextView bill;
	private TextView points;
	private TextView efficiency;

	private Button qr_view;
	private Button pay_bill;


	private OnFragmentInteractionListener mListener;

	public UtilityElectricity() {
		// Required empty public constructor
	}


	public static UtilityElectricity newInstance(String param1, String param2) {
		UtilityElectricity fragment = new UtilityElectricity();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_utility_electricity, container, false);;
		graph = (GraphView) rootView.findViewById(R.id.utility_graph);

		points = rootView.findViewById(R.id.points_utility_electricity);
		bill = rootView.findViewById(R.id.bill_utility_electricity);
		efficiency = rootView.findViewById(R.id.efficiency_utility_electricity);

		pay_bill = rootView.findViewById(R.id.pay_bill_electricity);
		qr_view = rootView.findViewById(R.id.use_point_electricity);

		pay_bill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getContext(),"Bills paid successfully!",Toast.LENGTH_LONG).show();
			}
		});



		final ImagePopup imagePopup = new ImagePopup(getContext());
		imagePopup.setWindowHeight(712); // Optional
		imagePopup.setWindowWidth(712); // Optional
		imagePopup.setBackgroundColor(Color.WHITE);  // Optional
		imagePopup.setFullScreen(true); // Optional
		imagePopup.setImageOnClickClose(true);  // Optional
		try {
			imagePopup.initiatePopup(new BitmapDrawable(QRCodeGenerator.Generate(MainActivity.userData.getString("id"), 712, 712)));
		}catch (JSONException e){
			Log.e("[ERROR]","Cannot get user id");
		}catch (WriterException e){
			Log.e("[ERROR]","Writer exception occured");
		}

		qr_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				imagePopup.viewPopup();

			}
		});


		StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
		staticLabelsFormatter.setHorizontalLabels(new String[] {"0", "2", "4", "6", "8", "10", "12", "14", "16", "18", "20", "22", "24"});
		staticLabelsFormatter.setVerticalLabels(new String[] {"0", "5", "10","15","20"});
		graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

		FileActions file_manager = new FileActions();
		String url = file_manager.readFromFile(getContext(),"server.config").trim();
		HashMap<String,String> request = new HashMap<>();
		try {
			request.put("user_id", MainActivity.userData.getString("id"));
		}catch (JSONException e){
			Log.e("[ERROR]","User data not correct");
		}
		request.put("type","electricity");
		WebRequest login_manager = new WebRequest(url + "/resources/",false,request,0);
		login_manager.addListener(this);
		login_manager.execute();


		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
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
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	public void receivedResponse(boolean success, String result,int reqid){
		if(success){
			try{
				electricityUsage = new JSONObject(result);
				LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {});
				LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {});
				for(int i = 0;i < electricityUsage.getJSONArray("daily_electricity_usage").length();i++){
					series.appendData(new DataPoint(i,electricityUsage.getJSONArray("daily_electricity_usage").getInt(i)),
							true,
							100);
				}

				for(int i = 0;i < electricityUsage.getJSONArray("ideal_electricity_usage").length();i++){
					series2.appendData(new DataPoint(i,electricityUsage.getJSONArray("ideal_electricity_usage").getInt(i)),
							true,
							100);
				}
				series.setTitle("ideal");
				series.setColor(Color.BLUE);
				series2.setTitle("usage");
				series2.setColor(Color.RED);
				graph.addSeries(series);
				graph.addSeries(series2);

				points.setText(String.valueOf(((Double)electricityUsage.get("points")).intValue()));
				bill.setText(String.valueOf(((Double)electricityUsage.get("bill")).intValue()));
				efficiency.setText(String.valueOf(((Double)electricityUsage.get("efficiency")).intValue()));

			}catch (JSONException e){
				Log.e("[ERROR]","Cannot interpret response from electric service");
			}
		}
	}
}
