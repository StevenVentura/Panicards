public class Rotate
{
	public int width, height;
	public int rotate = 0;//rotation_180 = 1, rotation_default = 0
	
	public Rotate(int width, int height, int rotate)
	{
		this.width=width; this.height=height; this.rotate = rotate;
	}
	public int getRotatedX(int x)
	{
		if (rotate == Player.ROTATION_DEFAULT)
			return x;
		else if (rotate == Player.ROTATION_180)
		return width-x;
		
		return -1;//not possible
	}
	public int getRotatedY(int y)
	{
		if (rotate == Player.ROTATION_DEFAULT)
			return y;
		else if (rotate == Player.ROTATION_180)
		return height-y;
		
		return -1;//not possible
	}
	
	
	
	
	
}