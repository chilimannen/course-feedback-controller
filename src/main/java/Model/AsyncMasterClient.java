package Model;

import io.vertx.core.Future;

/**
 * @author Robin Duda
 *         <p/>
 *         A connection to a master.
 */
public interface AsyncMasterClient {
    /**
     * Command a master to create a vote.
     *
     * @param future
     * @param voting to be created.
     */
    void create(Future<Void> future, Voting voting);

    /**
     * Terminate a voting on any master.
     *
     * @param future
     * @param voting to be terminated.
     */
    void terminate(Future<Void> future, Voting voting);
}
