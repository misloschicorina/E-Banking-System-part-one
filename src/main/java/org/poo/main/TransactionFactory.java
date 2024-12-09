package org.poo.main;

import java.util.List;

public class TransactionFactory {

    private TransactionFactory() {
    }

    public static Transaction createAccountTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "New account created",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null
        );
    }

    public static Transaction createCardTransaction(
            int timestamp, String cardNumber,
            String email, String accountIBAN) {
        return new Transaction(
                timestamp,
                "New card created",
                null, null, null, null,
                null, cardNumber, email,
                accountIBAN, null, null, null
        );
    }

    public static Transaction createSentMoneyTransaction(
            int timestamp, String senderIBAN,
            String receiverIBAN, Double amount,
            String currency) {
        return new Transaction(
                timestamp,
                "Money sent",
                senderIBAN, receiverIBAN,
                amount, currency,
                "sent", null, null,
                null, null, null, null
        );
    }

    public static Transaction createReceivedMoneyTransaction(
            int timestamp, String senderIBAN,
            String receiverIBAN, Double amount,
            String currency) {
        return new Transaction(
                timestamp,
                "Money received",
                senderIBAN, receiverIBAN,
                amount, currency,
                "received", null, null,
                null, null, null, null
        );
    }

    public static Transaction createOnlinePaymentTransaction(
            int timestamp, String cardNumber,
            Double amount, String commerciant, String accountIBAN) {
        return new Transaction(
                timestamp,
                "Card payment",
                null, null, amount,
                null, null, null, null,
                accountIBAN, commerciant, null, null
        );
    }

    public static Transaction createInsuffiecientFundsTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "Insufficient funds",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null
        );
    }

    public static Transaction createDeletedCardTransaction(
            int timestamp, String accountIBAN,
            String cardNumber, String email) {
        return new Transaction(
                timestamp,
                "The card has been destroyed",
                null, null, null, null,
                null, cardNumber, email,
                accountIBAN, null, null, null
        );
    }

    public static Transaction createWarningTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "You have reached the minimum amount "
                        + "of funds, the card will be frozen",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null
        );
    }

    public static Transaction createCardFrozenErrorTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "The card is frozen",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null
        );
    }

    public static Transaction createSuccessSplitTransaction(int timestamp, double amount, double splitAmount,
                                                            String currency, List<String> accounts) {
        String formattedAmount = String.format("%.2f", amount);
        return new Transaction(
                timestamp,
                "Split payment of " + formattedAmount + " " + currency,
                null, null, splitAmount, currency,
                null, null, null, null,
                null, null, accounts
        );
    }
}
