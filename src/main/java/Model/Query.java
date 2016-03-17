package Model;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-16.
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
