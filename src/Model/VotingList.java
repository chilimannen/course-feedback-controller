package Model;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-16.
 */
public class VotingList {
    private ArrayList<Voting> votings;

    public ArrayList<Voting> getVotings() {
        return votings;
    }

    public void setVotings(ArrayList<Voting> votings) {
        this.votings = votings;
    }

    public void add(Voting voting) {
        votings.add(voting);
    }
}
