package com.stockanalysis.models;

public class SentimentResult {
    private final String label;
    private final double score;

    public SentimentResult(String label, double score) {
        this.label = label;
        this.score = score;
    }

    public String getLabel() {
        return label;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "SentimentResult{" +
                "label='" + label + '\'' +
                ", score=" + score +
                '}';
    }
}