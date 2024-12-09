package org.poo.main;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private Double amount;
    private String transferType;
    private String cardNumber;
    private String cardHolder;
    private String accountIBAN;
    private String commerciant;
    private String currency;
    private String email;
    private List<String> involvedAccounts;
    private String error;

    // Constructor with email field
    public Transaction(int timestamp, String description, String senderIBAN, String receiverIBAN,
                       Double amount, String currency, String transferType, String cardNumber,
                       String cardHolder, String accountIBAN, String commerciant, String email,
                       List<String> involvedAccounts, String error) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.currency = currency;
        this.transferType = transferType;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.accountIBAN = accountIBAN;
        this.commerciant = commerciant;
        this.email = email;
        this.involvedAccounts = involvedAccounts;
        this.error = error; // Initialize error field
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getSenderIBAN() {
        return senderIBAN;
    }

    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getAccountIBAN() {
        return accountIBAN;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public String getEmail() {
        return email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    // Add transaction to a user's transaction list
    public void addTransaction(User user) {
        if (user == null)
            return;
        user.addTransaction(this);
    }

    public String getError() { // Getter for error field
        return error;
    }

}
