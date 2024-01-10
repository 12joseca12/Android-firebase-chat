package com.example.josechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.josechat.Utils.AndroidUtil;
import com.example.josechat.Utils.FirebaseUtil;
import com.example.josechat.model.UserModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
            String userId=getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    UserModel model=task.getResult().toObject(UserModel.class);
                    Intent mainIntent=new Intent(this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mainIntent);
                    Intent intent = new Intent(this, ChatActivity.class);
                    AndroidUtil.passUserModelAsIntent(intent,model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, PhoneNumberActivity.class));
                    }
                    finish();
                }
            }, 1000);
        }
    }
}