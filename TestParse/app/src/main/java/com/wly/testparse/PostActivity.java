package com.wly.testparse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * click picButton, get a image from local
 * click postButton, store image and content to the parse. If success, go back to userListActivity
 * click cancel, go back to userListActivity
 */
public class PostActivity extends AppCompatActivity {

    EditText content;
    Button picButton;
    Button postButton;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        content = findViewById(R.id.content);
        picButton = findViewById(R.id.picButton);
        postButton = findViewById(R.id.postButton);
        image = findViewById(R.id.imageView);
        image.setImageDrawable(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // show the image
        Uri selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Fail", "Can not show image");
        }
    }

    public void getShowPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    public void post(View view) {
        ParseObject post = new ParseObject("Recipe");
        post.put("username", ParseUser.getCurrentUser().getUsername());
        if (!content.getText().toString().matches("")) {
            post.put("content",content.getText().toString());
        }
        if (image.getDrawable() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) (image.getDrawable())).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile file = new ParseFile("image.png", byteArray);
            post.put("image", file);
        }

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Post Success!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Post Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cancel(View view) {
        // TODO
        // add an alert window
        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
    }

}