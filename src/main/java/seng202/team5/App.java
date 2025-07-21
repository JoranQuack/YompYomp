package seng202.team5;

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
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}