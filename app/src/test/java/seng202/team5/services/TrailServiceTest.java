package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TrailServiceTest {

    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private TrailService trailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trailService = new TrailService(mockTrailRepo);
    }

    @Test
    @DisplayName("addTrail should call repo upsert() once")
    void testAddTrail() {
        Trail mockTrail = new Trail(1, "Trail1", "Desc", "Region", "Easy",
                "Loop", "Info", "Desc", "thumb.jpg", "url", "culture",
                4.5, 0, 0);

        trailService.addTrail(mockTrail);

        verify(mockTrailRepo, times(1)).upsert(mockTrail);
    }
}
