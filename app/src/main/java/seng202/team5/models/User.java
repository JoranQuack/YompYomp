package seng202.team5.models;

import java.util.List;

public class User {

    private int id;
    private String name;
    private List<String> regions;
    private boolean isFamilyFriendly;
    private boolean isAccessible;
    private int experienceLevel;
    private int gradientPreference;
    private int bushPreference;
    private int reservePreference;
    private int lakeRiverPreference;
    private int coastPreference;
    private int mountainPreference;
    private int wildlifePreference;
    private int historicPreference;
    private int waterfallPreference;
    private boolean isProfileComplete;

    public User() {
        this.isProfileComplete = false;
    }

    /**
     * Constructor for User class with all parameters.
     *
     * @param id
     * @param name
     * @param regions
     * @param isFamilyFriendly
     * @param isAccessible
     * @param experienceLevel
     * @param gradientPreference
     * @param bushPreference
     * @param reservePreference
     * @param lakeRiverPreference
     * @param coastPreference
     * @param mountainPreference
     * @param wildlifePreference
     * @param historicPreference
     * @param waterfallPreference
     * @param isProfileComplete
     */
    public User(int id, String name, List<String> regions, boolean isFamilyFriendly, boolean isAccessible,
            int experienceLevel, int gradientPreference, int bushPreference, int reservePreference,
            int lakeRiverPreference, int coastPreference, int mountainPreference, int wildlifePreference,
            int historicPreference, int waterfallPreference, boolean isProfileComplete) {
        this.id = id;
        this.name = name;
        this.regions = regions;
        this.isFamilyFriendly = isFamilyFriendly;
        this.isAccessible = isAccessible;
        this.experienceLevel = experienceLevel;
        this.gradientPreference = gradientPreference;
        this.bushPreference = bushPreference;
        this.reservePreference = reservePreference;
        this.lakeRiverPreference = lakeRiverPreference;
        this.coastPreference = coastPreference;
        this.mountainPreference = mountainPreference;
        this.wildlifePreference = wildlifePreference;
        this.historicPreference = historicPreference;
        this.waterfallPreference = waterfallPreference;
        this.isProfileComplete = isProfileComplete;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getRegion() {
        return regions;
    }

    public boolean isFamilyFriendly() {
        return isFamilyFriendly;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public int getGradientPreference() {
        return gradientPreference;
    }

    public int getBushPreference() {
        return bushPreference;
    }

    public int getReservePreference() {
        return reservePreference;
    }

    public int getLakeRiverPreference() {
        return lakeRiverPreference;
    }

    public int getCoastPreference() {
        return coastPreference;
    }

    public int getMountainPreference() {
        return mountainPreference;
    }

    public int getWildlifePreference() {
        return wildlifePreference;
    }

    public int getHistoricPreference() {
        return historicPreference;
    }

    public int getWaterfallPreference() {
        return waterfallPreference;
    }

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    // setters

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(List<String> regions) {
        this.regions = regions;
    }

    public void setIsFamilyFriendly(boolean isFamilyFriendly) {
        this.isFamilyFriendly = isFamilyFriendly;
    }

    public void setIsAccessible(boolean isAccessible) {
        this.isAccessible = isAccessible;
    }

    public void setExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public void setGradientPreference(int gradientPreference) {
        this.gradientPreference = gradientPreference;
    }

    public void setBushPreference(int bushPreference) {
        this.bushPreference = bushPreference;
    }

    public void setReservePreference(int reservePreference) {
        this.reservePreference = reservePreference;
    }

    public void setLakeRiverPreference(int lakeRiverPreference) {
        this.lakeRiverPreference = lakeRiverPreference;
    }

    public void setCoastPreference(int coastPreference) {
        this.coastPreference = coastPreference;
    }

    public void setMountainPreference(int mountainPreference) {
        this.mountainPreference = mountainPreference;
    }

    public void setWildlifePreference(int wildlifePreference) {
        this.wildlifePreference = wildlifePreference;
    }

    public void setHistoricPreference(int historicPreference) {
        this.historicPreference = historicPreference;
    }

    public void setWaterfallPreference(int waterfallPreference) {
        this.waterfallPreference = waterfallPreference;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.isProfileComplete = profileComplete;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", regions=" + regions +
                ", isFamilyFriendly=" + isFamilyFriendly +
                ", isAccessible=" + isAccessible +
                ", experienceLevel=" + experienceLevel +
                ", gradientPreference=" + gradientPreference +
                ", bushPreference=" + bushPreference +
                ", reservePreference=" + reservePreference +
                ", lakeRiverPreference=" + lakeRiverPreference +
                ", coastPreference=" + coastPreference +
                ", mountainPreference=" + mountainPreference +
                ", wildlifePreference=" + wildlifePreference +
                ", historicPreference=" + historicPreference +
                ", waterfallPreference=" + waterfallPreference +
                ", isProfileComplete=" + isProfileComplete +
                '}';
    }
}
