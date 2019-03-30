package gq.yigit.mycity.utilityFragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import gq.yigit.mycity.R;

import java.util.ArrayList;
import java.util.List;


public class UtilityFragment extends Fragment {




	private OnFragmentInteractionListener mListener;

	public UtilityFragment() {
	}


	public static UtilityFragment newInstance(String param1, String param2) {
		UtilityFragment fragment = new UtilityFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_utility, container, false);
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

		ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		TabLayout tabs = (TabLayout) rootView.findViewById(R.id.result_tabs);
		tabs.setupWithViewPager(viewPager);

		return rootView;
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	private void setupViewPager(ViewPager viewPager) {


		Adapter adapter = new Adapter(getChildFragmentManager());
		adapter.addFragment(new UtilityFragment(), "Electricity");
		viewPager.setAdapter(adapter);



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
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}


class Adapter extends FragmentPagerAdapter {
	private final List<Fragment> mFragmentList = new ArrayList<>();
	private final List<String> mFragmentTitleList = new ArrayList<>();

	public Adapter(FragmentManager manager) {
		super(manager);
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	public void addFragment(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
}




