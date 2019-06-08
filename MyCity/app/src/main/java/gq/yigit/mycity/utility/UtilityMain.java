package gq.yigit.mycity.utility;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gq.yigit.mycity.R;

import java.util.ArrayList;
import java.util.List;


public class UtilityMain extends Fragment {




	private OnFragmentInteractionListener mListener;

	public UtilityMain() {
	}


	public static UtilityMain newInstance(String param1, String param2) {
		UtilityMain fragment = new UtilityMain();
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



		// Setting ViewPager for each Tabs
		ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
		setupViewPager(viewPager);
		// Set Tabs inside Toolbar
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
		adapter.addFragment(new UtilityElectricity(), "Electricity");
		adapter.addFragment(new UtilityWater(), "Water");
		viewPager.setAdapter(adapter);



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

	static class Adapter extends FragmentPagerAdapter {
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
}









