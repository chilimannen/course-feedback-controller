package Model;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * An individual question in a vote.
 */
public class Query {
    private String name;
    private ArrayList<String> values;

    public String getName() {
        return name;
    }

    public Query setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public Query setValues(ArrayList<String> values) {
        this.values = values;
        return this;
    }
}
