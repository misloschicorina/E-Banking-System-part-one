package org.poo.main;

public class SavingsAccount extends Account {
    private double interestRate; // dobanda

    public SavingsAccount(String currency, String ownerEmail, double interestRate) {
        super(currency, "savings", ownerEmail);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
