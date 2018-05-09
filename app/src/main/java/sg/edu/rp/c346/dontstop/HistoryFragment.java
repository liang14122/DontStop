package sg.edu.rp.c346.dontstop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    private static String TAG = "HistoryFragment";

    private HistoryAdapter ha;
    private ArrayList<History> historyList = new ArrayList<>();
    private ListView lvHistory;
    private History historyItem;
    private String duration;
    private double distance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        lvHistory = view.findViewById(R.id.listViewHistory);
        ha = new HistoryAdapter(getActivity(), R.layout.history_row, historyList);
        lvHistory.setAdapter(ha);
        historyItem = new History();

        if (getArguments() != null){
            duration = getArguments().getString("duration");
            distance = getArguments().getDouble("distance");

            historyItem.setDuration(duration);
            historyItem.setDistance(distance);

            historyList.add(historyItem);
            ha.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(),"Not Ready Yet", Toast.LENGTH_LONG).show();
        }

        return view;
    }

}
