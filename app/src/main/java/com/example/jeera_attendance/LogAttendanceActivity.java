package com.example.jeera_attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class LogAttendanceActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid, password, serverAddress, database;

    private List<hr_attendance> hr_attendances = new ArrayList<>() ;
    attendanceeAdapter attendanceeAdapter ;
    TextView tvName ;

    RecyclerView recycleViewlog;
    private long searchTaskId;
    String date, date_depan, check_in, check_out, month;
    Integer user_id, id_employee, id_attende;
    private long searchAttendeId;
    private String name_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_attendance);

        uid = SharedData.getKey(this, "uid");
        password = SharedData.getKey(this, "password");
        serverAddress = SharedData.getKey(this, "serverAddress");
        database = SharedData.getKey(this, "database");
        odoo = new OdooUtility(serverAddress, "object");

        tvName = findViewById(R.id.tvName) ;
        name_user = getIntent().getStringExtra("name_user") ;
        tvName.setText(name_user);

        month = Helper.getTimeStamp("yyy-MM");


        date = Helper.getTimeStamp("yyy-MM-dd HH:mm:ss");
        date_depan = Helper.getTimeStamp("yyy-MM-dd");
        check_in = Helper.dateFormatIn(date_depan);
        check_in = Helper.dateTimeFormat(check_in);
        check_out = Helper.dateFormatOut(date_depan);
        check_out = Helper.dateTimeFormat(check_out);
        date = Helper.dateTimeFormat(date);
        recycleViewlog = findViewById(R.id.recycleViewlog) ;

        attendanceeAdapter = new attendanceeAdapter(this, hr_attendances) ;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleViewlog.setLayoutManager(layoutManager);
        recycleViewlog.setItemAnimator(new DefaultItemAnimator());
        recycleViewlog.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewlog.setAdapter(attendanceeAdapter);
//
        searchEmployee() ;



    }

    private void searchEmployee() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("id", "=", Integer.valueOf(uid))
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
            ));
        }};
//
        searchTaskId = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.employee",
                conditions,
                fields) ;

    }

    private void searchAttende() {
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("employee_id", "=", id_employee),
                        Arrays.asList("month", "=", month)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "employee_id",
                    "check_in",
                    "check_out"
            ));
        }};
//
        searchAttendeId = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.attendance",
                conditions,
                fields) ;
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;

                if (result instanceof Boolean && (Boolean) result == false) {
                    odoo.MessageDialog(LogAttendanceActivity.this, "Login Error. Please try again");
                } else {
//                    Toast.makeText(Activity_attendance.this,"Employee ada ==>"+length, Toast.LENGTH_LONG).show();
                    for (int i = 0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> classObj = (Map<String, Object>) classObjs[i];
                        id_employee = (Integer) classObj.get("id");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAttende();
                        }
                    });

            }
            } else if (id == searchAttendeId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;
                if (length > 0) {
                    hr_attendances.clear();
                    for (int i = 0; i < length; i++) {

                        hr_attendance hr_attendance = new hr_attendance() ;
                        @SuppressWarnings("unchecked")
                        Map<String, Object> classObj = (Map<String, Object>) classObjs[i];
                        hr_attendance.setData(classObj);
                        hr_attendances.add(hr_attendance) ;

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attendanceeAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }

            Looper.loop();
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            Looper.loop();
        }
    } ;

}
