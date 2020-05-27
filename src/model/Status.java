package model;

import java.util.HashMap;
import java.util.Map;

public enum Status {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PROCESSING("Processing"),
    PROCESSED("Processed"),
    IN_TRANSIT("In transit"),
    DELIVERED("Delivered");

    public final String value;

    Status(String value) {
        this.value = value;
    }

    private static final Map<String, Status> lookup = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            lookup.put(status.value, status);
        }
    }

    public static Status get(String value) {
        return lookup.get(value);
    }
}
