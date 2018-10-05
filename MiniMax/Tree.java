// create path func to find distances from PacMan to all Ghosts
import java.util.ArrayList; 
import java.util.List;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator; 

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

    private static Pman getPManMoves(Point pc)
    {
        Pman p = new Pman(); 
        Point pmanlocation = pc; 
        List<Point> directions = new ArrayList<Point>();
        Point down = new Point((int)pmanlocation.getX(), (int)pmanlocation.getY() + 1);
        directions.add(down);
        Point up = new Point((int)pmanlocation.getX(), (int)pmanlocation.getY() - 1);
        directions.add(up);
        Point right = new Point((int)pmanlocation.getX() + 1, (int)pmanlocation.getY());
        directions.add(right);
        Point left = new Point((int)pmanlocation.getX() - 1, (int)pmanlocation.getY());
        directions.add(left);
        int x = 0;
        for(int i = 0; i < 2; i++)
        {
            p.addPoint(directions.get(i));
            x++;
            p.setNumPossibilities(x);
        }

        return p;
    }

    private static Adversary getAdversaryMoves(Point adv)
    {
        Adversary a = new Adversary(); 
        Point advLocation = adv; 
        List<Point> directions = new ArrayList<Point>();
        Point down = new Point((int)advLocation.getX(), (int)advLocation.getY() + 1);
        directions.add(down);
        Point up = new Point((int)advLocation.getX(), (int)advLocation.getY() - 1);
        directions.add(up);
        Point right = new Point((int)advLocation.getX() + 1, (int)advLocation.getY());
        directions.add(right);
        Point left = new Point((int)advLocation.getX() - 1, (int)advLocation.getY());
        directions.add(left);
        int x = 0;

        for(int i = 0; i < 2; i++)
        {
            a.addPoint(directions.get(i));
            x++;
            a.setNumPossibilities(x);
        }

        return a;
    }

    public static void main(String args[])
    {
        Point pc = new Point(3, 4);
        Tree t = new Tree();
        List<Point> advs = new ArrayList<Point>();
        Point w = new Point(5, 6);
        advs.add(w);
        Point z = new Point(1, 0);
        advs.add(z);
        Pman p = new Pman();
        p = getPManMoves(pc);

        int size = p.getNumPossibilities();
        int i, j, k, l, range = 10, min = 1, a;

        int depth;
        List<Node> copyList = new ArrayList<Node>(); 

        for(a = 0; a < depth; a++)
        {
            if(a == 0)
            {
                p = getPManMoves(pc);
            }
            for(i = 0; i < size; i++)
            {
                Point point = p.possibleMoves.get(i);
                if (a >= 1)
                {
                    Node n = new Node();
                    n = copyList.remove(0)
                }
                Node n = new Node();
                n.pm.addPoint(point);
                t.insertNode(t.root, n);
                Adversary adv1 = new Adversary();
                adv1 = getAdversaryMoves(advs.get(0));
                Adversary adv2 = new Adversary();
                adv2 = getAdversaryMoves(advs.get(1));

                for(j = 0; j < adv1.getNumPossibilities(); j++)
                {
                    for(k = 0; k < adv2.getNumPossibilities(); k++)
                    {
                        Point adv1Move = adv1.possibleMoves.get(j);
                        Point adv2Move = adv2.possibleMoves.get(k);
                        Node m = new Node();
                        m.adv1.path.add(adv1Move);
                        m.adv2.path.add(adv2Move);
                        for(l = 0; l < n.pm.path.size(); l++)
                        {
                            m.pm.path.add(n.pm.path.get(l));
                        }
                        int random = (int)(Math.random() * range) + min;
                        System.out.println(random);
                        m.setData(random);
                        t.insertNode(n, m);
                    }
                }
            }
        }


        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        t.printTree(t.root);
        System.out.println();
        t.alphaBeta(t.root, 2, alpha, beta, true);
        t.printTree(t.root);

    }
}


