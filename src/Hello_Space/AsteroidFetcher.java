package Hello_Space;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AsteroidFetcher {

    // Trage hier deinen NASA API Key ein: https://api.nasa.gov/
    // Ohne Key wird DEMO_KEY verwendet (stark limitiert: 30 Anfragen/Stunde)
    private static final String API_KEY = "x6Git12b2CfyHeAmJKKrTh2iamfmKkGddhZFm9Zc";

    public static void fetch(Hello_Space_GUI gui, String date) throws Exception {

        String urlStr = "https://api.nasa.gov/neo/rest/v1/feed"
                      + "?start_date=" + date
                      + "&end_date=" + date
                      + "&api_key=" + API_KEY;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(urlStr).openStream())
        );
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(result.toString()).getAsJsonObject();
        JsonObject neo = json.getAsJsonObject("near_earth_objects");

        JsonArray asteroidsArray = neo.getAsJsonArray(date);
        if (asteroidsArray == null || asteroidsArray.size() == 0) {
            System.out.println("Keine Asteroiden für den Tag gefunden.");
            return;
        }
        
        for (JsonElement element : asteroidsArray) {
            JsonObject a = element.getAsJsonObject();

            String name = a.get("name").getAsString();
            boolean hazardous = a.get("is_potentially_hazardous_asteroid").getAsBoolean();
            String nasaUrl = a.get("nasa_jpl_url").getAsString();

            // Durchmesser
            double diameter = 0;
            JsonObject diamObj = a.getAsJsonObject("estimated_diameter");
            if (diamObj != null && diamObj.has("kilometers")) {
                JsonObject kmObj = diamObj.getAsJsonObject("kilometers");
                if (kmObj != null && kmObj.has("estimated_diameter_max")) {
                    diameter = kmObj.get("estimated_diameter_max").getAsDouble();
                }
            }

            // Close Approach Daten
            double distance = 0;
            double speed = 0;
            JsonArray closeApproachArray = a.getAsJsonArray("close_approach_data");
            if (closeApproachArray != null && closeApproachArray.size() > 0) {
                JsonObject approach = closeApproachArray.get(0).getAsJsonObject();

                if (approach.has("miss_distance")) {
                    JsonObject missDist = approach.getAsJsonObject("miss_distance");
                    if (missDist.has("kilometers")) {
                        distance = missDist.get("kilometers").getAsDouble();
                    }
                }

                if (approach.has("relative_velocity")) {
                    JsonObject velocity = approach.getAsJsonObject("relative_velocity");
                    if (velocity.has("kilometers_per_hour")) {
                        speed = velocity.get("kilometers_per_hour").getAsDouble();
                    }
                }
            }

            // Asteroid Objekt erstellen und zur GUI hinzufügen
            Asteroid asteroid = new Asteroid(name, hazardous, nasaUrl, distance, speed, diameter);
            gui.addAsteroid(asteroid);
        }
    }
}