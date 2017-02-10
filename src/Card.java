import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
 
 
 
public class Card
{
    public boolean faceUp = false;
    public int x, y;
    public int value = -1;
    public int suit = -1;
    public int ID = -1;
    public int pickupX, pickupY; //client sided, for game-specific
     
    //public static int width = 200, height = 100;
    public static final int width = 71, height = 96;
     
    public BufferedImage cardImage;
    public BufferedImage backCardImage;
    public final BufferedImage originalBackImage;
    public final BufferedImage originalImage;
    public BufferedImage grayImage;
    
    public double lx=0, ly=0, dx, dy;
    
    public final static double friction = 0.85;//for sliding
    public final static double handFriction = 0.65;//for when the player is still holding it
    public final static double speed = 0.5; 
    public final static double notMovingMuchDefinition = 5;
    
    public void addPointToMovement(Point P, int r)//call this on the client when the mouse is moved while holding the card.
    {
    	
    	double x = P.x;
    	double y = P.y;
    	

    	if (lx == 0)
    		lx = x;//initialize
    	if (ly == 0)
    		ly = y;
    	
    	if (r == 0)
    	{
    	this.dx += (x - lx)*speed;
    	this.dy += (y - ly)*speed;
    	}
    	else // screen is flipped
    	{
    		this.dx -= (x - lx)*speed;
        	this.dy -= (y - ly)*speed;
    	}
    	
    	
    	boolean notMovingMuch = (Math.sqrt(Math.pow(lx-x,2) + Math.pow(ly-y,2)) < notMovingMuchDefinition);
    	if (notMovingMuch)// the mouse didn't move; shrink the velocity.
    	{
    		dx *= handFriction;
    		dy *= handFriction;
    		
    		if (Math.abs(dx) < 0.15)
    		{
    			dx = 0;
    		}
    		if (Math.abs(dy) < 0.15)
    		{
    			dy = 0;
    		}
    	}
    	
    	this.lx = x; this.ly = y;
    }
    public boolean moving()
    {
    	return (!(dx == 0 && dy == 0));
    }
    
    public void velocitate()//client only -- used to slide the card.
    {
    	this.x += dx; this.y += dy;
    	this.dx *= friction;//slow them down.
    	this.dy *= friction;
    	if (Math.abs(dx) < 0.15)
		{
			dx = 0;
		}
		if (Math.abs(dy) < 0.15)
		{
			dy = 0;
		}
		
    }
     public boolean isCenterField()
     {
    	 return (this.y > 150 && this.y < 450);
     }
     public boolean isExactlyOneAwayFrom(Card other) 
     {
    	 int dist = Math.abs(this.value - other.value);
    	 boolean aceKingReason = (this.value == 0 && other.value == 12) || (this.value == 12 && other.value == 0);
    	 return (dist == 1 || aceKingReason);
     }
    public static String getName(int value)
    {
    	//ace = 0, king = 12.
        String[] names = {"ace","two","three","four","five","six","seven","eight","nine","ten","jack","queen","king"};
        return names[value];
    }
    public static String getSuit(int suit)
    {
        String[] suits = {"clubs","spades","hearts","diamonds"};
        return suits[suit];
    }
    PolyShape collisionRect;
    public Card(int value, int suit, int x, int y)
    {
        this.value=value;this.x=x;this.y=y;
        this.suit=suit;
        faceUp = true;
        cardImage = Panicards.cardFaces.get(suit*13+value);
        originalImage = Panicards.cardFaces.get(suit*13+value);
        originalBackImage = Panicards.backCardFaces.get(0);
        backCardImage = Panicards.backCardFaces.get(0);
        int[] xs = {x+width,x,x,x+width};
        int[] ys = {y+height,y+height,y,y};
        collisionRect = new PolyShape(xs,ys);
    }
    public Card(int value, int suit, int x, int y, String server)
    {
        this.value=value;this.x=x;this.y=y;
        this.suit=suit;
        faceUp = true;
        cardImage = null; originalImage = null; originalBackImage = null; backCardImage = null;
    }
     
    public void setOwner(int owner)
    {
        this.owner = owner;
    }
    public void setCarrier(int carrier)
    {
        this.carrier = carrier;
    }
     
    public int owner, carrier;
     
    public static final int CARRIER_NONE = -1;
    public static final int OWNER_NONE = -1;
     
    public void setCardPosition(int x, int y)
    {
        this.x = x; this.y = y;
        int[] xs = {x+width,x,x,x+width};
        int[] ys = {y+height,y+height,y,y};
        collisionRect = new PolyShape(xs,ys);
         
    }
     
    public boolean pointIntersectsCard(int x, int y)
    {
    	if (collisionRect.drawableShape != null)
        return collisionRect.drawableShape.contains(new Point(x,y));
    	else
    		return false;
    }
     
    public void flip()
    {
        faceUp = !faceUp;
    }
     
    double lastRW=-1, lastRH=-1,lastPriority=-1;
    public void draw(Graphics2D g, double rw, double rh, double priority, Rotate rotate, GameRules gr)
    {
    	boolean grayOut = false;
    	
    	//determine grayOut -- grays out the card so the user can't see it. (Used to hide opponent's cards.)
    	if (gr.get(GameRules.SEPARATE_SIDES))
    	{
    		
    	//600 is the bottom of the screen. 0 is the top.
    	
    	if (rotate.rotate == Player.ROTATION_DEFAULT)
    	{
    		
    		if (this.y < 150)
    		{
    			grayOut = true;
    		}
    		
    		
    	}
    	else if (rotate.rotate == Player.ROTATION_180)
    	{
    		int cardCenterY = this.y + Card.height;
    		if (cardCenterY > 450)
    		{
    			grayOut =true;
    		}
    		
    		
    	}
    	
    	}
    	
    	
        //priority: cards.get(i).draw(g, rw, rh, ((float)(i)+1f)/cards.size());
        //  cards at the beginning of the deck will be shaded, and the end will be unaffected/clear-shaded.
        //  if priority = 1, then it is the top card.
         
        collisionRect.defineDrawableShape(rw, rh, rotate);
         
        if (lastRW != rw || lastRH != rh || lastPriority != priority)//window resized and initial
        {
             
            //scale the images to match new window size
            {//scale front of card
            int newWidth = (int)(Card.width*rw); int newHeight = (int)(Card.height*rh);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, originalImage.getType());
            
         	{//setup grayImage.
         		 grayImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
         		Graphics2D g2 = grayImage.createGraphics();
         		g2.setPaint(Color.GRAY);
             	g2.fill(collisionRect.getTranslatedDrawableShape(rw, rh, -x,-y));
             	
         		
         		
         	}
         	
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
            if (priority != 1)
            {
            if (priority > 1f-0.32f)
            {
                g2.setPaint(new Color(0f,0f,0f,.32f));
            }
            else
                g2.setPaint(new Color(0f, 0f, 0f, 1f-(float)priority));
            g2.fill(collisionRect.getTranslatedDrawableShape(rw, rh, -x,-y));
             
            }
             
            cardImage = resized;
            }
             
            {//scale back of card
            int newWidth = (int)(Card.width*rw); int newHeight = (int)(Card.height*rh);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, originalBackImage.getType());
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(originalBackImage, 0, 0, newWidth, newHeight, 0, 0, originalBackImage.getWidth(), originalBackImage.getHeight(), null);
            if (priority != 1)
            {
            if (priority > 1f-0.32f)
            {
                g2.setPaint(new Color(0f,0f,0f,.32f));
            }
            else
            	g2.setPaint(new Color(0f, 0f, 0f, 1f-(float)priority));
            g2.fill(collisionRect.getTranslatedDrawableShape(rw, rh, -x,-y));
            
            
            
            
            }
             
            backCardImage = resized;
            }
             
             
             
             
             
             
            lastPriority = priority;
            lastRW = rw; lastRH = rh;
             
        }
         
         
         
         int xx = (int)(x*rw);
         int yy = (int)(y*rh);
         if (rotate.rotate == 1)
         {
        	 xx = rotate.getRotatedX(xx) - (int)((Card.width+1)*rw);
        	 yy = rotate.getRotatedY(yy) - (int)((Card.height+1)*rh);
         }
         
         if (grayOut)
         {
        	 //draw black square at end if separate sides are enabled.
             
             	g.drawImage(grayImage,null, xx, yy);
             	
         }
         else
         {
        if (faceUp)
        g.drawImage(cardImage, null, xx, yy);// (int)(x*rw), (int)(y*rh));
        else
        g.drawImage(backCardImage, null, xx, yy);// (int)(x*rw),(int)(y*rh));
         }   
     
        
         
         
         
         
        
    }
    
    
     
     
    public Card(String z)//constructor from toSocketString
    {
        String[] s = z.split("["+n+"]");
        //0 and 1 reserved
        faceUp = Boolean.parseBoolean(s[2]);
        x = pint(s[3]); y = pint(s[4]);
        suit = pint(s[5]);
        value = pint(s[6]);
         
         
        if (!(Panicards.cardFaces.size() == 0))//happens when the server calls the constructor
        {
        cardImage = Panicards.cardFaces.get(suit*13+value);
        originalImage = Panicards.cardFaces.get(suit*13+value);
        originalBackImage = Panicards.backCardFaces.get(0);
        backCardImage = Panicards.backCardFaces.get(0);
        }
        else
        {
        cardImage = null; originalImage = null; originalBackImage = null; backCardImage = null;	
        	
        	
        }
        setCardPosition(x,y);
         
    }
    public static int pint(String s)
    {
        return Integer.parseInt(s);
    }
    public static final String n = "+";
    public String toSocketString()
    {
        String out = "new" + n + "card" + n;
         
        out += "" + faceUp + n + x + n + y + n + suit + n + value;
        return out;
    }
     
}

