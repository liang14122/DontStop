package sg.edu.rp.c346.dontstop;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseUser user;
    FirebaseAuth mAuth;

    ImageView ivProfile;
    TextView tvUserName;
    NavigationView navigationView;
    View headerView;

    @Override
    protected void onStart() {
        super.onStart();


        Intent i = getIntent();
        boolean data = i.getBooleanExtra("data", false);
        if (data) {
            Bundle bundle = new Bundle();
            double distance = i.getDoubleExtra("distance", 0.0);
            String duration = i.getStringExtra("duration");
            bundle.putDouble("distance", distance);
            bundle.putString("duration", duration);

            Fragment frag = new HistoryFragment();
            frag.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.drawer_fragment_container, frag);
            ft.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.textViewUsername);
        ivProfile = headerView.findViewById(R.id.imageViewProfilePic);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference myRef = FirebaseFirestore.getInstance().document(uid + "/Profile");
            String userName = user.getDisplayName();
            String url = user.getPhotoUrl().toString();
            Log.d("tag", url);
            tvUserName.setText(userName);
            Picasso.get().load(url).into(ivProfile);
        }

        Fragment fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.drawer_fragment_container, fragment);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedFragment(id);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedFragment(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_start_running:
                fragment = new MapFragment();
                break;
            case R.id.nav_Logout:
                mAuth.signOut();
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(i);
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_fragment_container, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
