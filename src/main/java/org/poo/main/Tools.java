package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.poo.fileio.CommandInput;

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

    public static ArrayNode getTransactions(List<Transaction> transactions){
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : transactions) {
            ObjectNode transactionNode = objectMapper.createObjectNode();

            // Add common fields for all transactions
            transactionNode.put("description", transaction.getDescription());
            transactionNode.put("timestamp", transaction.getTimestamp());

            // Check if the transaction involves split payment
            if (transaction.getInvolvedAccounts() != null && !transaction.getInvolvedAccounts().isEmpty()) {
                ArrayNode involvedAccountsArray = objectMapper.createArrayNode();
                for (String account : transaction.getInvolvedAccounts()) {
                    involvedAccountsArray.add(account);
                }

                transactionNode.put("description", transaction.getDescription());

                transactionNode.put("amount", transaction.getAmount());
                transactionNode.put("currency", transaction.getCurrency());
                transactionNode.set("involvedAccounts", involvedAccountsArray);
            } else {
                // Add other fields conditionally
                if (transaction.getDescription().equals("The card has been destroyed")
                || transaction.getDescription().equals("New card created")) {
                    transactionNode.put("account", transaction.getAccountIBAN());
                }
                if (transaction.getCardNumber() != null) {
                    transactionNode.put("card", transaction.getCardNumber());
                }
                if (transaction.getCardHolder() != null) {
                    transactionNode.put("cardHolder", transaction.getCardHolder());
                }
                if (transaction.getAmount() != null) {
                    if ("Card payment".equals(transaction.getDescription())) {
                        transactionNode.put("amount", transaction.getAmount());
                    } else {
                        String formattedAmount = String.format("%.1f %s",
                                transaction.getAmount(), transaction.getCurrency());
                        transactionNode.put("amount", formattedAmount);
                    }
                }
                if (transaction.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                }
                if (transaction.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                }
                if (transaction.getTransferType() != null) {
                    transactionNode.put("transferType", transaction.getTransferType());
                }
                if (transaction.getCommerciant() != null) {
                    transactionNode.put("commerciant", transaction.getCommerciant());
                }
            }

            // Add the transaction node to the array
            transactionsArray.add(transactionNode);
        }
        return transactionsArray;
    }

    public static ObjectNode generateReportData(
            CommandInput command,
            TransactionFilter filter,
            boolean includeCommerciants,
            List<User> users,
            List<ExchangeRate> exchangeRates) {

        ObjectMapper objectMapper = new ObjectMapper();
        String IBAN = command.getAccount();

        // Validarea contului și utilizatorului
        User user = validateUserAndAccount(command, users);
        if (user == null) {
            return createErrorNode(command, "Account not found");
        }

        // Filtrarea tranzacțiilor
        List<Transaction> filteredTransactions = filterTransactions(user.getTransactions(), command, filter, IBAN);

        // Calcularea totalurilor pentru comercianți (dacă este cazul)
        Map<String, Double> commerciantsTotals = includeCommerciants
                ? calculateCommerciantsTotals(filteredTransactions)
                : null;

        // Crearea nodului de ieșire
        return createReportOutputNode(command, filteredTransactions, commerciantsTotals, includeCommerciants, IBAN, user);
    }

    private static User validateUserAndAccount(CommandInput command, List<User> users) {
        String IBAN = command.getAccount();
        Account account = findAccountByIBAN(IBAN, users);
        if (account == null) {
            return null;
        }

        return findUserByAccount(IBAN, users);
    }

    private static ObjectNode createReportOutputNode(
            CommandInput command,
            List<Transaction> filteredTransactions,
            Map<String, Double> commerciantsTotals,
            boolean includeCommerciants,
            String IBAN,
            User user) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode outputNode = objectMapper.createObjectNode();

        // Informații generale
        outputNode.put("IBAN", IBAN);
        outputNode.put("balance", findAccountByIBAN(IBAN, List.of(user)).getBalance());
        outputNode.put("currency", findAccountByIBAN(IBAN, List.of(user)).getCurrency());

        // Adaugă tranzacțiile
        ArrayNode transactionsArray = getTransactions(filteredTransactions);
        outputNode.set("transactions", transactionsArray);

        // Adaugă comercianții dacă este cazul
        if (includeCommerciants && commerciantsTotals != null) {
            ArrayNode commerciantsArray = objectMapper.createArrayNode();
            for (Map.Entry<String, Double> entry : commerciantsTotals.entrySet()) {
                ObjectNode commerciantNode = objectMapper.createObjectNode();
                commerciantNode.put("commerciant", entry.getKey());
                commerciantNode.put("total", entry.getValue());
                commerciantsArray.add(commerciantNode);
            }
            outputNode.set("commerciants", commerciantsArray);
        }

        return outputNode;
    }

    private static ObjectNode createErrorNode(CommandInput command, String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("description", message);
        errorNode.put("timestamp", command.getTimestamp());
        return errorNode;
    }

    private static List<Transaction> filterTransactions(
            List<Transaction> transactions,
            CommandInput command,
            TransactionFilter filter,
            String IBAN) {

        int startTimestamp = command.getStartTimestamp();
        int endTimestamp = command.getEndTimestamp();
        List<Transaction> filteredTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp &&
                    transaction.getTimestamp() <= endTimestamp &&
                    filter.filter(transaction, IBAN)) {
                filteredTransactions.add(transaction);
            }
        }

        // Sortează tranzacțiile
        filteredTransactions.sort((t1, t2) -> Integer.compare(t1.getTimestamp(), t2.getTimestamp()));
        return filteredTransactions;
    }

    private static Map<String, Double> calculateCommerciantsTotals(List<Transaction> transactions) {
        Map<String, Double> commerciantsTotals = new HashMap<>();
        for (Transaction transaction : transactions) {
            String commerciant = transaction.getCommerciant();
            if (commerciant != null) {
                commerciantsTotals.put(commerciant,
                        commerciantsTotals.getOrDefault(commerciant, 0.0) + transaction.getAmount());
            }
        }
        return commerciantsTotals;
    }


}
