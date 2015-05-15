package org.aiesec.experience.expa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyOpportunitiesFragment extends Fragment {
    public MyOpportunitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_opportunities_fragment, container, false);

        ListView listView = (ListView) view.findViewById(R.id.myOpportunitiesListView);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(), R.layout.my_opportunies_listview_item,
                new String[]{"title", "status"},
                new int[]{R.id.myOpportunitiesTitleTextView, R.id.myOpportunitiesStatusTextView});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OpportunityInfoActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        getActivity().setTitle("My Opportunies");
        return view;
    }

    private List<Map<String, String>> getData()
    {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("title", "Dare to Dream");
        map.put("status", "Applied");
        list.add(map);

        map = new HashMap<>();
        map.put("title", "Project 2");
        map.put("status", "Matched");
        list.add(map);

        return list;
    }
}
