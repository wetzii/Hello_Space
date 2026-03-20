package Hello_Space;

import java.net.Socket;

public class Asteroid {
    private String name;
    private boolean is_potentially_hazardous_asteroid;
    private String nasa_jpl_url;
    private double missDistance;
    
    private double kilometersPerHour;
    private double estimatedDiameter;

    //Constructor
    public Asteroid(String name, boolean is_potentially_hazardous_asteroid, String nasa_jpl_url,
                    double missDistance, double kilometersPerHour, double estimatedDiameter) {
        this.name = name;
        this.is_potentially_hazardous_asteroid = is_potentially_hazardous_asteroid;
        this.nasa_jpl_url = nasa_jpl_url;
        this.missDistance = missDistance;
        this.kilometersPerHour = kilometersPerHour;
        this.estimatedDiameter = estimatedDiameter;
    }

    // Getter for GUI
    public String getName() { return name; }
    public boolean isPotentiallyHazardous() { return is_potentially_hazardous_asteroid; }
    public String getNasaJplUrl() { return nasa_jpl_url; }
    public double getMissDistance() { return missDistance; }
    public double getKilometersPerHour() { return kilometersPerHour; }
    public double getEstimatedDiameter() { return estimatedDiameter; }

    
    public String getInfo() {
        return String.format("Name: %s | Diameter: %.2f km | Distance: %.0f km | Speed: %.0f km/h | Hazardous: %s",
                name, estimatedDiameter, missDistance, kilometersPerHour, is_potentially_hazardous_asteroid);

     }
 
}
