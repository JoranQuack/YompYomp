package seng202.team5;

import seng202.team5.gui.FXAppEntry;

public class App {
    public String helloWorld() {
        return "Hello World";
    }

    public String teamName() {
        return "The Dream Team 5";
    }

    public String helloHayley() {
        return "Hello Hayley";
    }

    public String appName() {
        return "YompYomp";
    }

    public String yellowCard() {
        return "Bring snacks";
    }

    public String redCard() {
        return "Your Out";
    }

    public static void main(String[] args) {
        FXAppEntry.launch(FXAppEntry.class, args);
    }
}