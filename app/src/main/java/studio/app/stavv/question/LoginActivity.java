package studio.app.stavv.question;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import studio.app.stavv.question.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity  {
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String APP_PREFERENCES = "mysettings";
    ActivityLoginBinding mBinding;
    FirebaseAuth mAuth;
    SharedPreferences pref;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_login);
        Log.d(TAG, "login act start");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

        mBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validatePhoneNumber()){
                    SharedPreferences.Editor editor = pref.edit();

                    if(mBinding.toggleButton.isChecked()){
                        editor.putString("sex", "male");
                    }
                    else{
                        editor.putString("sex", "female");
                    }
                    editor.commit();
                    sendCode(mBinding.numberTxt.getText().toString());
                    getCodeUi();
                }
            }
        });

        mBinding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinding.codeTxt.getText().toString().length() == 6){
                    verifyPhoneNumberWithCode(mVerificationId, mBinding.codeTxt.getText().toString());
                }
                else{
                    mBinding.codeTxt.setError("invaliade code");
                }

            }
        });

    }

    public void sendCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //openMapActivity();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mBinding.numberTxt.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.numberTxt.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void getCodeUi(){
        mBinding.materialTextField.animate()
                .translationY(mBinding.materialTextField.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mBinding.materialTextField.setVisibility(View.GONE);

                    }
                });
        mBinding.sendBtn.animate()
                .translationY(mBinding.materialTextField.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mBinding.materialTextField.setVisibility(View.GONE);
                        mBinding.waitTxt.setVisibility(View.VISIBLE);
                        mBinding.progress.setVisibility(View.VISIBLE);

                    }
                });
        mBinding.toggleButton.animate()
                .translationY(mBinding.toggleButton.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mBinding.toggleButton.setVisibility(View.GONE);

                    }
                });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void openMapActivity() {
        final Intent i = new Intent(LoginActivity.this, MainActivity.class);
        mBinding.progress.animate()
                .translationY(mBinding.materialTextField.getHeight())
                .alpha(0.0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //mBinding.progress.setVisibility(View.GONE);

                    }
                });
        mBinding.waitTxt.animate()
                .translationY(mBinding.materialTextField.getHeight())
                .alpha(0.0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mBinding.waitTxt.setVisibility(View.GONE);
                        startActivityForResult(i, 1);
                        overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                        finish();

                    }
                });
    }

}
