package org.poo.main;

public class ClassicAccount extends Account {

    // Constructor
    public ClassicAccount(String currency, String ownerEmail, String IBAN) {
        super(currency, "classic", ownerEmail, IBAN);
    }

}
