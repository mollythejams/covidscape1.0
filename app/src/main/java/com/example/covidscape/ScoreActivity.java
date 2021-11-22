package com.example.covidscape;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.HashMap;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity {
    CircularProgressBar circularProgressBar;
    TextView txtScore;
    private Button back;
    int score, newXP, userXP, xpEarned;
    final int TOTAL_QUESTIONS=5;

    DatabaseReference dbRef;
    private String userID;
    FirebaseUser user;

    final MediaPlayer mediaplayer = MediaPlayer.create(this,R.raw.pop);
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);


        score=getIntent().getIntExtra("score",0);

        circularProgressBar= (CircularProgressBar) findViewById(R.id.circularProgressBar);
        txtScore= (TextView) findViewById(R.id.txtScore);
        back = findViewById(R.id.backBtnQuiz);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaplayer.start();
                startActivity(new Intent(ScoreActivity.this,quiz.class));
            }
        });

        circularProgressBar.setProgress(score);
        txtScore.setText(score+"/"+TOTAL_QUESTIONS);

        dbRef=FirebaseDatabase.getInstance("https://covidscape-login-logout-sop-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();

        //get current XP points
        dbRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user userProfile = snapshot.getValue(user.class);
                if(userProfile != null){
                    userXP=userProfile.userXP;
                    System.out.println("userXP: "+userXP);

                    updateXP(score,userXP, dbRef, userProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Can't get current XP");
            }
        });

        //update new XP points

    }
    private void updateXP(int score, int userXP, DatabaseReference dbRef, user user){
        xpEarned = score * 20;
        System.out.println("xpEarned: " + xpEarned);
        newXP = xpEarned + userXP;
        System.out.println("newXP: " + newXP);
        if (user != null) {
            Map<String, Object> pointsUpdate = new HashMap<>();
            pointsUpdate.put(userID + "/userXP", newXP);
            dbRef.updateChildren(pointsUpdate);

            Toast.makeText(this, xpEarned + " XP has been added!", Toast.LENGTH_SHORT).show();
        }
    }

}
