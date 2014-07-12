
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Gabriel
 * Date: 7/13/12
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 * Additional contributions by Ray Alfano throughout until 7/15/12 7:45pm
 */
public class Ant {

    Set<WorldGridSquare> discoveredFood = new HashSet<WorldGridSquare>();

    public static double dropOffRate = .99; //the amount of pheromone dropped based upon the food source
    public static double bestNextSquare = .99; //drives the opportunity to use the routes already taken

    //Defines current vector position in a cartesian grid
    private int x,y;

    public boolean goToNest; //prepares the ant for returning home
    double maxPheromone = 10.0; //the maximum pheromone it can lay
    int movementsMade = 0; //track the number of a steps an ant takes.

    WorldGridSquare[][] antNetwork;
    private Colony antColony;

    public Ant(WorldGridSquare startingVertex, WorldGridSquare[][] antNetwork, Colony antColony)
    {
        this.x = startingVertex.column;
        this.y = startingVertex.row;
        this.antNetwork = antNetwork;
        this.antColony = antColony;
    }

    public void die()
    {
        goToNest = false;
        movementsMade = 0;
        discoveredFood.clear();
        Set<WorldGridSquare> nests = antColony.getNests();
        if(!nests.isEmpty())
        {
            int nestIndex = (int) (nests.size() * Math.random());
            WorldGridSquare nest = (WorldGridSquare) nests.toArray()[nestIndex];
            this.x = nest.column;
            this.y = nest.row;
        }
    }

    public void step()
    {
        double chancetoTakeBest = Math.random(); //I don't like this, it s/b based upon pg. 222, but temp for random movement
        movementsMade++;

        discoveredFood.retainAll(antColony.getFood());

        if(this.goToNest) //has found ALL food and is working way back home.
        {
            if(this.antNetwork[this.x][this.y].hasNest())
                die(); //ant "dies" upon return to the nest
            else
            {
                double currentMaxNestPheromone = 0;
                Map<WorldGridSquare, Double> maxFoodMap = new HashMap<WorldGridSquare, Double>();
                List<WorldGridSquare> maxNestGridSquaresList = new ArrayList<WorldGridSquare>();
                List<WorldGridSquare> allNeighborGridSquaresList = new ArrayList<WorldGridSquare>();
                double totalNeighborPheromones = 0;

                for(int c = -1; c <=1; c++)
                {
                    if(this.x+c < 0 || this.x+c >= antNetwork.length)
                        continue;

                    for (int r = -1; r <= 1; r++)
                    {
                        //ignore self, edges
                        if(c==0 && r ==0)
                            continue;
                        if (y+r < 0 || y+r >= antNetwork[0].length)
                            continue;

                        if(!antNetwork[this.x+c][this.y+r].isBlocked())
                        {
                            allNeighborGridSquaresList.add(antNetwork[this.x+c][this.y+r]);
                            totalNeighborPheromones += antNetwork[this.x+c][this.y+r].nestPheromoneLevel;

                            if(antNetwork[this.x+c][this.y+r].getNestPheromoneLevel() > currentMaxNestPheromone)
                            {
                                currentMaxNestPheromone = antNetwork[this.x+c][this.y+r].getNestPheromoneLevel();
                                maxNestGridSquaresList.clear();
                                maxNestGridSquaresList.add(antNetwork[this.x+c][this.y+r]);
                            }
                            else if(antNetwork[this.x+c][this.y+r].getNestPheromoneLevel() == currentMaxNestPheromone)
                                maxNestGridSquaresList.add(antNetwork[this.x+c][this.y+r]);

                            for(WorldGridSquare food : discoveredFood) {
                                if(!maxFoodMap.containsKey(food) || antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food)
                                                                    > maxFoodMap.get(food))
                                    maxFoodMap.put(food, antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food));
                            }
                        }
                    }
                }

                if(antNetwork[x][y].isFood())
                    maxFoodMap.put(antNetwork[this.x][this.y], WorldGridSquare.maxFoodPheromoneLevel);

                for(WorldGridSquare food : discoveredFood)
                {
                    antNetwork[this.x][this.y].setFoodPheromone(food, maxFoodMap.get(food) * Ant.dropOffRate);
                }

                //There's a % chance, essentially, that the Ant will choose the best route
                if(Ant.bestNextSquare > chancetoTakeBest)
                {
                    if(!maxNestGridSquaresList.isEmpty())
                    {
                        int randBestGSIndex = (int) (maxNestGridSquaresList.size()*Math.random());
                        WorldGridSquare bestGridSquare = maxNestGridSquaresList.get(randBestGSIndex);

                        this.x = bestGridSquare.column;
                        this.y = bestGridSquare.row;
                    }
                }
                else //if random didn't result in the best route, choose a (partially) random alternate
                {
                    double currentPheromones = 0;
                    double randPheromoneLevel = totalNeighborPheromones * Math.random();
                    for(WorldGridSquare neighbor : allNeighborGridSquaresList)
                    {
                        currentPheromones += neighbor.getNestPheromoneLevel();
                        if(currentPheromones > randPheromoneLevel)
                        {
                            this.x = neighbor.column;
                            this.y = neighbor.row;
                            break;
                        }
                    }
                }
            }
        }
        else //go hunting for food in the wild
        {
            if(antNetwork[this.x][this.y].isFood())
            {
                discoveredFood.add(antNetwork[this.x][this.y]);
                if(discoveredFood.size() >= antColony.getFood().size())
                {
                    movementsMade = 0; //reset the track
                    goToNest = true; //start heading home
                    return;
                }
            }
            else if(antNetwork[this.x][this.y].hasNest())
            {
                if (movementsMade > 1)
                {
                    die();
                    return;
                }
            }

            double currentMaxFoodPheromone = 0;
            double currentMaxNestPheromone = 0;

            Map<WorldGridSquare, Double> maxFoodMap = new HashMap<WorldGridSquare, Double>();
            List<WorldGridSquare> maxFoodGridSquaresList = new ArrayList<WorldGridSquare>();
            List<WorldGridSquare> allNeighborGridSquaresList = new ArrayList<WorldGridSquare>();
            double totalNeighborPheromones = 0;

            for(int c = -1; c <=1; c++)
            {
                if(this.x+c < 0 || this.x+c >= antNetwork.length)
                    continue;

                for (int r = -1; r <= 1; r++)
                {
                    //ignore self, edges
                    if(c==0 && r ==0)
                        continue;
                    if(this.y+r < 0 || this.y+r >= antNetwork[0].length)
                        continue;

                    if(!antNetwork[this.x+c][this.y+r].isBlocked())
                    {
                        allNeighborGridSquaresList.add(antNetwork[this.x+c][this.y+r]);

                        if(currentMaxFoodPheromone == 0)
                            maxFoodGridSquaresList.add(antNetwork[this.x+c][this.y+r]);

                        for(WorldGridSquare food : discoveredFood) {
                            if(!maxFoodMap.containsKey(food) || antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food)
                                                                > maxFoodMap.get(food))
                                maxFoodMap.put(food, antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food));
                        }

                        if(antNetwork[this.x][this.y].isFood())
                            maxFoodMap.put(antNetwork[this.x][this.y], WorldGridSquare.maxFoodPheromoneLevel);

                        for(WorldGridSquare food : discoveredFood)
                        {
                            antNetwork[this.x][this.y].setFoodPheromone(food, maxFoodMap.get(food)*Ant.dropOffRate);
                        }

                        if(antNetwork[this.x+c][this.y+r].getNestPheromoneLevel() > currentMaxNestPheromone)
                        {
                            currentMaxNestPheromone = antNetwork[this.x+c][this.y+r].getNestPheromoneLevel();
                        }

                        if(antColony.getFood().isEmpty())
                            totalNeighborPheromones += 1;
                        else
                        {
                            for (WorldGridSquare food : antColony.getFood())
                            {
                                if(discoveredFood.contains(food))
                                    continue;

                                totalNeighborPheromones += antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food);

                                if(antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food) > currentMaxFoodPheromone)
                                {
                                    currentMaxFoodPheromone = antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food);
                                    maxFoodGridSquaresList.clear();
                                    maxFoodGridSquaresList.add(antNetwork[this.x+c][this.y+r]);
                                }
                                else if(antNetwork[this.x+c][this.y+r].getFoodPheromoneLevel(food) == currentMaxFoodPheromone)
                                {
                                    maxFoodGridSquaresList.add(antNetwork[this.x+c][this.y+r]);
                                }
                            }
                        }
                    }
                }
            }

            if(antNetwork[this.x][this.y].hasNest())
                currentMaxNestPheromone = WorldGridSquare.maxNestPheromoneLevel;

            antNetwork[this.x][this.y].setNestPheromone(currentMaxNestPheromone * Ant.dropOffRate);

            if(Ant.bestNextSquare > chancetoTakeBest)
            {
                if(!maxFoodGridSquaresList.isEmpty())
                {
                    int randBestGSIndex = (int) (maxFoodGridSquaresList.size()*Math.random());
                    WorldGridSquare bestGridSquare = maxFoodGridSquaresList.get(randBestGSIndex);

                    this.x = bestGridSquare.column;
                    this.y = bestGridSquare.row;
                }
            }

            else //if random didn't result in the best route, choose a (partially) random alternate
            {
                double currentPheromones = 0;
                double randPheromoneLevel = totalNeighborPheromones * Math.random();

                for(WorldGridSquare neighbor : allNeighborGridSquaresList)
                {
                    if(antColony.getFood().isEmpty())
                    {
                        currentPheromones +=1;
                        if(currentPheromones > randPheromoneLevel)
                        {
                            this.x = neighbor.column;
                            this.y = neighbor.row;
                            break;
                        }
                    }
                    else
                    {
                        for(WorldGridSquare food : antColony.getFood())
                        {
                            if(discoveredFood.contains(food))
                                continue;
                            currentPheromones += neighbor.getFoodPheromoneLevel(food);
                            if(currentPheromones > randPheromoneLevel)
                            {
                                this.x = neighbor.column;
                                this.y = neighbor.row;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getCol()
    {
        return this.x;
    }

    public int getRow()
    {
        return this.y;
    }

    public boolean isReturningHome()
    {
        return goToNest;
    }









}
