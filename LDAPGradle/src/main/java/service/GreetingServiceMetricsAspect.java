package service;

import java.util.NoSuchElementException;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GreetingServiceMetricsAspect {

    private final CounterService counterService;

    @Autowired
    public GreetingServiceMetricsAspect(CounterService counterService) {
        this.counterService = counterService;
    }

    @AfterReturning(pointcut = "execution(* hello.MainController.greeting1(String)) && args(name)", argNames = "name")
    public void afterCallingGreeting1(String name) {
        counterService.increment("counter.calls.get_greeting1");
    }

   /* @AfterThrowing(pointcut = "execution(* eu.kielczewski.example.service.greeting.GreetingService.getGreeting(int))", throwing = "e")
    public void afterGetGreetingThrowsException(NoSuchElementException e) {
        counterService.increment("counter.errors.get_greeting");
    }*/

}
