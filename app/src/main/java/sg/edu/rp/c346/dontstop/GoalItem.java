package sg.edu.rp.c346.dontstop;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 16004118 on 28/12/2017.
 *
 */

public class GoalItem {
    private String goalType;
    private int targetAmount;
    private int currentAmount;
    private ArrayList<Map> weeklyAmount;

    GoalItem(){}

    GoalItem(String goalName, int targetAmount, int currentAmount, ArrayList<Map> weeklyAmount) {
        this.goalType = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.weeklyAmount = weeklyAmount;
    }

    ArrayList<Map> getWeeklyAmount() {
        return weeklyAmount;
    }

    public void setWeeklyAmount(ArrayList<Map> weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
    }

    String getGoalName() {
        return goalType;
    }

    public void setGoalName(String goalType) {
        this.goalType = goalType;
    }

    int getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(int targetAmount) {
        this.targetAmount = targetAmount;
    }

    int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }
}
