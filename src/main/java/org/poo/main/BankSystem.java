package org.poo.main;

import org.poo.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                case "setMinimumBalance" -> setMinimumBalance(command);
                case "checkCardStatus" -> checkCardStatus(command, output);
                case "splitPayment" -> splitPayment(command);
                case "report" -> report(command, output);
                case "spendingsReport" -> spendingsReport(command, output);
                case "addInterest" -> addInterest(command, output);
                case "changeInterestRate" -> changeInterestRate(command, output);
                default -> {
                }
            }
        }
        Utils.resetRandom();
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
            ObjectNode userNode = Tools.printUser(user);
            usersArray.add(userNode);
        }

        // Add the serialized users list under the "output" key
        commandResultNode.set("output", usersArray);
        commandResultNode.put("timestamp", command.getTimestamp());

        // Add the constructed command node to the output
        output.add(commandResultNode);
    }

    // METODE DE CREARE SI ADAUGARE A TRANZACTIILOR LA LISTA DE TRANZACTII A USERULUI DAT

    // Creare tranzacție pentru un cont nou
    public void createAccountTransaction(int timestamp, Account account, User user) {
        if (user == null || account == null)
            return;

        Transaction transaction = TransactionFactory.createAccountTransaction(timestamp, account.getIBAN());
        user.addTransaction(transaction);
    }

    public void createDeleteAccountErrorTransaction(int timestamp, User user) {
        if (user == null)
            return;

        // Creează tranzacția de eroare pentru ștergerea contului
        Transaction transaction = TransactionFactory.deleteAccountErrorTransaction(timestamp);

        // Adaugă tranzacția în istoricul utilizatorului
        user.addTransaction(transaction);
    }


    // Creare tranzacție pentru un card nou
    public void createCardTransaction(int timestamp, Card card, Account account, User user) {
        if (user == null || account == null || card == null)
            return;

        Transaction transaction = TransactionFactory.createCardTransaction(timestamp, card.getCardNumber(),
                user.getEmail(), account.getIBAN());
        user.addTransaction(transaction);
    }

    // Creare tranzacție pentru "Card payment"
    public void createOnlinePaymentTransaction(int timestamp, Card card, double amount, String commerciant, User user, String IBAN) {
        if (user == null || card == null)
            return;

        Transaction transaction = TransactionFactory.createOnlinePaymentTransaction(timestamp, card.getCardNumber(), amount, commerciant, IBAN);
        user.addTransaction(transaction);
    }

    // Creare tranzacție pentru "send money"
    public void createSendMoneyTransaction(int timestamp, Account sender, Account receiver, Double amount, String currency, String description) {
        if (sender == null || receiver == null || amount == null || amount <= 0)
            return;

        User senderUser = Tools.findUserByEmail(sender.getOwnerEmail(), users);
        if (senderUser == null)
            return;

        Transaction sentTransaction = TransactionFactory.createSentMoneyTransaction(timestamp, sender.getIBAN(),
                receiver.getIBAN(), amount, currency);

        sentTransaction.setDescription(description);
        senderUser.addTransaction(sentTransaction);
    }

    // Creare tranzacție pentru "received money"
    public void createReceivedMoneyTransaction(int timestamp, Account sender, Account receiver, Double amount, Double rate, String currency, String description) {
        if (sender == null || receiver == null || amount == null || amount <= 0)
            return;

        User receiverUser = Tools.findUserByEmail(receiver.getOwnerEmail(), users);
        if (receiverUser == null)
            return;

        Transaction receivedTransaction = TransactionFactory.createReceivedMoneyTransaction(timestamp, sender.getIBAN(),
                receiver.getIBAN(), amount, rate, currency, description);
        receiverUser.addTransaction(receivedTransaction);
    }

    public void createInsuffiecientFundsTransaction(int timestamp, String description, User user, String IBAN) {
        if (user == null)
            return;

        // Create a failed transaction
        Transaction failureTransaction = TransactionFactory.createInsuffiecientFundsTransaction(timestamp, IBAN);
        // Add the transaction to the user's transaction list
        user.addTransaction(failureTransaction);
    }

    public void createDeletedCardTransaction(int timestamp, Account account, Card card, User user) {
        if (user == null || account == null || card == null)
            return;

        Transaction transaction = TransactionFactory.createDeletedCardTransaction(
                timestamp,
                account.getIBAN(),
                card.getCardNumber(),
                user.getEmail()
        );
        user.addTransaction(transaction);
    }

    private void createWarningTransaction(int timestamp, User user, String IBAN) {
        Transaction warningTransaction = TransactionFactory.createWarningTransaction(timestamp, IBAN);
        user.addTransaction(warningTransaction);
    }

    private void createCardFrozenTransaction(int timestamp, User user, String IBAN) {
        Transaction frozenTransaction = TransactionFactory.createCardFrozenErrorTransaction(timestamp, IBAN);
        user.addTransaction(frozenTransaction);
    }

    private void createSuccessSplitTransaction(int timestamp, double amount, double splitAmount, String currency, List<String> accounts, User user) {
        Transaction splitTransaction = TransactionFactory.createSuccessSplitTransaction(timestamp, amount, splitAmount, currency, accounts);
        user.addTransaction(splitTransaction);
    }

    public void createSplitErrorTransaction(
            double splitAmount, int timestamp, double totalAmount, String currency, String cheapIBAN, List<String> accounts, User user) {
        if (user == null || accounts == null || accounts.isEmpty() || cheapIBAN == null || currency == null)
            return;

        // Creează tranzacția de eroare pentru split
        Transaction transaction = TransactionFactory.createSplitErrorTransaction(
                splitAmount, timestamp, totalAmount, currency, cheapIBAN, accounts);

        // Adaugă tranzacția în lista utilizatorului
        user.addTransaction(transaction); // Această linie trebuie să existe!
    }


    void addAccount(CommandInput command) {
        User user = Tools.findUserByEmail(command.getEmail(), users);
        if (user == null)
            return;

        String accountType = command.getAccountType();
        String currency = command.getCurrency();

        Account account = null;

        String IBAN = Utils.generateIBAN();

        if ("classic".equals(accountType))
            account = new ClassicAccount(currency, command.getEmail(), IBAN);
        else if ("savings".equals(accountType)) {
            double interestRate = command.getInterestRate();
            account = new SavingsAccount(currency, command.getEmail(), interestRate, IBAN);
        }

        user.addAccount(account);
        createAccountTransaction(command.getTimestamp(), account, user);
    }


    private void createCard(CommandInput command) {
        User user = Tools.findUserByEmail(command.getEmail(), users);

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
        User user = Tools.findUserByEmail(command.getEmail(), users);

        if (user == null)
            return;

        Account account = findAccountByIBANForUser(user, command.getAccount());

        if (account != null) {
            String cardNumber = org.poo.utils.Utils.generateCardNumber();
            OneTimeCard oneTimeCard = new OneTimeCard(user, account, cardNumber);
            account.addCard(oneTimeCard);
            createCardTransaction(command.getTimestamp(), oneTimeCard, account, user);
        }
    }

    private void addFunds(CommandInput command) {
        Account account = Tools.findAccountByIBAN(command.getAccount(), users);

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
        User user = Tools.findUserByEmail(command.getEmail(), users);
        int timestamp = command.getTimestamp();

        if (user == null)
            return;

        String IBAN = command.getAccount();

        Account foundAccount = findAccountByIBANForUser(user, IBAN);

        if (foundAccount == null)
            return;

        // Verifică dacă soldul este zero
        if (foundAccount.getBalance() == 0) {
            // Distruge toate cardurile asociate
            List<Card> associatedCards = foundAccount.getCards(); // Se presupune că Account are metoda getCards()
            for (Card card : associatedCards) {
                String cardNumber = card.getCardNumber();
                String email = user.getEmail();

                // Creează tranzacție pentru distrugerea cardului
                Transaction deleteCardTransaction = TransactionFactory.createDeletedCardTransaction(
                        command.getTimestamp(), IBAN, cardNumber, email);
                user.addTransaction(deleteCardTransaction); // Se presupune că User are metoda addTransaction()
            }

            // Elimină toate cardurile asociate contului
            foundAccount.clearCards(); // Se presupune că Account are metoda clearCards()

            // Elimină contul
            user.removeAccount(foundAccount);

            // Creare răspuns de succes
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
            createDeleteAccountErrorTransaction(timestamp, user);
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

    private void payOnline(CommandInput command, ArrayNode output) {
        String email = command.getEmail();
        String cardNumber = command.getCardNumber();
        double amount = command.getAmount();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();
        String description = command.getDescription();
        String commerciant = command.getCommerciant();

        User user = Tools.findUserByEmail(email, users);

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

        if (card.getStatus().equals("frozen")) {
            createCardFrozenTransaction(timestamp, user, account.getIBAN());
            return;
        }

        performPayment(user, card, account, amount, currency, commerciant, timestamp, output);
    }

    private void performPayment(User user, Card card, Account account, double amount, String currency,
                                String commerciant, int timestamp, ArrayNode output) {
        // Fac conversie valutara daca trebuie
        double finalAmount = Tools.calculateFinalAmount(account, amount, exchangeRates, currency);

        double balance = account.getBalance();
        double diff = balance - finalAmount;

        if (balance >= finalAmount) {

            if (balance <= account.getMinBalance()) {
                createWarningTransaction(timestamp, user, account.getIBAN());
                card.freezeCard();
                return;
            }

            account.spend(finalAmount);
            createOnlinePaymentTransaction(timestamp, card, finalAmount, commerciant, user, account.getIBAN());

            if (card.isOneTimeCard())
                handleOneTimeCard(user, card, account, timestamp);
        } else {
            createInsuffiecientFundsTransaction(timestamp, "Insufficient funds", user, account.getIBAN());
        }
    }

    private void handleOneTimeCard(User user, Card card, Account account, int timestamp) {
        OneTimeCard oneTimeCard = (OneTimeCard) card;

        if (!oneTimeCard.isUsed()) {
            oneTimeCard.markAsUsed();

            // Creating a new card after payment
            String newCardNumber = Utils.generateCardNumber();
            OneTimeCard newOneTimeCard = new OneTimeCard(user, account, newCardNumber);

            // Adding new card in account
            account.addCard(newOneTimeCard);

            // Create a transaction for the new card
            createCardTransaction(timestamp, newOneTimeCard, account, user);
        }

        if (oneTimeCard.isUsed() == true)
            account.removeCard(oneTimeCard);
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

        Account senderAccount = Tools.findAccountByIBAN(senderIBAN, users);
        Account receiverAccount = Tools.findAccountByIBAN(receiverIBAN, users);

        // verif existenta ambelor conturi
        if (senderAccount == null || receiverAccount == null)
            return;

        // Conversie in moneda receiverului
        double finalAmount = amount;
        double exchangeRate = 1;
        if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
            exchangeRate = ExchangeRate.getExchangeRate(
                    senderAccount.getCurrency(),
                    receiverAccount.getCurrency(),
                    exchangeRates
            );
            if (exchangeRate == 0)
                return;
            finalAmount = amount * exchangeRate;
        }

        // Verific daca senderul are fonduri destule
        if (senderAccount.getBalance() < amount) {
            User user = Tools.findUserByAccount(senderIBAN, users);
            createInsuffiecientFundsTransaction(timestamp, "Insufficient funds", user, senderIBAN);
            return;
        }

        // Execut transferul
        senderAccount.spend(amount);
        receiverAccount.deposit(finalAmount);


        // Creare tranzactii in listele ambilor useri
        createSendMoneyTransaction(timestamp, senderAccount, receiverAccount, amount, senderAccount.getCurrency(), description);
        createReceivedMoneyTransaction(timestamp, senderAccount, receiverAccount, amount, exchangeRate, receiverAccount.getCurrency(), description);
    }


    private void setAlias(CommandInput command) {
        String email = command.getEmail();
        String alias = command.getAlias();
        String IBAN = command.getAccount();

        User user = Tools.findUserByEmail(email, users);
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

        User user = Tools.findUserByEmail(email, users);

        if (user == null) {
            return;
        }

        // Creează nodul rezultat pentru outputul tranzacțiilor
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "printTransactions");
        resultNode.put("timestamp", timestamp);

        // Sortează tranzacțiile utilizatorului
        List<Transaction> transactions = new ArrayList<>(user.getTransactions());
        transactions.sort((t1, t2) -> Integer.compare(t1.getTimestamp(), t2.getTimestamp()));

        // Obține nodurile tranzacțiilor
        ArrayNode transactionsArray = Tools.getTransactions(transactions);

        // Adaugă nodul tranzacțiilor în rezultatul final
        resultNode.set("output", transactionsArray);
        output.add(resultNode);
    }


    private void setMinimumBalance(CommandInput command) {
        String IBAN = command.getAccount();
        double limit = command.getAmount();

        Account account = Tools.findAccountByIBAN(IBAN, users);

        if (account != null)
            account.setMinBalance(limit);
    }

    private void checkCardStatus(CommandInput command, ArrayNode output) {
        String cardNumber = command.getCardNumber();
        int timestamp = command.getTimestamp();

        // Find the card
        Card foundCard = null;
        Account foundAccount = null;
        User foundUser = null;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        foundCard = card;
                        foundAccount = account;
                        foundUser = user;
                        break;
                    }
                }
            }
        }

        if (foundCard == null) {
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", "checkCardStatus");

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("description", "Card not found");
            outputNode.put("timestamp", timestamp);

            resultNode.set("output", outputNode);
            resultNode.put("timestamp", timestamp);

            output.add(resultNode);
            return;
        }

        if (foundAccount.getBalance() - foundAccount.getMinBalance() <= 30) {
            createWarningTransaction(timestamp, foundUser, foundAccount.getIBAN());
            foundCard.freezeCard();
        }
    }

    private void createSplitTransactionForUser(
            int timestamp,
            double splitAmount,
            double totalAmount,
            String currency,
            String cheapIBAN,
            List<String> accounts,
            User user) {
        if (user == null || accounts == null || accounts.isEmpty()) {
            return; // Verificăm dacă utilizatorul sau lista de conturi este invalidă
        }

        // Creează tranzacția de tip split folosind TransactionFactory
        Transaction splitTransaction = TransactionFactory.createSplitErrorTransaction(
                splitAmount, timestamp, totalAmount, currency, cheapIBAN, accounts
        );

        // Adaugă tranzacția în lista de tranzacții a utilizatorului
        user.addTransaction(splitTransaction);
    }

    private void splitPayment(CommandInput command) {

        List<String> ibans = command.getAccounts();
        double totalAmount = command.getAmount();
        double splitAmount = totalAmount / ibans.size();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();

        boolean canDoSplit = true;
        String cheapIBAN = null;

        for (String IBAN : ibans) {
            Account account = Tools.findAccountByIBAN(IBAN, users);
            double finalSplitAmount = Tools.calculateFinalAmount(account, splitAmount, exchangeRates, currency);

            if (account.getBalance() < finalSplitAmount) {
                canDoSplit = false;
                cheapIBAN = IBAN; // Ultimul cont care nu poate efectua plata
                // break;
            }
        }

        if (canDoSplit) {
            for (String IBAN : ibans) {
                Account account = Tools.findAccountByIBAN(IBAN, users);
                User currUser = Tools.findUserByAccount(IBAN, users);
                double finalSplitAmount = Tools.calculateFinalAmount(account, splitAmount, exchangeRates, currency);
                account.spend(finalSplitAmount);
                createSuccessSplitTransaction(timestamp, totalAmount, splitAmount, currency, ibans, currUser);
            }
        } else {
            // Creăm tranzacția de eroare doar o singură dată pentru fiecare utilizator
            for (String IBAN : ibans) {
                    User user = Tools.findUserByAccount(IBAN, users);
                    if (user != null) {
                        createSplitTransactionForUser(timestamp, splitAmount, totalAmount, currency, cheapIBAN, ibans, user);
                }
            }
        }
    }


    private void report(CommandInput command, ArrayNode output) {
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("command", command.getCommand());

        TransactionFilter filter = new ReportTransactionFilter();
        ObjectNode outputNode = Tools.generateReportData(command, filter, false, users, exchangeRates);

        reportNode.set("output", outputNode);
        reportNode.put("timestamp", command.getTimestamp());

        output.add(reportNode);
    }

    private void spendingsReport(CommandInput command, ArrayNode output) {
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("command", command.getCommand());

        TransactionFilter filter = new SpendingsTransactionFilter();
        ObjectNode outputNode = Tools.generateReportData(command, filter, true, users, exchangeRates);

        reportNode.set("output", outputNode);
        reportNode.put("timestamp", command.getTimestamp());

        output.add(reportNode);
    }

    private void addInterest(CommandInput command, ArrayNode output) {
        int timestamp = command.getTimestamp();
        String IBAN = command.getAccount();

        Account account = Tools.findAccountByIBAN(IBAN, users);

        if (account == null) {
            return;
        }

        if (account.isSavingsAccount()) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            double interestRate = savingsAccount.getInterestRate();
            double balance = savingsAccount.getBalance();
            savingsAccount.deposit(interestRate * balance);
        } else {
            ObjectNode result = output.addObject(); // Creează un nou nod în ArrayNode
            result.put("command", "addInterest");
            result.put("timestamp", timestamp);
            // Adaugă mesaj de eroare pentru conturi non-economii
            ObjectNode errorOutput = result.putObject("output");
            errorOutput.put("description", "This is not a savings account");
            errorOutput.put("timestamp", timestamp);
        }
    }

    private void changeInterestRate(CommandInput command, ArrayNode output) {
        int timestamp = command.getTimestamp();
        String IBAN = command.getAccount();

        Account account = Tools.findAccountByIBAN(IBAN, users);

        if (account == null) {
            return;
        }

        if (account.isSavingsAccount()) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            double interestRate = savingsAccount.getInterestRate();
            savingsAccount.setInterestRate(interestRate);

        } else {
            ObjectNode result = output.addObject(); // Creează un nou nod în ArrayNode
            result.put("command", "changeInterestRate");
            result.put("timestamp", timestamp);
            // Adaugă mesaj de eroare pentru conturi non-economii
            ObjectNode errorOutput = result.putObject("output");
            errorOutput.put("description", "This is not a savings account");
            errorOutput.put("timestamp", timestamp);
        }
    }

}




