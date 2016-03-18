package Model;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * A complete voting, without results.
 */
public class Voting {
    private String owner;
    private String topic;
    private String id;
    private Duration duration;
    private ArrayList<Query> options;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getOwner() {
        return owner;
    }

    public Voting setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Voting setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getId() {
        return id;
    }

    public Voting setId(String id) {
        this.id = id;
        return this;
    }

    public ArrayList<Query> getOptions() {
        return options;
    }

    public Voting setOptions(ArrayList<Query> options) {
        this.options = options;
        return this;
    }
}
