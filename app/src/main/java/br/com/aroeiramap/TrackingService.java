package br.com.aroeiramap;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;

public final class TrackingService extends Service implements LocationListener {
    static final String ACTION_UPDATED="br.com.aroeiramap.UPDATED";
    static final String ACTION_STOP="br.com.aroeiramap.STOP";
    private LocationManager manager;
    private FieldDb db;

    public void onCreate(){ super.onCreate(); db=new FieldDb(this); createChannel(); }
    public int onStartCommand(Intent intent,int flags,int id){
        if(intent!=null && ACTION_STOP.equals(intent.getAction())){ stopSelf(); return START_NOT_STICKY; }
        Intent stop=new Intent(this,TrackingService.class).setAction(ACTION_STOP);
        PendingIntent pi=PendingIntent.getService(this,2,stop,PendingIntent.FLAG_IMMUTABLE);
        Notification n=new Notification.Builder(this,"walk")
            .setContentTitle("Mapping search walk").setContentText("GPS trail is being recorded")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation).setOngoing(true)
            .addAction(new Notification.Action.Builder(null,"Stop",pi).build()).build();
        startForeground(7,n);
        manager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,3f,this,Looper.getMainLooper());
        return START_STICKY;
    }
    public void onLocationChanged(Location l){
        if(l.getAccuracy()>40) return;
        db.track(l.getLatitude(),l.getLongitude(),System.currentTimeMillis());
        Intent i=new Intent(ACTION_UPDATED).setPackage(getPackageName());
        i.putExtra("lat",l.getLatitude()).putExtra("lon",l.getLongitude()).putExtra("accuracy",l.getAccuracy());
        sendBroadcast(i);
    }
    public void onDestroy(){if(manager!=null)manager.removeUpdates(this);super.onDestroy();}
    public android.os.IBinder onBind(Intent i){return null;}
    private void createChannel(){ NotificationManager n=getSystemService(NotificationManager.class); n.createNotificationChannel(new NotificationChannel("walk","Walk tracking",NotificationManager.IMPORTANCE_LOW)); }
}
