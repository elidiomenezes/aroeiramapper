package br.com.aroeiramap;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.Build;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public final class MainActivity extends Activity {
    private static final int REQ=8;
    private FieldDb db; private TrailView map; private TextView status; private double lat,lon; private boolean fix,tracking;
    private final BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){lat=i.getDoubleExtra("lat",0);lon=i.getDoubleExtra("lon",0);fix=true;status.setText(String.format("GPS ±%.0f m",i.getFloatExtra("accuracy",0)));refresh(true);}};
    public void onCreate(Bundle b){super.onCreate(b);db=new FieldDb(this);buildUi();refresh(false);if(hasLocationPermission())startTracking();else requestPermissions();}
    private void buildUi(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(12,12,12,12);root.setBackgroundColor(0xff08110b);
        root.setOnApplyWindowInsetsListener((v,insets)->{int top,bottom,left,right;if(Build.VERSION.SDK_INT>=30){android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());top=bars.top;bottom=bars.bottom;left=bars.left;right=bars.right;}else{top=insets.getSystemWindowInsetTop();bottom=insets.getSystemWindowInsetBottom();left=insets.getSystemWindowInsetLeft();right=insets.getSystemWindowInsetRight();}v.setPadding(12+left,12+top,12+right,12+bottom);return insets;});
        status=new TextView(this);status.setText("GPS not acquired");status.setTextColor(0xffeeeeee);status.setTextSize(16);status.setPadding(8,6,8,10);root.addView(status);
        map=new TrailView(this,null);root.addView(map,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER); String[] names={"START / STOP","MARK AROEIRA","CENTER","NEW WALK"};
        int buttonHeight=(int)(56*getResources().getDisplayMetrics().density);for(String n:names){Button x=new Button(this);x.setText(n);x.setTextSize(11);x.setTextColor(0xffeef5ef);GradientDrawable bg=new GradientDrawable();bg.setColor(0xff263b2c);bg.setCornerRadius(8);x.setBackground(bg);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,buttonHeight,1);bp.setMargins(4,8,4,0);bar.addView(x,bp);if(n.startsWith("START"))x.setOnClickListener(v->toggle());else if(n.startsWith("MARK"))x.setOnClickListener(v->mark());else if(n.equals("CENTER"))x.setOnClickListener(v->{if(fix)map.center(lat,lon);});else x.setOnClickListener(v->confirmClear());}
        root.addView(bar);setContentView(root);
    }
    private boolean hasLocationPermission(){return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED;}
    private void requestPermissions(){
        if(hasLocationPermission()){startTracking();return;}
        if(Build.VERSION.SDK_INT>=33)requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.POST_NOTIFICATIONS},REQ);
        else requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},REQ);
    }
    public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);if(r==REQ&&hasLocationPermission())startTracking();else if(r==REQ)status.setText("Location permission denied — tap START to retry");}
    private void startTracking(){if(tracking)return;tracking=true;startForegroundService(new Intent(this,TrackingService.class));status.setText("Tracking — acquiring location…");}
    private void toggle(){if(!hasLocationPermission()){requestPermissions();return;}if(!tracking){startTracking();}else{tracking=false;startService(new Intent(this,TrackingService.class).setAction(TrackingService.ACTION_STOP));status.setText("Tracking stopped");}}
    private void mark(){if(!fix){Toast.makeText(this,"Wait for a GPS fix",Toast.LENGTH_SHORT).show();return;}db.tree(lat,lon,System.currentTimeMillis());refresh(false);Toast.makeText(this,"Aroeira saved",Toast.LENGTH_SHORT).show();}
    private void confirmClear(){new AlertDialog.Builder(this).setTitle("Start a new walk?").setMessage("This clears the blue trail. Saved trees remain.").setNegativeButton("Cancel",null).setPositiveButton("Clear",(d,w)->{db.clearTrack();refresh(false);}).show();}
    private void refresh(boolean follow){map.setData(db.points("track"),db.points("tree"));if(follow)map.center(lat,lon);}
    protected void onStart(){super.onStart();registerReceiver(receiver,new IntentFilter(TrackingService.ACTION_UPDATED),RECEIVER_NOT_EXPORTED);}
    protected void onStop(){unregisterReceiver(receiver);super.onStop();}
}
