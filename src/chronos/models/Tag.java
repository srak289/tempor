package chronos.models;

import java.util.ArrayList;
import java.util.HashMap;

import chronos.models.Task;


public class Tag {
    private int id;
    private String name;
    private ArrayList<Task> tasks;

    public Tag() {
    }

    public static class Builder {
        private int id;
        private String name;
        private ArrayList<Task> tasks;

        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder tags(ArrayList<Task> tasks) {
            this.tasks = tasks; return this;
        }

        public Tag build() {
            return new Tag(this);
        }
    }

    public Tag(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.tasks = builder.tasks;
    }

    public static Tag fromColumns(HashMap<String, Object> args) {
        return new Tag.Builder()
                .id(args.get("id"))
                .name(args.get("name"))
                .tasks(args.get("tasks"))
                .build();
    }

    public HashMap<String, Object> toColumns() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("id", this.id);
        ret.put("name", this.name);
        ret.put("tasks", this.tasks);
        return ret;
    }
}
