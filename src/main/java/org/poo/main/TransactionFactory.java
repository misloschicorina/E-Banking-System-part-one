package org.poo.main;

public class TransactionFactory {

    private TransactionFactory() {
        // Constructor privat pentru a preveni instanțierea
    }

    public static Transaction createAccountTransaction(int timestamp, String accountIBAN) {
        return new Transaction(timestamp, "New account created", null, null, null,
                null, null, null, null, null, null, null);
    }

    public static Transaction createCardTransaction(int timestamp, String cardNumber, String email, String accountIBAN) {
        return new Transaction(timestamp, "New card created", null, null, null,
                null, null, cardNumber, email, accountIBAN, null, null);
    }

    public static Transaction createSentMoneyTransaction(int timestamp, String senderIBAN, String receiverIBAN,
                                                         Double amount, String currency) {
        return new Transaction(timestamp, "Money sent", senderIBAN, receiverIBAN, amount,
                currency, "sent", null, null, null, null, null);
    }

    public static Transaction createReceivedMoneyTransaction(int timestamp, String senderIBAN, String receiverIBAN,
                                                             Double amount, String currency) {
        return new Transaction(timestamp, "Money received", senderIBAN, receiverIBAN, amount,
                currency, "received", null, null, null, null, null);
    }

    public static Transaction createOnlinePaymentTransaction(int timestamp, String cardNumber,
                                                             Double amount, String commerciant) {
        return new Transaction(timestamp, "Card payment", null, null, amount,
                null, null, null, null, null, commerciant, null);
    }

    public static Transaction createOnlinePaymentFailureTransaction(int timestamp, String description) {
        return new Transaction(
                timestamp, // timestamp
                description, // description
                null, // senderIBAN
                null, // receiverIBAN
                null, // amount
                null, // currency
                null, // transferType
                null, // cardNumber
                null, // cardHolder
                null, // accountIBAN
                null, // commerciant
                null // email
        );
    }

    public static Transaction createDeletedCardTransaction(int timestamp, String accountIBAN, String cardNumber, String email) {
        return new Transaction(
                timestamp, // timestamp
                "The card has been destroyed", // description
                null, // senderIBAN
                null, // receiverIBAN
                null, // amount
                null, // currency
                null, // transferType
                cardNumber, // cardNumber
                email, // cardHolder (email in this context)
                accountIBAN, // accountIBAN
                null, // commerciant
                null // email (separate from cardHolder)
        );
    }

}
