package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WarehouseOrderRevision {
    private int id;
    private LocalDateTime date;
    private String note;
    private Status status;
    private WarehouseOrder warehouseOrder;

    public WarehouseOrderRevision(int id, LocalDateTime date, String note, Status status, WarehouseOrder warehouseOrder) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.status = status;
        this.warehouseOrder = warehouseOrder;
    }
    public WarehouseOrderRevision(LocalDateTime date, Status status, String note, WarehouseOrder warehouseOrder) {
        this.date = date;
        this.note = note;
        this.status = status;
        this.warehouseOrder = warehouseOrder;
    }

    public WarehouseOrderRevision(LocalDateTime date, String note, Status status) {
        this.date = date;
        this.note = note;
        this.status = status;
    }
    public WarehouseOrderRevision(WarehouseOrder warehouseOrder) {
        this.warehouseOrder = warehouseOrder;

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

    public WarehouseOrder getWarehouseOrder() {
        return warehouseOrder;
    }

    public void setWarehouseOrder(WarehouseOrder warehouseOrder) {
        this.warehouseOrder = warehouseOrder;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) + " - " + status.value + " (" + note + ")";
    }
}
