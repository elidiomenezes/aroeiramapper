package br.com.aroeiramap;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public final class MainActivity extends Activity {
    private static final int REQ=8;
    private FieldDb db; private TrailView map; private TextView status; private double lat,lon; private boolean fix,tracking;
    private final BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){lat=i.getDoubleExtra("lat",0);lon=i.getDoubleExtra("lon",0);fix=true;status.setText(String.format("GPS ±%.0f m",i.getFloatExtra("accuracy",0)));refresh(true);}};
    public void onCreate(Bundle b){super.onCreate(b);db=new FieldDb(this);buildUi();refresh(false);requestPermissions();}
    private void buildUi(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(12,12,12,12);root.setBackgroundColor(0xff102115);
        status=new TextView(this);status.setText("GPS not acquired");status.setTextColor(0xffeeeeee);status.setTextSize(16);status.setPadding(8,6,8,10);root.addView(status);
        map=new TrailView(this,null);root.addView(map,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER); String[] names={"START / STOP","MARK AROEIRA","CENTER","NEW WALK"};
        for(String n:names){Button x=new Button(this);x.setText(n);x.setTextSize(11);bar.addView(x,new LinearLayout.LayoutParams(0,-2,1));if(n.startsWith("START"))x.setOnClickListener(v->toggle());else if(n.startsWith("MARK"))x.setOnClickListener(v->mark());else if(n.equals("CENTER"))x.setOnClickListener(v->{if(fix)map.center(lat,lon);});else x.setOnClickListener(v->confirmClear());}
        root.addView(bar);setContentView(root);
    }
    private void requestPermissions(){if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.POST_NOTIFICATIONS},REQ);}
    public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);if(r==REQ&&g.length>0&&g[0]==PackageManager.PERMISSION_GRANTED)status.setText("Ready — tap START");}
    private void toggle(){if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){requestPermissions();return;}tracking=!tracking;if(tracking){startForegroundService(new Intent(this,TrackingService.class));status.setText("Tracking — acquiring GPS…");}else{startService(new Intent(this,TrackingService.class).setAction(TrackingService.ACTION_STOP));status.setText("Tracking stopped");}}
    private void mark(){if(!fix){Toast.makeText(this,"Wait for a GPS fix",Toast.LENGTH_SHORT).show();return;}db.tree(lat,lon,System.currentTimeMillis());refresh(false);Toast.makeText(this,"Aroeira saved",Toast.LENGTH_SHORT).show();}
    private void confirmClear(){new AlertDialog.Builder(this).setTitle("Start a new walk?").setMessage("This clears the blue trail. Saved trees remain.").setNegativeButton("Cancel",null).setPositiveButton("Clear",(d,w)->{db.clearTrack();refresh(false);}).show();}
    private void refresh(boolean follow){map.setData(db.points("track"),db.points("tree"));if(follow)map.center(lat,lon);}
    protected void onStart(){super.onStart();registerReceiver(receiver,new IntentFilter(TrackingService.ACTION_UPDATED),RECEIVER_NOT_EXPORTED);}
    protected void onStop(){unregisterReceiver(receiver);super.onStop();}
}
