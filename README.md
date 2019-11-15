Make sure you have java installed on your machine

download link: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

Running executable

Go into the root directory of the project and run from terminal/cmd
`java -jar PS2_FormFiller-1.0-SNAPSHOT-jar-with-dependencies.jar <username> <password>`

Building from source

1. Fork/clone the repo
2. Setup Maven
    - Download https://maven.apache.org/download.cgi 
    - Install maven https://www.javahelps.com/2017/10/install-apache-maven-on-linux.html(linux)
    https://www.mkyong.com/maven/how-to-install-maven-in-windows/(windows)
3. cd into project repo root
4. run `mvn clean compile assembly:single`
5. run `java -jar PS2_FormFiller-1.0-SNAPSHOT-jar-with-dependencies.jar <username> <password>`