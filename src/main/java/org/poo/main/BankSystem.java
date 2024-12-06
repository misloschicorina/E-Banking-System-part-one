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
    }


    private void createCard(CommandInput command) {
        User user = findUserByEmail(command.getEmail());

        if (user == null)
            return;

        Account account = findAccountByIBANForUser(user, command.getAccount());

        if (account != null) {
            String cardNumber = org.poo.utils.Utils.generateCardNumber();
            Card Card = new Card(user, account, cardNumber);
            account.addCard(Card);
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
        }
    }

    private void deleteCard(CommandInput command) {
        String cardNumber = command.getCardNumber();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
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






}
