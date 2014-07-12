
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gabriel
 * Date: 7/13/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 * Additional contributions throughout by Ray Alfano until 7/15/12 7:46pm
 */
public class Colony extends JPanel{

    public static enum Tile{OBSTACLE, FOOD};

    private Tile tile = Tile.FOOD;

//    should be defined by the user!

    int rows;
    int columns;
    int nestColumn;
    int nestRow;

    WorldGridSquare[][] earthGrid; // instantiate in public

    private int maxAnts;

    private List<Ant> antColony; //instantiate in public

    private Set<WorldGridSquare> nests = new HashSet<WorldGridSquare>();
    private Set<WorldGridSquare> food = new HashSet<WorldGridSquare>();

    Random rand;

    public Colony(int rows, int columns, int maxAnts)  //perhaps add option to move nest?
    {
        this.rows = rows;
        this.columns = columns;
        this.earthGrid = new WorldGridSquare[this.columns][this.rows];
        this.antColony = new ArrayList<Ant>();

        this.maxAnts = maxAnts;

        this.nests = new HashSet<WorldGridSquare>();
        this.food = new HashSet<WorldGridSquare>();
        setGrid(columns, rows);

        rand = new Random();

        //random nest location
        this.nestColumn =  rand.nextInt(this.columns);
        this.nestRow = rand.nextInt(this.rows);

        setBackground(Color.WHITE);

        resetGrid();
    }

    public void generateFood(int number)
    {
        for (int i = 0; i<number; i++)
        {
            Random rand = new Random();

            int randX = rand.nextInt(earthGrid.length);
            int randY = rand.nextInt(earthGrid[0].length);

            this.earthGrid[randX][randY].setIsFood(true);
            this.food.add(earthGrid[randX][randY]);
        }

        validate();
        repaint();
    }

    public void setGrid(int columns, int rows)
    {
        this.columns = columns;
        this.rows = rows;

        this.earthGrid = new WorldGridSquare[this.columns][this.rows];
        resetGrid();
        this.antColony.clear();

        //build nest
        earthGrid[nestColumn][nestRow].setHasNest(true);
        this.nests.add(earthGrid[nestColumn][nestRow]);

        repaint();
    }

    public void setGrid(int columns, int rows, int numberOfFoodSites)
    {
        this.columns = columns;
        this.rows = rows;

        this.earthGrid = new WorldGridSquare[this.columns][this.rows];
        resetGrid();
        this.antColony.clear();

        //build nest
        earthGrid[nestColumn][nestRow].setHasNest(true);
        this.nests.add(earthGrid[nestColumn][nestRow]);

//        this.earthGrid[45][45].setIsFood(true);
//        this.food.add(earthGrid[45][45]);
//
//        this.earthGrid[40][40].setIsFood(true);
//        this.food.add(earthGrid[40][40]);

        generateFood(numberOfFoodSites);


        repaint();
    }

    public void resetGrid()
    {
        this.nests.clear();
        this.food.clear();

        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++)
            {
                earthGrid[c][r] = new WorldGridSquare(c, r);
            }
        }

        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        double gridSquareWidth = (double) getWidth()/this.columns;
        double gridSquareHeight = (double) getHeight()/this.rows;

        for(int c = 0; c < this.columns; c++){
            for (int r = 0; r < this.rows; r++)
            {
                if(earthGrid[c][r].hasNest())
                    g.setColor(Color.RED); //Nest is colored red.
                else if(earthGrid[c][r].isFood())
                {
                    Random random = new Random(earthGrid[c][r].hashCode());
                    //create a color, can never be pure red, but will always be the same because of hash code seed
                    g.setColor(new Color(random.nextInt(250), random.nextInt(256), random.nextInt(256)));
                }

                else if (earthGrid[c][r].isBlocked())
                    g.setColor(Color.GRAY);
                else
                {
                    double nestPheremone = Math.min(1, (earthGrid[c][r].nestPheromoneLevel-1)/
                                                        WorldGridSquare.maxNestPheromoneLevel);
                    double foodPheromone = 0;
                    double maxFood = 0;
                    WorldGridSquare maxFoodGridSquare = null;

                    for (WorldGridSquare food : getFood())
                    {
                        if(earthGrid[c][r].getFoodPheromoneLevel(food) > maxFood)
                        {
                            maxFood = earthGrid[c][r].getFoodPheromoneLevel(food);
                            maxFoodGridSquare = food;
                        }
                        foodPheromone = Math.max(foodPheromone, Math.min(1, (earthGrid[c][r].getFoodPheromoneLevel(food)-1)/
                                                 WorldGridSquare.maxFoodPheromoneLevel));
                    }

                    if(nestPheremone > foodPheromone)
                    {
                        g.setColor(new Color(0, 255, 0, (int) (255*nestPheremone))); //use green as nest pheromone color
                    }
                    else if(maxFood > 0)
                    {
                        Random random = new Random(maxFoodGridSquare.hashCode()); //consistently seeded.
                        g.setColor(new Color(random.nextInt(250), random.nextInt(256), random.nextInt(256), (int) (255*foodPheromone)));
                    }
                    else
                    {
                        g.setColor(Color.white); //if there's nothing there, let the grid squares be white.
                    }
                }

                int gridSquareX_i = (int) (gridSquareWidth * c);
                int gridSquareY_i = (int) (gridSquareHeight * r);
                int currentGridSWidth = (int) (gridSquareWidth*(c+1) - gridSquareX_i);
                int currentGridSHeight = (int) (gridSquareHeight*(r+1) - gridSquareY_i);

                g.fillRect(gridSquareX_i+1, gridSquareY_i+1, Math.max(currentGridSWidth-1, 1), Math.max(currentGridSHeight-1, 1));
            }
        }

        for(Ant ant: antColony)
        {
            int c = ant.getCol();
            int r = ant.getRow();
            int gridSquareX_i = (int) (gridSquareWidth * c);
            int gridSquareY_i = (int) (gridSquareHeight * r);
            int currentGridSWidth = (int) (gridSquareWidth*(c+1) - gridSquareX_i);
            int currentGridSHeight = (int) (gridSquareHeight*(r+1) - gridSquareY_i);

            if(ant.goToNest)
                g.setColor(Color.GRAY);
            else
                g.setColor(Color.BLACK);

            g.fillRect(gridSquareX_i+2, gridSquareY_i+2, (int) currentGridSWidth-3, (int) currentGridSHeight-3);
        }
    }


    public Set<WorldGridSquare> getFood()
    {
        return this.food;
    }

    public Set<WorldGridSquare> getNests()
    {
        return this.nests;
    }

    public void step()
    {
        if(antColony.size() < this.maxAnts)
        {
            if (!this.nests.isEmpty())
            {
                int nextRandIndex = (int) (this.nests.size()*Math.random());
                antColony.add(new Ant((WorldGridSquare) this.nests.toArray()[nextRandIndex], earthGrid, this));
            }
        }
        else if(antColony.size() > maxAnts)
            antColony.remove(0);

        for(Ant ant : antColony)
            ant.step();

        for( int c = 0; c < this.columns; c++) {
            for(int r = 0; r < this.rows; r++)
            {
                earthGrid[c][r].step();
            }
        }

        repaint();
    }
//
//    public void setTileToAdd(Tile tile)
//    {
//        this.tile = tile;
//    }
//
//    public void setMaxAnts(int maxAnts)
//    {
//        this.maxAnts = maxAnts;
//        while(antColony.size() > maxAnts)
//            antColony.remove(0);
//    }
}
