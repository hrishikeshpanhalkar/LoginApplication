package com.example.loginapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginapplication.Adapter.MainAdapter;
import com.example.loginapplication.R;
import com.example.loginapplication.Model.MainData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements MainAdapter.SelectedUser {
    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList<MainData> list;
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.myRecycler);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list = new ArrayList<MainData>();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait ...");
        dialog.setCancelable(true);
        dialog.show();

        String url = "https://jsonplaceholder.typicode.com/users";

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        dialog.dismiss();
                        JSONArray jsonArray = new JSONArray(response);
                        parseArray(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                MainData mainData = new MainData();
                mainData.setName(object.getString("name"));
                mainData.setUsername(object.getString("username"));
                mainData.setEmail(object.getString("email"));
                mainData.setPhone(object.getString("phone"));
                String address = object.getString("address");
                JSONObject object1 = new JSONObject(address);
                mainData.setCity(object1.getString("city"));


                list.add(mainData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mainAdapter = new MainAdapter(HomeActivity.this, list, HomeActivity.this);
        }
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    public void selectedUser(MainData mainData) {
            final Dialog dialog1= new Dialog(HomeActivity.this);
            dialog1.setContentView(R.layout.alert_dialog_user);
            dialog1.setCanceledOnTouchOutside(false);
            TextView name = (TextView)dialog1.findViewById(R.id.name_value);
            TextView username = (TextView)dialog1.findViewById(R.id.username_value);
            TextView email = (TextView)dialog1.findViewById(R.id.email_value);
            TextView phone = (TextView)dialog1.findViewById(R.id.phone_value);
            TextView city = (TextView)dialog1.findViewById(R.id.city_value);
            Button OK = (Button) dialog1.findViewById(R.id.ok);

            name.setText(mainData.getName());
            username.setText(mainData.getUsername());
            email.setText(mainData.getEmail());
            phone.setText(mainData.getPhone());
            city.setText(mainData.getCity());


            OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });
            dialog1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mainAdapter == null || mainAdapter.getItemCount() == 0) {
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu, menu);
            MenuItem menuItem = menu.findItem(R.id.search_view);
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mainAdapter.getFilter().filter(newText);
                    return true;
                }
            });
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_view) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("LOGOUT?");
        alertDialog.setMessage("Are you sure you want to Logout?");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}

