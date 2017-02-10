import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class ImageHelper
{
	public static final int IMAGE_PLAYER_DEFAULT = 0;

	public ImageHelper()
	{
		
	}
	
	
	// http://code.google.com/p/game-engine-for-java/source/browse/src/com/gej/util/ImageTool.java#31
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	//http://www.rgagnon.com/javadetails/java-0265.html
	private static Image makeColorTransparent
	(Image im, final Color color) {
	ImageFilter filter = new RGBImageFilter() {
	  // the color we are looking for... Alpha bits are set to opaque
	  public int markerRGB = color.getRGB() | 0xFF000000;

	  public final int filterRGB(int x, int y, int rgb) {
	    if ( ( rgb | 0xFF000000 ) == markerRGB ) {
	      // Mark the alpha bits as zero - transparent
	      return 0x00FFFFFF & rgb;
	      }
	    else {
	      // nothing to do
	      return rgb;
	      }
	    }
	  }; 

	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	return Toolkit.getDefaultToolkit().createImage(ip);
	}
	
	public static BufferedImage makeTransparent(Image im, Color color)
	{
		return toBufferedImage(makeColorTransparent(im, color));
	}
	
	public static void main(String[]args)
	{
		
	}
	
	
}