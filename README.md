starter-snake-java
===

[![Build Status](https://travis-ci.org/battlesnakeio/starter-snake-java.svg?branch=master)](https://travis-ci.org/battlesnakeio/starter-snake-java)

A simple [Battlesnake AI](http://battlesnake.io) written in Java.

Visit [https://docs.battlesnake.io](https://docs.battlesnake.io) 
for API documentation and instructions for running your AI.

This snake is built using a lightweight http server Spark framework - [http://sparkjava.com/documentation](http://sparkjava.com/documentation)

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Requirements
---

- Install JDK 8 [http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- Install Maven [https://maven.apache.org/install.html](https://maven.apache.org/install.html)

Running the snake
---

```bash
mvn compile exec:exec
```

Snake will start up on port 8080

This snake has also been run from within Intellij successfully. 

Run the tests
---

```bash
mvn compile test
```


Executable Jar
---

```bash
mvn compile package
```

Will result in a jar file in `target` called `starter-snake-java.jar`

You can then run this file with the command

```bash
java -jar target/starter-snake-java.jar
```


Deploying to Heroku
---

1) Create a new Heroku app:
```
heroku create [APP_NAME]
```

2) Deploy code to Heroku servers:
```
git push heroku master
```

3) Open Heroku app in browser:
```
heroku open
```
or visit [http://APP_NAME.herokuapp.com](http://APP_NAME.herokuapp.com).

4) View server logs with the `heroku logs` command:
```
heroku logs --tail
```


Improvement
---
Defensive:
----
1) (Easy) Head-2-Head: when next step will reach a food, if head-2-head happen in the next step, avoid it if 
we are equal or shorter. 

2) (Hard) Avoid trap ourselves.

Aggressive:
----
1. (Middle) Eat others food: find the nearest food of each snake and try to take those closer to 
us than the any others. (Make sure we can eat the food earlier than others); 
2. (Middle) Enhancement of the one above, if the other snake moving to the same food and 
is shorter, try to eat the food at the same time so that we can kill it.
3. (Hard) Trap others, once we grow to some length (22?), start to move in some pattern to lock some area 
or try to trap other snakes. 

