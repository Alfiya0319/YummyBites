package com.example.onlinefoodordering.model;

public class TransactionModel {
    private String txnId;
    private String title;
    private String date;
    private String amount;
    private String type; // "Credit" for adding, "Debit" for spending

    public TransactionModel() {} // Required for Firebase

    public TransactionModel(String txnId, String title, String date, String amount, String type) {
        this.txnId = txnId;
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public String getTxnId() { return txnId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public String getType() { return type; }
}