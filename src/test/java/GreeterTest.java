import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;

/**
 * 
 * see http://arquillian.org/guides/getting_started/#write_an_arquillian_test
 * 
 * @author tony
 *
 */
@RunWith(Arquillian.class) 
public class GreeterTest {
	@Inject
	private Greeter greeter;
	
  
	public GreeterTest(){
		
	}
	

	@Deployment
    public static JavaArchive createDeployment(){
    	JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
    	        .addClass(Greeter.class)
    	        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    	    System.out.println(jar.toString(true));
    	    return jar;   
    	  }
    
    @Test
    public void should_create_greeting() {
    	 Assert.assertEquals("Hello, Earthling!",
    		        greeter.createGreeting("Earthling"));
    		    greeter.greet(System.out, "Earthling");  
   }
    
    public Greeter getGreeter() {
		return greeter;
	}

	public void setGreeter(Greeter greeter) {
		this.greeter = greeter;
	}

    
}