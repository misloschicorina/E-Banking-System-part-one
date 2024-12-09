package org.poo.main;

public interface TransactionFilter {
    boolean filter(Transaction transaction, String IBAN);
}
