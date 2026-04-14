package com.HabitTracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AvatarPicker extends AppCompatActivity {

    public static final String EXTRA_AVATAR_RES_NAME = "avatar_res_name";
    public static final String EXTRA_GALLERY_URI = "gallery_uri";

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        setupGalleryLauncher();

        findViewById(R.id.btn_open_gallery).setOnClickListener(v -> openGallery());

        int[] avatarIds = {
                R.id.ll_avatar_turtle,
                R.id.ll_avatar_rabbit,
                R.id.ll_avatar_sloth,
                R.id.ll_avatar_penguin,
                R.id.ll_avatar_duck,
                R.id.ll_avatar_cow,
                R.id.ll_avatar_cat,
                R.id.ll_avatar_squirrel,
                R.id.ll_avatar_dog,
                R.id.ll_avatar_pig,
                R.id.ll_avatar_mouse,
                R.id.ll_avatar_camel
        };

        String[] resNames = {
                "turtle", "rabbit", "sloth", "penguin",
                "duck", "cow", "cat", "squirrel",
                "dogoo", "pig", "mouse", "camel"
        };

        for (int i = 0; i < avatarIds.length; i++) {
            final String resName = resNames[i];
            View card = findViewById(avatarIds[i]);
            if (card != null) {
                card.setOnClickListener(v -> returnPresetResult(resName));
            }
        }
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if (uri != null) {
                            try {
                                final int takeFlags = data.getFlags() & (
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                );
                                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                returnGalleryResult(uri);
                            } catch (SecurityException e) {
                                Toast.makeText(this, "Unable to access selected photo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryLauncher.launch(intent);
    }

    private void returnPresetResult(String resName) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AVATAR_RES_NAME, resName);
        setResult(RESULT_OK, result);
        finish();
    }

    private void returnGalleryResult(Uri uri) {
        Intent result = new Intent();
        result.putExtra(EXTRA_GALLERY_URI, uri.toString());
        setResult(RESULT_OK, result);
        finish();
    }
}