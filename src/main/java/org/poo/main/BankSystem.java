package org.poo.main;

import org.poo.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class BankSystem {
    private final List<User> users = new ArrayList<>();
    private final List<ExchangeRate> exchangeRates = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addUser(User user) {
        users.add(user);
    }

    public void addExchangeRate(ExchangeRate exchangeRate) {
        exchangeRates.add(exchangeRate);
    }

    public void processCommands(CommandInput[] commands, ArrayNode output) {
        for (CommandInput command : commands) {
            switch (command.getCommand()) {
                case "printUsers" -> printUsers(command, output);
                case "addAccount" -> addAccount(command);
                case "createCard" -> createCard(command);
                case "createOneTimeCard" -> createOneTimeCard(command);
                case "addFunds" -> addFunds(command);
                case "deleteAccount" -> deleteAccount(command, output);
                case "deleteCard" -> deleteCard(command);
                case "payOnline" -> payOnline(command, output);
                case "sendMoney" -> sendMoney(command);
                case "setAlias" -> setAlias(command);
                case "printTransactions" -> printTransactions(command, output);
                default -> {
                }
            }
        }
        Utils.resetRandom();
    }

    private ArrayNode printCardsForAccount(List<Card> cards) {
        // Create an array to hold the cards
        ArrayNode cardsArray = objectMapper.createArrayNode();

        for (Card card : cards) {
            ObjectNode cardNode = objectMapper.createObjectNode();
            cardNode.put("cardNumber", card.getCardNumber());
            cardNode.put("status", card.getStatus());
            cardsArray.add(cardNode);
        }

        return cardsArray;
    }

    private ArrayNode printAccountsForUser(List<Account> accounts) {
        // Create an array to hold the accounts
        ArrayNode accountsArray = objectMapper.createArrayNode();

        for (Account account : accounts) {
            ObjectNode accountNode = objectMapper.createObjectNode();
            accountNode.put("IBAN", account.getIBAN());
            accountNode.put("balance", account.getBalance());
            accountNode.put("currency", account.getCurrency());
            accountNode.put("type", account.getACcountType());

            // Add cards to the account
            ArrayNode cardsArray = printCardsForAccount(account.getCards());
            accountNode.set("cards", cardsArray);

            accountsArray.add(accountNode);
        }

        return accountsArray;
    }

    private ObjectNode printUser(User user) {
        // Create a node for the user
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", user.getFirstName());
        userNode.put("lastName", user.getLastName());
        userNode.put("email", user.getEmail());

        // Create an array for the accounts and add it to the user node
        ArrayNode accountsArray = printAccountsForUser(user.getAccounts());
        userNode.set("accounts", accountsArray);

        return userNode;
    }

    private void printUsers(CommandInput command, ArrayNode output) {
        // Create a node to represent the command result
        ObjectNode commandResultNode = objectMapper.createObjectNode();

        // Add command name and timestamp
        commandResultNode.put("command", "printUsers");

        // Create an array to hold the users
        ArrayNode usersArray = objectMapper.createArrayNode();

        // Serialize each user and include their accounts
        for (User user : users) {
            ObjectNode userNode = printUser(user);
            usersArray.add(userNode);
        }

        // Add the serialized users list under the "output" key
        commandResultNode.set("output", usersArray);
        commandResultNode.put("timestamp", command.getTimestamp());

        // Add the constructed command node to the output
        output.add(commandResultNode);
    }

    private User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    private Account findAccountByIBAN(String iban) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban))
                    return account;
            }
        }
        return null;
    }

    // METODE DE CREARE SI ADAUGARE A TRANZACTIILOR LA LISTA DE TRANZACTII A USERULUI DAT

    // Creare tranzacție pentru un cont nou
    public void createAccountTransaction(int timestamp, Account account, User user) {
        if (user == null || account == null) {
            return;
        }
        Transaction transaction = TransactionFactory.createAccountTransaction(timestamp, account.getIBAN());
        user.addTransaction(transaction); // Adaugă tranzacția în lista utilizatorului
    }

    // Creare tranzacție pentru un card nou
    public void createCardTransaction(int timestamp, Card card, Account account, User user) {
        if (user == null || account == null || card == null) {
            return;
        }
        Transaction transaction = TransactionFactory.createCardTransaction(timestamp, card.getCardNumber(),
                user.getEmail(), account.getIBAN());
        user.addTransaction(transaction);
    }

    // Creare tranzacție pentru "Card payment"
    public void createOnlinePaymentTransaction(int timestamp, Card card, double amount, String commerciant, User user) {
        if (user == null || card == null) {
            return;
        }

        Transaction transaction = TransactionFactory.createOnlinePaymentTransaction(timestamp, card.getCardNumber(), amount, commerciant);
        user.addTransaction(transaction);
    }


    // Creare tranzacție pentru "send money"
    public void createSendMoneyTransaction(int timestamp, Account sender, Account receiver, Double amount, String currency, String description) {
        if (sender == null || receiver == null || amount == null || amount <= 0) {
            return;
        }
        User senderUser = findUserByEmail(sender.getOwnerEmail());
        if (senderUser == null) {
            return;
        }

        Transaction sentTransaction = TransactionFactory.createSentMoneyTransaction(timestamp, sender.getIBAN(),
                receiver.getIBAN(), amount, currency);
        // Setăm descrierea specificată din input
        sentTransaction.setDescription(description);
        senderUser.addTransaction(sentTransaction); // Adaugă tranzacția în istoricul utilizatorului expeditor
    }

    // Creare tranzacție pentru "received money"
    public void createReceivedMoneyTransaction(int timestamp, Account sender, Account receiver, Double amount, String currency) {
        if (sender == null || receiver == null || amount == null || amount <= 0) {
            return;
        }
        User receiverUser = findUserByEmail(receiver.getOwnerEmail());
        if (receiverUser == null) {
            return;
        }

        Transaction receivedTransaction = TransactionFactory.createReceivedMoneyTransaction(timestamp, sender.getIBAN(),
                receiver.getIBAN(), amount, currency);
        receiverUser.addTransaction(receivedTransaction); // Adaugă tranzacția în istoricul utilizatorului destinatar
    }

    public void createOnlinePaymentFailureTransaction(int timestamp, String description, User user) {
        if (user == null) {
            return;
        }
        // Create a failed transaction
        Transaction failureTransaction = TransactionFactory.createOnlinePaymentFailureTransaction(timestamp, description);
        // Add the transaction to the user's transaction list
        user.addTransaction(failureTransaction);
    }

    public void createDeletedCardTransaction(int timestamp, Account account, Card card, User user) {
        if (user == null || account == null || card == null) {
            return;
        }
        Transaction transaction = TransactionFactory.createDeletedCardTransaction(
                timestamp,
                account.getIBAN(),
                card.getCardNumber(),
                user.getEmail()
        );
        user.addTransaction(transaction);
    }


    void addAccount(CommandInput command) {
        User user = findUserByEmail(command.getEmail());
        if (user == null)
            return;

        String accountType = command.getAccountType();
        String currency = command.getCurrency();

        Account account = null;

        String IBAN = Utils.generateIBAN();

        if ("classic".equals(accountType)) {
            account = new ClassicAccount(currency, command.getEmail(), IBAN);
        } else if ("savings".equals(accountType)) {
            account = new SavingsAccount(currency, command.getEmail(), 0, IBAN); // Initial interest rate set to 0
        }

        user.addAccount(account);
        createAccountTransaction(command.getTimestamp(), account, user);
    }


    private void createCard(CommandInput command) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        Account account = findAccountByIBANForUser(user, command.getAccount());

        if (account != null) {
            String cardNumber = org.poo.utils.Utils.generateCardNumber();
            Card card = new Card(user, account, cardNumber);
            account.addCard(card);
            createCardTransaction(command.getTimestamp(), card, account, user);
        }
    }

    private void createOneTimeCard(CommandInput command) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        Account account = findAccountByIBANForUser(user, command.getAccount());

        if (account != null) {
            String cardNumber = org.poo.utils.Utils.generateCardNumber();
            OneTimeCard oneTimeCard = new OneTimeCard(user, account, cardNumber);
            account.addCard(oneTimeCard);
        }
    }

    private void addFunds(CommandInput command) {
        Account account = findAccountByIBAN(command.getAccount());

        if (account != null)
            account.deposit(command.getAmount());
    }

    private Account findAccountByIBANForUser(User user, String iban) {
        for (Account account : user.getAccounts()) {
            if (account.getIBAN().equals(iban))
                return account;
        }
        return null;
    }

    private void deleteAccount(CommandInput command, ArrayNode output) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        String IBAN = command.getAccount();

        Account foundAccount = findAccountByIBANForUser(user, IBAN);

        if (foundAccount == null)
            return;

        if (foundAccount.getBalance() == 0) {
            user.removeAccount(foundAccount);

            ObjectNode commandResultNode = objectMapper.createObjectNode();
            commandResultNode.put("command", "deleteAccount");

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("success", "Account deleted");
            outputNode.put("timestamp", command.getTimestamp());

            commandResultNode.set("output", outputNode);
            commandResultNode.put("timestamp", command.getTimestamp());

            output.add(commandResultNode);
        } else {
            deleteAccountError(command, output);
        }
    }

    private void deleteAccountError(CommandInput command, ArrayNode output) {
        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("command", "deleteAccount");

        ObjectNode errorOutput = objectMapper.createObjectNode();
        errorOutput.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        errorOutput.put("timestamp", command.getTimestamp());

        errorNode.set("output", errorOutput);
        errorNode.put("timestamp", command.getTimestamp());

        output.add(errorNode);
    }

    private void deleteCard(CommandInput command) {
        String cardNumber = command.getCardNumber();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        createDeletedCardTransaction(command.getTimestamp(), account, card, user);
                        account.removeCard(card);
                        return;
                    }
                }
            }
        }
    }

    private double getExchangeRate(String from, String to) {
        return ExchangeRate.getExchangeRate(from, to, exchangeRates);
    }

    private void payOnline(CommandInput command, ArrayNode output) {
        String email = command.getEmail();
        String cardNumber = command.getCardNumber();
        double amount = command.getAmount();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();
        String description = command.getDescription();
        String commerciant = command.getCommerciant();

        User user = findUserByEmail(email);

        if (user == null)
            return;

        Card card = null;
        Account account = null;

        for (Account acc : user.getAccounts()) {
            for (Card c : acc.getCards()) {
                if (c.getCardNumber().equals(cardNumber)) {
                    card = c;
                    account = acc;
                    break;
                }
            }
            if (card != null)
                break;
        }

        if (card == null || account == null) {
            payOnlineError("Card not found", timestamp, output);
            return;
        }

        // verifcare moneda si efectuare conversie daca trebuie
        double finalAmount = amount;


        if (!account.getCurrency().equals(currency)) {
            double rate = getExchangeRate(currency, account.getCurrency());
            if (rate == 0) {
                System.out.println("alooo");
                return;
            }
            finalAmount = amount * rate;
        }

        // verific daca am bani sa cheltui si fac tranzactia
        if (account.getBalance() >= finalAmount) {
            account.spend(finalAmount);
            createOnlinePaymentTransaction(timestamp, card, finalAmount, commerciant, user);
        } else {
            createOnlinePaymentFailureTransaction(timestamp, "Insufficient funds", user);
        }
    }

    private void payOnlineError(String description, int timestamp, ArrayNode output) {
        ObjectNode errorNode = objectMapper.createObjectNode();

        errorNode.put("command", "payOnline");

        ObjectNode errorOutput = objectMapper.createObjectNode();
        errorOutput.put("timestamp", timestamp);
        errorOutput.put("description", description);

        errorNode.set("output", errorOutput);

        errorNode.put("timestamp", timestamp);

        output.add(errorNode);
    }

    private void sendMoney(CommandInput command) {
        String senderIBAN = command.getAccount();
        String receiverIBAN = command.getReceiver();
        double amount = command.getAmount();
        String description = command.getDescription(); // Preluăm descrierea din input
        int timestamp = command.getTimestamp();

        Account senderAccount = findAccountByIBAN(senderIBAN);
        Account receiverAccount = findAccountByIBAN(receiverIBAN);

        // Verific dacă ambele conturi există
        if (senderAccount == null || receiverAccount == null) {
            return;
        }

        // Conversie valutară în moneda destinatarului
        double finalAmount = amount;
        if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
            double exchangeRate = ExchangeRate.getExchangeRate(
                    senderAccount.getCurrency(),
                    receiverAccount.getCurrency(),
                    exchangeRates
            );
            if (exchangeRate == 0)
                return;
            finalAmount = amount * exchangeRate;
        }

        // Verific dacă expeditorul are suficiente fonduri
        if (senderAccount.getBalance() < amount)
            return;

        // Execut transferul
        senderAccount.spend(amount);
        receiverAccount.deposit(finalAmount);

        // Creare tranzacții cu descrierea specificată
        createSendMoneyTransaction(timestamp, senderAccount, receiverAccount, amount, senderAccount.getCurrency(), description);
        createReceivedMoneyTransaction(timestamp, senderAccount, receiverAccount, finalAmount, receiverAccount.getCurrency());
    }


    private void setAlias(CommandInput command) {
        String email = command.getEmail();
        String alias = command.getAlias();
        String IBAN = command.getAccount();

        User user = findUserByEmail(email);
        if (user == null)
            return;

        Account account = findAccountByIBANForUser(user, IBAN);
        if (account == null)
            return;

        account.setAlias(alias);
    }

    private void printTransactions(CommandInput command, ArrayNode output) {
        String email = command.getEmail();
        int timestamp = command.getTimestamp();

        // Găsește utilizatorul după email
        User user = findUserByEmail(email);
        if (user == null) {
            return;
        }

        // Creează nodul rezultat pentru afișarea tranzacțiilor
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "printTransactions");
        resultNode.put("timestamp", timestamp);

        // Sortează tranzacțiile utilizatorului după timestamp
        List<Transaction> transactions = new ArrayList<>(user.getTransactions());
        transactions.sort((t1, t2) -> Integer.compare(t1.getTimestamp(), t2.getTimestamp()));

        // Creează lista de tranzacții pentru output
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : transactions) {
            ObjectNode transactionNode = objectMapper.createObjectNode();

            // Construim nodul tranzacției în ordinea dorită
            if (transaction.getAccountIBAN() != null) {
                transactionNode.put("account", transaction.getAccountIBAN());
            }
            if (transaction.getCardNumber() != null) {
                transactionNode.put("card", transaction.getCardNumber());
            }
            if (transaction.getCardHolder() != null) {
                transactionNode.put("cardHolder", transaction.getCardHolder());
            }
            transactionNode.put("description", transaction.getDescription());
            transactionNode.put("timestamp", transaction.getTimestamp());

            // Adaugă alte câmpuri în funcție de tipul tranzacției
            if (transaction.getSenderIBAN() != null) {
                transactionNode.put("senderIBAN", transaction.getSenderIBAN());
            }
            if (transaction.getReceiverIBAN() != null) {
                transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
            }
            if (transaction.getAmount() != null) {
                if ("Card payment".equals(transaction.getDescription())) {
                    // Include suma fără format special pentru "Card payment"
                    transactionNode.put("amount", transaction.getAmount());
                } else {
                    // Format pentru alte tranzacții (cu monedă)
                    String formattedAmount = String.format("%.1f %s", transaction.getAmount(), transaction.getCurrency());
                    transactionNode.put("amount", formattedAmount);
                }
            }
            if (transaction.getTransferType() != null) {
                transactionNode.put("transferType", transaction.getTransferType());
            }
            if (transaction.getCommerciant() != null) {
                transactionNode.put("commerciant", transaction.getCommerciant());
            }

            // Adaugă tranzacția la lista de tranzacții
            transactionsArray.add(transactionNode);
        }

        // Adaugă lista de tranzacții în output
        resultNode.set("output", transactionsArray);
        output.add(resultNode);
    }

}


