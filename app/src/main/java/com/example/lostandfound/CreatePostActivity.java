package com.example.lostandfound;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.lang.Thread;

public class CreatePostActivity extends AppCompatActivity {
   private EditText usern;
   private DBHelper dbHelper;
   private EditText  itemName, desc, etdate, ettime, location, etEmail;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri image;
    RadioButton lost, found;
    Button selectImage;
    ImageView imageView;
    String status = ""; private String key;
    private Button save, back, saveDraft; RadioGroup radioGroup;
    String errors = ""; SharedPreferences prefs; private String oldImageUrl = "";
    private AlertDialog dialog;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(imageView);
                }
            } else {
                showDialog("Please select an image");
            }
        }
    });

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
//        firebase storage
        FirebaseApp.initializeApp(CreatePostActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Data");

        //shared pref
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username= prefs.getString("Username", "");
        String email = prefs.getString("Email", "");

//     edit text
        usern = findViewById(R.id.username);
        usern.setText(username);
        radioGroup = findViewById(R.id.status);
        etEmail = findViewById(R.id.email);
        etEmail.setText(email);
        itemName = findViewById(R.id.item_name);
        desc = findViewById(R.id.desc);
        etdate = findViewById(R.id.etDate);
        ettime = findViewById(R.id.etTime);
        location = findViewById(R.id.location);
        lost = findViewById(R.id.radio_lost);
        found = findViewById(R.id.radio_found);
        imageView = findViewById(R.id.photo_field);
        save = findViewById(R.id.buttonPost);
        back = findViewById(R.id.back);
        selectImage = findViewById(R.id.selectImage);
        saveDraft = findViewById(R.id.saveDraft);

        // the DatePickerDialog.OnDateSetListener object
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Handle the date selection
                String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                etdate.setText(date);
            }
        };

        // Calendar object
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // the DatePickerDialog object
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, dayOfMonth);

        //OnClickListener for the etdate EditText
//        etdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                datePickerDialog.show();
//            }
//        });


        etdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                datePickerDialog.show();
            }
        });



//        if update is called
        Intent intent = getIntent();
         key = intent.getStringExtra("Key");
        if (key != null && !key.isEmpty()) {
            // It's an update, fetch the data using the key and populate the form
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Data").child(key);
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Data data = dataSnapshot.getValue(Data.class);
                    // getters in Data class
                    itemName.setText(data.getItemName());
                    desc.setText(data.getDesc());
                    etdate.setText(data.getDate());
                    ettime.setText(data.getTime());
                    location.setText(data.getLocation());
                    etEmail.setText(data.getEmail());
                    String status = data.getStatus();
                    if ("Lost".equals(status)) {
                        lost.setChecked(true);
                    } else if ("Found".equals(status)) {
                        found.setChecked(true);
                    }
                    oldImageUrl = data.getImageUrl();

                    // For the image, use Glide or similar library to load it
                    Glide.with(CreatePostActivity.this).load(data.getImageUrl()).into(imageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CreatePostActivity.this, "Failed to load data for update.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //if data is available in sql lite than set
        dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getLatestDraft();
        if (cursor.moveToFirst()) {
            usern.setText(cursor.getString(cursor.getColumnIndex("username")));
            itemName.setText(cursor.getString(cursor.getColumnIndex("itemName")));
            desc.setText(cursor.getString(cursor.getColumnIndex("desc")));
            etdate.setText(cursor.getString(cursor.getColumnIndex("date")));
            ettime.setText(cursor.getString(cursor.getColumnIndex("time")));
            location.setText(cursor.getString(cursor.getColumnIndex("location")));
            etEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            if ("Lost".equals(status)) {
                lost.setChecked(true);
            } else if ("Found".equals(status)) {
                found.setChecked(true);
            }
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("image"));
            if (imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(bitmap);

                // Convert bitmap to Uri
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                image = Uri.parse(path);
            }
        }
        cursor.close();

        //image selection
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        saveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdate("Saving as draft......");
                // To delete all data
                dbHelper.deleteAllData();
                //save current data
                saveAsDraft();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lost.isChecked()) {
                    status = "Lost";
                } else if (found.isChecked()) {
                    status = "Found";
                }
                if (status.isEmpty()) {
                    errors += "Please choose if the item is lost or found.\n";
                }
                int itemNameLength = itemName.getText().toString().trim().length();
                if (itemNameLength < 3) {
                    errors = errors + "Item Name too short.\n";
                }
                if (!etdate.getText().toString().trim().isEmpty()){
                    if (!etdate.getText().toString().trim().matches(
                            "^(0[1-9]|1[0-9]|2[0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d)$"

                    )){errors = errors + "Date Formate wrong.\n";}
                }
                if (isEmpty(usern) || isEmpty(desc) || isEmpty(etdate) || isEmpty(ettime) || isEmpty(location) || isEmpty(etEmail) || status.trim().isEmpty()) {
                    errors = errors + "Please Fill all the field.\n";
                }
                if (errors.length() > 0) {
                    errors = "Error\n" + errors;
                    showDialog(errors);
                    errors ="";
                } else {
                    uploadData(image, usern.getText().toString(), itemName.getText().toString(), desc.getText().toString(),
                            etdate.getText().toString(), ettime.getText().toString(), location.getText().toString(),
                            etEmail.getText().toString(), status);

                 showUpdate("Uploading......");

                }
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
        private void uploadData( Uri file, String username, String itemName, String desc, String date, String time, String location, String email, String status) {
        if (file!= null) {
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            saveData(uri.toString(), username, itemName, desc, date, time, location, email, status);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showDialog(e.getMessage());
                }
            });
        } else if (!oldImageUrl.isEmpty()) {
            saveData(oldImageUrl, username, itemName, desc, date, time, location, email, status);
        } else {
  saveData("https://i.pinimg.com/564x/ed/e5/33/ede53399d682cd6a2f78da04c0918a2b.jpg", username, itemName, desc, date, time, location, email, status);
        }
    }
    private void saveData(String imageUrl, String username, String itemName, String desc, String date, String time, String location, String email, String status) {
        Data data = new Data(username, itemName, desc, date, time, location, email, status, imageUrl);

        if (key == null) {
            // If key is null, create a new item
            key = databaseReference.push().getKey();
        }
        // Set the key here
        data.setKey(key);
        if(key == null){
            showDialog("Database Issue!");
        }
        else{
            databaseReference.child(key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  // To delete all data
                    dbHelper.deleteAllData();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            showCustomDialog("Posted successfully");
                            clearFields();
                        }
                    }, 2000); // Delay of 3 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();

                        }
                    }, 4000); // Delay of 3 seconds
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showDialog("Error: " + e.getMessage());
                }
            });
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
//    @string/app_name
    private void showCustomDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText(message);
        AlertDialog dialog = builder.create();

        Button dialogButton = dialogView.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void clearFields() {
        // Clear the text fields
        itemName.setText("");
        itemName.setError(null);
        desc.setText("");
        etdate.setText("");
        etdate.setError(null);
        ettime.setText("");
        location.setText("");
        // Clear the radio group selection
        radioGroup.clearCheck();
        // Reset the image view to a default image if needed
        imageView.setImageResource(R.drawable.default_image);
    }

    //save as draft in sql lite
    private void saveAsDraft(){
        dbHelper = new DBHelper(this);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("username", usern.getText().toString().trim());
                values.put("itemName", itemName.getText().toString().trim());
                values.put("desc", desc.getText().toString().trim());
                values.put("date", etdate.getText().toString().trim());
                values.put("time", ettime.getText().toString().trim());
                values.put("location", itemName.getText().toString().trim());
                values.put("email", etEmail.getText().toString().trim());
                if (lost.isChecked()) {
                    status = "Lost";
                } else if (found.isChecked()) {
                    status = "Found";
                }
                values.put("status", status);

                byte[] imageBytes;
                if (image != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(image);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        imageBytes = outputStream.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                        imageBytes = new byte[0]; // Default value
                    }
                } else {
                    imageBytes = new byte[0]; // Default value
                }
                values.put("image", imageBytes);

                if(!(isEmpty(usern) && isEmpty(etEmail))){

                    if (isEmpty(desc) && isEmpty(etdate) && isEmpty(ettime) && isEmpty(location) && status.trim().isEmpty()) {

                        errors = errors + "No information to save.\n";
                        dismissDialog();
                    }
                    if (errors.length() > 0) {
                        errors = "Error\n" + errors;
                        showDialog(errors);
                        errors ="";
                        return;
                }
                }else{
                    showDialog("Username and email is must.");
                    dismissDialog();
                    return;
                }

                long rowId = db.insert("drafts", null, values);
                if (rowId == -1) {
                    // Insert failed, handle the error
                    Toast.makeText(getApplicationContext(), "Failed to save draft", Toast.LENGTH_SHORT).show();
                } else {

                    //toast after 2 seconds
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Draft saved successfully", Toast.LENGTH_LONG).show();
                            dismissDialog();
                        }
                    }, 1500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);

                }

            }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showUpdate(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setMessage(message);
        dialog = builder.create();
        dialog.show();
    }

}