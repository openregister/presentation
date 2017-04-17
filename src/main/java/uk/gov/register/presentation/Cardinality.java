package uk.gov.register.presentation;

public enum Cardinality {
    ONE("1"),MANY("n");

    private final String id;

    Cardinality(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
