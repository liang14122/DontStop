package sg.edu.rp.c346.dontstop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.today.step.lib.BaseClickBroadcast;

public class MyReceiver extends BaseClickBroadcast {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent mainIntent = new Intent(context,HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
//        FragmentActivity activity = HomeActivity.class.cast(new FragmentActivity());
//        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
//        Fragment frag = new HomeFragment();
//        transaction.replace(R.id.drawer_fragment_container, frag).commit();

    }
}
