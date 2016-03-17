package Model;

import io.vertx.core.Future;

/**
 * Created by Robin on 2016-03-17.
 */
public interface AsyncMasterClient {
    void create(Future<Void> future, Voting voting);

    void terminate(Future<Void> future, Voting voting);
}
