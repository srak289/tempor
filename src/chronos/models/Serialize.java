package chronos.models;


interface Serialize {
    HashMap<String, Object> toColumns();
    T fromColumns();
}
