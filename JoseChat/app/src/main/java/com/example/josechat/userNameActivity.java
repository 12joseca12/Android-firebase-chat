package com.example.josechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.josechat.Utils.FirebaseUtil;
import com.example.josechat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class userNameActivity extends AppCompatActivity {

    EditText usernameinput;
    Button accederbtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        usernameinput=findViewById(R.id.nombre_usuario);
        accederbtn=findViewById(R.id.acceder_btn);
        progressBar=findViewById(R.id.progres_usuario);
        phoneNumber=getIntent().getExtras().getString("phone");
        getUsername();
        accederbtn.setOnClickListener(view -> {
            setUSerName();
        });

    }

    void setUSerName(){

        String username=usernameinput.getText().toString();
        if(username.isEmpty() || username.length()<3){
            usernameinput.setError("El nombre de usuario debe ser mayor de 3 letras");
            return;
        }
        setInProgress(true);
        if(userModel!=null){
            userModel.setUserName(username);
        }else{
            userModel=new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserID());
        }
        FirebaseUtil.currentUsersDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              setInProgress(false);
              if(task.isSuccessful()){
                  Intent intent = new Intent(userNameActivity.this, MainActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
              }
            }
        });

    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUsersDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                   if(userModel!=null){
                       usernameinput.setText(userModel.getUserName());
                   }
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            accederbtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            accederbtn.setVisibility(View.VISIBLE);
        }
    }

}