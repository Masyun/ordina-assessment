package com.ordina_assessment.word_count.analyzer;

import com.ordina_assessment.word_count.analyzer.model.WordFrequency;
import com.ordina_assessment.word_count.analyzer.model.WordFrequencyAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the WordFrequencyAnalyzer interface that provides methods
 * to calculate various frequencies of words within a given text.
 */
@Slf4j
@Service
public class WordFrequencyAnalyzerImpl implements WordFrequencyAnalyzer {

    /**
     * Calculate the highest frequency of any word in the provided text.
     *
     * @param text the text to analyze
     * @return the highest frequency found, or 0 if the text is empty
     */
    @Override
    public int calculateHighestFrequency(String text) {
        log.info("Calculating the highest frequency in text: {}", text);
        return Arrays.stream(text.split("\\W+"))
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(w -> w, Collectors.summingInt(w -> 1)))
                .values().stream()
                .max(Integer::compare)
                .orElse(0);
    }

    /**
     * Calculate the frequency of the specified word in the provided text.
     *
     * @param text the text to analyze
     * @param word the word to count in the text
     * @return the frequency of the specified word
     */
    @Override
    public int calculateFrequencyForWord(String text, String word) {
        log.info("Calculating frequency for word: '{}' in text: {}", word, text);
        return (int) Arrays.stream(text.split("\\W+"))
                .filter(w -> w.equalsIgnoreCase(word))
                .count();
    }

    /**
     * Calculate a list of the most frequent 'n' words in the provided text.
     *
     * @param text the text to analyze
     * @param n    the number of top frequent words to return
     * @return a list of word frequencies, ordered by frequency and then alphabetically
     */
    @Override
    public List<WordFrequency> calculateMostFrequentNWords(String text, int n) {
        log.info("Calculating the most frequent {} words in text: {}", n, text);
        Map<String, Integer> wordCounts = Arrays.stream(text.split("\\W+"))
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(w -> w, Collectors.summingInt(w -> 1)));

        return wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .map(e -> new WordFrequencyImpl(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
