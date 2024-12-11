package Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        //Initially set the Dashboard Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Dashboard())
                .commit();

        // Set up listener for bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected( MenuItem menuItem) {
                int id = menuItem.getItemId();
                Log.d("Navigation Activity", "Selected Menu ID: " + id);
                Fragment selectedFragment;

                // Change the background color of the BottomNavigationView to red when an item is selected
                bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.yellow_gold)); // Set red color

                // Handle fragment selection based on the menu item selected
                if (id == R.id.navi_home) {
                    selectedFragment = new Dashboard();
                } else if (id == R.id.navi_settings) {
                    selectedFragment = new Settings();
                } else if (id == R.id.navi_add) {
                    selectedFragment = new AddList();
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