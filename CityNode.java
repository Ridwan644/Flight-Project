import java.util.LinkedList; 
public class CityNode
{
    String cityName; 
    LinkedList<FlightInfo>  flights; 

    public CityNode(String cityName)
    {
        this.cityName = cityName; 
        flights = new LinkedList<>(); 
    }

    public void addFlight(String destinationCity, double cost, int time) 
    {
        //create a new FlightInfo object using these values
        FlightInfo newFlight = new FlightInfo(destinationCity, cost, time); 

        //add that flightinfo to the linked list flights 
        flights.add(newFlight); 
    }
}