package com.ordina_assessment.word_count.analyzer;

import com.ordina_assessment.word_count.analyzer.model.WordFrequency;

public record WordFrequencyImpl(String word, int frequency) implements WordFrequency {

    @Override
    public String toString() {
        return '(' + word + ':' + frequency + ')';
    }
}
