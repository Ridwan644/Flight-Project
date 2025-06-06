public class FlightInfo
{
    //1. Declare the fields here
    private String destinationCity; 
    private double cost; 
    private int time; 


    //2. Constructor to set the fields 
    public FlightInfo(String destinationCity, double cost, int time) 
    {
        this.destinationCity = destinationCity; 
        this.cost = cost; 
        this.time = time; 
    }

    public String getDestinationCity()
    {
        return destinationCity; 
    }

    public double getCost()
    {
        return cost; 
    }

    public int getTime()
    {
        return time; 
    }
}