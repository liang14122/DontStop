package sg.edu.rp.c346.dontstop;

import android.content.Context;
import android.content.Intent;

import com.today.step.lib.BaseClickBroadcast;

public class MyReceiver extends BaseClickBroadcast {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent mainIntent = new Intent(context,HomeFragment.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);


    }
}
