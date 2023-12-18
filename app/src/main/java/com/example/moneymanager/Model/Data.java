package com.example.moneymanager.Model;

public class Data {
    private int amount;
    private String category;
    private String note;
    private String id;
    private String date;

    public Data() {
        // No-argument constructor code
    }

    public Data(int amount, String category, String note, String id, String date) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.id = id;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
