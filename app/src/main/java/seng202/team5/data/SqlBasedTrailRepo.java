package seng202.team5.data;

import seng202.team5.models.Trail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlBasedTrailRepo implements ITrail{

    private final DatabaseService databaseService;

    public SqlBasedTrailRepo(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    private static final String SELECT_BASE = """
        SELECT
            id,
            name,
            description,
            difficulty,
            completion_time,
            type,
            thumb_url,
            web_url,
            date_loaded_raw,
            x,
            y
            FROM trail
        """;

    private static final String UPSERT_SQL = """
            INSERT INTO trail (
                id, name, description, difficulty, completion_time,
                type, thumb_url, web_url, date_loaded_raw, x, y
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?)
                ON CONFLICT(id) DO UPDATE SET
                    name=excluded.name,
                    description=excluded.description,
                    difficulty=excluded.difficulty,
                    completion_time=excluded.completion_time,
                    type=excluded.type,
                    thumb_url=excluded.thumb_url,
                    web_url=excluded.web_url,
                    date_loaded_raw=excluded.date_loaded_raw,
                    x=excluded.x,
                    y=excluded.y
            """;

    private static final String DELETE_SQL = "DELETE FROM trail WHERE id = ?";

    public void upsert(Trail trail) {
        try(Connection conn = databaseService.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPSERT_SQL)) {
            //setParams()...
            stmt.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("upsertTrail failed (id=" + trail.getId() + ")", e);
        }
    }

    public void upsertAll(List<Trail> trails) {}
    public void deleteById(int id) {}
    public java.util.Optional<Trail> findById(int id) {return null;}
}
