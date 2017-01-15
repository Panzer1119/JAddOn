/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.gps;

/**
 *
 * @author Paul
 */
public class Point {
    
    /**
     * Long X value
     */
    public long x = -1;
    /**
     * Long Y value
     */
    public long y = -1;
    /**
     * Long Z value
     */
    public long z = -1;
    /**
     * Double X rotation
     */
    public double rotx = 0.0;
    /**
     * Double Y rotation
     */
    public double roty = 0.0;
    /**
     * Double Z rotation
     */
    public double rotz = 0.0;
    
    /**
     * This is a point in a 3D coordinate system
     */
    public Point() {
        
    }
    
    /**
     * This is a point in a 3D coordinate system
     * @param x Long X value
     * @param y Long Y value
     * @param z Long Z value
     */
    public Point(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public String toString() {
        return String.format("X: %d, Y: %d, Z: %d", x, y, z);
    }
    
    /**
     * Calculates the point from the four points like gps
     * @param p1 Point 1
     * @param d1 Double Distance to point 1
     * @param p2 Point 2
     * @param d2 Double Distance to point 2
     * @param p3 Point 3
     * @param d3 Double Distance to point 3
     * @param p4 Point 4
     * @param d4 Double Distance to point 4
     * @return Point in the middle of the four points
     */
    public static Point calculatePoint(Point p1, double d1, Point p2, double d2, Point p3, double d3, Point p4, double d4) {
        long x = -1;
        long y = -1;
        long z = -1;
        return new Point(x, y, z);
    }
    
    /**
     * Returns the distance between two points
     * @param p1 Point 1
     * @param p2 Point 2
     * @return Double Distance between two points
     */
    public static double getDistance(Point p1, Point p2) {
        if(p1 == null || p2 == null) {
            return -1;
        }
        double distance = Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2) + Math.pow((p2.z - p1.z), 2));
        return distance;
    }
    
    /**
     * Returns a pseudo randomly generated point
     * @param minx Integer Minimal x value
     * @param maxx Integer Maximal x value
     * @param miny Integer Minimal y value
     * @param maxy Integer Maximal y value
     * @param minz Integer Minimal z value
     * @param maxz Integer Maximal z value
     * @return Point Pseudo randomly generated point
     */
    public static Point getRandom(int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        maxx++;
        maxy++;
        maxz++;
        long x = ((long) (Math.random() * (maxx + Math.abs(minx)))) + minx;
        long y = ((long) (Math.random() * (maxy + Math.abs(miny)))) + miny;
        long z = ((long) (Math.random() * (maxz + Math.abs(minz)))) + minz;
        return new Point(x, y, z);
    }
    
    /**
     * Returns a pseudo randomly generated point between -10 and 10
     * @return Point Pseudo randomly generated point
     */
    public static Point getRandom() {
        return getRandom(-10, 10, -10, 10, -10, 10);
    }
    
}
