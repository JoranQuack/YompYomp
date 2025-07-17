package seng202.team5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    private App app;

    @BeforeEach
    public void setUp() {
        app = new App();
    }

    @Test
    public void testHelloWorld() {
        assertEquals("Hello World", app.helloWorld());
    }

    @Test
    public void testTeamName() {
        assertEquals("The Dream Team 5", app.teamName());
    }

    @Test
    public void testHayley() {
        assertEquals("Hello Hayley", app.helloHayley() );
    }

    @Test
    public void testAppName() {
        assertEquals("YompYomp", app.appName());
    }

    @Test
    public void testYellowCard() {
        assertEquals("Bring snacks", app.yellowCard());
    }

    @Test
    public void testRedCard() {
        assertEquals("Your Out", app.redCard());
    }
}
