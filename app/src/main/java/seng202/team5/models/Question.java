package seng202.team5.models;

/**
 * Enum for the questions in the profile quiz.
 */
public enum Question {
    ONE("Your tramping experience level?", new String[]{"Beginner", "Intermediate", "Expert"}),
    TWO("Preferred gradient of tramp?", new String[]{"Gentle", "Moderate", "Steep"}),
    THREE("Importance of bush/forest areas on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    FOUR("Importance of reserves on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    FIVE("Importance of lakes/rivers on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    SIX("Importance of coast on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    SEVEN("Preferred kind of mountain environment?", new String[]{"Lowland", "Mixed", "Alpine focus"}),
    EIGHT("Importance of wildlife/birdlife on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    NINE("Importance of historic/cultural features on tramp?", new String[]{"Not important", "Nice to have", "Must-have"}),
    TEN("Importance of waterfalls on tramp?", new String[]{"Not important", "Nice to have", "Must-have"});

    public final String question;
    public final String[] sliderLabels;

    /**
     * Constructor for the question enum.
     *
     * @param question the question description
     * @param sliderLabels the labels for the slider
     */
    Question(String question, String[] sliderLabels) {
        this.question = question;
        this.sliderLabels = sliderLabels;
    }
}

