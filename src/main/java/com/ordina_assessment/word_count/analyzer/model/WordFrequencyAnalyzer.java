package com.ordina_assessment.word_count.analyzer.model;

import java.util.List;


public interface WordFrequencyAnalyzer {
    int calculateHighestFrequency(String text);
    int calculateFrequencyForWord(String text, String word);
    List<WordFrequency> calculateMostFrequentNWords(String text, int n);
}
