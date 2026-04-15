package com.HabitTracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileSetupActivity extends AppCompatActivity {

    TextInputEditText etFullname, etBirthday, etNewHabitName, etNewHabitDesc;
    LinearLayout llHabitInput, llHabitsList;
    Button btnAddHabit, btnSaveHabit, btnContinue;

    ImageView ivProfilePhoto;
    TextView tvPickPhoto;

    ChipGroup chipGroupGender, chipGroupGoal;

    private String selectedGender = "";
    private String selectedGoal = "";
    private String pickedAvatarResName = "";
    private String pickedGalleryUri = "";

    SharedPreferences prefs;
    ArrayList<String> habitNames = new ArrayList<>();
    ArrayList<String> habitDescs = new ArrayList<>();

    private String currentUserEmail = "";

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
        setContentView(R.layout.activity_profile_setup);

        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        currentUserEmail = prefs.getString("current_user_email", "");

        etFullname = findViewById(R.id.et_fullname);
        etBirthday = findViewById(R.id.et_birthday);
        etNewHabitName = findViewById(R.id.et_new_habit_name);
        etNewHabitDesc = findViewById(R.id.et_new_habit_desc);
        llHabitInput = findViewById(R.id.ll_habit_input);
        llHabitsList = findViewById(R.id.ll_habits_list);
        btnAddHabit = findViewById(R.id.btn_add_habit);
        btnSaveHabit = findViewById(R.id.btn_save_habit);
        btnContinue = findViewById(R.id.btn_continue);
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        tvPickPhoto = findViewById(R.id.tv_pick_photo);
        chipGroupGender = findViewById(R.id.chip_group_gender);
        chipGroupGoal = findViewById(R.id.chip_group_goal);

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

        btnAddHabit.setOnClickListener(v -> {
            llHabitInput.setVisibility(View.VISIBLE);
            etNewHabitName.requestFocus();
        });

        btnSaveHabit.setOnClickListener(v -> {
            String name = etNewHabitName.getText().toString().trim();
            String desc = etNewHabitDesc.getText().toString().trim();
            if (name.isEmpty()) {
                etNewHabitName.setError("Please enter a habit name");
                return;
            }

            habitNames.add(name);
            habitDescs.add(desc);

            TextView habitChip = new TextView(this);
            habitChip.setText("✓ " + name);
            habitChip.setTextColor(getResources().getColor(R.color.dark_green));
            habitChip.setTextSize(14);
            habitChip.setPadding(16, 12, 16, 12);
            habitChip.setBackgroundResource(R.drawable.card_soft_white);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 0);
            habitChip.setLayoutParams(params);
            llHabitsList.addView(habitChip);

            etNewHabitName.setText("");
            etNewHabitDesc.setText("");
            llHabitInput.setVisibility(View.GONE);
        });

        btnContinue.setOnClickListener(v -> saveAndContinue());
    }

    void saveAndContinue() {
        String fullname = etFullname.getText().toString().trim();
        String birthday = etBirthday.getText() != null ? etBirthday.getText().toString().trim() : "";

        if (fullname.isEmpty()) {
            etFullname.setError("Please enter your name");
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_user_email", currentUserEmail);
        editor.putString("name", fullname);
        editor.putString("birthday", birthday);
        editor.putString("gender", selectedGender);
        editor.putString("goal", selectedGoal);
        editor.putBoolean("profile_setup_done", true);
        editor.putString("habit_names", String.join(",", habitNames));
        editor.putString("habit_descs", String.join(",", habitDescs));
        editor.putString("avatar_res_name", pickedAvatarResName);
        editor.putString("avatar_gallery_uri", pickedGalleryUri);
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        DatabaseHelper db = new DatabaseHelper(this);
        db.saveUser(
                currentUserEmail,
                fullname,
                birthday,
                selectedGender,
                selectedGoal,
                pickedAvatarResName,
                pickedGalleryUri
        );

        String today = new java.text.SimpleDateFormat(
                "yyyy-MM-dd", java.util.Locale.getDefault()
        ).format(new java.util.Date());

        for (int i = 0; i < habitNames.size(); i++) {
            db.saveHabit(currentUserEmail, habitNames.get(i), habitDescs.get(i), "Health", "Daily", today);
        }

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}