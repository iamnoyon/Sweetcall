package com.noyon.sweetcall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private Button ContinueAndNextButton;
    private TextView textPhoneNumber;
    private EditText inputVerificationCode,inputPhoneNumber;
    private CountryCodePicker ccp;
    private RelativeLayout PhoneNumberContainer;
    private String checker = "", phoneNumber = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        ContinueAndNextButton = findViewById(R.id.continueButton);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputVerificationCode = findViewById(R.id.inputVerificationCode);
        PhoneNumberContainer = findViewById(R.id.phoneNumberContainer);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(inputPhoneNumber);
        ContinueAndNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContinueAndNextButton.getText().equals("Submit") || checker.equals("CodeSent")){

                    String verificationCode = inputVerificationCode.getText().toString();
                    if(verificationCode.equals("")){
                        Toast.makeText(RegistrationActivity.this, "Please write the verification code first..", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loadingBar.setTitle("Verifying Code");
                        loadingBar.setMessage("Please wait.. while we are verifying the code...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
                else{
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if(!phoneNumber.equals("")){
                        loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("Please wait.. Verification code sending...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                RegistrationActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }
                    else{
                        Toast.makeText(RegistrationActivity.this, "Please write valid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegistrationActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                ContinueAndNextButton.setText("Continue");
                inputVerificationCode.setVisibility(View.INVISIBLE);
                PhoneNumberContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;
                PhoneNumberContainer.setVisibility(View.GONE);
                checker = "CodeSent";
                ContinueAndNextButton.setText("Submit");
                inputVerificationCode.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
                Toast.makeText(RegistrationActivity.this, "Code has been sent, please check..", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            Intent homeIntent = new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Phone Verified Successfully. Signing in now...", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            String e = task.getException().toString();
                            Toast.makeText(RegistrationActivity.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity(){
        Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }




}

