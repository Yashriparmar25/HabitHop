package com.HabitTracker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class RewardManager {

    private Context context;
    private Dialog rewardDialog;
    private View layoutState1;
    private View layoutState2;
    private TextView txtMotivationalMessage;
    private Button btnAction;
    private Animation popAnimation;
    private boolean isShowingState2 = false;

    private String[] motivationalMessages = {
            "You're doing amazing! Keep Habithopping! 🐰✨",
            "Small steps daily lead to big changes. Great job!",
            "Habits made simple, results made powerful. Well done!",
            "Consistency is key, and you just turned the lock! 🔑"
    };

    public RewardManager(Context context) {
        this.context = context;
        setupDialog();
    }

    private void setupDialog() {
        rewardDialog = new Dialog(context);
        rewardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rewardDialog.setContentView(R.layout.dialog_habit_reward);

        if (rewardDialog.getWindow() != null) {
            rewardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        rewardDialog.setCancelable(false);

        layoutState1 = rewardDialog.findViewById(R.id.layoutState1);
        layoutState2 = rewardDialog.findViewById(R.id.layoutState2);
        txtMotivationalMessage = rewardDialog.findViewById(R.id.txtMotivationalMessage);
        btnAction = rewardDialog.findViewById(R.id.btnRewardAction);

        popAnimation = AnimationUtils.loadAnimation(context, R.anim.pop_and_squeeze);

        btnAction.setOnClickListener(v -> {
            if (!isShowingState2) {
                switchToState2();
            } else {
                rewardDialog.dismiss();
            }
        });
    }

    public void showReward() {
        isShowingState2 = false;
        layoutState1.setVisibility(View.VISIBLE);
        layoutState2.setVisibility(View.GONE);
        btnAction.setText("Next");
        rewardDialog.show();

        View dialogContent = rewardDialog.findViewById(android.R.id.content);
        if (dialogContent != null) {
            dialogContent.startAnimation(popAnimation);
        }
    }

    private void switchToState2() {
        isShowingState2 = true;
        layoutState1.setVisibility(View.GONE);

        Random random = new Random();
        String randomMessage = motivationalMessages[random.nextInt(motivationalMessages.length)];
        txtMotivationalMessage.setText(randomMessage);

        layoutState2.setVisibility(View.VISIBLE);
        layoutState2.startAnimation(popAnimation);
        btnAction.setText("Got it!");
    }
}