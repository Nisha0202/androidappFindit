package com.example.lostandfound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<Data> dataList;
    public UserAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = dataList.get(position);
        // Set the data to the views
        holder.titleTextView.setText(data.itemName);
        holder.statusTextView.setText(data.status);
        holder.dateTextView.setText(data.date);
        holder.locationTextView.setText(data.location);
        holder.nameTextView.setText("Posted by " + data.username);
//        holder.contactTextView.setText(data.email);
        // For the image, a library like Glide
        Glide.with(context).load(data.imageUrl).into(holder.imageView);

        final int finalPosition = position;  // a final copy of position

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //options for Update or Delete
                    final CharSequence[] options = {"Update", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose an option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Update")) {
                                // Send the key to CreatePostActivity for update
                                Intent updateIntent = new Intent(context, CreatePostActivity.class);
                                updateIntent.putExtra("Key", data.getKey()); // Assuming 'getKey()' method exists in your Data class
                                updateIntent.putExtra("Username", data.getUsername());
                                updateIntent.putExtra("Email", data.getEmail());
                                // ... pass other data as needed ...
                                context.startActivity(updateIntent);
                            } else if (options[item].equals("Delete")) {
                                // Confirm before deleting
                                new AlertDialog.Builder(context)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure you want to delete this item?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete
                                                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Data").child(data.getKey());
                                                itemRef.removeValue();
                                                dataList.remove(finalPosition);
                                                notifyItemRemoved(finalPosition);
                                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        }
                    });
                    builder.show();
                }
            });

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, statusTextView, dateTextView, locationTextView, nameTextView, contactTextView;
        de.hdodenhof.circleimageview.CircleImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);

        }
    }
}
