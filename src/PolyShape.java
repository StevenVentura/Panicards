import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
 
public class PolyShape//polyShape of Panicards
{
    public int[] xs, ys;
    public int[] defaultXs, defaultYs;
    public Color color;
    public PolyShape(int[] xs, int[]ys)
    {
        this.defaultXs = xs; this.defaultYs = ys;
        collisionShape = new Polygon(xs,ys,xs.length);
    }
     
    public Shape drawableShape, collisionShape;
     
    public Shape getTranslatedDrawableShape(double rw, double rh, int cx, int cy)
    {
        int[] xs = new int[defaultXs.length];
        int[] ys = new int[defaultYs.length];
        for (int i = 0; i < xs.length; i++)
        {
            xs[i] = (int)((defaultXs[i]+cx)*rw);
            ys[i] = (int)((defaultYs[i]+cy)*rw);
        }
        return new Polygon(xs,ys,xs.length);
         
    }
    
    public void defineDrawableShape(double rw, double rh, Rotate r)
    {
        xs = new int[defaultXs.length];
        ys = new int[defaultYs.length];
        for (int i = 0; i < xs.length; i++)
        {
        	xs[i] = (int)(defaultXs[i]*rw);
            ys[i] = (int)(defaultYs[i]*rh);
        	
        	if (r.rotate == Player.ROTATION_180)
        	{
        	xs[i] = r.getRotatedX(xs[i]);
        	ys[i] = r.getRotatedY(ys[i]);
        	}
        }
        drawableShape = new Polygon(xs,ys,xs.length);
    }
     
    public static Point2D getScaledPoint(double x, double y, double rw, double rh)
    {
        double X = x*rw;
        double Y = y*rh;
         
        return new Point2D.Double(X,Y);
    }
     
 
     
}

