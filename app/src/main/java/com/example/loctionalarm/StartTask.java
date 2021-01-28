package com.example.loctionalarm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StartTask extends AppCompatActivity implements OnMapReadyCallback {

    ImageButton imageButton;
    DataBaseConnection mydb;
    Cursor rs;
    GoogleMap map;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    AlertDialog alertDialog;
    List<Address> addressList;
    LatLng currentlocation;
    Context context;
    int nearestloc;
    public static final String NOTIFICATION_CHANNEL_ID = StartTask.class.getSimpleName();
    MediaPlayer mp;
    Uri alarmSound;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static double lati,longi;
    public static String locname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_task);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageButton = findViewById(R.id.imageButton);
        alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(getApplicationContext(),alarmSound);
        context=this;
        alertDialog=new AlertDialog.Builder(context).create();
       // Toast.makeText(context,"Please click refresh button!",Toast.LENGTH_SHORT).show();
        mydb = new DataBaseConnection(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tast_google_map);
        geocoder = new Geocoder(StartTask.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Button exittowork = (Button) findViewById(R.id.exittowork);
        exittowork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartTask.this, WorkArea.class);
                startActivity(i);
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartTask.this, StartTask.class));
                finish();
            }
        });


        mapFragment.getMapAsync( this);
        getLocation();
        currentlocation=new LatLng(lati,longi);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        MarkerOptions options = new MarkerOptions();
        ArrayList<String> loclist = new ArrayList<String>();
        ArrayList<String> taskdetails = new ArrayList<String>();
        ArrayList<LatLng> loca = new ArrayList<LatLng>();
        ArrayList<String> tasklocality = new ArrayList<String>();
        rs=mydb.gettaskid();
        if(rs.moveToFirst())
            do {
                loclist.add(rs.getString(1));
                taskdetails.add(rs.getString(2));
            }while (rs.moveToNext());

            try {
                for(int i=0;i<loclist.size();i++) {
                    addressList = geocoder.getFromLocationName(loclist.get(i), 10);
                    Address address = addressList.get(0);
                    tasklocality.add(address.getSubAdminArea());
                    loca.add(new LatLng(address.getLatitude(), address.getLongitude()));

                }
            }catch (Exception e){
                Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
        }
            double min=getdifference(lati,longi,loca.get(0).latitude,loca.get(0).longitude);
            for(int i=0;i<loca.size();i++)
            {
                double x=getdifference(lati,longi,loca.get(i).latitude,loca.get(i).longitude);
                if(x<min)
                {
                    min=x;
                    nearestloc=i;
                }
            }

        for(int i=0;i< loca.size();i++)
        {
            CircleOptions cp = new CircleOptions();
            cp.center(loca.get(i));
            cp.radius(150);
            cp.strokeColor(Color.GREEN);
            //cp.fillColor(Color.GREEN);
            cp.strokeWidth(10);
            options.position(loca.get(i));

            options.title(taskdetails.get(i));
            options.snippet("Location : "+tasklocality.get(i));
            map.addMarker(options);
            map.addCircle(cp);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loca.get(i), (float) 15));

        }
        map.addMarker(new MarkerOptions().snippet("Current Location : "+locname).title("You are here").position(new LatLng(lati,longi)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati,longi), (float) 15));
        //Toast.makeText(context,"Nearest task is "+taskdetails.get(nearestloc)+", "+min+" meters",Toast.LENGTH_LONG).show();
        displayAlert("Nearest task is : "+taskdetails.get(nearestloc)+"\nLocation Name : "+loclist.get(nearestloc)+"\nDistance : "+String.format("%.2f",(min/1000))+" Kilometers");

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(context,
                                Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        lati=addresses.get(0).getLatitude();
                        longi=addresses.get(0).getLongitude();
                        locname=addresses.get(0).getLocality();
     //                   map.addMarker(new MarkerOptions().snippet(lati+" - "+longi).title("You are here").position(new LatLng(lati,longi)));
       //                 map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati,longi), (float) 15));
         //               Toast.makeText(context,currentlocation.latitude+"\t"+currentlocation.longitude,Toast.LENGTH_LONG);
                    }catch (Exception e){
                        Toast.makeText(context,e+"",Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }
    public double getdifference(double lat1,double long1,double lat2, double long2) {
        Location startPoint = new Location("");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(long1);

        Location endPoint = new Location("");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);
        double distance = startPoint.distanceTo(endPoint);
        return distance;
    }
    public void displayAlert(String x)
    {
        mp.start();
        alertDialog.setTitle("Nearest Location Alert....!");
        alertDialog.setMessage(x);
        alertDialog.setIcon(R.drawable.ic_baseline_warning_24);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mp.stop();
                alertDialog.hide();
            }
        });

        alertDialog.show();
    }

}