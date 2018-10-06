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
import pacsim.WallCell;
import pacsim.HouseCell;
import pacsim.GhostCell;

class Path
{
	int costVal;
    List<Point> path;

    public Path()
    {
        path = new ArrayList<Point>(); 
    }

    public int getCostVal()
    {
        return this.costVal; 
    }

    public void addPoint(Point x)
    {
        this.path.add(x);
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
        pm = new Path();
        adv1 = new Path();
        adv2 = new Path();
    }

    public Node()
    {
        children = new ArrayList<Node>();
        pm = new Path();
        adv1 = new Path();
        adv2 = new Path();
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

class Tree
{
	Node root;

	public Tree()
	{
		this.root = new Node();
	}

	public void insertNode(Node parent, Node child)
	{
		parent.children.add(child);
	}

	public void printTree(Node n) 
    {     
        if (n == null)
            return;

        int x;


        for(x = 0; x < n.children.size(); x++)
        {
            printTree(n.children.get(x)); 
            // n.displayNode(n.children.get(x)); 
        }

        if (n == this.root)
        {
            
            // System.out.println();
            // System.out.print(n.data + "  <- root");
        }           
    }

    public Node alphaBeta(Node n, int depth, int alpha, int beta, boolean max)
    {
        int value; 
        Node x = new Node(); 
        if (depth == 0 || n.children.size() == 0)
            return n;
        
        if (max)
        {   
            value = Integer.MIN_VALUE;

            for(int j = 0; j < n.children.size(); j++)
            {
                x = alphaBeta(n.children.get(j), depth - 1, alpha, beta, false);
                if(x.data > value)
                {
                    // System.out.println("x.data > value, so this is x.pm.path: " + x.pm.path);
                    n.pm.path = x.pm.path;
                    // System.out.println("Let's see if it copied correctly: " + n.pm.path);
                }
                value = Math.max(value, x.data);
                if (value >= beta)
                {
                    // System.out.println("Pruning out in max with a value of: " + value);
                    break;
                }
                alpha = Math.max(alpha, value);
            }
            n.setData(value);
            return n;
        }
        else 
        {
            value = Integer.MAX_VALUE;
            
            for(int j = 0; j < n.children.size(); j++)
            {
                x = alphaBeta(n.children.get(j), depth - 1, alpha, beta, true);
                if(x.data < value)
                {
                    // System.out.println("x.data < value, so this is x.pm.path: " + x.pm.path);
                    n.pm.path = x.pm.path;
                    // System.out.println("Let's see if it copied correctly: " + n.pm.path);
                }
                if(x.data < value)
                {
                    n.pm.path = x.pm.path;
                }
                value = Math.min(value, x.data);
                
                if (value <= alpha)
                {
                    // System.out.println("Pruning out in min with a value of: " + value);
                    break;
                }
                beta = Math.min(beta, value);
            }
            n.setData(value); 
            // System.out.println(n.pm.path);
            return n;
        }
    }

    public static Pman getPManMoves(Point pc, PacCell[][] grid)
    {
        Pman p = new Pman(); 
        Point pmanlocation = pc; 
        List<Point> directions = new ArrayList<Point>();
        int grix; 
        int griy; 
        
        Point down = new Point((int)pmanlocation.getX(), (int)pmanlocation.getY() + 1);
        grix = (int)down.getX();
        griy = (int)down.getY();
        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))
            directions.add(down);
        
        Point up = new Point((int)pmanlocation.getX(), (int)pmanlocation.getY() - 1);
        grix = (int)up.getX();
        griy = (int)up.getY();
        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))
            directions.add(up);
        
        Point right = new Point((int)pmanlocation.getX() + 1, (int)pmanlocation.getY());
        grix = (int)right.getX();
        griy = (int)right.getY();
        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))    
            directions.add(right);
        
        Point left = new Point((int)pmanlocation.getX() - 1, (int)pmanlocation.getY());
        grix = (int)left.getX();
        griy = (int)left.getY();
        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))   
            directions.add(left);

        int x = 0;
        for(int i = 0; i < directions.size(); i++)
        {
            p.addPoint(directions.get(i));
            x++;
            p.setNumPossibilities(x);
        }

        return p;
    }

    public static Adversary getAdversaryMoves(Point adv, PacCell[][] grid)
    {
        Adversary a = new Adversary(); 
        Point advLocation = adv; 
        List<Point> directions = new ArrayList<Point>();
        int grix; 
        int griy;


        Point down = new Point((int)advLocation.getX(), (int)advLocation.getY() + 1);
        grix = (int)down.getX();
        griy = (int)down.getY();
        if(!(grid[grix][griy] instanceof WallCell))
            directions.add(down);
        
        Point up = new Point((int)advLocation.getX(), (int)advLocation.getY() - 1);
        grix = (int)up.getX();
        griy = (int)up.getY();
        if(!(grid[grix][griy] instanceof WallCell))
            directions.add(up);
        
        Point right = new Point((int)advLocation.getX() + 1, (int)advLocation.getY());
        grix = (int)right.getX();
        griy = (int)right.getY();
        if(!(grid[grix][griy] instanceof WallCell))
            directions.add(right);
        
        Point left = new Point((int)advLocation.getX() - 1, (int)advLocation.getY());
        grix = (int)left.getX();
        griy = (int)left.getY();
        if(!(grid[grix][griy] instanceof WallCell))
            directions.add(left);
        
        int x = 0;
        for(int i = 0; i < directions.size(); i++)
        {
            a.addPoint(directions.get(i));
            x++;
            a.setNumPossibilities(x);
        }

        return a;
    }

    public void createTree(Node root, int depth, Point plocation, Point adv1location, Point adv2location, PacCell[][] grid)
    {
        if(depth == 0)
            return;

        Pman p = new Pman();
        p = getPManMoves(plocation, grid);


        int size = p.getNumPossibilities();
        int i, j, k, l, range = 10, min = 1, a;

        for(i = 0; i < size; i++)
        {
            Point point = p.possibleMoves.get(i);
            // int nrandom = (int)(Math.random() * 10) + 1; 
            Node n = new Node();
            if(root != this.root)
            {
                for(l = 0; l < root.pm.path.size(); l++)
                {
                    n.pm.path.add(root.pm.path.get(l)); 
                    n.adv1.path.add(root.adv1.path.get(l));
                    n.adv2.path.add(root.adv2.path.get(l)); 
                }
            }
            // System.out.println("This is n.pm.path before addPoint(): " + n.pm.path);
            n.pm.addPoint(point);
            // System.out.println("This is n.pm.path after addPoint(): " + n.pm.path);
            this.insertNode(root, n);
            Adversary adv1 = new Adversary();
            adv1 = getAdversaryMoves(adv1location, grid);
            Adversary adv2 = new Adversary();
            adv2 = getAdversaryMoves(adv2location, grid);

            for(j = 0; j < adv1.getNumPossibilities(); j++)
            {
                for(k = 0; k < adv2.getNumPossibilities(); k++)
                {
                    Point adv1Move = adv1.possibleMoves.get(j);
                    Point adv2Move = adv2.possibleMoves.get(k);
                    Node m = new Node();
                    m.pm.path = n.pm.path;
                    m.adv1.path = n.adv1.path;
                    m.adv2.path = n.adv2.path;

                    m.adv1.path.add(adv1Move);
                    m.adv2.path.add(adv2Move);
                    int random = (int)(Math.random() * 10) + 1;
                    // System.out.println(random);
                    m.setData(random);
                    this.insertNode(n, m);
                    createTree(m, depth - 1, m.pm.path.get(m.pm.path.size() - 1), m.adv1.path.get(m.adv1.path.size() - 1), m.adv2.path.get(m.pm.path.size() - 1), grid);
                    // m.displayNode(m); 
                    // System.out.println("This is m.pm.path that it has: " + m.pm.path); 
                }
            }
        }


    }
}

public class MiniMax implements PacAction {
    
    int depth;
    public MiniMax(int depth, String fname, int te, int gran, int max)
    {
        this.depth = depth;
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

    @Override
    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman(grid);
        Point plocation = new Point(); 
        plocation.setLocation((int)pc.getX(), (int)pc.getY()); 
        if (pc == null)
            return null;
        // System.out.println("This is depth at the beginning of the program: " + depth); 
        Tree t = new Tree();
        List<Point> advs = PacUtils.findGhosts(grid);  
        Point w = advs.get(0);
        Point x = advs.get(1); 
        t.createTree(t.root, depth, plocation, w, x, grid);

        t.printTree(t.root);
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        t.alphaBeta(t.root, depth * 2, alpha, beta, true); 
        Point next = t.root.pm.path.get(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        return face;
    }
}