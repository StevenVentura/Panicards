import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.TreeMap;
 
public class CardGame
{
    public ArrayList<Player> players;
    public TreeMap<Integer, Deck> decks;//public ArrayList<Deck> decks;
   
    public CardGame()
    {
        decks = new TreeMap<Integer, Deck>();
        players = new ArrayList<Player>();
    }
     
     
    public void addDeck(Deck d)
    {
        decks.put(d.gameIndex,d);
    }
     
    public void draw(Graphics2D g,double rw, double rh, Rotate rotate, GameRules gr)
    {
    	try{
        for (Integer I : decks.keySet())
        {
        	Deck d = decks.get(I);
            d.draw(g,rw,rh, rotate, gr);
        }
    	}catch(ConcurrentModificationException e){};
    }
     
     
     
     
     
     
}

