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

      int i = 0, k = 1;
      PacCell[][] grid = (PacCell[][]) state;
      PacmanCell pc = PacUtils.findPacman(grid);
      List<Point> possiblePath = new ArrayList<>();
      ArrayList<List<Point>> allPaths = new ArrayList<List<Point>>();
      List<Point> pellets = new ArrayList<>();
      Point next;
      // check to see if there exists a PacMan in the game
      if( pc == null ) return null;

      // if current path completed (or just starting out),
      // select a the nearest food using the city-block
      // measure and generate a path to that target
      Long startTime = System.currentTimeMillis();
      Long endTime;
      int executionTime;
      if( path.isEmpty() ) {
         int[][] costTable = generateCostTable(grid, pc);
         pellets = generateFoodTable(grid);
         int[] orderOfFood = aSolPath(costTable, possiblePath, pellets);
         allPaths.add(possiblePath);
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
          path.remove(0);
          // System.out.println("Removing stuff");
          break;
        }
      }
      List<Point> intermediatePath = BFSPath.getPath(grid, pc.getLoc(), path.get(0));
      // System.out.println(intermediatePath);
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

   private int[] aSolPath(int[][] costTable, List<Point> pathPoints, List<Point> foodTable){
     int idx=0, testVal=0, testIdx=0, minTotalCost=0, step=0, row=0, col=0, count=0;
     int n = costTable[0].length;
     int[] minVals = new int[n];
     int[] minValIndexes = new int[n];
     int[] validSearchSpace = new int[n*n];


    //Call helper functions to perform initializations:
    validSearchSpace = initSearchSpace(validSearchSpace, n);
    minVals = initMinVals(minVals);
    // showGrid(n);

     //Step is a fluid counter traversing the search space based on economical steps
     //counter is a strict number of steps to traverse the entire costTable only once.
     //idx is the incrementation of the 1D arrays minVals[] and minValIndexes[]
     //row & col are used for the readability of their mathematical derivations.
     step=0; count=0; row=0; col=0; idx=1;
     while(count < n*n){

        //Before searching the next row, finish end of row procedures
        if(count != 0 && count%n == 0){
          validSearchSpace = reduceSearchSpace(validSearchSpace, minValIndexes, idx, n);
          //locate the next cell location to start traversing the row
          idx++;
          step = minValIndexes[idx-1]*n;
          //Remove: Test print:
          // System.out.println(" \t\t\t" +  minValIndexes[idx-1] + " " + n + " \t\t" + step );

          if(step%n == minValIndexes[idx]){
              ;
          }else{
              solIndexes.add(minValIndexes, minVals);

              branches++;
              System.out.println(step%n + " " + minValIndexes[idx] + ": " + branches);
          }
        }

        //convert from a linear to a 2D traversal style
        row = step/n;
        col = step%n;

        //If the cell is part of the valid search space, use it
        if(validSearchSpace[step] == 1){
            //Remove: Test print:
             // System.out.print(costTable[row][col] + " ");
             testVal = costTable[row][col];
             testIdx = col;

             //Evaluate each row based on the lowest cost return:
             if(testVal < minVals[idx]){
               minVals[idx] = testVal;
               minValIndexes[idx] = testIdx;
             }

             //omit the search space just taken
             validSearchSpace[step] = 0;
         }
          // System.out.print(step + " ");
          step++;
          count++;
      }

      // System.out.print("\nHere is the valid search space for this solution path:\n");
      // showSearchSpace(validSearchSpace, n);

      System.out.print("\nHere is a possible solution path:\nGo to index:    ");
      showArray(minValIndexes);
      System.out.print("\nhas a value of: ");
      showArray(minVals);

      System.out.print("\nThis solution can be solved with a cost of " + calcMinCost(minVals) + "\n\n");

      int i;

      System.out.println("This is the path PacMan will take: ");
      for(i = 1; i <= foodTable.size(); i++)
      {
          int j = minValIndexes[i];
          // System.out.println("j is equal to: " + j);
          // System.out.println(foodTable.get(j - 1));
          pathPoints.add(foodTable.get(j - 1));
          System.out.print("(" + (int)pathPoints.get(i - 1).getX() + "," + (int)pathPoints.get(i - 1).getY() + ") ");
      }
      System.out.println();

      return minValIndexes;

  }
    //Helper functions to support finding the most economical path:
    int[] initSearchSpace(int[] validSearchSpace, int n){
      int i, j, step=0;
        for(i=0; i<n; i++){
          for(j=0; j<n; j++){
             step = i*n + j;
             if(i == j)
                validSearchSpace[step] = 0;
             else
                validSearchSpace[step] = 1;
             if(j == 0)
                validSearchSpace[step] = 0;
           }
        }
        return validSearchSpace;
    }

    void showSearchSpace(int[] validSearchSpace, int n){
      int i, j, step=0;
      for(i=0; i<n; i++){
        for(j=0; j<n; j++){
          System.out.print(validSearchSpace[step] + " ");
          step++;
        }
        System.out.println();
      }
    }

    int calcMinCost(int[] minVals){
        int totalCost=0, i=0;

        for(i=0; i<minVals.length; i++){
          totalCost += minVals[i];
        }
        return totalCost;
    }

    void showGrid(int n){
      System.out.print("\nNumerical grid:\n");
      int i, j, step=0;
      for(i=0; i<n; i++){
         for(j=0; j<n; j++){
           System.out.print(step + " ");
           step++;
         }
         System.out.println();
      }
    }
    void showArray(int[] array){
      int i;
      for(i=0; i<array.length; i++){
        System.out.print(array[i] + " ");
      }
      System.out.println();
    }

    int[] initMinVals(int[] minVals){
      int i;
      minVals[0] = 0;
      for(i=1; i<minVals.length; i++){
        minVals[i] = Integer.MAX_VALUE;
      }
      return minVals;
    }

    int[] reduceSearchSpace(int[] validSearchSpace, int[] minValIndexes, int idx, int n){
      //idx is chosen as the next most economical food pellet.
      int i, cell=0;

      cell = minValIndexes[idx];
      for(i=0; i<n-1; i++){
        cell += n;
        if(validSearchSpace[cell] == 0){
          continue;
        }else{
          //delete it from search spaces
          validSearchSpace[cell] = 0;
        }
      }
      // System.out.println("\n\n");
      return validSearchSpace;
    }
}
