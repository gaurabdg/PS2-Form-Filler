
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Station{
    private String name;
    private String domain;
    private String location;
    private String stipend;
    private String branches;
    private String stationId;
    private String companyId;
    private String description;
    private String skillSet;
    private String prefElecs;

    public String getSkillSet() {
        return skillSet;
    }

    public String getPrefElecs() {
        return prefElecs;
    }

    public void setSkillSet(String skillSet) {
        this.skillSet = skillSet;
    }

    public void setPrefElecs(String prefElecs) {
        this.prefElecs = prefElecs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Station(String name, String domain, String location, String stationId, String companyId) {
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

    String getStationId() {
        return stationId;
    }

    String getCompanyId() {
        return companyId;
    }

    public void setBranches(String branches) {
        this.branches = branches;
    }
}

public class script {
    public static void main(String[] args)
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
        HashSet<Integer> companyIds = new HashSet<>();

        WebElement tab = driver.findElement(By.xpath("//table[@id='data-table-hrteam']//tbody"));
        List<WebElement> rows = tab.findElements(By.tagName("tr"));
        ArrayList<Station> newps = new ArrayList<>();
        HashSet prev = null;

        try(ObjectInputStream fin = new ObjectInputStream(new FileInputStream("./data/prevData.ser")))
        {
            prev = (HashSet)fin.readObject();
        }
        catch(Exception e){}

        for(WebElement row:rows)
        {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            Station t = new Station(cols.get(2).getText(),
                    cols.get(3).getText(),
                    cols.get(1).getText(),
                    cols.get(5).getAttribute("stationid"),
                    cols.get(5).getAttribute("companyid")
            );

            data.add(t);

            if(!prev.contains(Integer.parseInt(cols.get(5).getAttribute("companyid"))))
                newps.add(t);
            companyIds.add(Integer.parseInt(cols.get(5).getAttribute("companyid")));
        }

        if(prev.size()!=companyIds.size())
            System.out.println(Math.abs(prev.size() - companyIds.size()) +" New Stations Added!");

        // store meta
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("./data/meta.txt")))
        {
            bw.write(Integer.toString(companyIds.size()));

        }catch(Exception e){}

        // serialize HashSet
        try(ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream("./data/prevData.ser")))
        {
            fout.writeObject(companyIds);
        }catch(Exception e){}

        if(!newps.isEmpty())
            for(Station s:newps)
                System.out.println(s.getName()+" || "+s.getLocation()+" || "+s.getDomain());

        for(Station s:data)
        {
            System.out.print("Fetching details of station: "+s.getName());
            driver.navigate().to("http://psd.bits-pilani.ac.in/Student/StationproblemBankDetails.aspx?CompanyId="+s.getCompanyId()+"&StationId="+s.getStationId()+"&BatchIdFor=9&PSTypeFor=3");
            WebElement stp = null;
            WebElement br = null;
            WebElement des = null;
            WebElement skillSet = null;
            WebElement prefElec = null;
            try {
                stp = driver.findElement(By.xpath("//*[@id=\"Stipend\"]"));
                br = driver.findElement(By.xpath(("//*[@id=\"Project\"]/table/tbody/tr[5]/td[3]/div")));
                des = driver.findElement(By.xpath("/html/body/form/div[3]/div[2]/div/div[2]/table[1]/tbody/div/table/tbody/tr[2]/td[2]"));
                skillSet = driver.findElement(By.xpath("/html/body/form/div[3]/div[2]/div/div[2]/table[1]/tbody/div/table/tbody/tr[3]/td[2]"));
                prefElec = driver.findElement(By.xpath("/html/body/form/div[3]/div[2]/div/div[2]/table[1]/tbody/div/table/tbody/tr[5]/td[2]"));

            }catch(Exception e)
            {}
            s.setStipend(stp==null?"":stp.getText());
            s.setBranches(br==null?"":br.getText());
            s.setSkillSet(skillSet==null?"":skillSet.getText());//==null?"":skillSet.getText()));
            s.setPrefElecs(prefElec==null?"":prefElec.getText());
            String clean = "";
            try{
                assert des != null;
                clean = des.getText();
            }
            catch (NullPointerException e)
            {}
            clean = clean==null?"":clean.replace(',', '|');
            s.setDescription(clean);
            System.out.println(" || "+s.getStipend()+" || "+s.getBranches());
        }

        // write to csv
        File file = new File("./data/stations_"+new SimpleDateFormat("dd-MM-yyyy").format(new Date())+".csv");

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write("Name,Domain,Stipend,Location,Branches,Project Description,Reqd Skill Sets, Reqd Electives");
            bw.newLine();
            for(Station st:data)
            {
                bw.write(st.getName()+","+st.getDomain()+","+st.getStipend()+","+st.getLocation()+","+st.getBranches()+","+st.getDescription()+","+st.getSkillSet()+","+st.getPrefElecs());
                bw.newLine();
            }
        }catch(IOException e){}

        driver.close();
    }
}
