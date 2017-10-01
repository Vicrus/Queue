package studio.app.stavv.question;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import studio.app.stavv.question.databinding.FragmentRoomBinding;

public class RoomFragment extends Fragment {
    private FragmentRoomBinding mBinding;
    private String FRAGMENT = "fragment_data";
    private SharedPreferences preferences;
    String TAG = RoomFragment.class.getSimpleName();

    RelativeLayout one;
    RelativeLayout two;
    ArrayList<People> online_peoples = new ArrayList<People>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false);
        preferences = getActivity().getSharedPreferences(FRAGMENT, Context.MODE_PRIVATE);
        one = (RelativeLayout) mBinding.one;
        two = (RelativeLayout) mBinding.two;
        GifView gif = new GifView(getActivity(), "file:///android_asset/girl_morganie.gif",45);
        gif.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        gotoChat("girl_talk.gif");
                        break;
                }
                return false;
            }
        });
        one.addView(gif);

        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        FirebaseUser fireUser = fireAuth.getCurrentUser();
        FirebaseDatabase fireData = FirebaseDatabase.getInstance();
        getData(fireData);

    }

    private void drawOnline(ArrayList<People> onlineP) {
        if (onlineP.size() > 1) {
            Log.d(TAG, "male add screen");
            GifView gif = new GifView(getActivity(), "file:///android_asset/man_morganie.gif", 45);
            gif.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            gotoChat("man_talk.gif");
                            break;
                    }
                    return false;
                }
            });
            two.addView(gif);
        }
    }

    private void getData( FirebaseDatabase fireData){
        final DatabaseReference onlineRef = fireData.getReference("online");
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot onlineSnapshot: dataSnapshot.getChildren()) {
                    People p = new People();
                    p.setAge(onlineSnapshot.child("age").getValue(Integer.class));
                    p.setName(onlineSnapshot.child("name").getValue(String.class));
                    p.setSex(onlineSnapshot.child("sex").getValue(String.class));
                    online_peoples.add(p);
                }
                Log.d(TAG, "Added peoples info");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error add peoples info : " + databaseError.toString());
            }
        });
    }

    private void gotoChat(String idGif){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idGif", idGif);
        editor.commit();
        ChatFragment chat = new ChatFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments, chat)
                .commit();
    }
    class DrawView extends View {

        public DrawView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
        }

    }

    private int getScale(){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(10d);
        val = val * 100d;
        return val.intValue();
    }
}
