package org.poo.main;

public class Card {
    private String cardNumber;
    private User cardOwner;
    private Account account; // Associated account
    private String status;

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "frozen";

    public Card(User cardOwner, Account account, String cardNumber) {
        this.cardNumber = cardNumber;
        this.cardOwner = cardOwner;
        this.account = account;
        this.status = STATUS_ACTIVE;
    }

    // Getters
    public String getCardNumber() {
        return cardNumber;
    }

    public User getCardOwner() {
        return cardOwner;
    }

    public Account getAccount() {
        return account;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOneTimeCard() {
        return false;
    }

    public void freezeCard() {
        this.status = STATUS_INACTIVE;
    }
}
