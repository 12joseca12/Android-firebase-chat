package com.example.josechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

public class PhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker codePicker;
    EditText phoneInput;
    Button sendBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        codePicker=findViewById(R.id.countrycode);
        phoneInput=findViewById(R.id.numeromobil);
        sendBtn=findViewById(R.id.login_numero);
        progressBar=findViewById(R.id.progres_numero);

        progressBar.setVisibility(View.GONE);

        codePicker.registerCarrierNumberEditText(phoneInput);
        sendBtn.setOnClickListener(view -> {
            if(!codePicker.isValidFullNumber()){
                phoneInput.setError("Phone number is not valid");
                return;
            }
            Intent intent = new Intent(PhoneNumberActivity.this, otpActivity.class);
            intent.putExtra("phone", codePicker.getFullNumberWithPlus());
            startActivity(intent);
                });

    }
}