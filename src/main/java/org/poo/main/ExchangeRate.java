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
     * Constructie matrice de adiacenta pe baza ratelor de schimb
     */
    public static double[][] buildAdjacencyMatrix(List<ExchangeRate> exchangeRates, List<String> currencies) {
        int n = currencies.size();
        double[][] adjacencyMatrix = new double[n][n];

        // Inițializează matricea cu valori mari (∞) pentru a indica absența muchiilor
        for (int i = 0; i < n; i++) {
            Arrays.fill(adjacencyMatrix[i], Double.POSITIVE_INFINITY);
            adjacencyMatrix[i][i] = 1.0; // Rata de conversie către sine este 1
        }

        // Adaugă ratele de schimb în matrice
        for (ExchangeRate rate : exchangeRates) {
            int fromIndex = currencies.indexOf(rate.getFrom());
            int toIndex = currencies.indexOf(rate.getTo());

            adjacencyMatrix[fromIndex][toIndex] = rate.getRate();
            adjacencyMatrix[toIndex][fromIndex] = 1.0 / rate.getRate(); // Adaugă rata inversă
        }

        return adjacencyMatrix;
    }

    /**
     * Folosim algoritmul Floyd-Warshall pt calculul ratei de schimb minime
     */
    public static void floydWarshall(double[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (adjacencyMatrix[i][k] != Double.POSITIVE_INFINITY &&
                            adjacencyMatrix[k][j] != Double.POSITIVE_INFINITY) {
                        adjacencyMatrix[i][j] = Math.min(adjacencyMatrix[i][j],
                                adjacencyMatrix[i][k] * adjacencyMatrix[k][j]);
                    }
                }
            }
        }
    }

    /**
     * Găsește rata de schimb între două monede folosind matricea de adiacență.
     */
    public static double getExchangeRate(String from, String to, List<ExchangeRate> exchangeRates) {
        // Fac o lista de monede distincte
        List<String> currencies = getCurrencies(exchangeRates);

        // Construiesc matrice de adiacenta care reprezinta graful, unde nodurile sunt monede
        double[][] adjacencyMatrix = buildAdjacencyMatrix(exchangeRates, currencies);

        // Aplic alg pt a gasi distanta minima dintre 2 monede
        floydWarshall(adjacencyMatrix);

        // Gasesc indicii pt cele 2 monede inte care se face conversie
        int fromIndex = currencies.indexOf(from);
        int toIndex = currencies.indexOf(to);

        if (fromIndex == -1 || toIndex == -1) {
            // Dacă cel puțin una dintre monede nu este în listă, returnăm 0
            return 0;
        }

        // Returnăm rata de schimb din matrice
        return adjacencyMatrix[fromIndex][toIndex];
    }

    /**
     * Extrag lista de monede distincte din input
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
