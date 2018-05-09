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

    GoalItem(String goalType, int targetAmount, int currentAmount) {
        this.goalType = goalType;
        this.targetAmount = targetAmount;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(int targetAmount) {
        this.targetAmount = targetAmount;
    }

}
