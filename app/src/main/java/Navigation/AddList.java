package Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eldroid.grocerylist.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AddList extends Fragment {

    private EditText groceryNameEditText, descriptionEditText;
    private Button addButton;
    private static final String API_URL = "https://php-rob-rproject-149a0118c18c.herokuapp.com/api/groceries";

    public AddList() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groceryNameEditText = view.findViewById(R.id.groceryName);
        descriptionEditText = view.findViewById(R.id.description);
        addButton = view.findViewById(R.id.addList);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groceryName = groceryNameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String username = "";
                // Get username from arguments
                Bundle args = getArguments();
                if (args != null) {
                    username = args.getString("username");
                }

                if (groceryName.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    addGrocery(username, groceryName, description);
                }
            }
        });
    }

    private void addGrocery(String username, String groceryName, String description) {
        try {
            // Create the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username);
            requestBody.put("groceryName", groceryName);
            requestBody.put("groceryDescription", description);

            // Initialize Volley Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

            // Create the POST request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    API_URL,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                JSONObject data = response.getJSONObject("data");
                                String createdId = data.getString("id");
                                Toast.makeText(getContext(), message + " (ID: " + createdId + ")", Toast.LENGTH_LONG).show();

                                // Clear input fields
                                groceryNameEditText.setText("");
                                descriptionEditText.setText("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), "Failed to add grocery: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Add the request to the queue
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}
