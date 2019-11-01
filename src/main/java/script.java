import org.jsoup.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

class Station{
    private String name;
    private String domain;
    private String location;
    private int stipend;
    private String branches;

    public Station(String name, String domain, String location) {
        this.name = name;
        this.domain = domain;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStipend() {
        return stipend;
    }

    public void setStipend(int stipend) {
        this.stipend = stipend;
    }

    public String getBranches() {
        return branches;
    }

    public void setBranches(String branches) {
        this.branches = branches;
    }
}

public class script {
    public static void main(String args[])
    {
        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver");
        WebDriver driver = new ChromeDriver();

        // login
        driver.navigate().to("http://psd.bits-pilani.ac.in");

        WebElement username = driver.findElement(By.id("TxtEmail"));
        username.clear();
        username.sendKeys("f20160255@hyderabad.bits-pilani.ac.in");

        WebElement pass = driver.findElement(By.id("txtPass"));
        pass.clear();
        pass.sendKeys("4V113RHO");

        driver.findElement(By.id("Button1")).click();

        // scrape table
        driver.navigate().to("http://psd.bits-pilani.ac.in/Student/ViewActiveStationProblemBankData.aspx");

        ArrayList<Station> data = new ArrayList<>();
        Document doc = Jsoup.parse(driver.getPageSource());
        Element tableData = doc.select("table").get(0);
        Elements rows = tableData.select("tr");
        rows.remove(0);

        for(Element row:rows)
        {
            Elements children = row.children();
            data.add(new Station(
                    children.get(2).text(),
                    children.get(3).text(),
                    children.get(1).text()
            ));
        }

        System.out.println(data.size());

        // write to csv
        File file = new File("./data/stations_"+new SimpleDateFormat("dd-MM-yyyy").format(new Date())+".csv");

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file));){
            bw.write("Name,Domain,Location");
            bw.newLine();
            for(Station st:data)
            {
                bw.write(st.getName()+","+st.getDomain()+","+st.getLocation());
                bw.newLine();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        driver.close();
    }
}
