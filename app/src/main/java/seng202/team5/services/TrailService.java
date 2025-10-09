package seng202.team5.services;

import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

public class TrailService {

    private final SqlBasedTrailRepo sqlBasedTrailRepo;

    public TrailService(SqlBasedTrailRepo sqlBasedTrailRepo) {
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
    }

    public void addTrail(Trail trail) {
        sqlBasedTrailRepo.upsert(trail);
    }

    public boolean existsByName(String inputTrailName, Integer excludeId) {
        return sqlBasedTrailRepo.existsByName(inputTrailName, excludeId);
    }

    public void deleteTrail(Trail trail) {
        sqlBasedTrailRepo.deleteById(trail.getId());
    }

    public Trail findTrailById(int id) {
        return sqlBasedTrailRepo.findById(id).orElse(null);
    }

    public int getNewTrailId() {
        return sqlBasedTrailRepo.getNewTrailId();
    }
}
