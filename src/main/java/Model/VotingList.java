package Model;

import java.util.ArrayList;

/**
 * @author Robin Duda
 * Transport object for a list of votings.
 */
public class VotingList {
    private ArrayList<Voting> votings = new ArrayList<>();

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
