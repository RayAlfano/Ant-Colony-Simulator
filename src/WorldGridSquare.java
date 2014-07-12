
import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Gabriel
 * Date: 7/13/12
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 *Additional contributions throughout by Ray Alfano until 7/15/12 7:48pm
 *
 */
public class WorldGridSquare {

    public static double maxFoodPheromoneLevel = 100.0;
    public static double maxNestPheromoneLevel = 100.0;
    public static double evaporationRate = .99; //this is "rho", and while .5 is recommended from pg. 221, it was too fast.
    public static double rho = evaporationRate; //just copied for terminology consistency from textbook.
    private boolean obstacleEncountered;
    private boolean hasNest;
    public Map<WorldGridSquare, Double> foodPheromoneMap = new HashMap<WorldGridSquare, Double>();
    public double nestPheromoneLevel = 1.0;
    private boolean isFood = false;

    int column;
    int row;

    public WorldGridSquare(int column, int row)
    {
        this.column = column;
        this.row = row;
    }

    public WorldGridSquare(int column, int row, int rho)
    {
        this.column = column;
        this.row = row;
        this.evaporationRate = rho;
    }

    public void setIsFood(boolean isFood)
    {
        this.isFood = isFood;
    }

    public void step()
    {
            // loops through the whole map
            for(WorldGridSquare food : foodPheromoneMap.keySet())
            {
                double foodPheromone = foodPheromoneMap.get(food);
                foodPheromone *= WorldGridSquare.evaporationRate;
                if(foodPheromone < 1)
                    foodPheromone = 1;
                if(foodPheromone > WorldGridSquare.maxFoodPheromoneLevel)
                    foodPheromone = WorldGridSquare.maxFoodPheromoneLevel;

                foodPheromoneMap.put(food, foodPheromone);
            }

        nestPheromoneLevel *= WorldGridSquare.evaporationRate;
        if (nestPheromoneLevel < 1)
            nestPheromoneLevel = 1;
        if (nestPheromoneLevel > WorldGridSquare.maxNestPheromoneLevel)
            nestPheromoneLevel = WorldGridSquare.maxNestPheromoneLevel;
    }

    public void setFoodPheromone(WorldGridSquare food, double foodPheromone)
    {
        if(foodPheromone > WorldGridSquare.maxFoodPheromoneLevel)
            foodPheromone = WorldGridSquare.maxFoodPheromoneLevel;

        this.foodPheromoneMap.put(food, foodPheromone);
    }

    public void setNestPheromone(double nestPheromone)
    {
        this.nestPheromoneLevel = nestPheromone;
        if (this.nestPheromoneLevel > WorldGridSquare.maxFoodPheromoneLevel)
            this.nestPheromoneLevel = WorldGridSquare.maxNestPheromoneLevel;
    }

    public double getFoodPheromoneLevel(WorldGridSquare food)
    {
        if(!this.foodPheromoneMap.containsKey(food))
            return 1;
        return foodPheromoneMap.get(food);
    }

    public double getNestPheromoneLevel()
    {
        return this.nestPheromoneLevel;
    }

    public boolean isBlocked()
    {
        return this.obstacleEncountered;
    }

    public boolean isFood()
    {
        return this.isFood;
    }

    public void setIsObstacle(boolean obstacleEncountered)
    {
        this.obstacleEncountered = obstacleEncountered;
    }

    public boolean hasNest()
    {
        return this.hasNest;
    }

    public void setHasNest(boolean hasNest)
    {
        this.hasNest = hasNest;
    }

}