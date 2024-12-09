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
    private String alias;           // Alias for the account, initially null
    private double minBalance;

    public Account(String currency, String type, String ownerEmail, String IBAN) {
        this.balance = 0;
        this.cards = new ArrayList<>();
        this.currency = currency;
        this.IBAN = IBAN;
        this.accountType = type;
        this.ownerEmail = ownerEmail;
        this.alias = null;
        this.minBalance = 0;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void addCard(Card card) {
        if (!cards.contains(card)) { // Verificare dacă cardul există deja
            cards.add(card);
        }
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

    public double getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(double minBalance) {
        this.minBalance = minBalance;
    }

    public void clearCards() {
        this.cards.clear();
    }

}
