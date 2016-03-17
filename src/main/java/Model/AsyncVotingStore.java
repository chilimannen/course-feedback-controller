package Model;

import io.vertx.core.Future;

/**
 * Created by Robin on 2016-03-16.
 */
public interface AsyncVotingStore {
    void terminate(Future<Void> future, String owner);

    void list(Future<VotingList> future, String owner);

    void create(Future<Void> future, Voting voting);
}
