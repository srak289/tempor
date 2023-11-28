package chronos.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import chronos.models.Tag;


public class Task {
    private int id;
    private String name;
    private String text;
    private boolean in_progress;
    private LocalDateTime time_created;
    private LocalDateTime time_stopped;
    private LocalDateTime time_started;
    private LocalDateTime time_worked;
    private LocalDateTime due_by;
    private ArrayList<Tag> tags;

    public Task() {
    }

    public static class Builder {
        private int id;
        private String name;
        private String text;
        private boolean in_progress;
        private LocalDateTime time_created;
        private LocalDateTime time_stopped;
        private LocalDateTime time_started;
        private LocalDateTime time_worked;
        private LocalDateTime due_by;
        private ArrayList<Tag> tags;

        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder text(String text) { this.text = text; return this; }
        public Builder in_progress(boolean in_progress) {
            this.in_progress = in_progress; return this;
        }
        public Builder time_created(LocalDateTime time_created) {
            this.time_created = time_created; return this;
        }
        public Builder time_stopped(LocalDateTime time_stopped) {
            this.time_stopped = time_stopped; return this;
        }
        public Builder time_started(LocalDateTime time_started) {
            this.time_started = time_started; return this;
        }
        public Builder time_worked(LocalDateTime time_worked) {
            this.time_worked = time_worked; return this;
        }
        public Builder due_by(LocalDateTime due_by) {
            this.due_by = due_by; return this;
        }
        public Builder tags(ArrayList<Tag> tags) {
            this.tags = tags; return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

    public Task(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.text = builder.text;
        this.in_progress = builder.in_progress;
        this.time_created = builder.time_created;
        this.time_stopped = builder.time_stopped;
        this.time_started = builder.time_started;
        this.time_worked = builder.time_worked;
        this.due_by = builder.due_by;
        this.tags = builder.tags;
    }

    public static Task fromColumns(HashMap<String, Object> args) {
        return new Task.Builder()
                .id(args.get("id"))
                .name(args.get("name"))
                .text(args.get("text"))
                .in_progress(args.get("in_progress"))
                .time_created(args.get("time_created"))
                .time_stopped(args.get("time_stopped"))
                .time_started(args.get("time_started"))
                .time_worked(args.get("time_worked"))
                .due_by(args.get("due_by"))
                .tags(args.get("tags"))
                .build();
    }

    public HashMap<String, Object> toColumns() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("id", this.id);
        ret.put("name", this.name);
        ret.put("text", this.text);
        ret.put("in_progress", this.in_progress);
        ret.put("time_created", this.time_created);
        ret.put("time_stopped", this.time_stopped);
        ret.put("time_started", this.time_started);
        ret.put("time_worked", this.time_worked);
        ret.put("due_by", this.due_by);
        return ret;
    }
}
