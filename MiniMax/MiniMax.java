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
import pacsim.PacMode; 

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
    // contain a Path variable for PacMan, Inky, and Blinky
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

// class designated for PacMan assignments
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

// class designed for Ghost assignments
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

// class to hold the tree structure that will be conducted MiniMax on
// THIS IS THE CLASS WHERE THE EVALUATION FUNCTION AND COMMENT BLOCK ARE 
// LOCATED
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

    // Method prunes tree permutations when future return values cannot impact a player's
	// decision making process because the standard for elimination (min or max) has already been derived.
	// initial call to alphaBeta has alpha set to Integer.MIN_VALUE and beta set
	// to Integer.MAX_VALUE
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

    // find all possible moves that PacMan can make. This is based on
    // the cartesian coordinates that surround PacMan. If instance of 
    // WallCell, GhostCell, or HouseCell, you cannot go in that direction.
    public static Pman getPManMoves(Point pc, PacCell[][] grid)
    {
        Pman p = new Pman(); 
        Point pmanlocation = pc; 
        List<Point> directions = new ArrayList<Point>();
        int grix; 
        int griy; 
        
        // Directing PacMan to which directions are available to choose going forward:
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


        // Once the available N,S,E,W directions have been determined,
		// return the number of possibilities for future reference pull
        int x = 0;
        for(int i = 0; i < directions.size(); i++)
        {
            p.addPoint(directions.get(i));
            x++;
            p.setNumPossibilities(x);
        }

        return p;
    }


    // find all possible moves that a Ghost can make. This is based on
    // the cartesian coordinates that surround the Ghost. If instance of 
    // WallCell, the ghost cannot go in that direction.
    public static Adversary getAdversaryMoves(Point adv, PacCell[][] grid)
    {
        Adversary a = new Adversary(); 
        Point advLocation = adv; 
        List<Point> directions = new ArrayList<Point>();
        int grix; 
        int griy;


        // Directing Adversary to which directions are available to choose going forward:
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
        
        // Once the available N,S,E,W directions have been determined,
		// return the number of possibilities for future reference pull
        int x = 0;
        for(int i = 0; i < directions.size(); i++)
        {
            a.addPoint(directions.get(i));
            x++;
            a.setNumPossibilities(x);
        }

        return a;
    }

    /*
		THIS IS THE COMMENT BLOCK!
		Create a ranking of value states for PacMan's possible directions,
		giving incentive to follow food dots and avoid going towards ghosts. This function takes in
		the current points of all three characters, the current grid, and the possible escape
		points that exist on the board. An escape point is classified as a Point on the grid
		where there exists three or more possible paths to take from that point. Anything that has
		two or fewer possible moves possible is a tunnel or a dead end. 
    */
    private static int eval(Point pm, Point adv1, Point adv2, PacCell[][] grid, List<Point> esc)
    {
    	int d1 = Integer.MAX_VALUE, d2 = Integer.MAX_VALUE, value = 0;
    	int dist1 = BFSPath.getPath(grid, pm, adv1).size();
    	int dist2 = BFSPath.getPath(grid, pm, adv2).size();
        int distFromEscapePoint = Integer.MAX_VALUE;
        int distance;
        int idxpm = 0, idxadv1 = 0, idxadv2 = 0;

        // System.out.println("d2 is this: " + d2);

    	List<Point> food = PacUtils.findFood(grid);

    	if(food.contains(pm))
    	{
    		// System.out.println(food);
    		value += 1; 
    	}


    	// proximity of ghost case value states to return decreasing levels
		// of value for PacMan to choose his next distance.
    	if(dist1 < 3 || dist2 < 3) {
    		value = -1;
    	} else if((dist1 < 5 && dist1 > 2) || (dist2 < 5 && dist2 > 2)){
    		value += 3;
        } else if(dist1 == 5 || dist2 == 5) {
            value += 5;
        } else {
            value += 10;
        }

    	return value;
    }

    // Find the nearest food element
    public int closestFood(PacCell grid[][], Point pm)
    {
    	List<Point> food = PacUtils.findFood(grid);
    	int distance;
    	int value = Integer.MAX_VALUE;
    	for(int i = 0; i < food.size(); i++)
    	{
    		distance = BFSPath.getPath(grid, food.get(i), pm).size();
    		if(distance < value)
    		{
    			value = distance;
    		}
    	}

    	return value;
    }

    // Generate all the possible moves
	// for character states. Allows PacMan to look ahead and take pre-emptive actions
	// to find optimal value states based on depth passed in through command line.
    public void createTree(Node root, int depth, Point plocation, Point adv1location, Point adv2location, PacCell[][] grid, List<Point> esc) 
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
            // create new node to represent PacMan's node
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
            
            n.pm.addPoint(point);
            n.setData(root.data + closestFood(grid, point)); 
            
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
                    // create a node m to represent both Ghosts 
                    // moving
                    Node m = new Node();
                    m.pm.path = n.pm.path;
                    m.adv1.path = n.adv1.path;
                    m.adv2.path = n.adv2.path;

                    m.adv1.path.add(adv1Move);
                    m.adv2.path.add(adv2Move);
                    m.setData(n.data); 
                    
                    int value = eval(m.pm.path.get(m.pm.path.size() - 1), m.adv1.path.get(m.adv1.path.size() - 1), m.adv2.path.get(m.adv2.path.size() - 1), grid, esc);
                    
                    m.setData(m.data + value);
                    this.insertNode(n, m);
                    createTree(m, depth - 1, m.pm.path.get(m.pm.path.size() - 1), m.adv1.path.get(m.adv1.path.size() - 1), m.adv2.path.get(m.pm.path.size() - 1), grid, esc);
                    
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
        /*for(PacMode c : PacMode.values())
            System.out.println(c);*/

        if (te > 0)
        {
            System.out.println("   Preliminary runs : " + te
            + "\n   Granularity    : " + gr 
            + "\n   Max move limit : " + ml
            + "\n\nPreliminary run results :\n");
        }
    }

    // Method to direct PacMan to the intersection points that have a greater
	// number of possible directions for PacMan to choose than there are ghost
	// so he can avoid being trapped. Also allows him to identify when he is
	// stuck in a tunnel and may need to evacuate, depending on ghost proximity.
    public static List<Point> getEscapePoints(PacCell[][] grid)
    {
    	List<Point> esc = new ArrayList<Point>(); 
    	int count, grix, griy;
    	for(int i = 0; i < grid.length; i++)
    	{
    		for(int j = 0; j < grid[i].length; j++)
    		{
    			if(!(grid[i][j] instanceof WallCell) && !(grid[i][j] instanceof HouseCell))
                {
                    Point p = new Point((int)grid[i][j].getX(), (int)grid[i][j].getY()); 
                    
        			count = 0;
        			Point down = new Point((int)grid[i][j].getX(), (int)grid[i][j].getY() + 1);
    		        grix = (int)down.getX();
                    
    		        griy = (int)down.getY();
                    
    		        if(grix > 0 && griy > 0 && grix < grid.length && griy < grid[i].length)
                    {
                        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))
                            count++;
                    }
    		        
    		        Point up = new Point((int)grid[i][j].getX(), (int)grid[i][j].getY() - 1);
    		        grix = (int)up.getX();
    		        griy = (int)up.getY();
    		        if(grix > 0 && griy > 0 && grix < grid.length && griy < grid[i].length)
                    {
                        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))
                            count++;
                    }
                        
    		        
    		        Point right = new Point((int)grid[i][j].getX() + 1, (int)grid[i][j].getY());
    		        grix = (int)right.getX();
    		        griy = (int)right.getY();
    		        if(grix > 0 && griy > 0 && grix < grid.length && griy < grid[i].length)
                    {
                        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))  
    		              count++;
                    }
    		        
    		        Point left = new Point((int)grid[i][j].getX() - 1, (int)grid[i][j].getY());
    		        grix = (int)left.getX();
    		        griy = (int)left.getY();
    		        if(grix > 0 && griy > 0 && grix < grid.length && griy < grid[i].length)
                    {
                        if(!(grid[grix][griy] instanceof WallCell) && !(grid[grix][griy] instanceof GhostCell) && !(grid[grix][griy] instanceof HouseCell))  
                            count++;
                    }
                    

    		        if(count > 2)
    		        {
    		        	esc.add(p);
    		        }
                }
        	}
    	}

    	return esc;
    }

    @Override
    public void init() 
    {

    }

    // Action method to describe what PacMan does
    @Override
    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman(grid);
        PacFace face;


        if (pc == null)
            return null;
        List<Point> esc = new ArrayList<Point>(); 
        esc = getEscapePoints(grid);
        
        Point plocation = new Point(); 
        plocation.setLocation((int)pc.getX(), (int)pc.getY());
        
        Tree t = new Tree();
        List<Point> advs = PacUtils.findGhosts(grid);  
        Point w = advs.get(0);
        
        Point x = advs.get(1); 
        
        t.createTree(t.root, depth, plocation, w, x, grid, esc);

        t.printTree(t.root);
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        t.alphaBeta(t.root, depth * 2, alpha, beta, false); 
        
        if(t.root.pm.path.size() >= 1)
        {
        	Point next = t.root.pm.path.get(0);
        	face = PacUtils.direction(pc.getLoc(), next);
        }
        else
        {
        	Point next = new Point(pc.getX(), pc.getY()); 
        	face = PacUtils.direction(pc.getLoc(), next);
        }
        return face;
    }
}