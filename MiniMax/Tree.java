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

class Adversary 
{
	int costVal;
	public Adversary(Path a, Path b)
	{
		this.costVal = a.costVal + b.costVal; 
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
	Path inky;
	Path blinky;
	Adversary ad;
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

	public Tree(Node x)
	{
		this.root = x;
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

    public static void main(String args[])
    {
    	int depth = 2;
    	int alpha = Integer.MIN_VALUE;
    	int beta = Integer.MAX_VALUE;
    	boolean max = true;
    	Node rt = new Node(); 
    	Tree t = new Tree(rt);
    	Node a = new Node();
    	Node b = new Node();
    	Node c = new Node();
    	Node d = new Node();
    	Node e = new Node();
    	Node f = new Node();
    	Node g = new Node();
    	Node h = new Node(); 
    	Node i = new Node(3);
    	Node j = new Node(9);
    	a.children.add(i); a.children.add(j);
    	Node k = new Node(2);
    	Node l = new Node(4);
    	b.children.add(k); b.children.add(l);
    	Node m = new Node(13);
    	Node n = new Node(4);
    	c.children.add(m); c.children.add(n);
    	Node o = new Node(2);
    	Node p = new Node(6);
    	d.children.add(o); d.children.add(p);
    	Node q = new Node(9);
    	Node r = new Node(14);
    	e.children.add(q); e.children.add(r);
    	Node s = new Node(7);
    	Node u = new Node(1);
    	f.children.add(s); f.children.add(u);
    	Node v = new Node(3);
    	Node w = new Node(8);
    	g.children.add(v); g.children.add(w);
    	Node x = new Node(4);
    	Node y = new Node(2);
    	h.children.add(x); h.children.add(y);
    	
    	rt.children.add(a); rt.children.add(b); rt.children.add(c); rt.children.add(d);
    	rt.children.add(e); rt.children.add(f); rt.children.add(g); rt.children.add(h);
    	    
    	   

    	t.printTree(t.root);
        System.out.println();
        t.root.data = t.alphaBeta(t.root, depth, alpha, beta, max);	
        t.printTree(t.root);
        System.out.println();
    }
}
