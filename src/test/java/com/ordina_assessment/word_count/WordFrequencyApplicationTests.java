package com.ordina_assessment.word_count;

import com.ordina_assessment.word_count.analyzer.WordFrequencyAnalyzerImpl;
import com.ordina_assessment.word_count.analyzer.WordFrequencyImpl;
import com.ordina_assessment.word_count.analyzer.model.WordFrequency;
import com.ordina_assessment.word_count.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WordFrequencyApplicationTests {

    private static final String BASE_TEXT = "The sun shines over the lake";

    private MockMvc mockMvc;

    @Mock
    private WordFrequencyAnalyzerImpl wordFrequencyAnalyzer;

    @InjectMocks
    private WordFrequencyController wordFrequencyController;

    @BeforeEach
    public void setup() {
        // Set up the Validator
        Validator validator = createValidator();

        // Include the validator in the MockMvc setup
        mockMvc = MockMvcBuilders.standaloneSetup(wordFrequencyController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Validator createValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }

    @Test
    void getHighestFrequency_ValidInput_ShouldReturnCorrectFrequency() throws Exception {
        when(wordFrequencyAnalyzer.calculateHighestFrequency(eq(BASE_TEXT))).thenReturn(2);

        mockMvc.perform(get("/wordcount/highest-frequency")
                        .param("text", BASE_TEXT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void getFrequencyForWord_ValidInput_ShouldReturnCorrectFrequency() throws Exception {
        String word = "the";
        when(wordFrequencyAnalyzer.calculateFrequencyForWord(eq(BASE_TEXT), eq(word))).thenReturn(2);

        mockMvc.perform(get("/wordcount/frequency")
                        .param("text", BASE_TEXT)
                        .param("word", word)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void getMostFrequentNWords_ValidInput_ShouldReturnCorrectList() throws Exception {
        int n = 3;
        List<WordFrequency> frequentWords = Arrays.asList(
                new WordFrequencyImpl("the", 2),
                new WordFrequencyImpl("lake", 1),
                new WordFrequencyImpl("over", 1)
        );
        when(wordFrequencyAnalyzer.calculateMostFrequentNWords(eq(BASE_TEXT), eq(n))).thenReturn(frequentWords);

        mockMvc.perform(get("/wordcount/word-frequency")
                        .param("text", BASE_TEXT)
                        .param("n", String.valueOf(n))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].word", is("the")))
                .andExpect(jsonPath("$[0].frequency", is(2)))
                .andExpect(jsonPath("$[1].word", is("lake")))
                .andExpect(jsonPath("$[1].frequency", is(1)))
                .andExpect(jsonPath("$[2].word", is("over")))
                .andExpect(jsonPath("$[2].frequency", is(1)));
    }

    // Test for non-existent word
    @Test
    void whenWordIsNotFound_ShouldReturnZero() throws Exception {
        String word = "moon";
        when(wordFrequencyAnalyzer.calculateFrequencyForWord(eq(BASE_TEXT), eq(word))).thenReturn(0);

        mockMvc.perform(get("/wordcount/frequency")
                        .param("text", BASE_TEXT)
                        .param("word", word)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void whenTextIsEmpty_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/wordcount/highest-frequency")
                        .param("text", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Whitespace Text Input
    @Test
    void whenTextIsWhitespace_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/wordcount/highest-frequency")
                        .param("text", "    ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Invalid `n` Value
    @Test
    void whenNIsNegative_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/wordcount/word-frequency")
                        .param("text", BASE_TEXT)
                        .requestAttr("n", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // `n` Larger Than Word Count
    @Test
    void whenNIsLargerThanWordCount_ShouldReturnAllWords() throws Exception {
        int n = 100;
        when(wordFrequencyAnalyzer.calculateMostFrequentNWords(eq(BASE_TEXT), eq(n)))
                .thenReturn(Arrays.asList(new WordFrequencyImpl("the", 2), new WordFrequencyImpl("sun", 1))); // and so on for all words

        mockMvc.perform(get("/wordcount/word-frequency")
                        .param("text", BASE_TEXT)
                        .param("n", String.valueOf(n))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Change this to the actual number of unique words in BASE_TEXT
                .andExpect(jsonPath("$[0].word", is("the")))
                .andExpect(jsonPath("$[0].frequency", is(2)));
    }

    // No Parameters Provided
    @Test
    void whenNoParametersProvided_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/wordcount/highest-frequency")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
