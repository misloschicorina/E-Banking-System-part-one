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
                null, null, null,
                null // Error set to null
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
                accountIBAN, null, null, null,
                null // Error set to null
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
                null, null, null, null,
                null // Error set to null
        );
    }

    public static Transaction createReceivedMoneyTransaction(
            int timestamp, String senderIBAN,
            String receiverIBAN, Double amount,
            Double exchangeRate, String currency,
            String description) {

        // Calculează amount-ul final folosind rata de schimb
        double finalAmount = amount * exchangeRate;

        // Creează tranzacția
        return new Transaction(
                timestamp,
                description,
                senderIBAN, receiverIBAN,
                finalAmount, currency,
                "received", null, null,
                null, null, null, null,
                null // Error set to null
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
                accountIBAN, commerciant, null, null,
                null // Error set to null
        );
    }

    public static Transaction createInsuffiecientFundsTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "Insufficient funds",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null,
                null // Error set to null
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
                accountIBAN, null, null, null,
                null // Error set to null
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
                null, null, null,
                null // Error set to null
        );
    }

    public static Transaction createCardFrozenErrorTransaction(
            int timestamp, String accountIBAN) {
        return new Transaction(
                timestamp,
                "The card is frozen",
                null, null, null, null,
                null, null, null, accountIBAN,
                null, null, null,
                null // Error set to null
        );
    }

    public static Transaction createSuccessSplitTransaction(
            int timestamp, double amount, double splitAmount,
            String currency, List<String> accounts) {
        String formattedAmount = String.format("%.2f", amount);
        return new Transaction(
                timestamp,
                "Split payment of " + formattedAmount + " " + currency,
                null, null, splitAmount, currency,
                null, null, null, null,
                null, null, accounts,
                null // Error set to null
        );
    }

    public static Transaction createSplitErrorTransaction(
            double totalAmount,
            int timestamp, double splitAmount, String currency, String cheapIBAN, List<String> accounts) {
        return new Transaction(
                timestamp,
                "Split payment of " + String.format("%.2f", splitAmount) + " " + currency,
                null, null, totalAmount, currency,
                null, null, null, null,
                null,
                null,
                accounts, "Account " + cheapIBAN + " has insufficient funds for a split payment."
        );
    }

    public static Transaction deleteAccountErrorTransaction(int timestamp) {
        return new Transaction(
                timestamp,
                "Account couldn't be deleted - there are funds remaining",
                null, // Sender IBAN
                null, // Receiver IBAN
                null, // Amount
                null, // Currency
                null, // Transfer type
                null, // Card number
                null, // Email
                null, // Account IBAN
                null, // Commerciant
                null, // Card holder
                null, // Involved accounts
                null  // Error message
        );
    }

    public static Transaction InterestRateChangeTransaction(int timestamp, double rate) {
        return new Transaction(
                timestamp,
                "Interest rate of the account changed to " + rate,
                null, // Sender IBAN
                null, // Receiver IBAN
                null, // Amount
                null, // Currency
                null, // Transfer type
                null, // Card number
                null, // Email
                null, // Account IBAN
                null, // Commerciant
                null, // Card holder
                null, // Involved accounts
                null  // Error message
        );
    }


}
