/*
University of Central Florida
CAP4630 - Fall 2018
Authors: Richard Snyder, Jimmy Seeber
*/
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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

      PacCell[][] grid = (PacCell[][]) state;
      PacmanCell pc = PacUtils.findPacman(grid);

      // check to see if there exists a PacMan in the game
      if( pc == null ) return null;

      // if current path completed (or just starting out),
      // select a the nearest food using the city-block
      // measure and generate a path to that target

      if( path.isEmpty() ) {
         int[][] costTable = generateCostTable(grid, pc); 
         List<Point> pellets = generateFoodTable(grid); 
      }

      // take the next step on the current path

      Point next = path.remove( 0 );
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
}
