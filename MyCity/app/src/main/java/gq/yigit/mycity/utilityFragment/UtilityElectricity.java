package gq.yigit.mycity.utilityFragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import gq.yigit.mycity.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UtilityElectricity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UtilityElectricity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UtilityElectricity extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


	// TODO: Rename and change types of parameters


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
		GraphView graph = (GraphView) rootView.findViewById(R.id.utility_graph);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3)
		});
		LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
				new DataPoint(0, 4),
				new DataPoint(1, 1),
				new DataPoint(2, 7)
		});

		StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
		staticLabelsFormatter.setHorizontalLabels(new String[] {"old", "middle", "new"});
		staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
		graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

		series.setTitle("ideal");
		series.setColor(Color.BLUE);
		series2.setTitle("usage");
		series2.setColor(Color.RED);
		graph.addSeries(series);
		graph.addSeries(series2);

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

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
