package Model;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-16.
 */
public class Query {
    private String name;
    private ArrayList<String> options;

    public String getName() {
        return name;
    }

    public Query setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public Query setOptions(ArrayList<String> options) {
        this.options = options;
        return this;
    }
}
