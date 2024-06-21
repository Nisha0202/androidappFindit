package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreatedPostActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private List<Data> dataList;
    private Button back;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_post);

        //shared pref
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username= prefs.getString("Username", "");
        String email = prefs.getString("Email", "");

        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        dataList = new ArrayList<>();
        adapter = new UserAdapter(CreatedPostActivity.this, dataList);
        recyclerView.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear(); // Clear the list before adding data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Data data = postSnapshot.getValue(Data.class);
                    // if the data object is not null
                    if (data != null) {
                        //if the data's email matches the user's email
                        if (data.getEmail().equals(email)) {
                            dataList.add(data);
                        }
                    }
                }
                if (dataList.isEmpty()) {
                    //a Toast or update a TextView with the message
                    Toast.makeText(CreatedPostActivity.this, "Nothing posted yet", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Database Error", databaseError.getMessage());
            }
        });


    }

    private void showError(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatedPostActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
