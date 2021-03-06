import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

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
    private static String[] headers = {"StationId", "Name", "Domain", "Stipend", "Location", "Branches",
            "Project Description", "Reqd Skill Sets", "Reqd Electives"};
    private static void fetchData(WebDriver driver)
    {
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

        System.out.println("Fetching stations ...");
        for(WebElement row:rows)
        {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            Station t = new Station(cols.get(2).getText(),
                    cols.get(3).getText(),
                    cols.get(1).getText(),
                    cols.get(4).getAttribute("stationid"),
                    cols.get(4).getAttribute("companyid")
            );

            data.add(t);

            if(!prev.contains(Integer.parseInt(cols.get(4).getAttribute("companyid"))))
                newps.add(t);
            companyIds.add(Integer.parseInt(cols.get(4).getAttribute("companyid")));
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
            System.out.print("Fetching details of station: "+" "+s.getStationId()+" "+s.getCompanyId()+" "+s.getName());
            driver.navigate().to("http://psd.bits-pilani.ac.in/Student/StationproblemBankDetails.aspx?CompanyId="+s.getCompanyId()+"&StationId="+s.getStationId()+"&BatchIdFor=10&PSTypeFor=2");
            WebElement stp = null;
            WebElement br = null;
            String des = "";
            String skillSet = "";
            String prefElec = "";
            try {
                stp = driver.findElement(By.xpath("//*[@id=\"Stipend\"]"));
                br = driver.findElement(By.xpath("//*[@id=\"Project\"]/table/tbody/tr[5]/td[3]"));
                List<WebElement> projects = driver.findElements(By.xpath("//*[@id=\"Project\"]"));
                for (int i = 1; i < projects.size(); i++) {
                    String prefix = "PROJECT-" + i + ": ";
                    des += prefix + driver.findElement(By.xpath("/html/body/form/div[3]/div[2]/div"
                            + "/div[2"
                            + "]/table[1]/tbody/div[" + i + "]/table/tbody/tr[2]/td[2]")).getText()
                    + '\n';
                    skillSet += prefix + driver.findElement(By.xpath("/html/body/form/div[3]/div[2"
                            + "]/div/div"
                            + "[2]/table[1]/tbody/div[" + i + "]/table/tbody/tr[3]/td[2]")).getText()
                    + '\n';
                    prefElec += prefix + driver.findElement(By.xpath("/html/body/form/div[3]/div[2"
                            + "]/div/div"
                            + "[2]/table[1]/tbody/div[" + i + "]/table/tbody/tr[5]/td[2]")).getText()
                    + '\n';
                }

            }catch(Exception e)
            {}
            s.setStipend(stp==null?"":stp.getText());
            String brstr = "";
            if(br!=null)
            {
                List<WebElement> brs = br.findElements(By.className("grouptag"));
                for(WebElement w:brs)
                    brstr+=w.getText();
            }
            else
                brstr+="-";
            s.setBranches(brstr);
            s.setSkillSet(skillSet);//==null?"":skillSet.getText()));
            s.setPrefElecs(prefElec);
            s.setDescription(des);
            System.out.println(" || "+s.getStipend()+" || "+s.getBranches());
        }

        // write to csv
        File file = new File("./data/stations_"+new SimpleDateFormat("dd-MM-yyyy").format(new Date())+".csv");

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for (String header: headers)
            {
                bw.write(String.format("\"%s\",", header));
            }
            bw.newLine();
            for(Station st:data)
            {
                String[] values = {st.getStationId(), st.getName(), st.getDomain(), st.getStipend(),
                        st.getLocation(), st.getBranches(), st.getDescription(), st.getSkillSet(), st.getPrefElecs()};
                for (String value: values)
                {
                    bw.write(String.format("\"%s\",", value));
                }
                bw.newLine();
            }
        }catch(IOException e){}

        driver.close();
    }

    private static void uploadData(WebDriver driver, String path) throws IOException {
        driver.navigate().to("http://psd.bits-pilani.ac.in/Student/StudentStationPreference.aspx");
        HashMap<String, Integer> sidToPos = new HashMap<>();

        WebElement statList = driver.findElement(By.xpath("//*[@id=\"sortable_nav\"]"));
        List<WebElement> stats = statList.findElements(By.tagName("li"));
        System.out.println("creating hashmap...");
        for(int i=1;i<=stats.size();i++)
        {
            WebElement curr = driver.findElement(By.xpath("//*[@id=\"sortable_nav\"]/li["+i+"]/span"));
            sidToPos.put(curr.getAttribute("spn"),i);
        }

        System.out.println("starting swaps...");
        int toPos = 1;
        Reader in = new FileReader(path);
        boolean first = true;

        for (CSVRecord csvRecord: CSVFormat.EXCEL.withHeader(headers).parse(in))
        {
            if(first)
            {
                first = false;
                continue;
            }
            String currSId = csvRecord.get("StationId");
            String currSName = csvRecord.get("Name");
            System.out.println(String.format("%s %s", currSId, currSName));
            if(sidToPos.get(currSId)==null)
                continue;
            int currElePos = sidToPos.get(currSId);
            WebElement from = driver.findElement(By.xpath("//*[@id=\"sortable_nav\"]/li["+currElePos+"]/span"));
            WebElement to = driver.findElement(By.xpath("//*[@id=\"sortable_nav\"]/li["+toPos+"]/span"));
            Actions act = new Actions(driver);
            act.dragAndDrop(from, to).build().perform();
            toPos++;
            for(Map.Entry e : sidToPos.entrySet())
            {
                Integer pos = sidToPos.containsKey(e.getKey())? (Integer) e.getValue() :0;
                if(pos<currElePos)
                    sidToPos.put((String) e.getKey(), pos+1);
            }

        }

        System.out.println("***SAVE YOUR PREFERENCES***");
    }

    private static void checkAllotment(WebDriver driver)
    {
        WebElement t = driver.findElement(By.xpath("/html/body/form/div[4]/div/div/div/div[2]/div/div[1]/div/div[3]/span/span[2]"));
        if(!t.getText().equals("-"))
            System.out.println("Station Alloted: "+t.getText());

        else
            System.err.println("nope not yet");
    }

    public static void main(String[] args) throws IOException {
        if(System.getProperty("os.name").indexOf("ndows")>=0)
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        else
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");

        System.setProperty("webdriver.chrome.logfile", "status.log");
        ChromeOptions options = new ChromeOptions();
      //  options.addArguments("--headless");
//        options.addArguments("--log-level=OFF");
        WebDriver driver = new ChromeDriver(options);
        
        // login
        driver.navigate().to("http://psd.bits-pilani.ac.in");
        WebElement username = driver.findElement(By.id("TxtEmail"));
        username.clear();
        username.sendKeys(args[1]);
        WebElement pass = driver.findElement(By.id("txtPass"));
        pass.clear();
        pass.sendKeys(args[2]);
        driver.findElement(By.id("Button1")).click();

        if(args[0].equals("--fetch"))
            fetchData(driver);
        else if(args[0].equals("--upload"))
            uploadData(driver, args[3]);
        else if(args[0].equals("--check"))
            checkAllotment(driver);
        else
        {
            System.out.println("specify function(flag)");
            return;
        }
    }
}
