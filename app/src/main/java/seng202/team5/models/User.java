package seng202.team5.models;

import java.util.List;

public class User {
    // Some basic variables
    private String type; // guest or profiled
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

    public User() {
    }

    // getters
    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getRegion() {
        return regions;
    }

    public boolean getIsFamilyFriendly() {
        return isFamilyFriendly;
    }

    public boolean getIsAccessible() {
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

    // setters
    public void setType(String type) {
        this.type = type;
    }

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
}
