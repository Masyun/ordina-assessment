package com.ordina_assessment.word_count;


import com.ordina_assessment.word_count.analyzer.model.WordFrequency;
import com.ordina_assessment.word_count.analyzer.model.WordFrequencyAnalyzer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * REST Controller for word frequency operations.
 * Provides endpoints for analyzing text input and retrieving information about word frequencies.
 */
@RestController
@RequestMapping("/wordcount")
@Validated
public class WordFrequencyController {
    private final WordFrequencyAnalyzer wordFrequencyAnalyzer;

    /**
     * Constructs a WordFrequencyController with the necessary word frequency analyzer.
     *
     * @param wordFrequencyAnalyzer the word frequency analyzer to use for computations
     */
    public WordFrequencyController(WordFrequencyAnalyzer wordFrequencyAnalyzer) {
        this.wordFrequencyAnalyzer = wordFrequencyAnalyzer;
    }

    /**
     * Retrieves the highest frequency of any word in a given text.
     *
     * @param text the text to analyze, must not be blank and must contain at least one character
     * @return ResponseEntity with the highest frequency as an integer
     */
    @GetMapping("/highest-frequency")
    public ResponseEntity<Integer> getHighestFrequency(@RequestParam @NotBlank String text) {
        int highestFrequency = wordFrequencyAnalyzer.calculateHighestFrequency(text);
        return ResponseEntity.ok(highestFrequency);
    }

    /**
     * Retrieves the frequency of a specific word in a given text.
     *
     * @param text the text to analyze, must not be blank
     * @param word the word to count within the text, must not be blank
     * @return ResponseEntity with the frequency of the specified word as an integer
     */
    @GetMapping("/frequency")
    public ResponseEntity<Integer> getFrequencyForWord(@RequestParam @NotBlank String text,
                                                       @RequestParam @NotBlank String word) {
        int frequency = wordFrequencyAnalyzer.calculateFrequencyForWord(text, word);
        return ResponseEntity.ok(frequency);
    }

    /**
     * Retrieves a list of the most frequent 'n' words in a given text.
     *
     * @param text the text to analyze, must not be blank
     * @param n    the number of top frequent words to retrieve, must be a non-negative integer
     * @return ResponseEntity with a list of {@link WordFrequency} instances representing the most frequent words
     */
    @GetMapping("/word-frequency")
    public ResponseEntity<List<WordFrequency>> getMostFrequentNWords(@RequestParam @NotBlank String text,
                                                                     @RequestParam @Positive(message = "The number 'n' must be a positive integer.") int n) {
        List<WordFrequency> frequentWords = wordFrequencyAnalyzer.calculateMostFrequentNWords(text, n);
        return ResponseEntity.ok(frequentWords);
    }
}
