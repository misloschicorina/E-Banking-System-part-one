package org.poo.main;

public class SpendingsTransactionFilter implements TransactionFilter {
    @Override
    public boolean filter(Transaction transaction, String IBAN) {
        return "Card payment".equals(transaction.getDescription()) &&
                transaction.getAccountIBAN() != null &&
                transaction.getAccountIBAN().equals(IBAN);
    }
}

