package com.HabitTracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * AvatarPickerActivity
 * ─────────────────────────────────────────────────────────────────
 * Lets the user pick one of the preset animal avatars OR upload
 * a photo from their gallery.
 *
 * Returns to ProfileSetupActivity via setResult() with either:
 *   • EXTRA_AVATAR_RES_NAME  (String) – e.g. "turtle", "rabbit" …
 *   • EXTRA_GALLERY_URI      (String) – content URI from gallery
 * ─────────────────────────────────────────────────────────────────
 */
public class AvatarPicker extends AppCompatActivity {

    // Keys used in the result Intent — ProfileSetupActivity reads these
    public static final String EXTRA_AVATAR_RES_NAME = "avatar_res_name";
    public static final String EXTRA_GALLERY_URI     = "gallery_uri";

    // Gallery launcher
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            returnGalleryResult(uri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

// ── Gallery button ───────────────────────────────────────────
        findViewById(R.id.btn_open_gallery).setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 100);
                } else {
                    galleryLauncher.launch("image/*");
                }
            } else {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    galleryLauncher.launch("image/*");
                }
            }
        });
        // ── Preset avatar grid ───────────────────────────────────────
        // Map each card's LinearLayout id → the drawable resource name
        // (must match what you have in res/drawable/)
        int[][] avatarMap = {
                { R.id.ll_avatar_turtle,   0 },  // drawable name passed as tag below
                { R.id.ll_avatar_rabbit,   0 },
                { R.id.ll_avatar_sloth,    0 },
                { R.id.ll_avatar_penguin,  0 },
                { R.id.ll_avatar_duck,     0 },
                { R.id.ll_avatar_cow,      0 },
                { R.id.ll_avatar_cat,      0 },
                { R.id.ll_avatar_squirrel, 0 },
                { R.id.ll_avatar_dog,      0 },
                { R.id.ll_avatar_pig,      0 },
                { R.id.ll_avatar_mouse,    0 },
                { R.id.ll_avatar_camel,    0 },
        };

        // Drawable resource names in the same order as avatarMap
        String[] resNames = {
                "turtle", "rabbit", "sloth", "penguin",
                "duck",   "cow",    "cat",   "squirrel",
                "dogoo",  "pig",    "mouse", "camel"
        };

        for (int i = 0; i < avatarMap.length; i++) {
            final String resName = resNames[i];
            View card = findViewById(avatarMap[i][0]);
            if (card != null) {
                card.setOnClickListener(v -> returnPresetResult(resName));
            }
        }
    }

    // ── Return a preset avatar back to ProfileSetupActivity ─────────
    private void returnPresetResult(String resName) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AVATAR_RES_NAME, resName);
        setResult(RESULT_OK, result);
        finish();
    }

    // ── Return a gallery URI back to ProfileSetupActivity ───────────
    private void returnGalleryResult(Uri uri) {
        Intent result = new Intent();
        result.putExtra(EXTRA_GALLERY_URI, uri.toString());
        setResult(RESULT_OK, result);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*");
        } else {
            Toast.makeText(this, "Permission needed to pick a photo", Toast.LENGTH_SHORT).show();
        }
    }
}