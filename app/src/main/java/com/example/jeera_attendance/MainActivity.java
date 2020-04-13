package com.example.jeera_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class MainActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private long loginTaskId, searchUser;
    private String uid, password, serverAddress, database,name_user;
    private Integer uidd ;
    EditText etServerUrl, etDatabase, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etServerUrl = findViewById(R.id.etServerUrl);
        etDatabase = findViewById(R.id.etDatabase);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etServerUrl.setText("http://odoodev.jeera.id:8070");
        etDatabase.setText("hr_jeera");
        etUsername.setText("admin");
        etPassword.setText("jeera123");


    }

    public void onLogin(View view) {
        String serverUrl = etServerUrl.getText().toString();
        String database = etDatabase.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        SharedData.setKey(MainActivity.this, "password", password);
        SharedData.setKey(MainActivity.this, "username", username);
        SharedData.setKey(MainActivity.this, "serverAddress", serverUrl);
        SharedData.setKey(MainActivity.this, "database", database);


        odoo = new OdooUtility(serverUrl, "common");
        loginTaskId = odoo.login(listener, database, username, password);
    }

    private void SearchUser() {
        uid = SharedData.getKey(this,"uid") ;
        password = SharedData.getKey(this, "password") ;
        serverAddress = SharedData.getKey(this,"serverAddress");
        database = SharedData.getKey(this,"database") ;
        odoo = new OdooUtility(serverAddress,"object") ;

        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("id", "=", Integer.parseInt(uid))
                )
        );


        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
            ));
        }};

        searchUser = odoo.search_read(listener,
                database,
                uid,
                password,
                "res.users",
                conditions,
                fields);
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == loginTaskId) {
                if (result instanceof Boolean && (Boolean) result == false) {
                    odoo.MessageDialog(MainActivity.this, "Login Error. Please try again");
                } else {
                    uid = result.toString();
                    uidd = Integer.parseInt(uid) ;
                    SharedData.setKey(MainActivity.this, "uid", uid);
//                    odoo.MessageDialog(MainActivity.this,"Login Succeed. uid=" + uid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SearchUser();
                        }
                    });

                }
            } else if (id == searchUser) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        Map<String, Object> classObj = (Map<String, Object>) classObjs[i];
                        name_user = (String) classObj.get("name");
                    }
                }
                Intent intent = new Intent(MainActivity.this, MenuActivity.class) ;
                intent.putExtra("uid", uid) ;
                intent.putExtra("name_user", name_user) ;
                startActivity(intent);
            }

            Looper.loop();
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            odoo.MessageDialog(MainActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();

            odoo.MessageDialog(MainActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }
    };


}
