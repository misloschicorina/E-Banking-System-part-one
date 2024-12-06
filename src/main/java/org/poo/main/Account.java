package org.poo.main;


import java.util.ArrayList;
import java.util.List;


public abstract class Account {
    private String IBAN;
    private double balance;         // Current balance
    private List<Card> cards;       // Cards associated with this account
    private String currency;        // Currency of the account
    private String accountType;     // Can be classic or savings
    private String ownerEmail;      // Email of the owner

    public Account(String currency, String type, String ownerEmail, String IBAN) {
        this.balance = 0;
        this.cards = new ArrayList<>();
        this.currency = currency;
        this.IBAN = IBAN;
        this.accountType = type;
        this.ownerEmail = ownerEmail;
    }

    // Getters in the desired order
    public double getBalance() {
        return balance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIBAN() {
        return IBAN;
    }

    public String getACcountType() {
        return accountType;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void spend(double amount) {
        this.balance -= amount;
    }
}
