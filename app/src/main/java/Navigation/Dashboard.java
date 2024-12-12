package Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eldroid.grocerylist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.GroceryAdapter;
import Model.GroceryItem;

public class Dashboard extends Fragment {

    private ListView listView;
    private ArrayList<GroceryItem> groceryList;
    private GroceryAdapter adapter;
    private String FETCH_URL = "https://php-rob-rproject-149a0118c18c.herokuapp.com/api/groceries/user";

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView); // Add an `id` to your ListView in `fragment_dashboard.xml`
        groceryList = new ArrayList<>();

        // Get username from arguments
        Bundle args = getArguments();
        String username = "";
        if (args != null) {
            username = args.getString("username");
            if (username != null) {
                fetchGroceries(username);
            }
        }

        adapter = new GroceryAdapter(requireContext(), username,  groceryList, new GroceryAdapter.RefreshCallback() {
            @Override
            public void onRefresh(String username) {
                fetchGroceries(username);  // Call fetchGroceries to refresh data
            }
        });
        listView.setAdapter(adapter);
    }

    private void fetchGroceries(String username) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        Log.d("FetchGroceries", "Fetching groceries for username: " + username);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("FetchGroceries", "Response received: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");

                            groceryList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject grocery = data.getJSONObject(i);

                                String name = grocery.getString("groceryName");
                                String id = grocery.getString("id");
                                String description = grocery.getString("groceryDescription");

                                Log.d("FetchGroceries", "Adding grocery: " + name + ", " + description);
                                groceryList.add(new GroceryItem(name, description, id));
                            }

                            Log.d("FetchGroceries", "Grocery list size before notify: " + groceryList.size());
                            adapter.notifyDataSetChanged();
                            Log.d("FetchGroceries", "Groceries successfully updated in adapter.");
                        } catch (JSONException e) {
                            Log.e("FetchGroceries", "Error parsing response", e);
                            Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FetchGroceries", "Error fetching groceries: " + error.getMessage(), error);
                        Toast.makeText(requireContext(), "Error fetching groceries: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                Log.d("FetchGroceries", "Request params: " + params);
                return params;
            }
        };

        queue.add(stringRequest);
        Log.d("FetchGroceries", "Request added to queue.");
    }
}
