package com.example.covidscape;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covidscape.R;
import com.example.covidscape.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {
    EditText usernameET, firstNameET, lastNameET, emailET;
    Button updateBtn, cancelBtn;
    DatabaseReference dbRef;
    private String userID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        usernameET=findViewById(R.id.editUsername);
        firstNameET=findViewById(R.id.editFirstName);
        lastNameET=findViewById(R.id.editLastName);
        emailET=findViewById(R.id.editEmail);
        updateBtn=findViewById(R.id.updateProfileBtn);
        cancelBtn=findViewById(R.id.cancelUpdateProfileBtn);

        dbRef= FirebaseDatabase.getInstance("https://covidscape-login-logout-sop-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();

        dbRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user userProfile = snapshot.getValue(user.class);
                if(userProfile != null){
                   usernameET.setText(userProfile.getUsername());
                   firstNameET.setText(userProfile.getFirstName());
                   lastNameET.setText(userProfile.getLastName());
                   emailET.setText(userProfile.getEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateActivity.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername, newFirstName, newLastName, newEmail;
                newUsername = usernameET.getText().toString();
                newFirstName = firstNameET.getText().toString();
                newLastName=lastNameET.getText().toString();
                newEmail=emailET.getText().toString();

                if (user != null) {
                    Map<String, Object> profileUpdate = new HashMap<>();
                    profileUpdate.put(userID + "/username", newUsername);
                    profileUpdate.put(userID + "/firstName", newFirstName);
                    profileUpdate.put(userID + "/lastName", newLastName);
                    profileUpdate.put(userID + "/email", newEmail);

                    dbRef.updateChildren(profileUpdate);

                    Toast.makeText(UpdateActivity.this, " Profile has been updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
