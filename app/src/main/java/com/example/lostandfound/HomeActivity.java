package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    Button menu;
    DatabaseReference databaseReference;
    List<Data> dataList;
    private DBHelper dbHelper;
    RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private AlertDialog customdialog;
    private String username, email;
    private FloatingActionButton fabCreate;

    private TextView navHome, navLost, navFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        showUpdate("Loading.....");
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        //shared pref
        username = prefs.getString("Username", "");
        email = prefs.getString("Email", "");

        //local storage
        dbHelper = new DBHelper(this);

        menu = findViewById(R.id.menuButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //navbar
        navHome = findViewById(R.id.navHome);
        navLost = findViewById(R.id.navLost);
        navFound = findViewById(R.id.navFound);
        fabCreate = findViewById(R.id.fabCreate);

        databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        dataList = new ArrayList<>();

        adapter = new MyAdapter(HomeActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear(); // Clear the list before adding data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Data data = postSnapshot.getValue(Data.class);
                    dataList.add(data);
                }
                // Reverse the order of the dataList
                Collections.reverse(dataList);
                adapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                    }
                }, 2000); // Delay of 2 seconds
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Database Error", databaseError.getMessage());
                dismissLoadingDialog();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavOption(navHome);
                adapter = new MyAdapter(HomeActivity.this, dataList);
                recyclerView.setAdapter(adapter);
            }
        });

        navLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavOption(navLost);
                filterDataList("lost");
            }
        });

        navFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavOption(navFound);
                filterDataList("found");
            }
        });

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreatePage();
            }
        });

        // Initialize with the Home option selected
        selectNavOption(navHome);
    }

    private void selectNavOption(TextView selectedNav) {
        // Reset colors for all navigation options
        navHome.setTextColor(Color.parseColor("#828282"));
        navLost.setTextColor(Color.parseColor("#828282"));
        navFound.setTextColor(Color.parseColor("#828282"));

        // Set color for the selected navigation option
        selectedNav.setTextColor(Color.parseColor("#555555"));
    }

    private void showError(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showOptionsDialog() {
        String[] options = {"Created", "Log Out"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Created
                            navigateToCreatedPage();
                            break;
                        case 1: // Log Out
                            showLogoutConfirmationDialog();
                            break;
                    }
                });

        AlertDialog dialog = builder.create();

        // Customize the position of the dialog
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 10; // right margin
        params.y = 50; // top margin

        int width = (int)(120 * getResources().getDisplayMetrics().density);
        int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.20);

        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }


    private void filterDataList(String status) {
        List<Data> filteredList = new ArrayList<>();
        for (Data data : dataList) {
            if (data.getStatus().equalsIgnoreCase(status)) {
                filteredList.add(data);
            }
        }
        adapter = new MyAdapter(HomeActivity.this, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void navigateToCreatePage() {
        // Replace MyCreateActivity with the actual name of your create page activity
        Intent intent = new Intent(this, CreatePostActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("Email", email);
        startActivity(intent);
    }

    private void navigateToCreatedPage() {
        Intent intent = new Intent(this, CreatedPostActivity.class);
        intent.putExtra("Username", username);
        intent.putExtra("Email", email);
        startActivity(intent);
    }

    private void showUpdate(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage(message);
        customdialog = builder.create(); //created dialog to customdialog
        customdialog.show();
    }

    private void dismissLoadingDialog() {
        // Dismiss the loading dialog if it's showing
        if (customdialog != null && customdialog.isShowing()) {
            customdialog.dismiss();
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        performLogout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        dbHelper.deleteAllData();
        // Clear all saved preferences except for StId and Password
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("IsLoggedIn");
        if (!prefs.getBoolean("RememberMe", false)) {
            editor.remove("Username");
            editor.remove("Email");
        }
        editor.apply();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}








