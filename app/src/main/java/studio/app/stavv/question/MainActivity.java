package studio.app.stavv.question;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import studio.app.stavv.question.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final String APP_PREFERENCES = "mysettings";
    ActivityMainBinding mBinding;
    RoomFragment roomFragment;
    People p;
    SharedPreferences preferences;

    public FirebaseAuth fireAuth;
    //private FirebaseDatabase fireData;
    public FirebaseUser fireUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(fireUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{

            String sex = preferences.getString("sex","null");
            p = new People(sex);
            imAlive(p);
            roomFragment = new RoomFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragments, roomFragment)
                    .commit();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fireUser != null){
            imDead();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fireUser != null){
            imAlive(p);
        }
    }

    private void imAlive(People people){
        FirebaseDatabase fireData = FirebaseDatabase.getInstance();
        Log.d(TAG, "put im alive");
        fireData.getReference("main")
                .child("online")
                .child(fireUser.getUid())
                .setValue(people);
    }

    private void imDead(){
        FirebaseDatabase fireData = FirebaseDatabase.getInstance();
        fireData.getReference("main")
                .child("online")
                .child(fireUser.getUid())
                .removeValue();
        Log.d(TAG, "delete online");
    }
}
