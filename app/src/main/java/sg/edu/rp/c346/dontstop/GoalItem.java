package sg.edu.rp.c346.dontstop;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 16004118 on 28/12/2017.
 *
 */

public class GoalItem {
    private String goalType;
    private Long targetAmount;
    private int currentAmount;

    public GoalItem() {
    }

    public GoalItem(String goalType, Long targetAmount, int currentAmount) {
        this.goalType = goalType;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long targetAmount) {
        this.targetAmount = targetAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }
}
