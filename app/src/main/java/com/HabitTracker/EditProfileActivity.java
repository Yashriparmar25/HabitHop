package com.HabitTracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullname, etBirthday;
    private ImageView ivProfilePhoto;
    private TextView tvPickPhoto;
    private ChipGroup chipGroupGender, chipGroupGoal;
    private Button btnSaveChanges;

    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;
    private String currentUserEmail = "";

    private String selectedGender = "";
    private String selectedGoal = "";
    private String pickedAvatarResName = "";
    private String pickedGalleryUri = "";

    private final ActivityResultLauncher<Intent> avatarPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();

                            String resName = data.getStringExtra(AvatarPicker.EXTRA_AVATAR_RES_NAME);
                            String galleryUri = data.getStringExtra(AvatarPicker.EXTRA_GALLERY_URI);

                            if (resName != null && !resName.isEmpty()) {
                                pickedAvatarResName = resName;
                                pickedGalleryUri = "";

                                int resId = getResources().getIdentifier(resName, "drawable", getPackageName());
                                if (resId != 0) {
                                    ivProfilePhoto.setImageResource(resId);
                                    ivProfilePhoto.setPadding(0, 0, 0, 0);
                                    ivProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                }
                                tvPickPhoto.setText("Tap to change avatar");
                            } else if (galleryUri != null && !galleryUri.isEmpty()) {
                                pickedGalleryUri = galleryUri;
                                pickedAvatarResName = "";
                                try {
                                    ivProfilePhoto.setImageURI(Uri.parse(galleryUri));
                                    ivProfilePhoto.setPadding(0, 0, 0, 0);
                                    ivProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    tvPickPhoto.setText("Tap to change photo");
                                } catch (Exception e) {
                                    Toast.makeText(this, "Unable to load gallery photo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        currentUserEmail = prefs.getString("current_user_email", "");

        bindViews();
        loadExistingProfile();
        setupListeners();
    }

    private void bindViews() {
        etFullname = findViewById(R.id.et_fullname);
        etBirthday = findViewById(R.id.et_birthday);
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        tvPickPhoto = findViewById(R.id.tv_pick_photo);
        chipGroupGender = findViewById(R.id.chip_group_gender);
        chipGroupGoal = findViewById(R.id.chip_group_goal);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
    }

    private void setupListeners() {
        View.OnClickListener openAvatarPicker = v -> {
            Intent intent = new Intent(this, AvatarPicker.class);
            avatarPickerLauncher.launch(intent);
        };

        ivProfilePhoto.setOnClickListener(openAvatarPicker);
        tvPickPhoto.setOnClickListener(openAvatarPicker);

        chipGroupGender.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                selectedGender = chip != null ? chip.getText().toString() : "";
            } else {
                selectedGender = "";
            }
        });

        chipGroupGoal.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                selectedGoal = chip != null ? chip.getText().toString() : "";
            } else {
                selectedGoal = "";
            }
        });

        etBirthday.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etBirthday.setText(day + " / " + (month + 1) + " / " + year);
            }, cal.get(Calendar.YEAR) - 20, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadExistingProfile() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Cursor cursor = null;
        try {
            cursor = dbHelper.getUser(currentUserEmail);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                String birthday = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDAY));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENDER));
                String goal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GOAL));
                String avatarRes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AVATAR_RES));
                String avatarUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AVATAR_URI));

                if (name != null) etFullname.setText(name);
                if (birthday != null) etBirthday.setText(birthday);

                selectedGender = gender != null ? gender : "";
                selectedGoal = goal != null ? goal : "";
                pickedAvatarResName = avatarRes != null ? avatarRes : "";
                pickedGalleryUri = avatarUri != null ? avatarUri : "";

                preselectChip(chipGroupGender, selectedGender);
                preselectChip(chipGroupGoal, selectedGoal);

                if (!pickedGalleryUri.isEmpty()) {
                    try {
                        ivProfilePhoto.setImageURI(Uri.parse(pickedGalleryUri));
                    } catch (Exception ignored) {
                    }
                } else if (!pickedAvatarResName.isEmpty()) {
                    int resId = getResources().getIdentifier(pickedAvatarResName, "drawable", getPackageName());
                    if (resId != 0) ivProfilePhoto.setImageResource(resId);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void preselectChip(ChipGroup group, String value) {
        if (value == null || value.isEmpty()) return;

        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (value.equalsIgnoreCase(chip.getText().toString())) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    private void saveChanges() {
        String fullname = etFullname.getText() != null ? etFullname.getText().toString().trim() : "";
        String birthday = etBirthday.getText() != null ? etBirthday.getText().toString().trim() : "";

        if (fullname.isEmpty()) {
            etFullname.setError("Please enter your name");
            return;
        }

        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedGoal.isEmpty()) {
            Toast.makeText(this, "Please select your goal", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit()
                .putString("name", fullname)
                .putString("birthday", birthday)
                .putString("gender", selectedGender)
                .putString("goal", selectedGoal)
                .putString("avatar_res_name", pickedAvatarResName)
                .putString("avatar_gallery_uri", pickedGalleryUri)
                .apply();

        int rows = dbHelper.updateUserProfile(
                currentUserEmail,
                fullname,
                birthday,
                selectedGender,
                selectedGoal,
                pickedAvatarResName,
                pickedGalleryUri
        );

        if (rows > 0) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Profile not updated", Toast.LENGTH_SHORT).show();
        }
    }
}