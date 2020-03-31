# jedai-ui
UI for JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for any data integration 
task, e.g., Record Linkage, Entity Resolution and Link Discovery.

This project is built on [JedaiToolkit](https://github.com/scify/JedAIToolkit) project.

## Dependencies
You can run from source using any of the JDK below. But in order to run from a .jar file, you need [Oracle's](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) Java 8 (due to JavaFX).
Other dependencies are managed using Maven. 

## How to run
#### From source (any JDK)
From the CLI navigate into project's directory, then run it with:
```
$ mvn javafx:run
```

#### From Jar (Oracle JDK)
From the CLI navigate into project's directory, then build it with:
```
$ mvn clean package
```

You'll find the executable in `{this_project_dir}/target/jedai-ui-{version}-jar-with-dependencies.jar`. Then run it with:
```
$ java -jar target/jedai-ui-{version}-jar-with-dependencies.jar
```
If on Linux, make sure the .jar file has permissions to be executed (e.g. with `chmod +x jedai-ui.jar`) and that you are running Oracle Java 8.
