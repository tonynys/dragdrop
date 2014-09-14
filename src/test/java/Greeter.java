import java.io.PrintStream;

/**
 * A component for creating personal greetings.
 * issue:https://issues.jboss.org/browse/ARQ-1479
 */
public class Greeter {
    public void greet(PrintStream to, String name) {
        to.println(createGreeting(name));
    }

    public String createGreeting(String name) {
        return "Hello, " + name + "!";
    }
}