package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private CardView cardView;
    private Animation animation;
    private AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        cardView = findViewById(R.id.carView);
        constraintLayout = findViewById(R.id.cardLayout);
        /*for layout background animation  */
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        /* for CardView Animation */

        animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.card_in_animation);
        cardView.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                /*for Activity Animation*/
                overridePendingTransition(R.anim.activity_in_animation, R.anim.activity_out_animation);
                finish();
            }
        }, 4000);
    }


}
