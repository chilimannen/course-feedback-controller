package Model;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by Robin on 2016-03-16.
 * <p/>
 * Async voting store implementation using MongoDB.
 */
public class VotingDB implements AsyncVotingStore {
    private static final String COLLECTION = "controller_metadata";
    private MongoClient client;

    public VotingDB(MongoClient client) {
        this.client = client;
    }


    @Override
    public void terminate(Future<Void> future, Voting voting) {
        JsonObject query = new JsonObject().put("owner", voting.getOwner()).put("id", voting.getId());

        client.removeOne(COLLECTION, query, result -> {
            if (result.succeeded())
                future.complete();
            else
                future.fail(result.cause());
        });
    }

    @Override
    public void list(Future<VotingList> future, String owner) {
        JsonObject query = new JsonObject().put("owner", owner);

        client.find(COLLECTION, query, result -> {
            if (result.succeeded()) {
                JsonObject votings = new JsonObject().put("votings", result.result());
                VotingList list = (VotingList) Serializer.unpack(votings, VotingList.class);

                future.complete(list);
            } else
                future.fail(result.cause());
        });
    }

    @Override
    public void create(Future<Void> future, Voting voting) {
        JsonObject query = new JsonObject(Serializer.pack(voting));

        client.save(COLLECTION, query, result -> {
            if (result.succeeded()) {
                future.complete();
            } else
                future.fail(result.cause());
        });
    }
}
