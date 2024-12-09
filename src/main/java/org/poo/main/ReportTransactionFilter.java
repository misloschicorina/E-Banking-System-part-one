package org.poo.main;

public class ReportTransactionFilter implements TransactionFilter {
    @Override
    public boolean filter(Transaction transaction, String IBAN) {
        if (transaction.getAccountIBAN() == null) {
            return (transaction.getSenderIBAN() != null && transaction.getSenderIBAN().equals(IBAN)) ||
                    (transaction.getReceiverIBAN() != null && transaction.getReceiverIBAN().equals(IBAN)) ||
                    (transaction.getInvolvedAccounts() != null && transaction.getInvolvedAccounts().contains(IBAN));
        } else {
            return transaction.getAccountIBAN().equals(IBAN);
        }
    }
}

