package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eldroid.grocerylist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.GroceryItem;

public class GroceryAdapter extends ArrayAdapter<GroceryItem> {
    private final RefreshCallback refreshCallback;
    String username;

    public interface RefreshCallback {
        void onRefresh(String username);  // Pass username to refetch the groceries
    }

    public GroceryAdapter(@NonNull Context context, String username, ArrayList<GroceryItem> groceries, RefreshCallback refreshCallback) {
        super(context, 0, groceries);
        this.refreshCallback = refreshCallback;  // Save the callback
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        GroceryItem item = getItem(position);

        if (item != null) {
            TextView nameTextView = convertView.findViewById(R.id.groceryName);
            TextView descriptionTextView = convertView.findViewById(R.id.description);
            Button editButton = convertView.findViewById(R.id.edit);
            Button deleteButton = convertView.findViewById(R.id.delete);

            nameTextView.setText(item.getName());
            descriptionTextView.setText(item.getDescription());

            // Edit Button
            editButton.setOnClickListener(v -> {
                // Show edit dialog
                showEditDialog(item);
            });

            // Delete Button
            deleteButton.setOnClickListener(v -> {
                showDeleteDialog(item.getId(), username);
            });
        }

        return convertView;
    }

    private void showEditDialog(GroceryItem item) {
        // Show dialog to update grocery name and description
        // Use AlertDialog or custom dialog layout here
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Grocery Item");

        // Use a layout with EditTexts to input the new name and description
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_grocery, null);
        EditText nameEditText = dialogView.findViewById(R.id.editGroceryName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editGroceryDescription);

        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = nameEditText.getText().toString();
                    String updatedDescription = descriptionEditText.getText().toString();
                    updateGrocery(item.getId(), updatedName, updatedDescription, username);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showDeleteDialog(String itemId, String username) {
        // Show confirmation dialog for deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Grocery Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> deleteGrocery(itemId, username))
                .setNegativeButton("No", null)
                .show();
    }

    private void updateGrocery(String id, String name, String description, String username) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "https://php-rob-rproject-149a0118c18c.herokuapp.com/api/groceries/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("UpdateGrocery", "Grocery updated: " + response);
                        Toast.makeText(getContext(), "Item updated", Toast.LENGTH_SHORT).show();
                        // Call the callback to refresh the grocery list
                        refreshCallback.onRefresh(username);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UpdateGrocery", "Error updating grocery: " + error.getMessage(), error);
                        Toast.makeText(getContext(), "Error updating grocery", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("groceryName", name);
                params.put("groceryDescription", description);
                return params;
            }
        };

        queue.add(stringRequest);
    }


    private void deleteGrocery(String id, String username) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "https://php-rob-rproject-149a0118c18c.herokuapp.com/api/groceries/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DeleteGrocery", "Grocery deleted: " + response);
                        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                        // Call the callback to refresh the grocery list
                        refreshCallback.onRefresh(username);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DeleteGrocery", "Error deleting grocery: " + error.getMessage(), error);
                        Toast.makeText(getContext(), "Error deleting grocery", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }
}
