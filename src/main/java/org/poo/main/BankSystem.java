package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class BankSystem {
    private final List<User> users = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addUser(User user) {
        users.add(user);
    }

    public void processCommands(CommandInput[] commands, ArrayNode output) {
        for (CommandInput command : commands) {
            switch (command.getCommand()) {
                case "printUsers" -> printUsers(command, output);
                case "addAccount" -> addAccount(command);
                case "createCard" -> createCard(command);
                case "createOneTimeCard" -> createOneTimeCard(command);
                case "addFunds" -> addFunds(command);
                default -> { }
            }
        }
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

    void addAccount(CommandInput command) {
        User user = findUserByEmail(command.getEmail());
        if (user == null)
            return;

        String accountType = command.getAccountType();
        String currency = command.getCurrency();

        Account account = null;

        if ("classic".equals(accountType)) {
            account = new ClassicAccount(currency, command.getEmail());
        } else if ("savings".equals(accountType)) {
            account = new SavingsAccount(currency, command.getEmail(), 0); // Initial interest rate set to 0
        }

        user.addAccount(account);
    }

    private void createCard(CommandInput command) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        Account account = findAccountByIBAN(command.getAccount());

        if (account != null) {
            Card Card = new Card(user, account);
            account.addCard(Card);
        }
    }

    private void createOneTimeCard(CommandInput command) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        Account account = findAccountByIBAN(command.getAccount());

        if (account != null) {
            OneTimeCard oneTimeCard = new OneTimeCard(user, account);
            account.addCard(oneTimeCard);
        }
    }

    private void addFunds(CommandInput command) {
        Account account = findAccountByIBAN(command.getAccount());

        if (account != null)
            account.deposit(command.getAmount());
    }
}
