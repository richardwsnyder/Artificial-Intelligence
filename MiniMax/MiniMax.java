// create path func to find distances from PacMan to all Ghosts
import java.util.ArrayList; 
import java.util.List;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator; 
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

class Path
{
	int costVal;
	List<Point> path;

	public Path(int cost)
	{
		costVal = cost;
		path = new ArrayList<Point>(); 
	}

	public int getCostVal()
	{
		return this.costVal; 
	}
}

class Node 
{
	List<Node> children;
	Path pm;
	Path adv1;
	Path adv2;
	int data;

	public Node(int entryData)
	{
		this.data = entryData;
		children = new ArrayList<Node>();
	}

	public Node()
	{
		children = new ArrayList<Node>(); 
	}

	public void displayNode(Node n)
	{
		System.out.print(n.data + " "); 
	}

	public void setData(int d)
	{
		this.data = d;
	}
}

class Tree
{
	Node root;

	public Tree()
	{
		this.root = new Node();
	}

	private void insertNode(Node parent, Node child)
	{
		parent.children.add(child);
	}

	private void printTree(Node n) 
	{     
        if (n == null)
            return;

        int x;


        for(x = 0; x < n.children.size(); x++)
        {
        	printTree(n.children.get(x)); 
        	n.displayNode(n.children.get(x)); 
        }

        if (n == this.root)
        {
        	
        	System.out.println();
        	System.out.print(n.data + "  <- root");
        }           
    }

    private int alphaBeta(Node n, int depth, int alpha, int beta, boolean max)
    {
    	int value;
    	if (depth == 0 || n.children.size() == 0)
    		return n.data;
    	if (max)
    	{	
    		value = Integer.MIN_VALUE;
    		for(int j = 0; j < n.children.size(); j++)
    		{
    			value = Math.max(value, alphaBeta(n.children.get(j), depth - 1, alpha, beta, false));
    			if (value >= beta)
    			{
    				// System.out.println("Pruning out in max with a value of: " + value);
    				break;
    			}
    			alpha = Math.max(alpha, value);
    		}
    		n.setData(value);
    		return value;
    	}
    	else 
    	{
    		value = Integer.MAX_VALUE;
    		for(int j = 0; j < n.children.size(); j++)
    		{
    			value = Math.min(value, alphaBeta(n.children.get(j), depth - 1, alpha, beta, true));
    			
    			if (value <= alpha)
    			{
    				// System.out.println("Pruning out in min with a value of: " + value);
    				break;
    			}
    			beta = Math.min(beta, value);
    		}
    		n.setData(value);
    		return value;
    	}
    }
}

class Pman {
    int numPossibilites;
    List<Point> possibleMoves;

    public Pman()
    {
        numPossibilites = 0;
        possibleMoves = new ArrayList<Point>();
    }

    public void setNumPossibilities(int x)
    {
        this.numPossibilites = x;
    }

    public int getNumPossibilities()
    {
        return this.numPossibilites;
    }

    public void addPoint(Point x)
    {
        this.possibleMoves.add(x);
    }

    public Point getPoint(int i)
    {
        return possibleMoves.get(i);
    }

}

class Adversary 
{
    int numPossibilites;
    List<Point> possibleMoves;

    public Adversary()
    {
        numPossibilites = 0;
        possibleMoves = new ArrayList<Point>();
    }

    public void setNumPossibilities(int x)
    {
        this.numPossibilites = x;
    }

    public int getNumPossibilities()
    {
        return this.numPossibilites;
    }

    public void addPoint(Point x)
    {
        this.possibleMoves.add(x);
    }

    public Point getPoint(int i)
    {
        return possibleMoves.get(i);
    }
}

public class MiniMax implements PacAction {
    
    public MiniMax(int depth, String fname, int te, int gran, int max)
    {
        PacSim sim = new PacSim(fname, te, gran, max);
        sim.init(this);
    }

    public static void main(String args[])
    {
        String fname = args[0];
        int depth = Integer.parseInt(args[1]);

        int te = 0;
        int gr = 0;
        int ml = 0;

        if(args.length == 5)
        {
            te = Integer.parseInt(args[2]);
            gr = Integer.parseInt(args[3]);
            ml = Integer.parseInt(args[4]);
        }

        new MiniMax(depth, fname, te, gr, ml); 

        System.out.println("\nAdversarial Search using Minimax by Richard Snyder and Jimmy Seeber:");
        System.out.println("\n    Game board   : " + fname);

        System.out.println("    Search depth : " + depth + "\n");

        if (te > 0)
        {
            System.out.println("   Preliminary runs : " + te
            + "\n   Granularity    : " + gr 
            + "\n   Max move limit : " + ml
            + "\n\nPreliminary run results :\n");
        }
    }

    @Override
    public void init() 
    {

    }

    private Pman getPManMoves(PacmanCell pc)
    {
        Pman p = new Pman(); 
        Point pmanlocation = pc.getLoc(); 
        List<Point> directions = new ArrayList<Point>();
        Point down = new Point(pmanlocation.getX(), pmanlocation.getY() + 1);
        directions.add(down);
        Point up = new Point(pmanlocation.getX(), pmanlocation.getY() - 1);
        directions.add(up);
        Point right = new Point(pmanlocation.getX() + 1, pmanlocation.getY());
        directions.add(right);
        Point left = new Point(pmanlocation.getX() - 1, pmanlocation.getY());
        directions.add(left);
        int x = 0;
        for(int i = 0; i < 4; i++)
        {
            if (!directions.get(i).instanceOf(HouseCell) && !directions.get(i).instanceOf(WallCell) && !directions.get(i).instanceOf(GhostCell))
            {
                p.addPoint(directions.get(i));
                x++;
                p.setNumPossibilities(x);
            }
        }
    }

    private Adversary getAdversaryMoves(PacmanCell adv)
    {
        Adversary a = new Pman(); 
        Point advLocation = adv.getLoc(); 
        List<Point> directions = new ArrayList<Point>();
        Point down = new Point(advLocation.getX(), advLocation.getY() + 1);
        directions.add(down);
        Point up = new Point(advLocation.getX(), advLocation.getY() - 1);
        directions.add(up);
        Point right = new Point(advLocation.getX() + 1, advLocation.getY());
        directions.add(right);
        Point left = new Point(advLocation.getX() - 1, advLocation.getY());
        directions.add(left);
        int x = 0;

        for(int i = 0; i < 4; i++)
        {
            if (!directions.get(i).instanceOf(WallCell))
            {
                a.addPoint(directions.get(i));
                x++;
                a.setNumPossibilities(x);
            }
        }
    }

    @Override
    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman(grid);
        Tree t = new Tree()
        List<Point> advs = PacUtils.findGhosts(grid);  
        List<Point> path = new ArrayList<Point>(); 
        if (pc == null)
            return null;

        Pman p = new Pman();
        p.possibleMoves = getPManMoves(pc);
        int size = p.getNumPossibilities();
        int i, j, k, l;

        for(i = 0; i < size; i++)
        {
            Point p = p.possibleMoves.get(i);
            Node n = new Node();
            n.pm.path.add(p);
            n.setData(Math.random())
            t.insertNode(t.root, n);
            Adversary adv1 = getAdversaryMoves(advs.get(0));
            Adversary adv2 = getAdversaryMoves(advs.get(1));

            for(j = 0; j < adv1.getNumPossibilities(); j++)
            {
                for(k = 0; k < adv2.getNumPossibilities(); k++)
                {
                    Point adv1Move = adv1.possibleMoves.get(j);
                    Point adv2Move = adv2.possibleMoves.get(k);
                    Node m = new Node();
                    m.adv1.path.add(adv1Move);
                    m.adv2.path.add(adv2Move);
                    m.pm.path.add(n.pm.path);
                    m.setData(Math.random());
                    t.insertNode(n, m);
                }
            }
        }

        t.printTree()

        Point next = path.remove(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        return face;
    }

    private int 

}