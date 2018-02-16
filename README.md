# jedai-ui
UI for JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for any data integration 
task, e.g., Record Linkage, Entity Resolution and Link Discovery.

## How to run
In order to run the .jar file, you need [Oracle's](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) Java 8 (due to JavaFX).

If on Linux, make sure the .jar file has permissions to be executed (e.g. with `chmod +x jedai-ui.jar`) and that you are running Java 8 by Oracle, *not* OpenJDK which comes pre-installed in many distributions. 

## Dependencies
Dependencies are managed using Maven. They should be downloaded automatically, except the compiled [JedAI library](https://github.com/scify/JedAIToolkit), which is expected to be found in the path `lib/jedaiLibrary.jar`.
