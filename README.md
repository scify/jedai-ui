# jedai-ui
UI for JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for any data integration 
task, e.g., Record Linkage, Entity Resolution and Link Discovery.

## Dependencies
In order to run the .jar file, you need [Oracle's](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) Java 8 (due to JavaFX).
Other dependencies are managed using Maven. 

## How to run
This should be used as a submodule of the main [JedaiToolkit](https://github.com/scify/JedAIToolkit) project.
From the CLI navigate into project's directory, then build it with:
```
$ mvn clean package
```

You'll find the executable in `{this_project_dir}/target/jedai-ui-{version}-SNAPSHOT.jar`. Then run it with:
```
$ java -jar target/jedai-ui-{version}-SNAPSHOT.jar
```
If on Linux, make sure the .jar file has permissions to be executed (e.g. with `chmod +x jedai-ui.jar`) and that you are running Java 8 by Oracle, *not* OpenJDK which comes pre-installed in many distributions. 


