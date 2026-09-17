package br.com.aroeiramap;

final class Geo {
    private static final double R = 6378137.0;
    static double east(double lon, double lon0, double lat0) { return Math.toRadians(lon-lon0)*R*Math.cos(Math.toRadians(lat0)); }
    static double north(double lat, double lat0) { return Math.toRadians(lat-lat0)*R; }
    static double distance(double aLat, double aLon, double bLat, double bLon) {
        double p1=Math.toRadians(aLat), p2=Math.toRadians(bLat), dp=Math.toRadians(bLat-aLat), dl=Math.toRadians(bLon-aLon);
        double a=Math.sin(dp/2)*Math.sin(dp/2)+Math.cos(p1)*Math.cos(p2)*Math.sin(dl/2)*Math.sin(dl/2);
        return 2*R*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    }
    private Geo() {}
}
