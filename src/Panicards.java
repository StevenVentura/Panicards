/*
 * Panicards
 * Steven Ventura
*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
 
public class Panicards extends JApplet implements KeyListener, MouseListener, MouseMotionListener
{
     
    /*
     * goal 1 21 14 :: draw a card object on the screen
     * 2 2 14 i just changed scaling to rw and rh
     * [-]goal 2 2 14 :: get the mouse click to work with the deck
     * [-]flip a card with right click
     * [-]place a card by clicking again
     * [-]set up a game of speed
     * 2 25 14 : grab another player's hand
     * 
     * [-] how am i going to do the other actions? like shuffling a deck, flipping it over, etc?
     * 3 20 14: attempting the macro functions
     * 
     * NauseaBackground option
     * 4 15 14 thinking about slidy cards GameOption
     * 
     * 11 12 14 need a way to delete cards so i can reset the game without restarting it
     * 
     * look up how synchronized block works
     * actually i still have to find out what the bug was in the first place. that means i probably have to have jenn run the server for me.
     * so let's remove all this syncrhonize stuff i throew in.  so 11/28/14 removing synch blocks
     */
     
    public PrintWriter out;
    public BufferedReader in;
    public Socket onlysocket;
    
    public GameRules gr;
    
     
    public String getOwnExternalIP()
    {
        try{
        URL whatismyip = new URL("http://checkip.amazonaws.com");//"http://automation.whatismyip.com/n09230945.asp");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String ip = in.readLine(); //you get the IP as a String
        return ip;
        }catch(Exception e){e.printStackTrace();return "couldn't get IP";}
         
    }
    public static final ArrayList<BufferedImage> cardFaces = new ArrayList<BufferedImage>();
    public static final ArrayList<BufferedImage> backCardFaces = new ArrayList<BufferedImage>();
    private BufferedImage bi;
    private Graphics2D g;
    private Graphics2D g2;
     
    public Keyboard k = new Keyboard();
    public Mouse m = new Mouse();
     
    public JFrame frame;
     
    public static void createFile(String fileName, String whatToWrite)//creates new File fileName, populated with String whatToWrite. If you want new lines, you will have to use \r\n's in the whatToWrite String.
    {
    try{
    FileWriter fstream = new FileWriter(fileName);
    BufferedWriter writer = new BufferedWriter(fstream);
 
    System.out.println(whatToWrite);
    writer.write(whatToWrite + "\r\n");
 
    writer.close();
 
    }catch(Exception e){e.printStackTrace();};
    }
    
    public volatile boolean connected = false;//needed the volatile!
    
    public boolean connect(String ip, int port)
    {
    	onlysocket = null;
    	try{
        onlysocket = new Socket();
        onlysocket.connect(new InetSocketAddress(ip,port), 3500);
        out = new PrintWriter(onlysocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(onlysocket.getInputStream()));
        
    	}catch(Exception e){return false;};
    	
    	if (onlysocket != null)
    	{
    		connected = true;
    	}
    	
    	return (onlysocket != null);
    }
  
 
    
    public Panicards() throws Exception
    {
    	bot = new Robot();
    	  
         
    }
    public static void defineCardFaceImages()
    {
        try {
             
            {
                BufferedImage pic  = ImageIO.read(new File("cards.png"));
             
             
            for (int r = 0; r < 4; r++)
            {
                for (int c = 0; c < 13; c++)
                {
                    BufferedImage clip = pic.getSubimage(c*73+1,r*98+1,70+1,95+1);
                     
                    //clubs, spades, hearts, diamonds
                    cardFaces.add(clip);
                }
            }
            }
             
            BufferedImage originalImage = ImageIO.read(new File("skullback.png"));
            int ow = originalImage.getWidth(), oh = originalImage.getHeight();
            //it doesn't matter what size the image originally was; i am resizing it now.
             
             
             
            int newWidth = Card.width, newHeight = Card.height;
            BufferedImage resized = new BufferedImage(newWidth, newHeight, originalImage.getType());
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
            g2.setPaint(new Color(1f,0.1f,0f,0.32f));
            g2.fillRect(0, 0, newWidth, newHeight);
             
            backCardFaces.add(resized);
             
             
            
        } catch (Exception ex) {
            ex.printStackTrace();System.exit(0);
        }
    }
    public void mouseMoved(MouseEvent e)
    {
        m.setLocation(e.getX(),e.getY());
         
         
    }
     
    public void mouseDragged(MouseEvent e)
    {
         
        m.setLocation(e.getX(),e.getY());
        
        
    }
    public void mousePressed(MouseEvent e)
    {
        int button = e.getButton();
        m.keyPress(button);
        m.setLocation(e.getX(),e.getY());
         
    }
    public void mouseReleased(MouseEvent e)
    {
        m.keyRelease(e.getButton());
    }
    public void mouseClicked(MouseEvent e)
    {
         
    }
    public void mouseEntered(MouseEvent e)
    {
         
    }
    public void mouseExited(MouseEvent e)
    {
         
    }
    public void keyPressed(KeyEvent e)
    {
        k.keyPress(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
    }
    public void keyReleased(KeyEvent e)
    {
        k.keyRelease(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
    }
    public void keyTyped(KeyEvent e)
    {
         
    }
     
     
     
     
    private void setFrameHeight(int value)
    {
        frame.setSize((int)(widthToHeight*value),value);
         
        this.setSize(new Dimension(frame.getWidth()-16, frame.getHeight()-38));
    }
     
    private void onWindowResized()
    {
        fixWindowRatio();
        fwidth = frame.getSize().width; fheight = frame.getSize().height;
        
        this.setSize(new Dimension(fwidth-16, fheight-38));
        bi = new BufferedImage(getSize().width,getSize().height, 5);
        
        
         
        rh = getSize().height/normalHeight;//ratioheight scalar
        rw = getSize().width/normalWidth;//ratiowidth scalar
 
        
        if (gr.get(GameRules.BACKGROUND_NAUSEA))
        {
        	double by = (int)(getSize().height/80);
        	backgroundNauseaLoopY = by;
        	backgroundNausea = new BufferedImage(getSize().width,getSize().height + (int)by, 5);
        	
        	Graphics2D d = backgroundNausea.createGraphics();
        	
        	for (int r = 0; r < 90; r++)
        	{
        		/*
        		 * (new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
    				(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
        		 */
        		if (r%2==0)
        			d.setPaint(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
        		else
        			d.setPaint(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
        		
        		double y = backgroundNauseaLoopY * r;
        		
        		
        		d.fillRect(0,(int)y,backgroundNausea.getWidth(),(int)(backgroundNausea.getHeight()/80));
        	}
        	
        	
        }
        
        rotate = new Rotate(getSize().width, getSize().height, user().rotation);
        
        
        g = bi.createGraphics();
        g2 = (Graphics2D)(this.getGraphics());
         
    }
    private Rotate rotate = null;
     
    private void fixWindowRatio()
    {
         
         
         
        boolean widthChanged = (fwidth != frame.getSize().width);
        boolean heightChanged = (fheight != frame.getSize().height);
         
        if (!(widthChanged && heightChanged))
        {
            if (widthChanged)
            {
                frame.setSize(new Dimension(frame.getSize().width, (int)(frame.getSize().width / widthToHeight)));
                 
            }
            else if (heightChanged)
            {
                frame.setSize(new Dimension((int)(frame.getSize().height*widthToHeight), frame.getSize().height));  
            }
        }
        else//both changed -- go with height
        {
             
            //frame.setSize(new Dimension(frame.getSize().width, (int)(frame.getSize().width / widthToHeight)));
            frame.setSize(new Dimension((int)(frame.getSize().height*widthToHeight), frame.getSize().height));  
        }
         
         
         
    }
     
     
    public int fwidth, fheight;
     
    public double normalWidth = 800, normalHeight = 600;
     
    long CTmain, LTpaint, LTwindowCheck, LTkeyPress;
    //public double widthToHeight, maximizedX, maximizedY;
    //public double maximizedAppletX, maximizedAppletY;
     
    public double rw, rh;
     
    public CardGame cg = new CardGame();
     
     
     
     
    public int user = -1;
    public TreeMap<Integer, Player> players = new TreeMap<Integer, Player>();
     
    public double widthToHeight;
    
    private ClientGUI cgui;
     
    public void begin()
    {
         
    	cgui = new ClientGUI(this);
    	
    	while(!connected);//pause code until the client connects to the server
    	
        /////////////////////////////////////////////// 
        frame = new JFrame("Panicards by Steven");
         
        //frame.setSize(800,600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         
         
         
        if (frame.getLocation().y < 0)
            frame.setLocation(frame.getLocation().x, 0);
         
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
         
        
        frame.add(this);//this is a JApplet
        frame.setVisible(true);
        
        this.setFocusable(true);
         
        widthToHeight = normalWidth/normalHeight;
         
        setFrameHeight(600);
         
        rh = getSize().height/normalHeight;//ratioheight scalar, used for scaling the image on window resize
        rw = getSize().width/normalWidth;//ratiowidth scalar
        
        
 
         
        bi = new BufferedImage(getSize().width,getSize().height, 5);
        g = bi.createGraphics();
        g2 = (Graphics2D)(this.getGraphics());
         
        gr = new GameRules(); //will be defined by the server next
        
        new Thread(receivedWriter()).start();//starts loops for server-client talking
        
         try{Thread.sleep(3000);}catch(Exception e){};//allows loading screen time to get info from server
         
        CTmain = LTpaint = LTkeyPress = LTwindowCheck = System.currentTimeMillis();
         
        cgui.setVisible(false);
        
        
        rotate = new Rotate(getSize().width, getSize().height, user().rotation);//for flipping the screen around
         
         
        while(true)
        {
            try{Thread.sleep(1);}catch(Exception e){};//so the game doesn't use max CPU
            CTmain = System.currentTimeMillis();
            if (CTmain-LTwindowCheck > 500)
            {
                LTwindowCheck = CTmain;
                 
                if (fwidth != frame.getSize().width || fheight != frame.getSize().height)
                    onWindowResized();
            }
             
            if (CTmain - LTkeyPress > 32)
            {
                LTkeyPress = CTmain;
                this.handleKeyPresses();
                this.handleMousePresses();
                this.sendUpdatedPlayer();
            }
             
            if (CTmain - LTpaint > 32)
            {
            	 backgroundNauseaOffsetY += ((CTmain-LTpaint)/32)*1.5;
             	if (backgroundNauseaOffsetY > (backgroundNauseaLoopY*2))
             		backgroundNauseaOffsetY = 0;
            	
                LTpaint = CTmain;
                this.paint();
               
            }
             
             
             
             
             
             
        }
         
         
    }
    
    
    
     
    private void sendUpdatedPlayer()
    {
        out.println(user().toString());
    }
     
     
    private BufferedImage backgroundNausea = null;//must be redefined whenever the window is resized
    private double backgroundNauseaOffsetY;
    private double backgroundNauseaLoopY;
    
    public void paint()
    {
    	
    	if (gr.get(GameRules.BACKGROUND_NAUSEA) == false)
    	{
        g.setPaint(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f)); //background brown!
    	g.fillRect(0,0,getSize().width,getSize().height);
    	}
    	else
    	{
    		g.drawImage(backgroundNausea, null, 0, (int)backgroundNauseaOffsetY - (int)backgroundNauseaLoopY*2);
    		
    	}
    	
    	if (gr.get(GameRules.SEPARATE_SIDES))//draw the side-lines. -- mark where the player's "sides" are
    	{
    		g.setPaint(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));//ice cream sundae yellow
    		g.draw(new Line2D.Double(0, 150*rh, 800*rw, 150*rh));//top line
    		
    		g.draw(new Line2D.Double(0, 450*rh, 800*rw, 450*rh));//bottom line
    		
    		
    	}
    	
    	
        
        
        cg.draw(g, rw, rh, rotate, gr);
         
        if (gr.get(GameRules.SLIDY_CARDS))
        for (int i = 0; i < user().slidyCards.size(); i++)
        {
        	Card cc = user().slidyCards.get(i);
        
        	cc.velocitate();

    		//bounds collide check
    		if (cc.x + Card.width > 800)//right
    		{
    			cc.x = 800 - Card.width-1;
    			cc.dx*=-1;
    		}
    		if (cc.x < 0)
    		{
    			cc.dx*=-1;
    			cc.x = 1;
    		}
    			
    		if (cc.y + Card.height > 600)
    		{
    			cc.dy*=-1;
    			cc.y = 600 - Card.height-1;
    		}
    		if (cc.y < 0)
    		{
    			cc.dy*=-1;
    			cc.y = 1;
    		}
    			
    		cc.setCardPosition(cc.x, cc.y);
    		cc.draw(g, rw, rh, 1, rotate, gr);
    		
    		
    		if (cc.moving() == false)
    		{
    			cc.collisionRect.defineDrawableShape(rw,rh,rotate);
    			this.placeCard(user, cc.x, cc.y, cc);
    			user().slidyCards.remove(i);
    			continue;
    		}
    	
        }//end for loop
        
        for (Integer i : players.keySet())//draw the players hands and their slidy-cards arrays
        {
            Player p = players.get(i);
            if (p.ID == user)
            {
            	//draw outline box of own player's hand, instead and don't worry about all the other stuff.
            	
            	
            		
            	
            	int[] xs = {user().mouseX+25,user().mouseX-25,user().mouseX-25,user().mouseX+25};
        		int[] ys = {user().mouseY-25,user().mouseY-25,user().mouseY+25,user().mouseY+25};
        		
        		int otherRotation = user().rotation;
        		if (rotate.rotate == 1)
        		{
        			if (otherRotation == 1)
        			{
        				otherRotation = 0;
        			}
        			else
        				otherRotation = 1;
        			
        		}
        		
            	PolyShape box = new PolyShape(xs, ys);
            	box.defineDrawableShape(rw, rh, (new Rotate(getSize().width, getSize().height, otherRotation)));
        		g.setPaint(user().color);
            	//just outline
        		g.draw(box.drawableShape);
            	
                continue;
            }
           
            {
            if (p.carryingCard != null)
            {
            	p.carryingCard.draw(g, rw, rh,1, rotate, gr);
            }
            for (int i2 = 0; i2 < p.slidyCards.size(); i2++)
            {
            	p.slidyCards.get(i2).draw(g,rw,rh,1, rotate, gr);
            }
            g.setPaint(p.color);
            
            int[] xs = {p.mouseX+25,p.mouseX-25,p.mouseX-25,p.mouseX+25};
    		int[] ys = {p.mouseY-25,p.mouseY-25,p.mouseY+25,p.mouseY+25};
    		
    		PolyShape box = new PolyShape(xs,ys);
    		
    		/*This is where my Problem should be with the flip rotate backwards thing.*/
    		
    		
    		int otherRotation = p.rotation;
    		if (rotate.rotate == 1)
    		{
    			if (otherRotation == 1)
    			{
    				otherRotation = 0;
    			}
    			else
    				otherRotation = 1;
    			
    		}
    		box.defineDrawableShape(rw, rh, (new Rotate(getSize().width, getSize().height, otherRotation)));
    		
    		g.fill(box.drawableShape);
            }
        }
        
         
        if (mode == MODE_HOLDING_CARD)
        {
            currentCard.draw(g, rw, rh,1, rotate, gr);
            float n = (float)(Math.sqrt(Math.pow(currentCard.dx,2) + Math.pow(currentCard.dy,2)) / 50f);
			if (n > 1)
				n = 1;
	g.setPaint(new Color(0, 0.25f, 1f, n/2));
	g.fill(currentCard.collisionRect.drawableShape);
        }
        
        if (mode == MODE_ACTION)
        {
        	if (actionPrimary == Options.PRIMARY_DECK_SELECTED)
        	{
        		//draw a border around the selected deck
        		Card c = cg.decks.get(actionData).peekTopCard();
        		
        		g.setPaint(new Color(1f,1f,0f,0.69f));
        		g.fill(c.collisionRect.drawableShape);
        	}
        	
        	this.drawOptions();
        }
        
        this.drawNames();
        
        g2.drawImage(bi, null, 0, 0);
        
    }
    
   private void drawNames()
   {
	   
	   int ratio = (int)(24*rw);
   	g.setFont(new Font("DejaVu Sans", Font.BOLD, ratio));
   	
   	//draw user's username
   	int y = (int)((600-24)*rh);
   	g.setPaint(user().color);
   g.drawString(user().username, 0, y);
	   
   
   try{
	 //find index for other player
	   int index = -1;
   for (Integer i : players.keySet())
   {
	   Player p = players.get(i);
	   if (p.ID != user)//first valid player will be the one i'm displaying.
	   {
		 index = p.ID;
		 break;
	   }
	   
   }
   
   //now draw their username on the other side.
   String name = players.get(index).username;
   
   g.setPaint(players.get(index).color);
   int xPos = (int)((800-14*name.length())*rw);
   g.drawString(name, xPos, ratio);
   
   
   
   }catch(Exception e){};
   
   
   
	   
   }
    
    private void drawOptions()
    {
    	int ratio = (int)(24*rw);
    	
    	g.setFont(new Font("Times New Roman", Font.BOLD, ratio));
    	
    	
    	String[] options = null;
    	if (actionPrimary == Options.PRIMARY_DECK_SELECTED)
    	options = Options.DECK_SELECTED;
    	if (actionPrimary == Options.PRIMARY_SPACE_SELECTED)
    	options = Options.SPACE_SELECTED;
    	
    	int y = ratio;
    	
    	g.setPaint(Color.YELLOW);
    	if (actionPrimary == Options.PRIMARY_DECK_SELECTED)
    		g.drawString("Deck Options", 0, y);
    	if (actionPrimary == Options.PRIMARY_SPACE_SELECTED)
    		g.drawString("Game Options", 0, y);
    	
    	g.setPaint(Color.WHITE);
    	for (int i = 0; i < options.length; i++)
    	{
    		y+=ratio;
    		g.drawString(Options.controls[i],0,y);
    		g.drawString(options[i],ratio,y);
    	}
    }
    private int actionPrimary, actionSecondary, actionData;//actionPrimary specifies what type of thing the user is trying to do. actionSecondary is the user's input. actionData specifies "which deck"
    
    
    
     
     
    public void handleKeyPresses()
    {
        if (k.t("space"))//ACTION button 
        {
        	if (mode == MODE_ACTION)
        	{
        		mode = MODE_EMPTY_HAND;
        	}
        	else if (mode == MODE_EMPTY_HAND)
        	{
        		//see what the user selected
        		
        		Point p = m.getLocation();
        		
        		actionPrimary = -1;
        		
        		if (actionPrimary == -1)
        			
        		for (Integer I : cg.decks.keySet())
        		{
        			Deck d = cg.decks.get(I);
        		if (d.pointIntersectsTop(p))
        		{
        			actionPrimary = Options.PRIMARY_DECK_SELECTED;
        			actionData = I;
        			setMode(MODE_ACTION);
        			break;
        		}
        		}
        			
        		
        		if (actionPrimary == -1)//if the mouse wasn't over a deck
        		{
        			actionPrimary = Options.PRIMARY_SPACE_SELECTED;
        			actionData = -1;
        			setMode(MODE_ACTION);
        		}
        		
        		
        		
        	}
        	
        	
        }
        
        
        if (mode == MODE_ACTION)// for the user's action choice
        {
        	for (int i = 0; i < Options.controls.length; i++) //send it to the server
        	{
        		String s = Options.controls[i];
        		
        		if (k.f(s))
        		{
        			actionSecondary = i;
        			
        			//check to see if it was actually a valid choice
        			String[] o = null;
        			if (actionPrimary == Options.PRIMARY_SPACE_SELECTED)
        				o = Options.SPACE_SELECTED;
        			if (actionPrimary == Options.PRIMARY_DECK_SELECTED)
        				o = Options.DECK_SELECTED;
        			
        			if (i >= o.length)
        				break;
        			
        			out.println("action " + actionPrimary + " " + actionSecondary + " " + actionData);
        			
        			
        			
        			if (actionPrimary == Options.PRIMARY_SPACE_SELECTED)
                	{
        				
                		String choice = Options.SPACE_SELECTED[actionSecondary];
                		
                		if (choice.equalsIgnoreCase("Flip Board"))
                		{
                			if (user().rotation == 1)
                				user().rotation = 0;
                			else
                				user().rotation = 1;
                			
                			rotate = new Rotate(getSize().width, getSize().height, user().rotation);
                		}
                		if (choice.equalsIgnoreCase("Setup for Speed"))
                		{
                			//do nothing. wait for server's response, over in execute().
                			
                			
                			
                		}
                	}
        			
        			setMode(MODE_EMPTY_HAND);
        			break;
        		}
        		
        	}
        	
        	
        	
        	
        }
        
        
        
        
    	
    	
    	
    	
    	k.untype();
    }
     
    public void setMode(int mode)
    {
    	int previousMode = user().mode;
        user().mode = mode;
        this.mode = mode;
        if (mode == MODE_HAND_BEING_HELD)
        {
        	if (previousMode == MODE_HOLDING_CARD)//drop the card you're holding
        	{
        		Point p = m.getLocation();
        		this.placeCard(user, p.x, p.y, currentCard);
        	}
        }
        if (mode == MODE_HOLDING_CARD)
        {
             
        }
        if (mode == MODE_EMPTY_HAND)
        {
        	
        }
        if (mode == MODE_HOLDING_HAND)
        {
        	
        }
        if (mode == MODE_ACTION)
        {
        	
        }
    }
     
    public Card currentCard = null;
     
    int mode = MODE_EMPTY_HAND;
    public static final int MODE_EMPTY_HAND = 0, MODE_HOLDING_CARD = 1, MODE_HOLDING_HAND = 2, MODE_HAND_BEING_HELD = 3, MODE_ACTION = 4;
    
     private Robot bot;
     

    public void handleMousePresses()
    {
    	
    	if (user().mode == MODE_HOLDING_CARD && gr.get(GameRules.SLIDY_CARDS))
        {
        	//for sliding vector!
        	currentCard.addPointToMovement(m.getScaledLocation(rw, rh), rotate.rotate);
        }
    	
    	 if (mode == MODE_HAND_BEING_HELD)
         {
         	int ex = players.get(user().playerGrabbingMe).mouseX;
         	int ey = players.get(user().playerGrabbingMe).mouseY;
         	
         	Point frameLocation = this.getLocationOnScreen();
         	
         	int destX = frameLocation.x;
         	int destY = frameLocation.y;
         	
         	
         	if (rotate.rotate == Player.ROTATION_DEFAULT)
         	{
         	destX += ex*rw;
         	destY += ey*rh;
         	}
         	if (rotate.rotate == Player.ROTATION_180)//test if this actually works (need 2 computers)
         	{
         	destX += rotate.getRotatedX(ex)*rw;
         	destY += rotate.getRotatedY(ey)*rh;
         	}
         	
         	
         	bot.mouseMove(destX, destY);
         	//return;//don't let the user do anything about it
         }
    	
    	
        Point p = m.getLocation();
        int x = p.x; int y = p.y;
        if (user().rotation == 1)
        {
        	x = rotate.getRotatedX(x);
        	y = rotate.getRotatedY(y);
        }
        user().mouseX = m.getScaledLocation(rw, rh).x; user().mouseY = m.getScaledLocation(rw,rh).y;
         
        
       
        
        
        if (mode == MODE_HOLDING_CARD)
        {
            currentCard.setCardPosition((int)(x/rw) - Card.width/2, (int)(y/rh) - Card.height/2);
        }
         
         
        if (m.f(Mouse.LEFT_CLICK))
        {
            if (mode == MODE_EMPTY_HAND)
            {
            	
            //first check to see if it intersected another player's hand
            	boolean grabbedAHand = false;
            
            
            if (gr.get(GameRules.GRAB_OTHER_PLAYERS_HAND) == true)
            {
            	
            	
            	for (Integer i : players.keySet())
            	{
            		if (i == user)
            			continue;
            		Player play = players.get(i);
            		
            		//defining the other-player-mouse square
            		
            		int[] xs = {play.mouseX+25,play.mouseX-25,play.mouseX-25,play.mouseX+25};
            		int[] ys = {play.mouseY-25,play.mouseY-25,play.mouseY+25,play.mouseY+25};
            		
            		PolyShape box = new PolyShape(xs,ys);
            		
            		//Should work now. has yet to be actually tested.
            		int otherRotation = play.rotation;
            		if (rotate.rotate == 1)
            		{
            			if (otherRotation == 1)
            			{
            				otherRotation = 0;
            			}
            			else
            				otherRotation = 1;
            			
            		}
            		box.defineDrawableShape(rw, rh, (new Rotate(getSize().width, getSize().height, otherRotation)));
            		
            		if (box.drawableShape.contains(new Point(p.x, p.y)))
            		{
            			grabbedAHand = true;
            			user().otherPlayerGrabbed = i;
            			setMode(MODE_HOLDING_HAND);
            			//the mouse repositioning will be done on the client side.
            			out.println("grabbedhand " + user + " " + i);
            		}
            		
            		
            		
            	}
            	
            	
            }
            
            if (!grabbedAHand)//can't grab a card through a hand; hand is on top.
            {
            	
            for (Integer I : cg.decks.keySet())//check to see if it intersected a top-deck-card
            {
            	Deck d = cg.decks.get(I);
                if (d.pointIntersectsTop(p))
                {   
                    setMode(MODE_HOLDING_CARD);
                    currentCard = d.takeTopCard();
                    user().carryingCard = currentCard;
                    user().carryingCard.pickupX = user().carryingCard.x;
                    user().carryingCard.pickupY = user().carryingCard.y;
                    out.println("taketop " + user + " " + d.gameIndex);//@TODO: i want to change "index" to an ID. so each deck will have an ID.
                    if (d.cards.size() == 0)
                    {
                        cg.decks.remove(d.gameIndex);
                    }
                    break;
                }
                 
            }
            	
            
            }
                 
             
            }//end MODE_EMPTY_HAND
             
        }//end first left click
         
        if (m.t(Mouse.LEFT_CLICK))
        {
        	if (mode == MODE_HOLDING_HAND)
        	{
        		out.println("releasehand " + user + " " + user().otherPlayerGrabbed);
        		//(user + " let go of a hand");
        		setMode(MODE_EMPTY_HAND);
        	}
        	//else
        		//(user + " released nothing" + mode);
            if (mode == MODE_HOLDING_CARD)
            {
            	if (gr.get(GameRules.SLIDY_CARDS))
            	{
            		user().slidyCards.add(new Card(currentCard.toSocketString()));
            		int i = user().slidyCards.size()-1;
            		user().slidyCards.get(i).dx = currentCard.dx;
            		user().slidyCards.get(i).dy = currentCard.dy;
            		setMode(MODE_EMPTY_HAND);
            	}
            	else
            	{
                placeCard(user, x, y, currentCard);
                setMode(MODE_EMPTY_HAND);
            	}
            }//end MODE_HOLDING_CARD
        }
         
         
        if (m.f(Mouse.RIGHT_CLICK))
        {
            if (mode == MODE_HOLDING_CARD)
            currentCard.faceUp = !currentCard.faceUp;
            else if (mode == MODE_EMPTY_HAND)
            {
            	for (Integer I : cg.decks.keySet())
                {
                    Deck d = cg.decks.get(I);
                    if (d.pointIntersectsTop(p))
                    {   
                        d.cards.get(d.cards.size()-1).flip();
                        out.println("fliptop " + I + " " + d.cards.get(d.cards.size()-1).faceUp);
                    }
                }
            	
            }
        }
         
        m.untype();
         
    }
     
    public void placeCard(int who, int x, int y, Card c)
    {
        //see if the card lands on any decks
        boolean landedOnADeck = false;
        int index = -1;
        int landedIndex = -1;
        double shortestDistance = Double.MAX_VALUE;
        for (Integer I : cg.decks.keySet())
        {
        	Deck d = cg.decks.get(I);
            index = I;
            if (d.cardIntersectsDeck(c))
            {
                double distance = Math.sqrt(Math.pow(c.x - d.cards.get(d.cards.size()-1).x, 2) + Math.pow(c.y - d.cards.get(d.cards.size()-1).y, 2));
                if (distance < shortestDistance)
                {
                    shortestDistance = distance;
                    landedOnADeck = true;
                    landedIndex = index;
                }
            }   
        }
        
        
        
        //check to see if the land index is valid or not.
        if (gr.get(GameRules.GAME_OF_SPEED))
        		{
        	if (landedIndex == -1)
        	{//open space land
        		out.println("placecard " + user + " " + landedIndex + " " + c.toSocketString());
        		return;
        	}
        		
        	
        	if (c.x == c.pickupX && c.y == c.pickupY)
        	{//force it to go back, even if it didn't fit where it started.
        		out.println("placecard " + user + " " + landedIndex + " " + c.toSocketString());
        		return;
        	}
        	
        	
        	else if (
        			
        			(cg.decks.get(landedIndex).peekTopCard().faceUp == false && c.faceUp == false)
        			|| 
        			(cg.decks.get(landedIndex).peekTopCard().faceUp && cg.decks.get(landedIndex).peekTopCard().isCenterField() && c.isExactlyOneAwayFrom(cg.decks.get(landedIndex).peekTopCard())))
        {
        	//the deck is valid. send the card.
        	out.println("placecard " + user + " " + landedIndex + " " + c.toSocketString());
        	
        	
        	
        }
        else
        {//landed on an invalid location. return to original location.
        	if (cg.decks.get(landedIndex).peekTopCard().isCenterField())
        		user().mistakes++;
        	c.setCardPosition(c.pickupX, c.pickupY);
        	placeCard(who, c.x, c.y, c);
        	return;
        	
        }
        		}
        else
        	out.println("placecard " + user + " " + landedIndex + " " + c.toSocketString());
        
    }
     
    public void fillRect(int x, int y, int w, int h)
    {
        //g.fillRect(x,getSize().height-y,w,h);
        fillScaledRect(x, y, w, h);
    }
    public void fillScaledRect(int x, int y, int w, int h)
    {
         
        g.fillRect((int)((x)*rw),(int)((y)*rh),(int)(rw*w),(int)(rh*h));
    }
 
     
    public void execute(String s)//Command from server, associated with "private Runnable receivedWriter()"
    {
         
        String[] z = s.split(" ");
         
        if (z[0].equals("new"))
        {
             
            if (z[1].equals("player"))
            {
            	Player p = new Player(s);
				if (p.ID == user)
					return;
				players.put(p.ID,p);
				return;
            }
            if (z[1].equals("user"))
            {
                if (user == -1)
                {
                Player p = new Player(s.replace("user", "player"));
                p.username = myUsername;
                players.put(p.ID, p);
                user = p.ID;
                }
                else // just update the position variables. // i think i used this for teleporting in Platipus
                {
                    //Player p = new Player(s.replace("user", "player")); 
                    //user().x = p.x; user().y = p.y;
                }
                return;
            }
            if (z[1].equals("deck"))
            {
                Deck d = new Deck(s);
                cg.addDeck(d);
                return;
            }
            if (z[1].equals("gamerules"))
            {
            	gr = new GameRules(s);
            	return;
            }
             
            return;
        }//end z[0].equals("new")
        
        if (z[0].equals("removeDeck"))
        {
        	final int deckNumber = pint(z[1]);
        	cg.decks.remove(deckNumber);
        	return;
        }
         
        if (z[0].equals("taketop"))
        {
            int playerID = pint(z[1]);
            //out.println("taketop " + user + " " + index);
            if (playerID != user)
            {
            players.get(playerID).carryingCard = cg.decks.get(pint(z[2])).takeTopCard();
            
            if (cg.decks.get(pint(z[2])).size() == 0)
            {
                cg.decks.remove(pint(z[2]));
            }
            }
             return;
        }
         
        if (z[0].equals("placecard"))//needs to be rewritten to be deck-ID friendly
        {
            int userID = pint(z[1]);
            int landedIndex = pint(z[2]);
            Card c = new Card(z[3]);
            
            if (landedIndex != -1)//it landed on [landedIndex] deck
            {
            	/*if (gr.get(GameRules.GAME_OF_SPEED))
                {
                	Deck temp = cg.decks.get(landedIndex);
                	if (temp.peekTopCard().faceUp && temp.peekTopCard().isCenterField() && c.isExactlyOneAwayFrom(temp.peekTopCard()))
                	{
                		cg.decks.get(landedIndex).add(c);
                	}
                	else
                	{
                		//server will put it back.
                		cg.decks.get(landedIndex).add(c);
                	}
                	
                }
            	else*/
            	{
            		//add it to the deck.
            		
                cg.decks.get(landedIndex).add(c);
            	}
            }
            else //it landed in open space
            {
            	//do nothing -- the server will send a deck constructor.
                //Deck d = new Deck(c);//i need to >not< use this constructor, because it doesn't allocate an ID for the deck. (must get ID from server) 
                //cg.addDeck(d);
            }
            return;
        }//
             
        
        
        
        if (z[0].equals("grabbedhand"))
        {
        	setMode(Panicards.MODE_HAND_BEING_HELD);
        	//drops the card they're holding too
        	user().playerGrabbingMe = pint(z[1]);
        	
        }
        
        if (z[0].equals("fliptop"))
        {
            //out.println("fliptop " + i + " " + d.cards.get(d.cards.size()-1).faceUp);
        	Deck d = cg.decks.get(pint(z[1]));
            d.cards.get(d.cards.size()-1).faceUp = Boolean.parseBoolean(z[2]);
        }
        
        if (z[0].equals("releasehand"))
        {
        	setMode(Panicards.MODE_EMPTY_HAND);
        }
    }
     
    private static int pint(String s)
    {
        return Integer.parseInt(s);
    }
    public Player user()
    {
        return players.get(user);
    }
    String myUsername = "";
     
    class ClientGUI
    {
    private final Panicards pani;
    private JFrame frame;
    private JTextArea console;
    private JScrollPane jsp;
    private final JTextField IPInput, nameInput;
 
    public void setVisible(boolean b)
    {
    	frame.setVisible(b);
    }
    public ClientGUI(final Panicards pani)
    {
    this.pani = pani;
    frame = new JFrame("~Panicards Client Menu~");
     
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800,600);
    frame.setLocation(200,50);
    frame.setResizable(false);
    frame.setLayout(new FlowLayout());
    
    console = new JTextArea();
    console.setFocusable(false);
    
    int y = 600;
    int x = 800;
    jsp = new JScrollPane(console);
    jsp.setPreferredSize(new Dimension(x-14, y-100-21));
    frame.add(jsp);
    
    IPInput = new JTextField();
    IPInput.setBackground(Color.BLACK);
    IPInput.setForeground(Color.GRAY);
    IPInput.setFont(new Font("DejaVu Sans", Font.BOLD, 26));
    IPInput.setPreferredSize(new Dimension((int)((x-14)*3/4) - 1, 100-21));
    IPInput.setText("localhost");
    
    nameInput = new JTextField();
    nameInput.setBackground(new Color(0.025f,0.025f,0.025f).darker());
    nameInput.setForeground(new Color(0.4862f, 0.1882f, 0.1882f).brighter());
    nameInput.setFont(new Font("DejaVu Sans", Font.BOLD, 26));
    nameInput.setPreferredSize(new Dimension((int)((x-14)*1/4) - 1, 100-21));
    nameInput.setText("Steven");
    
	nameInput.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
				IPInput.requestFocus();
				IPInput.requestFocusInWindow();	
				try{
					Robot r = new Robot();
					r.keyPress(KeyEvent.VK_ENTER);
					Thread.sleep(100);
					r.keyRelease(KeyEvent.VK_ENTER);
				}catch(Exception e2) {
					e2.printStackTrace();
				};
				
			}
			});
    
    IPInput.addActionListener(
    		new ActionListener()
    		{
    			public void actionPerformed(ActionEvent e)//on the Enter keypress
    			{
    			String text = e.getActionCommand().trim();
    			if (text.equals(""))
    				return;
    			
				IPInput.setText("attempting to connect...");
				
    			if (pani.connect(text, 8123))
    			{
    				console.append("Successfully connected to " + text + "\r\n");
    				myUsername = nameInput.getText().replace(' ', '_');//to be put in user() object later
    			}
    			else
    				console.append("Couldn't connect to " + text + ".  Make sure\r\n    you use the server's IP\r\n");
    			
    			IPInput.setText("");
    			
    			}
    			
    			
    		}
    				);
    frame.add(nameInput);
    frame.add(IPInput);
    
    
    //chocolate sundae color scheme from notepad++
    //43,15,1 background
    //188,187,128 text
     
    console.setBackground(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
    console.setForeground(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
    console.setFont(new Font("Times New Roman", Font.BOLD, 40));
    
    
    
    
    
    
    console.append("Enter an IP address to connect to on port 8123\r\n   and your name!\r\n");
    
    frame.setVisible(true);
	IPInput.requestFocus();
    IPInput.requestFocusInWindow();
    }
 
    public void print(String s)
    {
    console.append(s);
    }
    public void println(String s)
    {
    this.print(s);
    this.print("\r\n");
    }
 
 
    }
    
    public static void checkIfImagesExist()
    {
    	try{
    	if (!new File("skullback.png").isFile())
    	{
    		BufferedImage skullback = ImageIO.read(new URL("http://i.imgur.com/IG7iQEq.png"));
    		saveImageToFile("skullback.png", skullback);
    		System.out.println("Creating skullback.png");
    	}
    	if (!new File("cards.png").isFile())
    	{
    		BufferedImage cards = ImageIO.read(new URL("http://i.imgur.com/CuIIay7.png"));
    		saveImageToFile("cards.png", cards);
    		System.out.println("Creating cards.png");
    	}
    	}catch(Exception e){e.printStackTrace();};
    	
    }
    
    public static void saveImageToFile(String name, BufferedImage bi)
    {
    	try{
    	File f = new File(name);
    	ImageIO.write(bi, "png", f);
    	}catch(Exception e){e.printStackTrace();};
    }
     
    public static void main(String[]args)
    {
       
    	
        try{
        checkIfImagesExist();
        defineCardFaceImages();
        Panicards b = new Panicards();
        b.begin();
        }catch(Exception e){
        String s = e.getMessage();
        createFile("errorfile.txt",s);
        	e.printStackTrace();System.exit(-1);}
 
         
    }
     
    private Runnable receivedWriter()//receives Strings from server, and passes them into public void execute(String command)
    {
    return new Runnable() {
    public void run() {
    try{
    String received;
    while(true)
    {
    received = in.readLine();
    execute(received);
    }
    }catch(Exception e){System.out.println("Lost connection to the server :: ");e.printStackTrace();System.exit(1);};
 
 
    }};
    }
 
     
     
     
}

