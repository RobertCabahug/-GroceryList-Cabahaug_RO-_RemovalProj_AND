package Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.eldroid.grocerylist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        // Get the username from the intent and add it to a bundle
        String username = getIntent().getStringExtra("username");
        Bundle bundle = new Bundle();
        bundle.putString("username", username);

        Log.d("NavActivity", "Username passed to Dashboard: " + username);

        // Create the Dashboard fragment and set arguments
        Dashboard dashboardFragment = new Dashboard();
        dashboardFragment.setArguments(bundle);

        // Initially set the Dashboard Fragment with the bundle
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment)
                .commit();

        // Set up listener for bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                Log.d("Navigation Activity", "Selected Menu ID: " + id);
                Fragment selectedFragment;

                // Pass the username to other fragments if necessary
                Bundle newBundle = new Bundle();
                newBundle.putString("username", username);

                // Change the background color of the BottomNavigationView to yellow
                bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.yellow_gold));

                // Handle fragment selection based on the menu item selected
                if (id == R.id.navi_home) {
                    selectedFragment = new Dashboard();
                    selectedFragment.setArguments(newBundle);
                } else if (id == R.id.navi_settings) {
                    selectedFragment = new Settings();
                    selectedFragment.setArguments(newBundle); // Pass bundle to settings if needed
                } else if (id == R.id.navi_add) {
                    selectedFragment = new AddList();
                    selectedFragment.setArguments(newBundle); // Pass bundle to AddList if needed

                } else {
                    throw new IllegalStateException("Unexpected value: " + id);
                }

                // Apply fragment transaction
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                );
                transaction.replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }
}
