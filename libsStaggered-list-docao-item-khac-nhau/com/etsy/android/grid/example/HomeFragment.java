package com.etsy.android.grid.example;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;
import com.ict.library.R;

public class HomeFragment extends Fragment {
	private StaggeredGridView mGridView;

	public HomeFragment() {
	}

	List<JSONObject> list = new ArrayList<JSONObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.libsstaggered_list_docao_item_khac_nhau, null);

		// viewpager = inflater.inflate(R.id.home_pager, null);
		mGridView = (StaggeredGridView) view.findViewById(R.id.grid_view);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});

		return view;
	}

}