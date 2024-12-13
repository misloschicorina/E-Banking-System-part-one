package org.poo.main.user;

import org.poo.main.accounts.Account;
import org.poo.main.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the banking system.
 */
public final class User {

    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts; // Accounts linked to the user
    private List<Transaction> transactions; // Transactions performed by the user

    public User(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Adds an account to the user's account list.
     *
     * @param account the account to add
     */
    public void addAccount(final Account account) {
        accounts.add(account);
    }

    /**
     * Removes an account from the user's account list.
     *
     * @param account the account to remove
     */
    public void removeAccount(final Account account) {
        accounts.remove(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Adds a transaction to the user's transaction list.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(final Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
