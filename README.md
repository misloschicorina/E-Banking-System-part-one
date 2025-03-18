MISLOSCHI ALEXANDRA CORINA 324 CA - PROIECT ETAPA 1

# E-Banking System

## Introduction

This project implements a simplified e-banking system in Java.
The system simulates basic banking functionalities, allowing users to create accounts, 
manage funds, and perform financial transactions while maintaining a secure and 
structured platform. The challenge lies in managing users, their accounts, and their
transactions while ensuring that operations are efficient and data integrity is maintained.

## Core Concepts

The system revolves around managing users, accounts, and transactions. 
Here's how each element fits together:

### Users
Each user in the banking system will have personal information, including their name and
email address. These users can own multiple accounts, and each account can have different 
types, such as classic accounts or savings accounts.

### Accounts
A user can have multiple accounts, and each account can be of one of two types:

- **Classic Account**: A standard account type with basic functionalities like deposit, 
    withdrawal, and transfer.
- **Savings Account**: An account that generates interest over time and has additional 
    features, such as setting and modifying interest rates.

### Cards
Accounts can have associated cards. The system supports two types of cards:

- **Classic Cards**: Regular cards for standard transactions.
- **One-time Cards**: Cards that are designed for a single transaction. Once used, they
    are automatically deleted from the system and a new card must be regenerated for 
    future transactions.

### Transactions
Every action taken by a user that affects their account is recorded as a transaction. 
These transactions are stored in ascending order by timestamp and can be printed 
using the `printTransactions` command. This command will provide a complete history 
of a user’s transactions.

### Exchange Rates
Since banks often deal with multiple currencies, exchange rates will be stored to
enable currency conversions between different account types. The exchange rate will 
be specified between a "from" and a "to" currency, along with the corresponding rate.

## System Design

Based on user input and current exchange rates, the system is designed to keep a 
comprehensive list of users. Each user has:

- A list of accounts (either classic or savings).
- Each account can have multiple cards (classic or one-time).
- Users also have a list of transactions associated with their accounts, stored 
in ascending order by timestamp. This list will be used when querying transactions 
with the `printTransactions` command.

## Transaction Factory

The TransactionFactory class is utilized to streamline the creation of transaction 
objects across the e-banking system. Employing the Factory design pattern, this class
simplifies the instantiation process for various transaction types, each tailored to 
specific banking operations such as account or card creation, card payments, money
transfers and error handling.

## Commands Implemented

Below is a description of each implemented command:

### `printUsers`
- Displays all users, along with their associated accounts and cards.

### `addAccount`
- Adds a new account for a user (either classic or savings).

### `createCard`
- Creates a new card for a user’s account.

### `createOneTimeCard`
- Creates a one-time use card for a user’s account.

### `addFunds`
- Adds funds to an existing account.

### `deleteAccount`
- Deletes an account from a user.

### `deleteCard`
- Deletes a card from a user’s account.

### `payOnline`
- Allows users to make online payments using their cards.

### `sendMoney`
- Transfers funds between accounts or to another user’s account.

### `setAlias`
- Assigns an alias to an account to make it easier to identify.

### `printTransactions`
- Displays all transactions for a specific user.

### `setMinimumBalance`
- Sets a minimum balance requirement for an account.

### `checkCardStatus`
- Checks the status of a user’s card (whether it’s active or blocked).

### `splitPayment`
- Splits a payment across multiple accounts.

### `report`
- Generates a report of all transactions for a specified account between two timestamps.

### `spendingsReport`
- Generates a report of the spending activity from a specific account, limited to 
transactions that occurred between two designated timestamps.

### `addInterest`
- Adds interest to a savings account.

### `changeInterestRate`
- Changes the interest rate of a savings account.

These commands are part of the system’s functionality and are mapped to specific 
user actions to simulate typical banking operations. Each command has a defined behavior 
to ensure the proper handling of transactions, account management, and reporting.
