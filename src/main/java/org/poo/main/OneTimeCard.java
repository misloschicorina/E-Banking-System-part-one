package org.poo.main;

public class OneTimeCard extends Card {
    private boolean used;

    public OneTimeCard(User cardOwner, Account account, String cardNumber) {
        super(cardOwner, account, cardNumber);
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }

    public void markAsUsed() {
        this.used = true;
    }

    @Override
    public boolean isOneTimeCard() {
        return true;
    }
}
