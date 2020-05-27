package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class WarehouseOrderRevision {
    private int id;
    private LocalDateTime date;
    private String note;
    private Status status;
    private WarehouseOrder warehouseOrder;
    private List<WarehouseOrderItem> itemsChanged;

    public WarehouseOrderRevision(int id, LocalDateTime date, String note, Status status) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.status = status;
    }
    public WarehouseOrderRevision(LocalDateTime date, Status status, String note, WarehouseOrder warehouseOrder, List<WarehouseOrderItem> itemsChanged) {
        this.date = date;
        this.note = note;
        this.status = status;
        this.warehouseOrder = warehouseOrder;
        this.itemsChanged = itemsChanged;
    }

    public WarehouseOrderRevision(LocalDateTime date, String note, Status status) {
        this.date = date;
        this.note = note;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
