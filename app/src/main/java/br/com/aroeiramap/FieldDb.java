package br.com.aroeiramap;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

final class FieldDb extends SQLiteOpenHelper {
    static final class Point { final double lat,lon; final long time; Point(double a,double o,long t){lat=a;lon=o;time=t;} }
    FieldDb(Context c){ super(c,"field.db",null,1); }
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE track(id INTEGER PRIMARY KEY, lat REAL, lon REAL, time INTEGER)");
        db.execSQL("CREATE TABLE tree(id INTEGER PRIMARY KEY, lat REAL, lon REAL, time INTEGER, note TEXT DEFAULT '')");
    }
    public void onUpgrade(SQLiteDatabase db,int o,int n){}
    void track(double lat,double lon,long t){ ContentValues v=values(lat,lon,t); getWritableDatabase().insert("track",null,v); }
    void tree(double lat,double lon,long t){ ContentValues v=values(lat,lon,t); getWritableDatabase().insert("tree",null,v); }
    private ContentValues values(double a,double o,long t){ ContentValues v=new ContentValues();v.put("lat",a);v.put("lon",o);v.put("time",t);return v; }
    List<Point> points(String table){
        ArrayList<Point> out=new ArrayList<>();
        try(Cursor c=getReadableDatabase().query(table,new String[]{"lat","lon","time"},null,null,null,null,"time")){
            while(c.moveToNext())out.add(new Point(c.getDouble(0),c.getDouble(1),c.getLong(2)));
        } return out;
    }
    void clearTrack(){getWritableDatabase().delete("track",null,null);}
}
