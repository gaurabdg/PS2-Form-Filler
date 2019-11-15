**Make sure you have java installed on your machine**

download link: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

**Chrome Compatibility**

The chromedriver used is dependent on your chrome browser version.
Check here:
https://chromedriver.chromium.org/downloads

After downloading, paste and overwrite it in the `drivers` folder.

# Running executable

1. Download the repo ZIP
2. Go into the root directory of the project and run from terminal/cmd
    - For downloading the station data  
    run `java -jar PS2_FormFiller-1.0-SNAPSHOT-jar-with-dependencies.jar --fetch <username> <password>`
    - To fill the form from your edited sheet  
    run `java -jar PS2_FormFiller-1.0-SNAPSHOT-jar-with-dependencies.jar --upload <username> <password>  <path to modified pref sheet>`

# Building from source

1. Fork/clone the repo
2. Setup Maven
    - Download https://maven.apache.org/download.cgi 
    - Install maven https://www.javahelps.com/2017/10/install-apache-maven-on-linux.html (linux)
    https://www.mkyong.com/maven/how-to-install-maven-in-windows/ (windows)
3. cd into project repo root
4. run `mvn clean compile assembly:single`