import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Station{
    private String name;
    private String domain;
    private String location;
    private String stipend;
    private String branches;
    private String stationId;
    private String companyId;

    public Station(String name, String domain, String location, String stationId, String companyId) {
        this.name = name;
        this.domain = domain;
        this.location = location;
        this.stationId = stationId;
        this.companyId = companyId;
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

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public String getBranches() {
        return branches;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

        WebElement tab = driver.findElement(By.xpath("//table[@id='data-table-hrteam']//tbody"));
        List<WebElement> rows = tab.findElements(By.tagName("tr"));

        for(WebElement row:rows)
        {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            data.add(new Station(cols.get(2).getText(),
                    cols.get(3).getText(),
                    cols.get(1).getText(),
                    cols.get(5).getAttribute("stationid"),
                    cols.get(5).getAttribute("companyid")
            ));
        }
        for(Station s:data)
        {
            driver.navigate().to("http://psd.bits-pilani.ac.in/Student/StationproblemBankDetails.aspx?CompanyId="+s.getCompanyId()+"&StationId="+s.getStationId()+"&BatchIdFor=9&PSTypeFor=3");
            WebElement stp = null;
            WebElement br = null;
            try {
                stp = driver.findElement(By.xpath("//*[@id=\"Stipend\"]"));
                br = driver.findElement(By.xpath(("//*[@id=\"Project\"]/table/tbody/tr[5]/td[3]/div")));
            }catch(Exception e)
            {
//                e.printStackTrace();
            }
            s.setStipend(stp==null?"":stp.getText());
            s.setBranches(br==null?"":br.getText());
        }

//        System.out.println(data.size());
//        System.out.println(data);

        // write to csv
        File file = new File("./data/stations_"+new SimpleDateFormat("dd-MM-yyyy").format(new Date())+".csv");

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file));){
            bw.write("Name,Domain,Stipend,Location,Branches");
            bw.newLine();
            for(Station st:data)
            {
                bw.write(st.getName()+","+st.getDomain()+","+st.getStipend()+","+st.getLocation()+","+st.getBranches());
                bw.newLine();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        driver.close();
    }
}
