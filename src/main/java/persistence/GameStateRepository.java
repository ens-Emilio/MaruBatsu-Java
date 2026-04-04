package persistence;

import model.GameState;

public interface GameStateRepository {
    void save(GameState state);
    GameState load();
    void delete();
    boolean exists();
}
