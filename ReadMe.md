# ☄ Hello Space — Asteroid Tracker

> a java desktop app that pulls live data from nasa's api and shows you which asteroids are flying past earth today — with a dark space-themed gui, color-coded hazard warnings and direct links to nasa's database.

---

## what it does

every time you run it, the app fetches today's near-earth objects straight from nasa's neows api and displays them in a sortable table. you can see at a glance how big each asteroid is, how close it gets, how fast it's moving, and whether nasa considers it potentially hazardous.

no database, no setup, no config files. just run it and the data appears.

**use cases:**
- you're learning java and want a real project that actually does something cool
- you want to play around with rest apis and json parsing in java
- you're into space stuff and want a little desktop tool that gives you daily asteroid data
- school / uni project that needs a working gui + api integration

---

## what you see

| column | what it means |
|---|---|
| name | official nasa designation |
| ⌀ diameter (km) | estimated max diameter in kilometers |
| distance (km) | how close it gets to earth at closest approach |
| speed (km/h) | relative velocity at closest approach |
| status | ⚠ hazardous or ✔ safe — color coded red/green |
| nasa url | direct link to the nasa JPL page for that object |

rows are **red** for potentially hazardous asteroids and **green** for safe ones. you can click any column header to sort.

---

## requirements

- java 11 or higher
- that's it if you're using the release jar

---

## setup

### option 1 — just run it (recommended)

download the latest `.jar` from the [releases page](../../releases) and run it:

```bash
java -jar HelloSpace.jar
```

window opens, data loads, done. no api key needed, no extra setup.

> the release jar already has an api key bundled in — nasa's neows api is completely free so no costs will ever occur for you.

---

### option 2 — build from source

if you want to tinker with the code yourself:

**1. get a free nasa api key**

go to [api.nasa.gov](https://api.nasa.gov/), sign up and you'll get a key by email. takes about 2 minutes.

**2. put your key into the code**

open `AsteroidFetcher.java` and drop it in on line 15:

```java
private static final String API_KEY = "YOUR_NASA_API_KEY_HERE";
```

**3. add gson to your classpath**

download [gson](https://github.com/google/gson) and add `gson-2.x.x.jar` to your project.

**4. compile + run**

```bash
javac -cp .;gson-2.x.x.jar Hello_Space/*.java
java  -cp .;gson-2.x.x.jar Hello_Space.RunCode
```

window opens, data loads, table fills up. that's it.

---

## project structure

```
Hello_Space/
├── Asteroid.java          # data model — holds all info for one asteroid
├── AsteroidFetcher.java   # fetches + parses the nasa api response
├── Hello_Space_GUI.java   # the window — starfield background, table, layout
└── RunCode.java           # entry point — launches gui + fetches today's data
```

---

## common errors

**`403 Forbidden`** *(source build only)*
your api key is wrong or not activated yet. new keys can take up to 30 min to go live. double-check there are no spaces or missing characters.

**empty table / no data**
happens when nasa has no entries for today's date. try a different date by changing `LocalDate.now()` in `RunCode.java` to something like `LocalDate.parse("2025-06-15")`.

**`ClassNotFoundException: com.google.gson...`** *(source build only)*
gson jar is missing from your classpath. make sure the path to the `.jar` file is correct when compiling and running.

---

## api used

[NASA NeoWs (Near Earth Object Web Service)](https://api.nasa.gov/) — free to use, no costs.

---

*data provided by nasa · built with java swing · no third-party ui frameworks*