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

class PopulationEntry
{
    private int cost; 
    private String pathStr; 
    private List<Point> path; 
    
    public PopulationEntry()
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
      List<Point> possiblePath = new ArrayList<>();
      ArrayList<List<Point>> allPaths = new ArrayList<List<Point>>();
      List<Point> pellets = new ArrayList<>();
      Point next;
      PopulationEntry var;
      // check to see if there exists a PacMan in the game
      if( pc == null ) return null;

      // if current path completed (or just starting out),
      // select a the nearest food using the city-block
      // measure and generate a path to that target
      Long startTime = System.currentTimeMillis();
      Long endTime;
      int executionTime;
      if( path.isEmpty() ) {
         //System.out.println("PacMan starting location" + pc);
         int[][] costTable = generateCostTable(grid, pc);
         pellets = generateFoodTable(grid);
         // int[] orderOfFood = searchAllPaths(costTable, possiblePath, pellets);
         var = generatePath(costTable, pellets, pc, grid);
         possiblePath = var.getPath();  
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

      /*for(int j = 0; j < path.size(); j++)
      {
            System.out.println("path at " + j + ": ()" + path.get(j));
      }*/
      // System.out.println("path.get(0)"); 
      // System.out.println(path.get(0)); 
      // System.out.println("intermediatePath"); 
      // System.out.println(BFSPath.getPath(grid, pc.getLoc(), path.get(0)) + " " + BFSPath.getPath(grid, pc.getLoc(), path.get(0)).size()); 
      List<Point> intermediatePath = BFSPath.getPath(grid, pc.getLoc(), path.get(0));
      // System.out.println(intermediatePath);
      // System.out.println(intermediatePath.get(0)); 
      next = intermediatePath.get(0);
      // take the next step on the current path
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

      // this loop will generat the cost of going from one food pellet to the next
      for(x = 1; x < tbSize; x++)
      {
         for(y = 1; y < tbSize; y++)
            costTable[x][y] = BFSPath.getPath(g, food.get(x - 1), food.get(y - 1)).size();
      }

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

   private PopulationEntry generatePath(int[][] costTable, List<Point> pellets, PacmanCell pc, PacCell[][] grid)
   {
      int population = 0, cost = 0, previousCost =0; 
      String pathStr = "";
      Point pacManLoc = new Point(); 
      Point currentFood; 
      List<PopulationEntry> populationList = new ArrayList<>();
      List<Point> previousPath = new ArrayList<>();
      ArrayList<Point> closestPellets = new ArrayList<Point>();

      List<PopulationEntry> popListCopy = new ArrayList<>();

      long start = System.currentTimeMillis();  
      // Calculate each step in the algorithm 
      for(int i = 0; i < pellets.size(); i++)
      {
        System.out.println("Population at step " + (i+1) + ":");
        population = 0; 
        cost = 0;
        
        int capacity = (populationList.size() > pellets.size()) ? populationList.size() : pellets.size();
        for (int j = 0; j < capacity; j++)
        {
          String previousPathStr = "";  
          // If we're in the first step, then instantiate 
          // each population entry and add them to the list
          
          if (i == 0)
          {
            PopulationEntry p = new PopulationEntry(); 
            populationList.add(p); 
            pacManLoc = pc.getLoc(); 
            currentFood = pellets.get(j);
          }
          else
          {

            closestPellets.clear();

            // System.out.println("i: " + i + "  j: " + j);
            pacManLoc = populationList.get(j).getPoint(i-1);
            
            // Get available food pellets closest to last simulated PacMan location. Branch if more than one available
            closestPellets = getClosestPellets(populationList.get(j), pellets, costTable, pacManLoc);
            currentFood = closestPellets.get(0);
  
          }
          previousPathStr = populationList.get(j).getPathStr();
          previousPath = populationList.get(j).getPath();
          previousCost = populationList.get(j).getCost();
          // Calculate the cost from pacman's current location to get current food 
          cost = BFSPath.getPath(grid, pacManLoc, currentFood).size();            
          
          // Set the cost for that particular population entry 
          populationList.get(j).setCost(populationList.get(j).getCost() + cost);
          
          //System.out.println("Distance between " + pacManLoc + " and " + currentFood + " is " + populationList.get(j).getCost());

          // Add new food pellet in point format to and set new cost
          populationList.get(j).addPoint(currentFood);

          // Get the path in a string format
          pathStr = "[("+ (int)currentFood.getX() + "," + (int)currentFood.getY() +
              ")," + cost + "]";
          populationList.get(j).setPathStr(previousPathStr + pathStr);

        }

        popListCopy = populationList; 
        Collections.sort(popListCopy, new Comparator<PopulationEntry>()
        {

          @Override
          public int compare(PopulationEntry o1, PopulationEntry o2) {
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
        
        for (int j = 0 ; j < popListCopy.size(); j++)
        {
          System.out.println(population + " : cost="+ popListCopy.get(j).getCost() + " : " +
              popListCopy.get(j).getPathStr());
          population++;
        }
      }

        return popListCopy.get(0); 
      }

      private ArrayList<Point> getClosestPellets(PopulationEntry currentPath, List<Point> foodPellets, int[][] costTable, Point pacmanLoc){
    
        //System.out.println("Looking at: (" + pacmanLoc.getX() + ", " + pacmanLoc.getY() + ")");
        int minDistance = Integer.MAX_VALUE;

        // Figure out pacmanLoc index
        int indexOfPacman = foodPellets.indexOf(pacmanLoc) + 1;
        
        // Keep track of points with least distance
        ArrayList<Point> chosenPoints = new ArrayList<Point>();

        for(int x = 0; x < foodPellets.size(); x++){
          //if((int)pacmanLoc.getX() == 3)
          //  System.out.println("Checking Food: (" + foodPellets.get(x).getX() + ", " + foodPellets.get(x).getY() + ")");
          if(!(currentPath.getPath().contains(foodPellets.get(x)))){
            //System.out.println("Distance to pellet " + x + " is " + costTable[x + 1][indexOfPacman]);
            if((x + 1) != indexOfPacman && costTable[x + 1][indexOfPacman] < minDistance){
              chosenPoints.clear();
              chosenPoints.add(foodPellets.get(x));
              minDistance = costTable[x + 1][indexOfPacman];
            }else if(costTable[x + 1][indexOfPacman] == minDistance){
              chosenPoints.add(foodPellets.get(x));
            }
          } 
        }
        //System.out.println(chosenPoint.getX() + ", " + chosenPoint.getY());
        return chosenPoints;
      }
}