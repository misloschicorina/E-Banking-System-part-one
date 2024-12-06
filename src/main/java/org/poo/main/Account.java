package org.poo.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// @JsonPropertyOrder({ "IBAN", "balance", "currency", "type", "cards" })

public abstract class Account {
    private String IBAN;
    private double balance;         // Current balance
    private List<Card> cards;       // Cards associated with this account
    private String currency;        // Currency of the account
    private String accountType;     // Can be classic or savings
    private String ownerEmail;      // Email of the owner

    public Account(String currency, String type, String ownerEmail) {
        this.balance = 0;
        this.cards = new ArrayList<>();
        this.currency = currency;
        this.IBAN = Utils.generateIBAN();
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

    // Additional methods
    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void deposit(double amount) {
        this.balance += amount;
    }
}
