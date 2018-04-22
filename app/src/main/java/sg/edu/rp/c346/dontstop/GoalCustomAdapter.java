package sg.edu.rp.c346.dontstop;

import android.annotation.SuppressLint;
import android.content.Context;
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

        //Set goal title
        String title = currentGoal.getGoalName();
        tvTargetAmount.setText(title);

        //pir chart
        PieChart chartMon = rowView.findViewById(R.id.chartToday);
        chartMon.setUsePercentValues(false);
        chartMon.getDescription().setEnabled(false);
        chartMon.setDrawHoleEnabled(true);
        chartMon.setHoleColor(Color.WHITE);
        chartMon.setHoleRadius(70f);
        chartMon.getLegend().setEnabled(false);
        ArrayList<PieEntry> values = new ArrayList<>();
        int difference = currentGoal.getTargetAmount() - currentGoal.getCurrentAmount();
        if (difference > 0) {
            values.add(new PieEntry(currentGoal.getCurrentAmount()));
            values.add(new PieEntry(difference));
        }

        PieDataSet dataSet = new PieDataSet(values, "Demo");
        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> color = new ArrayList<>();
        color.add(Color.LTGRAY);
        color.add(Color.DKGRAY);
        dataSet.setColors(color);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chartMon.setData(data);

        //Using Array<Map> display



        //set image for ivs
        ImageView currentImageView = null;
        int amountforThatDay;
        int length = currentGoal.getWeeklyAmount().size();
        for (int i = 1; i < length+1; i++) {
            amountforThatDay = (Integer) currentGoal.getWeeklyAmount().get(i).keySet().toArray()[0];
            switch (i) {
                case 1:
                    currentImageView = rowView.findViewById(R.id.ivOne);
                    break;
                case 2:
                    currentImageView = rowView.findViewById(R.id.ivTwo);
                    break;
                case 3:
                    currentImageView = rowView.findViewById(R.id.ivThree);
                    break;
                case 4:
                    currentImageView = rowView.findViewById(R.id.ivFour);
                    break;
                case 5:
                    currentImageView = rowView.findViewById(R.id.ivFive);
                    break;
                case 6:
                    currentImageView = rowView.findViewById(R.id.ivSix);
                    break;
                case 7:
                    currentImageView = rowView.findViewById(R.id.ivSeven);
                    break;
            }
            if (currentImageView != null) {
                int targetAmount = currentGoal.getTargetAmount();
                if (amountforThatDay >= targetAmount) {
                    currentImageView.setImageResource(R.drawable.check);
                }else if (amountforThatDay < targetAmount){
                    currentImageView.setImageResource(R.drawable.cross);
                }
            }
        }
            return rowView;

    }
}
