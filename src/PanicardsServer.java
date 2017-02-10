/*
 * Panicards
 * Steven Ventura
*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
 






import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
 
 
 
/*
 * 2/27/14: 12:45pm: i think the bug with click grabbing is with clientArrayIndex
 * yes it was. now, my only problem with grabbing is the 1. Repositioning and 2. collisionDetecting (resize is wrong)
 * 11/27/14 i think i fixed a concurrentmodificationexception on broadcast(str)
 */
public class PanicardsServer
{
    public ServerGUI gui;
    public TreeMap<Integer, Player> players;
     
    public ArrayList<clientdata> clients = new ArrayList<clientdata>();
    public ServerSocket serversocket;
     
    public CardGame cg;
     
    public static final int normalWidth = 800, normalHeight = 600;
    public ArrayList<Player> currentPlayers = new ArrayList<Player>();
    
    public GameRules gr = new GameRules();
     
    public String getOwnExternalIP()
    {
        try{
        URL whatismyip = new URL("http://checkip.amazonaws.com");//"http://automation.whatismyip.com/n09230945.asp");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String ip = in.readLine(); //you get the IP as a String
        return ip;
        }catch(Exception e){e.printStackTrace();return "couldn't get IP";}
         
    }
    
    int deckID = -1;
    private void addDeck(Deck d)
    {
    	deckID++;
    	d.gameIndex = deckID;
    	cg.addDeck(d);
    }
    private void setupWarGame()
    {
        cg = new CardGame();
        cg.players.addAll(currentPlayers);
         
        int centerX = (int)(normalWidth/2), centerY = (int)(normalHeight/2);
        int cardWidth = Card.width;
        int cardHeight = Card.height;
         
        Deck maindeck = new Deck(true, "server");
         
        maindeck.shuffle();
         
         
        Deck myCards = maindeck.takeTop(26);
        Deck yourCards = maindeck.takeTop(26);
         
        myCards.setDeckFaceUp(false);
        yourCards.setDeckFaceUp(false);
         
        myCards.setDeckPosition(centerX - (int)(cardWidth*1.5), centerY - cardHeight/2 + cardHeight*2);
         
        yourCards.setDeckPosition(centerX + (int)(cardWidth*1.5), centerY - cardHeight/2 - cardHeight*2);
         
        addDeck(myCards);
        addDeck(yourCards);
    }
     
    private void setupRandom()
    {
    	cg = new CardGame();
    	cg.players.addAll(currentPlayers);
    	Deck maindeck = new Deck(true,"server");
    	maindeck.shuffle();
    	for (int r = 0; r < 4; r++)
    	{
    		for (int c = 0; c < 13; c++)
    		{
    			Deck k = maindeck.takeTop(1);
    			k.setDeckFaceUp(true);
    			k.setDeckPosition(Card.width*c, Card.height*r);
    			addDeck(k);
    		}
    	}
    	
    }
    
    private void setupBSGame(int players)
    {
    	cg = new CardGame();
    	cg.players.addAll(currentPlayers);
    	int centerX = (int)(normalWidth/2), centerY = (int)(normalHeight/2);
        int cardWidth = Card.width;
        int cardHeight = Card.height;
    	
        Deck maindeck = new Deck(true,"server");
        
        for (int i = 0; i < 7*7; i++)
        	maindeck.shuffle();
        
        for (int i = 0; i < players; i++)
        {
        	Deck hand = maindeck.takeTop((int)(52/players));
        	hand.setDeckFaceUp(false);
        	hand.setDeckPosition(centerX*2 - (i+1)*cardWidth*2, centerY);
        	addDeck(hand);
        	
        }
    	
    }
    private void setupSpeedGame()
    {
        cg = new CardGame();
        cg.players.addAll(currentPlayers);
         
        int centerX = (int)(normalWidth/2), centerY = (int)(normalHeight/2);
        int cardWidth = Card.width;
        int cardHeight = Card.height;
         
        Deck maindeck = new Deck(true,"server");
        Deck extraDeck = new Deck(true,"server");
        extraDeck.shuffle();
        extraDeck.setDeckFaceUp(false);
        
        for (int i = 0; i < 7; i++)
        	maindeck.shuffle();
         
        Deck leftDrawPile = maindeck.takeTop(5);
        leftDrawPile.setDeckFaceUp(false);
        leftDrawPile.addAll(extraDeck.takeTop(26));
        leftDrawPile.setDeckPosition(centerX - cardWidth*4, centerY-cardHeight/2);
        addDeck(leftDrawPile);
        Deck rightDrawPile = maindeck.takeTop(5);
        rightDrawPile.addAll(extraDeck.takeTop(26));
        rightDrawPile.setDeckFaceUp(false);
        rightDrawPile.setDeckPosition(centerX + cardWidth*3, centerY-cardHeight/2);
        
        
        
        addDeck(rightDrawPile);
        Deck firstLeftCard = maindeck.takeTop(1);
        firstLeftCard.setDeckFaceUp(true);
        firstLeftCard.setDeckPosition(centerX - (int)(cardWidth*1.5), centerY - cardHeight/2);
        addDeck(firstLeftCard);
        Deck firstRightCard = maindeck.takeTop(1);
        firstRightCard.setDeckFaceUp(true);
        firstRightCard.setDeckPosition(centerX + (int)(cardWidth*0.5), centerY - cardHeight/2);
        addDeck(firstRightCard);
         
         
         
        Deck personalDrawServer = maindeck.takeTop(15);   
        personalDrawServer.setDeckFaceUp(false);
        personalDrawServer.setDeckPosition(centerX + (int)(cardWidth*2.5), centerY - cardHeight/2 + cardHeight*2);
        addDeck(personalDrawServer);
        for (int i = 0; i < 5; i++)
        {
            Deck handCard = maindeck.takeTop(1);
            handCard.setDeckFaceUp(true);
            handCard.setDeckPosition(centerX - (int)(cardWidth*3) + i*(cardWidth + 3),centerY - cardHeight/2 + cardHeight*2);
            addDeck(handCard);
        }
         
         
         
        Deck personalDrawClient = maindeck.takeTop(15);
        personalDrawClient.setDeckFaceUp(false);
        personalDrawClient.setDeckPosition(centerX - (int)(cardWidth*3.5), centerY - cardHeight/2 - cardHeight*2);
        addDeck(personalDrawClient);
        for (int i = 0; i < 5; i++)
        {
            Deck handCard = maindeck.takeTop(1);
            handCard.setDeckFaceUp(true);
            handCard.setDeckPosition(centerX + (int)(cardWidth*2) - i*(cardWidth + 3), centerY - cardHeight/2 - cardHeight*2);
            addDeck(handCard);
        }
         
         
         
    }
    
    
    
    public void setupTestCard()
    {
    	cg = new CardGame();
        Deck testDeck = new Deck(true, "SERVER").takeTop(1);
        addDeck(testDeck);
    }
     
    public void begin()
    {
        try{
        players = new TreeMap<Integer, Player>();
        gui = new ServerGUI(this);
        gui.println(">Panicards server on " + getOwnExternalIP() + ":8123<");
         
        boolean grFileFound = gr.defineSelfFromFile();

        gui.println("GameRules:");
        if (!grFileFound)
        	gui.println("PLEASE RESTART SERVER (gamerules file)");
                for (int i = 0; i < GameRules.descriptions.length; i++)
                {
                	String on = "off";
                	if (gr.get(i))
                		on = "on";
                	gui.println("    " + GameRules.descriptions[i] + " :: " + on);
                }
                
                gui.println("----------");
                gui.println("Listening for connections on port 8123...");
                 
         
         
        // setupRandom();
        setupSpeedGame();
        //setupBSGame(3);
        //setupWarGame();
        //setupTestCard();
         
         
         
         
        serversocket = new ServerSocket(8123);
        new Thread(this.accept()).start();//accepts connections
         
        
        
        
        long CT = System.currentTimeMillis(), LT = System.currentTimeMillis();
        while(true)
        {
            CT = System.currentTimeMillis();
            if (CT - LT > 0)
            {
                LT = CT;
                 
                this.handleClientCommands();
                 
            }
        }
         
        }catch(Exception e){e.printStackTrace();};
    }
    private static int pint(String s)
    {
        return Integer.parseInt(s);
    }
     
    public void handleClientCommands()
    {
    	
    	
        for (int client = 0; client < clients.size(); client++)
        {
           
        	
        	
            Stack<String> commands = clients.get(client).commands;
             
            while(!commands.empty())
            {
                 
                String s = commands.pop();
                 
                if (s.startsWith("new"))
                {
                    String[] z = s.split(" ");
                    if (z[1].equals("player"))
                    {
                        Player p = new Player(s);
                        players.put(p.ID, p);
                        broadcast(p.toString());
                    }
                    continue;
                }
                if (s.startsWith("action"))
                {
                	//actionPrimary specifies what type of thing the user is trying to do. actionSecondary is the user's input. actionData specifies "which deck"
                	String[] z = s.split(" ");
                	int primary = pint(z[1]);
                	int secondary = pint(z[2]);
                	int data = pint(z[3]);
                	/*
                	 * primary is option TYPE
                	 * secondary is option CHOICE
                	 * data is more specific, such as ID of card selected.
                	 */
                	
                	if (primary == Options.PRIMARY_DECK_SELECTED)
                	{
                		Deck d = cg.decks.get(data);
                		String choice = Options.DECK_SELECTED[secondary];
                		
                		if (choice.equalsIgnoreCase("Straighten"))
                		{
                			Card top = d.peekTopCard();
                			for (Card c : d.cards)
                			{
                				c.setCardPosition(top.x, top.y);
                			}
                		}
                		if (choice.equalsIgnoreCase("Shuffle"))
                		{
                			for (int i = 0; i < 7; i++)
                			d.shuffle();
                		}
                		if (choice.equalsIgnoreCase("Flip"))
                		{
                			d.setDeckFaceUp(!d.peekTopCard().faceUp);
                		}
                		
                		//send the updated deck back
                		broadcast(d.toSocketString());
                		
                		
                		//delete the deck
                		if (choice.equalsIgnoreCase("Delete"))
                		{
                			cg.decks.remove(data);
                			broadcast("removeDeck " + data);
                		}
                		
                		
                		
                	}//end primary = deck selected
                	
                	if (primary == Options.PRIMARY_SPACE_SELECTED)
                	{
                		String choice = Options.SPACE_SELECTED[secondary];
                		if (choice.equalsIgnoreCase("Setup for Speed"))
                		{
                			//Delete all of the existing decks.
                			for (Integer I : cg.decks.keySet())
                			{
                				Deck d = cg.decks.get(I);
                				broadcast("removeDeck " + d.gameIndex);
                			}
                			
                			setupSpeedGame();
                			//send the new game back to all the clients.
                			for (Integer I : cg.decks.keySet())
                		    {
                		    	Deck d = cg.decks.get(I);
                		        broadcast(d.toSocketString());
                		    }
                			
                		}
                		
                		
                	}
                	
                	
                }
                if (s.startsWith("grabbedhand"))
                {
                	String[] z = s.split(" ");
                	
                	int other = pint(z[2]);
                	//drops the card they're holding too
                	clients.get(players.get(other).clientArrayIndex).out.println(s);
                	
                	
                	continue;
                }
                if (s.startsWith("releasehand"))
                {
                	//out.println("releasehand " + user + " " + user().otherPlayerGrabbed);
                	String[] z = s.split(" ");
                	int other = pint(z[2]);
                	
                	clients.get(players.get(other).clientArrayIndex).out.println(s);
                	

                }
                 
                if (s.startsWith("taketop"))
                {
                    String[] z = s.split(" ");
                    Card take = cg.decks.get(pint(z[2])).takeTopCard();
                    take.pickupX = take.x; take.pickupY = take.y;
                    players.get(pint(z[1])).carryingCard = take;
                    
                    if (cg.decks.get(pint(z[2])).size() == 0)
                    {
                        cg.decks.remove(pint(z[2]));
                    }
                    broadcast(s);
                    return;
                }
                if (s.startsWith("placecard"))
                {
                    String[] z = s.split(" ");
                     
                    int userID = pint(z[1]);
                    int landedIndex = pint(z[2]);
                     
                    Card c = new Card(z[3]);
                 
                    if (landedIndex != -1)//it landed on [landedIndex] deck
                    {
                    	
                        cg.decks.get(landedIndex).add(c);
                    	
                    }
                    else //it landed in open space, so create a new deck.
                    {
                        Deck d = new Deck(c);
                        addDeck(d);
                        broadcast(d.toSocketString());
                    }
                   
                    broadcast(s);
                    return;
                }
                if (s.startsWith("fliptop"))
                {
                	String[] z = s.split(" ");
                	Deck d = cg.decks.get(pint(z[1]));
                        d.cards.get(d.cards.size()-1).faceUp = Boolean.parseBoolean(z[2]);
                        broadcast(s);
                }
                 
                 
                 
            }
             
             
        }
         
         
         
         
    }
     
     
     
     
     
    public static void main(String[]args)
    {
    try{
        PanicardsServer p;
         
    p = new PanicardsServer();
    p.begin();
    }catch(Exception e){e.printStackTrace();};
    }
     
     
     
     
     
     
     
     
    public Runnable accept()
    {
    return new Runnable() { 
    public void run() {
    try{
    int numConnections = 0;
    while(true)
    {
    clientdata temp = new clientdata(serversocket.accept());//has a Socket parameter
 
    numConnections++;//not necessarily unique connections; this value could still increase if the same user leaves and joins again
 
    clients.add(temp);
 
    int newPlayerID = players.size()+1;
    temp.player.ID = newPlayerID;
    temp.player.clientArrayIndex = clients.size()-1;
    System.out.println(temp.player.clientArrayIndex);
    players.put(temp.player.ID,temp.player);
     
    temp.out.println(temp.player.toString().replace("player","user"));
    
    temp.out.println(gr.toSocketString());
     
 
    //send map here
 
     
    for (Integer I : cg.decks.keySet())
    {
    	Deck d = cg.decks.get(I);
        temp.out.println(d.toSocketString());
    }
     
     
     
    for (Integer i : players.keySet())
        broadcast(players.get(i).toString());
 
    }
    }catch(Exception e){e.printStackTrace();}; } };
    }
     
     
    public void broadcast(String command)
    {
    	try{
    	for (int i = 0; i < clients.size(); i++)
    	{
    		clientdata c = clients.get(i);
            c.out.println(command);
    	}
    	}catch(Exception e){System.out.println("Error on broadcast -- probably concurrent");};
    }
     
     
     
    class clientdata//the Player object is created on both the Client and the Server, but the clientdata object is only created on the server.
    {
    private Socket s;
    private PrintWriter out;
    public BufferedReader in;
    public Stack<String> commands;//holds all of the data sent from the user to the server!
 
    public Player player;
 
    public clientdata(Socket s)
    {
    try{
    this.s=s;
    player = new Player(this.s.getRemoteSocketAddress().toString(), false);//player = new Player();
    gui.println(player.IP + " has connected.");
    this.s=s;
    commands = new Stack<String>();
    out = new PrintWriter(s.getOutputStream(),true); in = new BufferedReader(new InputStreamReader(s.getInputStream())) ;
    new Thread(receivedWriter()).start();
 
 
 
    }catch(Exception e){e.printStackTrace();}
    }
 
    public void write(String write)
    {
    this.out.println(write);
    }
 
    private Runnable receivedWriter()// 
    {
    return new Runnable() {
    public void run() {
    try{
    String received;
    while(true)
    {
    received = in.readLine();
    commands.add(received);
    }
    }catch(Exception e){gui.println(""+player.IP+" lost connection.");e.printStackTrace();};
 
 
    }};
    }
 
    }
 
    class ServerGUI
    {
    private PanicardsServer bs;
    private JFrame frame;
    private JTextArea console;
    private JScrollPane jsp;
    private final JTextField jtf;
 
    public ServerGUI(PanicardsServer bs)
    {
    this.bs=bs;
    frame = new JFrame("PanicardsServer by Steven");
     
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800,600);
    frame.setResizable(false);
    frame.setLayout(new FlowLayout());
    
    console = new JTextArea();
    console.setEditable(false);
    
    jsp = new JScrollPane(console);
    jsp.setPreferredSize(new Dimension(800-14, 500-21));
    frame.add(jsp);
    
    jtf = new JTextField();
    jtf.setBackground(new Color(0.025f,0.025f,0.025f).darker());
    jtf.setForeground(new Color(0.4862f, 0.1882f, 0.1882f).brighter());
    jtf.setFont(new Font("DejaVu Sans", Font.BOLD, 26));
    jtf.setPreferredSize(new Dimension(800-14, 100-21));
    jtf.setText("/help");
    
    
    jtf.addActionListener(
    		new ActionListener()
    		{
    			public void actionPerformed(ActionEvent e)//on the Enter keypress
    			{
    			
    				jtf.setText("");
    			}
    			
    			
    		}
    				);
    frame.add(jtf);
 
    //chocolate sundae color scheme from notepad++
    //43,15,1 background
    //188,187,128 text
     
    console.setBackground(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
    console.setForeground(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
    console.setFont(new Font("Times New Roman", Font.BOLD, 40));
    frame.setVisible(true);
 
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
     
     
     
     
}

