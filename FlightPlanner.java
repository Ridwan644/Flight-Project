import java.util.ArrayList;
import java.util.Comparator;
import java.io.File; 
import java.util.Scanner; 
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Stack; 

public class FlightPlanner {

    public static void main(String[] args) {
        FlightPlanner planner = new FlightPlanner();
        planner.readFlightData("FlightDataFile.txt");
        planner.processRequestedFlights("RequestedFlights.txt");


    }
    
    //manages all cities 
    private ArrayList<CityNode> allCities; 
    private ArrayList<PathInfo> allPaths;


    public FlightPlanner(){
        allCities = new ArrayList(); 
        allPaths = new ArrayList<>();
    }

    //create a method like addFlight
    //first lets make sure both cities (the origin city and the destination city) exist
    public void addCity(String cityName)
    {
        //loop through every CityNode in allCities 
        for(CityNode city: allCities) 
        {
            if (city.cityName.equals(cityName))
            {

                return; 
            }
        }
        CityNode newCity = new CityNode(cityName); 
        allCities.add(newCity); 

        //for each CityNode check: if the city node equals the city name 
        //If you find one that matches exit the method cuz city already exists
        //if you finish the loop without finding it, add a new cityNode
    }

    public void addFlight(String origin, String destination, double cost, int time) 
    {
        //call addCity(origin) to make sure origin exists 
        addCity(origin); 
        addCity(destination); 

        //loop through allCities to find the CityNode object for origin 
        CityNode originCityNode = null; 
        for (CityNode city: allCities)
        {
            if(city.cityName.equals(origin))
            {
                originCityNode = city; 
                break; 
            }
        }
        //loop through allCities to find the CityNode object for destination
        CityNode destinationCityNode = null; 
        for (CityNode city: allCities) 
        {
            if(city.cityName.equals(destination))
            {
                destinationCityNode = city; 
                break; 
            }
        }

        //Add a flightInfo(destination, cost, time) to the orignin's flights list 
        //Add a FlightInfo(origin, cost, time) to the destination's flight list
        
        if(originCityNode != null && destinationCityNode != null) 
        {
            originCityNode.addFlight(destination, cost, time); 
            destinationCityNode.addFlight(origin, cost, time); 
        }
    }



    public void readFlightData(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            // First line contains the number of rows
            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine();
                int numFlights = Integer.parseInt(firstLine.trim());
                // We don't really need to use numFlights unless you want to validate.
            }

            // Now read each flight connection
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue; // Skip any blank lines
                }

                // Split the line by "|"
                String[] parts = line.split("\\|");

                if (parts.length == 4) {
                    String origin = parts[0].trim();
                    String destination = parts[1].trim();
                    double cost = Double.parseDouble(parts[2].trim());
                    int time = Integer.parseInt(parts[3].trim());

                    // Add the flight into the graph
                    addFlight(origin, destination, cost, time);
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found!");
            e.printStackTrace();
        }
    }

    //building the actual DFS to find all paths between cities 
    public void findAllPaths(String startCity, String endCity)
    {
        //start at startCity 
        //explore all paths toward endCity 
        //Save every complete path you find 
        //Later sort or choose top 3 best paths based on cost/time 
        
        allPaths.clear(); 

        //Stack to keep track of the paths we are currently exploring 
        Stack<PathInfo> stack = new Stack<>(); 

        //Create initial PathInfo with the start city 
        PathInfo initialPath = new PathInfo(); 
        initialPath.cities.add(startCity); 
        initialPath.totalCost = 0; 
        initialPath.totalTime = 0; 

        //Start by pushing a path containing only the start city 
        stack.push(initialPath); 
        
        while(!stack.isEmpty())
        {
            PathInfo currentPath = stack.pop(); 
            String currentCity = currentPath.cities.get(currentPath.cities.size() - 1);
            //check if the currentCity == endCity 
            if (currentCity.equals(endCity))
            {
                PathInfo completedPath = new PathInfo(currentPath); 
                allPaths.add(completedPath); 
            }
            //if yes then save the path 
            //if no then continue and expand to neighbors
            else{
                CityNode currentCityNode = null; 
            
                for(CityNode city: allCities) 
                {
                    if(city.cityName.equals(currentCity))
                    {
                        currentCityNode = city; 
                        break; 
                    }
                }

                for(FlightInfo flight: currentCityNode.flights) 
                {
                    if(!currentPath.cities.contains(flight.getDestinationCity()))
                    {
                            PathInfo newPath = new PathInfo(currentPath); 
                            newPath.cities.add(flight.getDestinationCity()); 
                            newPath.totalCost += flight.getCost(); 
                            newPath.totalTime += flight.getTime(); 
                            stack.push(newPath); 
                    }
                }
            }
        }
    }

    
    //Mehtod to find and print the top 3 flight paths between two cities 
    public void printTopPaths(String startCity, String endCity, String sortBy, PrintWriter output) {
        //findAll possible paths between startCity and endCity 
        findAllPaths(startCity, endCity);
        
        //if not paths are found print error message
        if (allPaths.isEmpty()) {
            System.out.println("No flight path from " + startCity + " to " + endCity + ".");
            output.println("No flight path from " + startCity + " to " + endCity + ".");
            return;
        }
        
        //sort the paths based on the requested criteria 
        if (sortBy.equalsIgnoreCase("Time")) {
            allPaths.sort(Comparator.comparingInt(path -> path.totalTime));
        } else if (sortBy.equalsIgnoreCase("Cost")) {
            allPaths.sort(Comparator.comparingDouble(path -> path.totalCost));
        }
    
        //print the flight header
        System.out.println("Flight: " + startCity + ", " + endCity + " (" + sortBy + ")");
        output.println("Flight: " + startCity + ", " + endCity + " (" + sortBy + ")");
    
        int count = 1;
        for (PathInfo path : allPaths) {
            if (count > 3) break;
    
            StringBuilder route = new StringBuilder();
            for (int i = 0; i < path.cities.size(); i++) {
                route.append(path.cities.get(i));
                if (i != path.cities.size() - 1) {
                    route.append(" -> ");
                }
            }
    
            String line = "Path " + count + ": " + route.toString()
                        + ". Time: " + path.totalTime + " Cost: " + (int) path.totalCost;
            
            System.out.println(line);
            output.println(line);
    
            count++;
        }
    }
    
        
    public void processRequestedFlights(String filename) {
    try {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        PrintWriter output = new PrintWriter("OutputFile.txt");

        // Read how many requests
        int numRequests = Integer.parseInt(scanner.nextLine().trim());

        for (int i = 1; i <= numRequests; i++) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            String[] parts = line.split("\\|");
            if (parts.length == 3) {
                String origin = parts[0].trim();
                String destination = parts[1].trim();
                String sortChoice = parts[2].trim();
                String sortBy = sortChoice.equalsIgnoreCase("T") ? "Time" : "Cost";

                System.out.println();
                System.out.println("Flight " + i + ": ");
                output.println();
                output.println("Flight " + i + ": ");

                // Here printTopPaths will print to both console and file
                printTopPaths(origin, destination, sortBy, output);
            }
        }

        scanner.close();
        output.close();
    } catch (FileNotFoundException e) {
        System.out.println("Error: Request file not found!");
        e.printStackTrace();
    }
}


    
}

//Make a small simple class called PathInfo whose job is to store one full completed path 
class PathInfo{
    //list of cities in the path 
    public ArrayList<String> cities; //list of cities in this path 
    public double totalCost; 
    public int totalTime; 

    //Constructor to make a new empty path 
    public PathInfo(){
        cities = new ArrayList<>(); 
        totalCost = 0; 
        totalTime = 0; 
    }

    //Constructo to clone another PathInfo
    public PathInfo(PathInfo other)
    {
        cities = new ArrayList<>(other.cities); 
        totalCost = other.totalCost; 
        totalTime = other.totalTime; 
    }
}