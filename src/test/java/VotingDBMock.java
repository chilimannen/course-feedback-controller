import Model.AsyncVotingStore;
import Model.Query;
import Model.Voting;
import Model.VotingList;
import io.vertx.core.Future;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-16.
 */
public class VotingDBMock implements AsyncVotingStore {
    private ArrayList<Voting> votings = new ArrayList<>();

    public VotingDBMock() {
        ArrayList<Query> options = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        values.add("a");
        values.add("b");
        values.add("c");

        options.add(
                new Query()
                        .setName("q1")
                        .setValues(values)
        );

        options.add(
                new Query()
                        .setName("q2")
                        .setValues(values)
        );

        votings.add(new Voting()
                        .setId("id")
                        .setTopic("Vote #1")
                        .setOwner("gosu")
                        .setOptions(options)
        );
    }

    @Override
    public void terminate(Future<Void> future, String id) {
        for (int i = 0; i < votings.size(); i++) {
            if (votings.get(i).getId().equals(id)) {
                votings.remove(i);
                break;
            }
        }
        future.complete();
    }

    @Override
    public void list(Future<VotingList> future, String owner) {
        VotingList list = new VotingList();

        for (Voting voting : votings)
            if (voting.getOwner().equals(owner))
                list.add(voting);

        future.complete(list);
    }

    @Override
    public void create(Future<Void> future, Voting voting) {
        votings.add(voting);
        future.complete();
    }
}
