package com.example.jeera_attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

import static android.util.Log.*;


public class AttendanceActivity extends AppCompatActivity {
    private static final String TAG = "AttendanceActivitya";


    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    private OdooUtility odoo;
    private String uid, password, serverAddress, database;
    String date, date_depan, check_in, check_out, info, locationStr, date_checkin_per;
    Integer user_id, id_employee, id_attende;
    private long searchTaskId, createTaskId, searchAttendeId, updateTaskId, searchCheckout;
    ImageView ivPhoto, image_location;
    Button btnAbsen;
    TextView tvTest;
    public static final int PERMISSION_CODE = 1000, IMAGE_CAPTURE_CODE = 1001;
    Uri image_url;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        ivPhoto = findViewById(R.id.ivPhoto);
        btnAbsen = findViewById(R.id.btnAbsen);

        uid = SharedData.getKey(this, "uid");
        password = SharedData.getKey(this, "password");
        serverAddress = SharedData.getKey(this, "serverAddress");
        database = SharedData.getKey(this, "database");
        odoo = new OdooUtility(serverAddress, "object");
        date = Helper.getTimeStamp("yyy-MM-dd HH:mm:ss");
        date_depan = Helper.getTimeStamp("yyy-MM-dd");
        check_in = Helper.dateFormatIn(date_depan);
        check_in = Helper.dateTimeFormat(check_in);
        check_out = Helper.dateFormatOut(date_depan);
        check_out = Helper.dateTimeFormat(check_out);
        date = Helper.dateTimeFormat(date);
        user_id = Integer.valueOf(uid);





        btnAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);

                    } else {
                        openCamera() ;
                    }
                } else {
                    openCamera() ;
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    locationStr = ("www.google.com/maps/place/"+location.getLatitude()+","+location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            locationStr = ("www.google.com/maps/place/"+ mLastLocation.getLatitude()+","+ mLastLocation.getLongitude());
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void openCamera() {

        ContentValues values = new ContentValues() ;
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_url) ;
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                    openCamera();
                } else {
                    Toast.makeText(this,"Permission denied ...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            ivPhoto.setImageURI(image_url);
            info = Helper.Base64(ivPhoto) ;

            List conditions = Arrays.asList(
                    Arrays.asList(
                            Arrays.asList("user_id", "=", user_id)
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
    }

    private void searchAttende() {
//        e(TAG, "searchAttende: " + date );
//        e(TAG, "searchAttende: " + date_checkin_per );

        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("employee_id", "=", id_employee),
                        Arrays.asList("check_in", ">=", check_in),
                        Arrays.asList("check_in", "<=", check_out)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "employee_id",
                    "check_in"
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

    private void searchCheckoutAttende() {


        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("employee_id", "=", id_employee),
                        Arrays.asList("check_out", ">=", check_in),
                        Arrays.asList("check_out", "<=", check_out)
                )
        );

        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "employee_id"
            ));
        }};

        searchCheckout = odoo.search_read(listener,
                database,
                uid,
                password,
                "hr.attendance",
                conditions,
                fields) ;
    }

    private void createAttende() {
        List data = Arrays.asList(new HashMap() {{
            put("employee_id", id_employee);
            put("check_in", date);
            put("image_check_in", info);
            put("checkin_maps", locationStr);
            put("in_android", true);
        }});

        createTaskId = odoo.create(listener,
                database,
                uid,
                password,
                "hr.attendance",
                data);

    }
    private void checkOutAttendance() {
        List data = Arrays.asList(
                Arrays.asList(id_attende),
                new HashMap(){{
                    put("check_out",date) ;
                    put("image_check_out",info) ;
                    put("checkout_maps",locationStr) ;
                }}
        );
        updateTaskId = odoo.update(listener,
                database,
                uid,
                password,
                "hr.attendance",
                data) ;

    }


    XMLRPCCallback listener = new XMLRPCCallback() {
        @Override
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id == searchTaskId){
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;

                if (result instanceof Boolean && (Boolean) result == false) {
                    odoo.MessageDialog(AttendanceActivity.this,"Login Error. Please try again");
                } else {
//                    Toast.makeText(Activity_attendance.this,"Employee ada ==>"+length, Toast.LENGTH_LONG).show();
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        id_employee = (Integer)classObj.get("id") ;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchAttende() ;
                        }
                    });

                }
            } else if (id == searchAttendeId) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;
//                Toast.makeText(Activity_attendance.this,"Attende ada ===>"+length, Toast.LENGTH_LONG).show();
                if (length > 0){
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj = (Map<String,Object>) classObjs[i];
                        id_attende = (Integer)classObj.get("id") ;
                        date_checkin_per = (String) classObj.get("check_in");
                    }
                    Integer a = Helper.cekCheckin(date,date_checkin_per);

                    if (a >= 5 ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchCheckoutAttende() ;
                            }
                        });
                    } else {
                        Toast.makeText(AttendanceActivity.this,"Anda Sudah Check-in", Toast.LENGTH_LONG).show();
                    }

                } else {
//                    Toast.makeText(AttendanceActivity.this,"check_id ===>"+ id_employee, Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createAttende() ;
                        }
                    });
                }
            } else if (id == searchCheckout) {
                Object[] classObjs = (Object[])result ;
                int length= classObjs.length;
                if (length > 0){
                    Toast.makeText(AttendanceActivity.this,"Anda Sudah Check-out", Toast.LENGTH_LONG).show();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkOutAttendance() ;
                        }
                    });
                }
            }else if (id == createTaskId) {
                Toast.makeText(AttendanceActivity.this, "Berhasil Check in ", Toast.LENGTH_LONG).show();
            } else if (id == updateTaskId) {
                Toast.makeText(AttendanceActivity.this, "Berhasil Check out ", Toast.LENGTH_LONG).show();
            }
                Looper.loop();
        }

        @Override
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            odoo.MessageDialog(AttendanceActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }

        @Override
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            odoo.MessageDialog(AttendanceActivity.this,
                    "Login Error. " + error.getMessage());
            Looper.loop();
        }
    } ;

}
