import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ApplicationMain {
	DesiredCapabilities capabilities;
    PhantomJSDriver driver;
    String baseUrl = "http://en.strikermanager.com";
    
    MutableInt seniorCurrent = new MutableInt(88), juniorCurrent = new MutableInt(84);
    String seniorCoachUrl = baseUrl+"/empleados.php?accion=lista&tipo=5";
    String juniorCoachUrl = baseUrl+"/empleados.php?accion=lista&tipo=6";
    
    
    boolean isLoginFormPresent(){
    	 return driver.findElements(By.xpath("//form[@name='login']")).size()>0;
    }
    
    void login(){
    	driver.findElement(By.xpath("//div[@class='caja']/input[@name='alias']")).sendKeys("");
    	driver.findElement(By.xpath("//div[@class='caja']/input[@name='pass']")).sendKeys("");
    	driver.findElement(By.xpath("//div[@class='botones']/input[@class='boton']")).click();
    }
    
    void hireCoach(String url, MutableInt currentAvg){
    	driver.get(url);
    	driver.switchTo().frame("marco");
    	List<WebElement> coaches = driver.findElements(By.xpath("//tr[@class='tipo1']|//tr[@class='tipo2']"));
    	int maxScore = 0, maxId = 0;
    	for(int i=0;i<coaches.size();i++){
    		String percent = coaches.get(i).findElement(By.xpath(".//td[@class='lastlong']//img")).getAttribute("title");
    		int score = Integer.parseInt(percent.substring(0,percent.length()-1));
    		if(maxScore < score){
    			maxScore = score;
    			maxId = i;
    		}
    	}
    	System.out.println("Coach "+maxId+" has a maximum avg. of "+maxScore);
    	if(maxScore > currentAvg.toInteger()){
    		System.out.println("Going to hire the coach with average "+maxScore);
    		coaches.get(maxId).findElement(By.xpath(".//td[@class='tdboton']/a")).click();    		
    		currentAvg = new MutableInt(maxScore);
    	}
    }
    
    public void hireCoaches() throws InterruptedException{
		capabilities = DesiredCapabilities.phantomjs();
		driver = new PhantomJSDriver(capabilities);
			
			driver.get(baseUrl);
			while(true){
			try{
		    	if(isLoginFormPresent()){
					login();
					System.out.println("successfully logged in");
				}
				else{
					hireCoach(seniorCoachUrl, seniorCurrent);
					Thread.sleep(2000);
					hireCoach(juniorCoachUrl, juniorCurrent);
					Thread.sleep(2000);
				}
			}
			catch(Exception e){
				System.out.println(e);
			}
			
    	}
    }
    
	public static void main(String args[]) throws IOException, InterruptedException{
		ApplicationMain app = new ApplicationMain();
		app.hireCoaches();
	}
}