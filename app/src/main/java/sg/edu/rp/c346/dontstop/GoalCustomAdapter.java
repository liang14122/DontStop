package sg.edu.rp.c346.dontstop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepService;

import java.util.ArrayList;


/**
 * Created by 16004118 on 18/11/2017.
 *
 */

public class GoalCustomAdapter extends ArrayAdapter {
    private Context parent_context;
    private int layout_id;
    private ArrayList<GoalItem> goalList;

    GoalCustomAdapter(Context context,
                      int resource,
                      ArrayList<GoalItem> objects) {
        super(context, resource, objects);
        parent_context = context;
        layout_id = resource;
        goalList = objects;
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

        GoalItem currentGoal = goalList.get(position);
        TextView tvTargetAmount = rowView.findViewById(R.id.tvTargetAmount);
        TextView tv = rowView.findViewById(R.id.textView);

        //Set goal title
        String type = currentGoal.getGoalType();
        Long targetAmount = currentGoal.getTargetAmount();
        Long amountLeft = targetAmount - currentGoal.getCurrentAmount();
        tvTargetAmount.setText(targetAmount + " " + type + " per day");
        tv.setText(amountLeft + " / " + targetAmount + " Steps Left");

            return rowView;

    }



}
