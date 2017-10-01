package studio.app.stavv.question;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import studio.app.stavv.question.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {
    private FragmentChatBinding mBinding;
    private String FRAGMENT = "fragment_data";
    private SharedPreferences preferences;
    //private CenterGravityBinding centerBind;
    private String TAG = ChatFragment.class.getSimpleName();
    private ArrayList<Chat> ChatList = new ArrayList<ChatFragment.Chat>();
    private ArrayList<String> actions = new ArrayList<String>();
    private int globalCount = 0;
    private boolean isLoaded = false;
    private boolean isbtnload = false;
    LinearLayout msgBox;
    LinearLayout botBox;
    PeopleData p;
    FirebaseUser fireUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        preferences = getActivity().getSharedPreferences(FRAGMENT, Context.MODE_PRIVATE);
        RelativeLayout layout = (RelativeLayout)mBinding.gifImage;
        msgBox = (LinearLayout)mBinding.messageLayout;
        botBox = (LinearLayout)mBinding.answerList;
        LinearLayout answerBox = (LinearLayout)mBinding.answerList;
        String idGif = preferences.getString("idGif", "null");
        if(!Objects.equals(idGif, "null")){
            layout.addView(new GifView(getActivity(),"file:///android_asset/"+idGif));
        }
        //
        p = new PeopleData();
        //p.setNum(fireUser.getPhoneNumber());
        //addTextAnswer("Hello",answerBox,p,"null", "null");



        return mBinding.getRoot();//comment test_danila
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        FirebaseDatabase fireData = FirebaseDatabase.getInstance();
        getChat("male", fireData);
        chatNow();
        //PeopleData p = new PeopleData(20,true,1000000,"Aleksey", "Ivavov", "male", fireUser.getPhoneNumber());


    }
    private void chatNow(){

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if(isLoaded){
                                        addQuestion(ChatList.get(globalCount).question);
                                        if(!isbtnload){
                                            for (int i = 0; i<ChatList.get(globalCount).answers.size(); i++){
                                                if(ChatList.get(globalCount).answers.get(i).str == null){
                                                    addInpAnswer(p,ChatList.get(globalCount).answers.get(i).action.val);
                                                    isbtnload = true;

                                                }
                                                else if(Objects.equals(ChatList.get(globalCount).answers.get(i).action.name,"setbuisness")
                                                        || Objects.equals(ChatList.get(globalCount).answers.get(i).action.val,"exit")
                                                        || Objects.equals(ChatList.get(globalCount).answers.get(i).action.val, null)){
                                                    addTextAnswer(ChatList.get(globalCount).answers.get(i).str
                                                            ,ChatList.get(globalCount).answers.get(i).action.name
                                                            ,ChatList.get(globalCount).answers.get(i).action.val);
                                                    isbtnload = true;
                                                }

                                            }
                                        }

                                    }

                                }
                            });
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(globalCount >= ChatList.size()){
                            pushBigData(fireUser, p);
                            gotoRoom();
                            break;

                        }
                    }
                }
            }.start();
        }

    private void addQuestion(String textViewText) {
        View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.text_layout, msgBox, false);
        TextView textView = (TextView) layout2.findViewById(R.id.textV);
        textView.setText(textViewText);
        textView.setPadding(10,10,10,10);
        textView.setElevation(8);
        msgBox.removeAllViews();
        msgBox.addView(layout2);
    }

    private void addTextAnswer(String text, final String action, final String val){
        final View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.inp_text, botBox, false);
        final Button btn = (Button)layout2.findViewById(R.id.btnBot);
        btn.setText(text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action != null){
                    switch (action){
                        case "setbuisness":
                            if(Objects.equals("on", val)){
                                p.setBuisness(true);
                            }else {
                                p.setBuisness(false);
                            }
                            break;
                        case "action":
                            if(Objects.equals(val, "exit")){
                                getActivity().finish();
                            }
                            break;
                    }
                }

                botBox.removeAllViews();
                isbtnload = false;
                globalCount++;
            }
        });
        //btn.setElevation(8);
        //msgBox.removeAllViews();
        botBox.addView(layout2);
    }
    private void addInpAnswer(final PeopleData p, final String val){
        View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.input_layout, botBox, false);
        Button btn = (Button)layout2.findViewById(R.id.butnSend);
        final EditText editText = (EditText)layout2.findViewById(R.id.editText);
        btn.setText("SEND");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (val){
                    case "setname":
                            p.setName(editText.getText().toString());
                        break;
                    case "setage":
                        p.setName(editText.getText().toString());
                        break;
                    case "setturnover":
                        p.setTurnover(Integer.parseInt(editText.getText().toString()));
                        break;
                }

                botBox.removeAllViews();
                isbtnload = false;
                globalCount++;

            }
        });

        //msgBox.removeAllViews();
        botBox.addView(layout2);
    }

    private void getChat(String sexOfBot, FirebaseDatabase fireData){
        final DatabaseReference chatRef = fireData.getReference("main").child("chat");
        chatRef.child("male").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data");
                for(DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    Chat c = new Chat();
                    c.question = chatSnapshot.child("str").getValue(String.class);
                    Log.d(TAG, "quest: " + c.question);
                    c.answers = new ArrayList<Answer>();
                    DataSnapshot answer = chatSnapshot.child("answers");
                    for(DataSnapshot answerSnapshot: answer.getChildren()){
                        Answer a = new Answer();
                        if(answerSnapshot.child("str") != null){
                            a.str = answerSnapshot.child("str").getValue(String.class);
                            Log.d(TAG, "answer: " + a.str);
                        }
                        else{
                            a.str = "";
                        }


                        if(answerSnapshot.child("name") != null){
                            Action ac = new Action();
                            ac.name = answerSnapshot.child("name").getValue(String.class);
                            ac.val = answerSnapshot.child("val").getValue(String.class);
                            a.action = ac;
                        }
                        c.answers.add(a);
                    }
                    ChatList.add(c);
                }
                //Log.d(TAG, "Loading questions done!");
                chatRef.removeEventListener(this);
                isLoaded = true;
                addQuestion("Hello");
                //chatNow();
                //sendQuestion(ChatList.get(globalCount).question, msgBox);*/




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Loading questions ERROR! : " + databaseError.toString());
            }
        });
    }

    private void gotoRoom(){
        RoomFragment room = new RoomFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments, room)
                .commit();
    }

    private void pushBigData(FirebaseUser fireUser, PeopleData peopleData){
        FirebaseDatabase fireData = FirebaseDatabase.getInstance();
            Log.d(TAG, "put big data");
            fireData.getReference("main")
                    .child("big_data")
                    .child(fireUser.getUid())
                    .setValue(peopleData);
    }

    class Chat{
        public String question;//0-0
        public ArrayList<Answer> answers;
    }
    class Action{
        public String name;
        public String val;
    }
    class Answer{
        public  String str;
        public Action action;
    }

}
