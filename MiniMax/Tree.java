import java.util.ArrayList; 
import java.util.List;

class Node 
{
	int data;
	List<Node> children;

	public Node(int entryData)
	{
		this.data = entryData;
		children = new ArrayList<Node>();
	}

	public void displayNode(Node n)
	{
		System.out.print(n.data + " "); 
	}
}

class Tree
{
	Node root;

	public Tree()
	{
		this.root = null;
	}

	public void insertNode(Node parent, Node child, int data)
	{
		parent.children.add(child);
	}

	public static void printTree(Node n) 
	{     
        if (n == null)
            return;

        int x;
        //System.out.println("These are the children"); 
        for(x = 0; x < n.children.size(); x++)
        {
        	printTree(n.children.get(x)); 
        	n.displayNode(n.children.get(x)); 
        }           
    }

    public static void main(String args[])
    {
    	Node a = new Node(5);
    	Node b = new Node(7);
    	Node c = new Node(4);
    	Node d = new Node(11);
    	Node e = new Node(13);
    	Node f = new Node(12);
    	Node g = new Node(2);
    	Node h = new Node(19); 
    	a.children.add(b); a.children.add(c); a.children.add(d); a.children.add(e);
    	c.children.add(f);
    	d.children.add(g); d.children.add(h);

    	printTree(a);
        System.out.println();	
    }
}
