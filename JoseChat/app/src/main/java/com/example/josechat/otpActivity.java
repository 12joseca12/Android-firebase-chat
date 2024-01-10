package com.example.josechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.josechat.Utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class otpActivity extends AppCompatActivity {

    String phoneNumber;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendotp;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    Long timeout=60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput=findViewById(R.id.numero_otp);
        nextBtn=findViewById(R.id.siguiente_otp);
        progressBar=findViewById(R.id.progres_otp);
        resendotp=findViewById(R.id.resend_otp);

        phoneNumber=getIntent().getExtras().getString("phone");
        sendOtp(phoneNumber, false);
        nextBtn.setOnClickListener(view -> {
            String enterOtp=otpInput.getText().toString();
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,enterOtp);
            signIn(credential);
            setInProgress(true);
        });
        resendotp.setOnClickListener(view -> {
            sendOtp(phoneNumber,true);
        });


    }

    void sendOtp(String phoneNumber, boolean isResend){
        startresendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeout, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(),"Fallo de verificación OTP");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode=s;
                                resendingToken=forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP enviado correctamente");
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void startresendTimer() {
        resendotp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               timeout--;
               resendotp.setText("Resend OTP in "+timeout+" second");
               if(timeout<=0){
                   timeout=60L;
                   timer.cancel();
                   runOnUiThread(()->{
                       resendotp.setEnabled(true);
                   });
               }
            }
        },0,1000);
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
    //login y seguir a la proxima actividad
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent =new Intent(otpActivity.this, userNameActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");

                }
            }
        });

    }

}