package Model;

import io.vertx.core.Future;

/**
 * @author Robin Duda
 */
public interface AsyncVotingStore {
    /**
     * Terminate a voting.
     *
     * @param future
     * @param voting the voting to be terminated.
     */
    void terminate(Future<Void> future, Voting voting);

    /**
     * List all votings attached to specified owner.
     *
     * @param future
     * @param owner  the owner which votings should be listed for.
     */
    void list(Future<VotingList> future, String owner);

    /**
     * Create a new voting.
     *
     * @param future
     * @param voting the voting to be created.
     */
    void create(Future<Void> future, Voting voting);
}
