import java.awt.Robot;
import java.awt.event.KeyEvent;


public class CheatPolySciLol
{
	
	public CheatPolySciLol()
	{
		try{
		r = new Robot();
		}catch(Exception e){};
	}
	
	public void p(int key)//keypress
	{
		try{
			Thread.sleep(50);
		r.keyPress(key);
		Thread.sleep(50);
		}catch(Exception e){};
	}
	public void r(int key)//release
	{
		try{
			Thread.sleep(50);
			r.keyRelease(key);
			Thread.sleep(50);
		}catch(Exception e){};
	}
	
	public void t(int key)//type
	{
		this.p(key);;
		this.r(key);;
	}
	Robot r;
	
	public static void main(String[]args)
	{
		try{
			CheatPolySciLol c = new CheatPolySciLol();
		for (int i = 0; i < 10; i++)
		{
			System.out.println("starting in... " + (10-i));
			Thread.sleep(1000);
		}
			while(true)
			{
		c.t(KeyEvent.VK_1);
		c.t(KeyEvent.VK_2);
		c.p(KeyEvent.VK_CONTROL);
		c.t(KeyEvent.VK_Y);
		c.r(KeyEvent.VK_CONTROL);
		c.t(KeyEvent.VK_ENTER);
		
		Thread.sleep(3000);
			}
		
		}catch(Exception e){};
	}
}