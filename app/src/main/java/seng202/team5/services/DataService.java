package seng202.team5.services;

import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.data.ITrail;

import java.util.List;

public class DataService {
    private final ITrail trailRepo;

    public DataService(String trailCSVPath) {
        this.trailRepo = new FileBasedTrailRepo(trailCSVPath);
    }

    public List<Trail> getTrails() {
        return trailRepo.getAllTrails();
    }
}
