package Hello_Space;

import java.time.LocalDate;




public class RunCode {
    public static void main(String[] args) throws Exception {
        // Heutiges Datum automatisch ermitteln (Format: YYYY-MM-DD)
        String today = LocalDate.now().toString();

        Hello_Space_GUI gui = new Hello_Space_GUI();
        AsteroidFetcher.fetch(gui, today);
    
    }
}
