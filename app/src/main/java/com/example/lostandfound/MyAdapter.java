package com.example.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<Data> dataList;

    public MyAdapter(Context context, List<Data> dataList) {
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
        // Set the data to the views here
        holder.titleTextView.setText(data.itemName);
        holder.statusTextView.setText(data.status);
        holder.dateTextView.setText(data.date);
        holder.timeTextView.setText(data.time);
        holder.locationTextView.setText(data.location);
        holder.nameTextView.setText("Posted by " + data.username);

        // For the image, a library like Glide
        Glide.with(context).load(data.imageUrl).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.custom_layout, null);

                TextView title = view.findViewById(R.id.dialogTitle);
                title.setText(data.getItemName());

                TextView emailSend = view.findViewById(R.id.dialogEmail);
                emailSend.setText(data.getEmail());

                TextView message = view.findViewById(R.id.dialogMessage);
                message.setText(data.getDesc());

                builder.setView(view); // the custom view on the builder before creating the dialog
                AlertDialog dialog = builder.create(); // Creating the dialog after setting the view

                Button backButton = view.findViewById(R.id.backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // opening Gmail
                emailSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + data.getEmail()));
                        context.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                    }
                });

                dialog.show(); // the dialog after setting up everything
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, statusTextView, dateTextView, locationTextView, nameTextView, timeTextView;
        de.hdodenhof.circleimageview.CircleImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}


