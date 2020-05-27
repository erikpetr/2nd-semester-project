package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WarehouseOrderRevision {
    private int id;
    private LocalDate date;
    private String note;
    private Status status;

    public WarehouseOrderRevision(int id, LocalDate date, String note, Status status) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.status = status;
    }

    public WarehouseOrderRevision(LocalDate date, String note, Status status) {
        this.date = date;
        this.note = note;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) + " - " + status.value + " (" + note + ")";
    }
}
