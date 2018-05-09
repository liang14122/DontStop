package sg.edu.rp.c346.dontstop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter {

    private Context parent_context;
    private int layout_id;
    private ArrayList<History> historyList;

    HistoryAdapter(Context context,
                   int resource,
                   ArrayList<History> objects) {
        super(context, resource, objects);
        parent_context = context;
        layout_id = resource;
        historyList = objects;
    }

    @SuppressLint("WrongViewCast")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {


        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) parent_context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            rowView = inflater.inflate(layout_id, parent, false);
        } else {
            rowView = convertView;
        }

        History history = historyList.get(position);
        TextView tvDuration = rowView.findViewById(R.id.textViewDuration);
        TextView tvDistance = rowView.findViewById(R.id.textViewDistance);

        tvDuration.setText("Duration: " + history.getDuration());
        tvDistance.setText("Distance: " + Double.toString(history.getDistance()) + "M");

        return rowView;
    }
}
