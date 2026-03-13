# ☄ Hello Space — Asteroid Tracker
> A Java desktop app that pulls live data from NASA's API and shows you which asteroids are flying past Earth today — with a dark space-themed GUI, color-coded hazard warnings and direct links to NASA's database.

---

## What It Does

Every time you run it, the app fetches today's near-Earth objects straight from NASA's NeoWs API and displays them in a sortable table. You can see at a glance how big each asteroid is, how close it gets, how fast it's moving, and whether NASA considers it potentially hazardous.

No database, no setup, no config files. Just run it and the data appears.

**Use cases:**
- You're learning Java and want a real project that actually does something cool
- You want to play around with REST APIs and JSON parsing in Java
- You're into space stuff and want a little desktop tool that gives you daily asteroid data
- School / uni project that needs a working GUI + API integration

---

## What You See

| Column | What it means |
|---|---|
| Name | Official NASA designation |
| ⌀ Diameter (km) | Estimated max diameter in kilometers |
| Distance (km) | How close it gets to Earth at closest approach |
| Speed (km/h) | Relative velocity at closest approach |
| Status | ⚠ Hazardous or ✔ Safe — color coded red/green |
| NASA URL | Direct link to the NASA JPL page for that object |

Rows are **red** for potentially hazardous asteroids and **green** for safe ones. You can click any column header to sort.

---

## Download & Run

### Windows
Grab the `Hello_Space.exe` from the [releases page](../../releases) — just double click and it starts. Requires Java 11+ to be installed on your system. If you don't have Java yet, grab it from [adoptium.net](https://adoptium.net/).

### Linux
Binary is coming soon — for now use the jar (see below).

### All Platforms — JAR
If you have Java 11+ installed you can run the jar directly:
```bash
java -jar Hello_Space.jar
```

> The release builds already have an API key bundled in — NASA's NeoWs API is completely free so no costs will ever occur for you.

---

## Build From Source

If you want to tinker with the code yourself:

**1. Get a free NASA API key**

Go to [api.nasa.gov](https://api.nasa.gov/), sign up and you'll get a key by email. Takes about 2 minutes.

**2. Put your key into the code**

Open `AsteroidFetcher.java` and drop it in on line 15:
```java
private static final String API_KEY = "YOUR_NASA_API_KEY_HERE";
```

**3. Add Gson to your classpath**

Download [Gson](https://github.com/google/gson) and add `gson-2.x.x.jar` to your project.

**4. Compile + run**
```bash
javac -cp .;gson-2.x.x.jar Hello_Space/*.java
java  -cp .;gson-2.x.x.jar Hello_Space.RunCode
```

---

## Project Structure

```
Hello_Space/
├── Asteroid.java          # Data model — holds all info for one asteroid
├── AsteroidFetcher.java   # Fetches + parses the NASA API response
├── Hello_Space_GUI.java   # The window — starfield background, table, layout
└── RunCode.java           # Entry point — launches GUI + fetches today's data
```

---

## Common Errors

**`403 Forbidden`** *(source build only)*

Your API key is wrong or not activated yet. New keys can take up to 30 minutes to go live. Double-check there are no spaces or missing characters.

**Empty table / no data**

Happens when NASA has no entries for today's date. Try a different date by changing `LocalDate.now()` in `RunCode.java` to something like `LocalDate.parse("2025-06-15")`.

**`ClassNotFoundException: com.google.gson...`** *(source build only)*

Gson jar is missing from your classpath. Make sure the path to the `.jar` file is correct when compiling and running.

---

## API Used

[NASA NeoWs (Near Earth Object Web Service)](https://api.nasa.gov/) — free to use, no costs.

---

*Data provided by NASA · Built with Java Swing · No third-party UI frameworks*