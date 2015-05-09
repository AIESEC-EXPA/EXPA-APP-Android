package org.aiesec.experience.expa;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchOpportunityFragment extends Fragment {


    public SearchOpportunityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_opportunity, container, false);

        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(true);
        searchView.onActionViewExpanded();
        searchView.clearFocus();

        ListView listView = (ListView) view.findViewById(R.id.searchOpportunityListView);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(), R.layout.search_opportunity_listview_item,
                new String[]{"title", "location"},
                new int[]{R.id.projectTitleTextView, R.id.projectLocationTextView});
        listView.setAdapter(adapter);

        getActivity().setTitle("Search Opportunities");

        return view;
    }

    private List<Map<String, String>> getData()
    {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("title", "Project 1");
        map.put("location", "Beijing");
        list.add(map);

        map = new HashMap<>();
        map.put("title", "Project 2");
        map.put("location", "Beijing");
        list.add(map);

        return list;
    }
}
