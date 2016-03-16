package Model;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-16.
 */
public class Voting {
    private String owner;
    private String name;
    private String id;
    private ArrayList<Query> options;

    public String getOwner() {
        return owner;
    }

    public Voting setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getName() {
        return name;
    }

    public Voting setName(String name) {
        this.name = name;
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
