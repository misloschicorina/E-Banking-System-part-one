package org.poo.main;

import java.util.*;

public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(String from, String to, double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getRate() {
        return rate;
    }

    /**
     * Găsește rata de schimb între două monede folosind DFS.
     */
    public static double getExchangeRate(String from, String to, List<ExchangeRate> exchangeRates) {
        // Construim graful ca hartă de adiacență
        Map<String, Map<String, Double>> graph = buildGraph(exchangeRates);

        // Dacă monedele nu există în graful nostru, returnăm 0
        if (!graph.containsKey(from) || !graph.containsKey(to)) {
            return 0;
        }

        // Set pentru a urmări nodurile vizitate
        Set<String> visited = new HashSet<>();

        // Căutare folosind DFS
        return dfs(graph, from, to, 1.0, visited);
    }

    /**
     * Construiește graful din lista de ExchangeRate.
     */
    private static Map<String, Map<String, Double>> buildGraph(List<ExchangeRate> exchangeRates) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (ExchangeRate rate : exchangeRates) {
            graph.putIfAbsent(rate.getFrom(), new HashMap<>());
            graph.putIfAbsent(rate.getTo(), new HashMap<>());

            graph.get(rate.getFrom()).put(rate.getTo(), rate.getRate());
            graph.get(rate.getTo()).put(rate.getFrom(), 1.0 / rate.getRate());
        }
        return graph;
    }

    /**
     * Algoritmul DFS pentru calculul ratei de schimb.
     */
    private static double dfs(Map<String, Map<String, Double>> graph, String current, String target, double product, Set<String> visited) {
        // Dacă am găsit moneda țintă, returnăm produsul curent
        if (current.equals(target)) {
            return product;
        }

        // Adăugăm nodul curent la setul de vizitate
        visited.add(current);

        // Explorăm toate monedele vecine
        for (Map.Entry<String, Double> neighbor : graph.get(current).entrySet()) {
            String nextCurrency = neighbor.getKey();
            double rate = neighbor.getValue();

            if (!visited.contains(nextCurrency)) {
                double result = dfs(graph, nextCurrency, target, product * rate, visited);
                if (result != 0) {
                    return result; // Dacă am găsit drumul, returnăm rata calculată
                }
            }
        }

        // Dacă nu găsim drumul, returnăm 0
        return 0;
    }

    /**
     * Extrag lista de monede distincte din input.
     */
    private static List<String> getCurrencies(List<ExchangeRate> exchangeRates) {
        Set<String> currencySet = new HashSet<>();
        for (ExchangeRate rate : exchangeRates) {
            currencySet.add(rate.getFrom());
            currencySet.add(rate.getTo());
        }
        return new ArrayList<>(currencySet);
    }


}
