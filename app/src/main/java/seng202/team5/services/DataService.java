package seng202.team5.services;

import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.models.Trail;

import java.util.List;

public class DataService {

    private FileBasedTrailRepo trailRepo;

    public DataService(String trailCSVPath) {
        trailRepo = new FileBasedTrailRepo(trailCSVPath);
    }

    public List<Trail> getTrails() {
        return trailRepo.getAllTrails();
    }
}
