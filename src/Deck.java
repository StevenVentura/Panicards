import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
 
 
public class Deck
{
    public int owner;
    public ArrayList<Card> cards;
    public int gameIndex=-1; // assigned by the server on creation of the deck object.
     
    public void draw(Graphics2D g,double rw, double rh, Rotate rotate, GameRules gr)
    {
         
        for (int i = 0; i < cards.size(); i++)
        {
            cards.get(i).draw(g, rw, rh, ((float)(i)+1f)/cards.size(), rotate, gr);
        }
         
    }
     
    public boolean pointIntersectsTop(Point p)
    {
        return cards.get(cards.size()-1).pointIntersectsCard(p.x, p.y);
    }
     public void addAll(Deck d)
     {
    	 while(d.cards.size() > 0)
    	 {
    		 this.cards.add(d.takeTopCard());
    	 }
     }
    public boolean polyShapeIntersectsDeck(PolyShape s)
    {
        return true;
    }
     
    public Deck()
    {
        cards = new ArrayList<Card>();
    }
    public void add(Card c)
    {
        cards.add(c);
    }
     
    public boolean cardIntersectsDeck(Card c)
    {
        return (cards.get(cards.size()-1).collisionRect.collisionShape.intersects(c.collisionRect.collisionShape.getBounds2D()));
             
    }
    public Deck takeTop(int amount)//the "top" is the end of the arraylist.
    {
        Deck top = new Deck();
        for (int i = 0; i < amount; i++)
        {
            top.add(cards.get(cards.size()-1));
            cards.remove(cards.get(cards.size()-1));
        }
        return top;
    }
    public Card takeTopCard()
    {
        return cards.remove(cards.size()-1);
    }
    public Card peekTopCard()
    {
    	return cards.get(cards.size()-1);
    }
    public int size()
    {
        return cards.size();
    }
    public Deck(boolean full)
    {
        cards = new ArrayList<Card>();
        if (full)
        for (int value = 0; value < 13; value++)
        {
                for (int suit = 0; suit < 4; suit++)
                cards.add(new Card(value,suit,0,0));
        }
    }
    public Deck(boolean full, String SERVER)
    {
        cards = new ArrayList<Card>();
        if (full)
        for (int value = 0; value < 13; value++)
        {
                for (int suit = 0; suit < 4; suit++)
                cards.add(new Card(value,suit,0,0,SERVER));
        }
    }
    public void setDeckFaceUp(boolean b)
    {
        for (Card c : cards)
        {
            c.faceUp = b;
        }
    }
    public void setOwner(int owner)
    {
        for (Card c : cards)
            c.setOwner(owner);
    }
     
    public void shuffle()
    {
        for (int n = 0; n < 3; n++)
        {
            for (int i = 0; i < cards.size(); i++)
            {
                int destination = (int)(Math.random()*cards.size());
                Card copy = cards.get(destination);
                cards.add(destination, cards.get(i));
                cards.remove(copy);
                cards.remove(i);
                cards.add(i, copy);
            }
        }
    }
     
    public void setDeckPosition(int x, int y)
    {
        for (Card c : cards)
        {
            c.setCardPosition(x, y);
        }
    }
    public Deck(ArrayList<Card> cards)
    {
        this.cards = cards;
    }
    public Deck(Card c)
    {
        cards = new ArrayList<Card>();
        cards.add(c);
        setDeckPosition(c.x, c.y);
    }
     
    public Deck(String z)//from socketString
    {
        cards = new ArrayList<Card>();
        z = z.replace('+', ' ');
        String[] s = z.split(" ");
         
        gameIndex = pint(s[2]);
        int deckSize = pint(s[3]);
        for (int i = 0; i < deckSize; i++)
        {
            String card = "";
            for (int c = 0; c < 7; c++)
                card += s[i*7+4+c]+ "+";
             
            cards.add(new Card(card));
             
        }
        int index = deckSize*7 + 4;
        this.owner = pint(s[index]);
         
         
    }
    public static int pint(String s)
    {
        return Integer.parseInt(s);
    }
     
    public String toSocketString()
    {
        String out = "new deck ";
        out += gameIndex + " " + cards.size() + " ";
        for (Card c : cards)
        {
            out += c.toSocketString() + " ";
        }
        out += owner;
         
         
         
         
        return out;
    }
     public static void main(String[]args)
     {
    	 
     }
     
     
     
     
}

