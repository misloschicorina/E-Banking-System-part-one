package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class Tools {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Tools() {
    }

    public static User findUserByEmail(String email, List<User> users) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    public static Account findAccountByIBAN(String iban, List<User> users) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban))
                    return account;
            }
        }
        return null;
    }

    public static User findUserByAccount(String iban, List<User> users) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban))
                    return user;
            }
        }
        return null;
    }

    public static ArrayNode printCardsForAccount(List<Card> cards) {
        ArrayNode cardsArray = objectMapper.createArrayNode();

        for (Card card : cards) {
            ObjectNode cardNode = objectMapper.createObjectNode();
            cardNode.put("cardNumber", card.getCardNumber());
            cardNode.put("status", card.getStatus());
            cardsArray.add(cardNode);
        }

        return cardsArray;
    }

    public static ArrayNode printAccountsForUser(List<Account> accounts) {
        ArrayNode accountsArray = objectMapper.createArrayNode();

        for (Account account : accounts) {
            ObjectNode accountNode = objectMapper.createObjectNode();
            accountNode.put("IBAN", account.getIBAN());
            accountNode.put("balance", account.getBalance());
            accountNode.put("currency", account.getCurrency());
            accountNode.put("type", account.getACcountType());

            ArrayNode cardsArray = printCardsForAccount(account.getCards());
            accountNode.set("cards", cardsArray);

            accountsArray.add(accountNode);
        }

        return accountsArray;
    }

    public static ObjectNode printUser(User user) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", user.getFirstName());
        userNode.put("lastName", user.getLastName());
        userNode.put("email", user.getEmail());

        ArrayNode accountsArray = printAccountsForUser(user.getAccounts());
        userNode.set("accounts", accountsArray);

        return userNode;
    }

    public static double getExchangeRate(String from, String to, List<ExchangeRate> exchangeRates) {
        return ExchangeRate.getExchangeRate(from, to, exchangeRates);
    }

    public static double calculateFinalAmount(Account account, double amount, List<ExchangeRate> exchangeRates, String currency){
        double finalAmount = amount;
        if (!account.getCurrency().equals(currency)) {
            double rate = Tools.getExchangeRate(currency, account.getCurrency(), exchangeRates);
            if (rate == 0)
                return 0;
            finalAmount = amount * rate;
        }
        return finalAmount;
    }
}
