# jedai-ui
UI for JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for any data integration 
task, e.g., Record Linkage, Entity Resolution and Link Discovery.

## Dependencies
In order to run the .jar file, you need [Oracle's](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) Java 8 (due to JavaFX).
Other dependencies are managed using Maven. They should be downloaded automatically, except for [JedAI library](https://github.com/scify/JedAIToolkit), which you have to manually build, then rename the resulting jar from `jedai-core-1.3-jar-with-dependencies.jar` to `jedaiLibrary.jar` and then move that jar to the path `{this_project_dir}/lib/`. 

## How to run
From the CLI navigate into project's directory, then build it with:
```
$ mvn clean package
```

You'll find the executable in `{this_project_dir}/target/jedai-ui-1.0-SNAPSHOT.jar`. Then run it with:
```
$ java -jar target/jedai-ui-1.0-SNAPSHOT.jar
```
If on Linux, make sure the .jar file has permissions to be executed (e.g. with `chmod +x jedai-ui.jar`) and that you are running Java 8 by Oracle, *not* OpenJDK which comes pre-installed in many distributions. 


