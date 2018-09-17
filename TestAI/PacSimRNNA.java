/*
University of Central Florida
CAP4630 - Fall 2018
Authors: Richard Snyder, Jimmy Seeber
*/
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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

/**
 * RNNA Search Agent
 * @author Richard Snyder and Jimmy Seeber
 */

class Path
{
    private int cost; 
    private String pathStr; 
    private List<Point> path; 
    
    public Path()
    {
      cost = 0; 
      pathStr = "";
      path = new ArrayList<>();
    }
    
    public void setCost(int cost)
    {
      this.cost = cost; 
    }
    
    public int getCost()
    {
      return this.cost; 
    }
    
    public void setPathStr(String pathStr)
    {
      this.pathStr = pathStr;
    }
    
    public String getPathStr()
    {
      return this.pathStr;
    }
    
    public void addPoint(Point newPoint)
    {
      this.path.add(newPoint);
    }

    public Point getPoint(int index)
    {
      return path.get(index); 
    }

    public List<Point> getPath(){
      return path;
    }
    
    public void setPath(List<Point> newPath){
      this.path = newPath;
    }
    
    public void printPath(){
      for(Point x : path)
        System.out.print(x + "  ");
      System.out.println();
    }
}

public class PacSimRNNA implements PacAction {

   private List<Point> path;
   private int simTime;
   int solutionMoves;

   public PacSimRNNA( String fname ) {
      PacSim sim = new PacSim( fname );
      sim.init(this);
   }

   public static void main( String[] args ) {
      System.out.println("\nTSP using RNNA agent by Richard Snyder and Jimmy Seeber:");
      System.out.println("\nMaze : " + args[ 0 ] + "\n" );
      new PacSimRNNA( args[ 0 ] );
   }

   @Override
   public void init() {
      simTime = 0;
      path = new ArrayList<>();
      solutionMoves = 0;
   }

   @Override
   public PacFace action( Object state ) {

      int i = 0, k = 1;
      PacCell[][] grid = (PacCell[][]) state;
      PacmanCell pc = PacUtils.findPacman(grid);
      // this is where the final path will be saved based off of the lowest cost
      List<Point> possiblePath = new ArrayList<>();
      List<Point> pellets = new ArrayList<>();
      // where is PacMan going next? 
      Point next;
      // variable to save the return value from generatePath
      Path var;
      // check to see if there exists a PacMan in the game
      if( pc == null ) 
        return null;

      Long startTime = System.currentTimeMillis();
      Long endTime;
      int executionTime;
      
      if( path.isEmpty() ) {
         // generate cost table
         int[][] costTable = generateCostTable(grid, pc);
         // find all the food pellets
         pellets = generateFoodTable(grid);
         //generate all of the possible paths, return 
         // the best one
         var = generatePath(costTable, pellets, pc, grid);
         // save path from best path to new variable
         // that is list of Points
         possiblePath = var.getPath();  

         // add path Points to member variable
         for(int j = 0; j < possiblePath.size(); j++)
         {
            // System.out.println("possiblePath at " + j + ": ()" + possiblePath.get(j));
            path.add(possiblePath.get(j));
         }

         possiblePath.clear();
         endTime = System.currentTimeMillis();
         executionTime = (int)(endTime - startTime);
         System.out.println("Time to generate plan: " + executionTime + " msec\n\nSolution moves:\n");
      }

      // if we run across a pellet and eat it, in order 
      // to prevent thrashing back to that spot in our 
      // intermediate path, remove it from the pellets 
      // list
      int size = path.size();
      for(int j = 0; j < size; j++)
      {
        if(pc.getX() == (int)path.get(j).getX() && pc.getY() == (int)path.get(j).getY())
        {
          path.remove(j);
          System.out.println("Removing stuff: " + path.get(j));
          break;
        }
      }

      // create an intermediate path from PacMan's current location
      // to the next closest food pellet in the list 
      List<Point> intermediatePath = BFSPath.getPath(grid, pc.getLoc(), path.get(0));
      
      // assign PacMan's next location to the BFSPath
      // next value
      next = intermediatePath.get(0);

      PacFace face = PacUtils.direction( pc.getLoc(), next );
      System.out.printf( "%5d : From [ %2d, %2d ] go %s%n",
            ++simTime, pc.getLoc().x, pc.getLoc().y, face );

      return face;
   }


   private int[][] generateCostTable(PacCell[][] g, PacmanCell p)
   {
      List<Point> food = PacUtils.findFood(g);
      int x, y, cost;

      // table is size of food pellets + 1 because you need the
      // diagonal of the matrix to be unused
      int tbSize = food.size() + 1;
      int[][] costTable = new int[tbSize][tbSize];

      // this loop will find the distance from the pacman starting
      // location to each food pellet
      for(x = 1; x < tbSize; x++)
      {
         cost = BFSPath.getPath(g, p.getLoc(), food.get(x - 1)).size();
         costTable[0][x] = cost;
         costTable[x][0] = cost;
      }

      // this loop will generate the cost of going from one food pellet to the next
      for(x = 1; x < tbSize; x++)
      {
         for(y = 1; y < tbSize; y++)
            costTable[x][y] = BFSPath.getPath(g, food.get(x - 1), food.get(y - 1)).size();
      }

      // print cost table
      System.out.println("Cost Table:\n");
      for(x = 0; x < tbSize; x++)
      {
         for(y = 0; y < tbSize; y++)
            System.out.printf("%-3d", costTable[x][y]);
         System.out.println();
      }

      System.out.println("\n");

      return costTable;
   }

   // very trivial method considering the PacUtils 
   // will return all of the points of the food pellets
   private List<Point> generateFoodTable(PacCell[][] g)
   {
      List<Point> food = PacUtils.findFood(g);
      int i, foodSize = food.size();

      System.out.println("Food Array: \n");

      for(i = 0; i < foodSize; i++)
      {
         System.out.println(i + " : (" + (int)food.get(i).getX() + "," + (int)food.get(i).getY() + ")");
      }
      System.out.println();

      return food;
   }

   private Path generatePath(int[][] costTable, List<Point> pellets, PacmanCell pc, PacCell[][] grid)
   {
      // save the previous cost so that you can 
      // just add the newly computed cost
      int val = 0, cost = 0, previousCost =0; 
      String pathString  = "";
      Point pacManLoc = new Point(); 
      Point currentFood; 
      // this will hold all of the possible paths 
      // that PacMan can take. There are a minimum of 
      // n paths, where n is the number of food pellets.
      // However, due to possible branching, there could be more paths.
      // Otherwise, we'd just put them in an array of size n. 
      List<Path> pathList = new ArrayList<>();
      // save the previous path so that we can 
      // concatenate the next point onto the path
      // after calculating it.
      List<Point> previousPath = new ArrayList<>();
      // save a list of the closest pellets to PacMan
      ArrayList<Point> closestPellets = new ArrayList<Point>();
      // save a copy of the path list so that you 
      // can easily add branches
      List<Path> pathListCopy = new ArrayList<>();


      // you're guranteed that you only need to run
      // this loop n times becuase you can only collect
      // n pellets
      for(int i = 0; i < pellets.size(); i++)
      {
        System.out.println("Population at step " + (i+1) + ":");
        val = 0; 
        cost = 0;
        
        // capacity is a variable that stores the size of pathList.
        // it can either be n, or it can be n + the number of branches.
        int capacity = (pathList.size() > pellets.size()) ? pathList.size() : pellets.size();
        for (int j = 0; j < capacity; j++)
        {
          String previousPathStr = "";  
          
          // If we're in the first step, then instantiate 
          // each population entry and add them to the list        
          if (i == 0)
          {
            Path p = new Path(); 
            pathList.add(p); 
            pacManLoc = pc.getLoc(); 
            currentFood = pellets.get(j);
          }
          else
          {
            // clear the previously closest pellets
            closestPellets.clear();

            pacManLoc = pathList.get(j).getPoint(i-1);
            
            // Get available food pellets closest to last simulated PacMan location. 
            // Branch if more than one available
            closestPellets = getClosestPellets(pathList.get(j), pellets, costTable, pacManLoc);
            currentFood = closestPellets.get(0);
  
          }
          // copy all of the previous values
          previousPathStr = pathList.get(j).getPathStr();
          previousPath = pathList.get(j).getPath();
          previousCost = pathList.get(j).getCost();

          // Calculate the cost from pacman's current location to get current food 
          cost = BFSPath.getPath(grid, pacManLoc, currentFood).size();            
          
          // Set the cost for that particular population entry 
          pathList.get(j).setCost(pathList.get(j).getCost() + cost);
          
          // Add new food pellet in point format to and set new cost
          pathList.get(j).addPoint(currentFood);

          // Get the path in a string format
          pathString = "[("+ (int)currentFood.getX() + "," + (int)currentFood.getY() + ")," + cost + "]";
          pathList.get(j).setPathStr(previousPathStr + pathString);

        }

        // copy list so that you can sort using the Collections Library
        // using lamba functions
        pathListCopy = pathList; 
        Collections.sort(pathListCopy, new Comparator<Path>()
        {

          @Override
          public int compare(Path o1, Path o2) {
            if(o1.getCost() > o2.getCost())
            {
              return 1;
            }
            else if (o1.getCost() == o2.getCost())
            {
              return 0; 
            }
            else
            {
              return -1; 
            }
          }
          
        });
        
        // print out the population costs 
        for (int j = 0 ; j < pathListCopy.size(); j++)
        {
          System.out.println(val + " : cost="+ pathListCopy.get(j).getCost() + " : " +
              pathListCopy.get(j).getPathStr());
          val++;
        }
      }
        // return the lowest cost path at the very
        // end of the calculations
        return pathListCopy.get(0); 
    }

      private ArrayList<Point> getClosestPellets(Path currentPath, List<Point> pellets, int[][] costTable, Point pacmanLoc){
    
        // initialize minDistance to max value so that any pellets on board 
        // will set a new min value
        int minDistance = Integer.MAX_VALUE;

        // Figure out PacMan's index based on the 
        // food pellet that he is currently at
        int idx = pellets.indexOf(pacmanLoc) + 1;
        
        // Keep track of points with least distance
        ArrayList<Point> chosenPoints = new ArrayList<Point>();

        // for all remaining pellets, find the nearest one
        for(int x = 0; x < pellets.size(); x++)
        {
          // as long as the pellet hasn't been removed from the
          // game board yet
          if(!(currentPath.getPath().contains(pellets.get(x))))
          {
            if((x + 1) != idx && costTable[x + 1][idx] < minDistance)
            {
              chosenPoints.clear();
              chosenPoints.add(pellets.get(x));
              // set the min distance to the value of the cost
              // table, which will then be added to the 
              // cost of the path back in generatePaths()
              minDistance = costTable[x + 1][idx];
            }
            // if the min distance is equal to the cost at the
            // costTable, add a point, which will then generate a branch
            // in generatePaths()
            else if(costTable[x + 1][idx] == minDistance)
            {
              chosenPoints.add(pellets.get(x));
            }
          } 
        }
        
        // return the list of Points that are the closest
        // to Pacman. If there is more than one, generatePaths()
        // will create a branch of that path with the next value
        return chosenPoints;
      }
}