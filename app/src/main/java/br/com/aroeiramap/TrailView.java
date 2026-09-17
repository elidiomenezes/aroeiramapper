package br.com.aroeiramap;

import android.content.*;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;
import java.util.*;

public final class TrailView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<FieldDb.Point> track=Collections.emptyList(), trees=Collections.emptyList();
    private double centerLat,centerLon; private boolean hasCenter=false; private float metersPerPx=1.2f;
    public TrailView(Context c, AttributeSet a){super(c,a);p.setStrokeCap(Paint.Cap.ROUND);setBackgroundColor(Color.rgb(8,17,11));}
    void setData(List<FieldDb.Point> t,List<FieldDb.Point> r){track=t;trees=r;if(!hasCenter&&!t.isEmpty()){FieldDb.Point q=t.get(t.size()-1);centerLat=q.lat;centerLon=q.lon;hasCenter=true;}invalidate();}
    void center(double lat,double lon){centerLat=lat;centerLon=lon;hasCenter=true;invalidate();}
    protected void onDraw(Canvas c){super.onDraw(c);if(!hasCenter){p.setColor(Color.rgb(180,198,183));p.setTextSize(38);p.setTextAlign(Paint.Align.CENTER);c.drawText("Waiting for GPS…",getWidth()/2f,getHeight()/2f,p);return;}
        grid(c); Path path=new Path(); boolean first=true;
        for(FieldDb.Point q:track){float x=x(q.lon),y=y(q.lat);if(first){path.moveTo(x,y);first=false;}else path.lineTo(x,y);}
        p.setStyle(Paint.Style.STROKE);p.setColor(Color.argb(65,46,125,50));p.setStrokeWidth(40f/metersPerPx);c.drawPath(path,p);
        p.setColor(Color.rgb(72,145,255));p.setStrokeWidth(4);c.drawPath(path,p);
        p.setStyle(Paint.Style.FILL);for(FieldDb.Point q:trees){p.setColor(Color.rgb(198,40,40));c.drawCircle(x(q.lon),y(q.lat),10,p);p.setColor(Color.WHITE);c.drawCircle(x(q.lon),y(q.lat),4,p);}
        p.setColor(Color.WHITE);c.drawCircle(getWidth()/2f,getHeight()/2f,7,p);
    }
    private void grid(Canvas c){p.setStrokeWidth(1);p.setColor(Color.argb(55,180,210,185));float step=50/metersPerPx;for(float x=getWidth()/2f%step;x<getWidth();x+=step)c.drawLine(x,0,x,getHeight(),p);for(float y=getHeight()/2f%step;y<getHeight();y+=step)c.drawLine(0,y,getWidth(),y,p);p.setColor(Color.rgb(180,198,183));p.setTextSize(24);p.setTextAlign(Paint.Align.LEFT);c.drawText("50 m grid • green = searched corridor",18,34,p);}
    private float x(double lon){return getWidth()/2f+(float)(Geo.east(lon,centerLon,centerLat)/metersPerPx);}
    private float y(double lat){return getHeight()/2f-(float)(Geo.north(lat,centerLat)/metersPerPx);}
    public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==MotionEvent.ACTION_UP){performClick();return true;}return true;}
    public boolean performClick(){super.performClick();return true;}
}
