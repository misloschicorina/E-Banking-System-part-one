package org.poo.main;

public class OneTimeCard extends Card {
    private boolean used;

    public OneTimeCard(User cardOwner, Account account) {
        super(cardOwner, account);
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }
}
