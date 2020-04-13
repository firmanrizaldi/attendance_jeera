package com.example.jeera_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    String uid, name_user;
    TextView tvName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        uid = getIntent().getStringExtra("uid") ;
        name_user = getIntent().getStringExtra("name_user") ;
        tvName = findViewById(R.id.tvName) ;
        tvName.setText(name_user);
    }

    public void onAttendance(View view) {
//        Toast.makeText(MenuActivity.this,"Attendance", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, AttendanceActivity.class));
    }

    public void onHistory(View view) {
        Intent intent = new Intent(MenuActivity.this, LogAttendanceActivity.class) ;
        intent.putExtra("name_user", name_user) ;
        startActivity(intent);
    }
}
