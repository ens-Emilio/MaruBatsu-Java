package persistence;

import model.Scoreboard;

public interface ScoreRepository {
    void save(Scoreboard scoreboard);
    void load(Scoreboard scoreboard);
    void delete();
    boolean exists();
}
