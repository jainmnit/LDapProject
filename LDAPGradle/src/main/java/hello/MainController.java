package hello;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Greeting;

/**
 * Example controller to test security calls
 */
@RestController
@RequestMapping("/security")
public class MainController {
	
	@Autowired
	private CounterService counterService;
    

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    Logger logger = Logger.getLogger(this.getClass());


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    	logger.debug("Comes here in hello message");
    	counterService.increment("counter.errors.get_greeting");
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
    
    @RequestMapping(value = "/hello1", method = RequestMethod.GET)
    public Greeting greeting1(@RequestParam(value = "name", defaultValue = "World") String name) {
    	logger.debug("Comes here in hello message");
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Greeting homePage(@RequestParam(value = "name", defaultValue = "World") String name) {

        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value = {"/user", "/me"}, method = RequestMethod.POST)
    public ResponseEntity<?> user(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}