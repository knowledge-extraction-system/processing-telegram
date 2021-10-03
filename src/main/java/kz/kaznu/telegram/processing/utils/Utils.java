package kz.kaznu.telegram.processing.utils;

import kz.kaznu.telegram.processing.models.TimeWindow;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Utils {

    private static final int TOKEN_MIN_LENGTH = 3;

    public static String promptString(String prompt) {
        System.out.print(prompt);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public List<String> extractTokens(String text) throws IOException {
        final String cleanedText = getCleanText(text.toLowerCase());
        final List<String> tokens = Arrays.asList(cleanedText.split(" "));

        return tokens.stream().filter(token -> token.length() >= TOKEN_MIN_LENGTH).collect(Collectors.toList());
    }

    private String getCleanText(String text) throws IOException {
        final Reader reader = new StringReader(text);
        CharArraySet russianStopWords = RussianAnalyzer.getDefaultStopSet();

        // Add custom stopWords from file "stop_words"
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/stop_words"), UTF_8));
        String line;
        while ((line = fileReader.readLine()) != null) {
            russianStopWords.add(line);
        }

        final Analyzer analyzer = new StandardAnalyzer();
        final TokenStream tokenStream = analyzer.tokenStream("", reader);
        StopFilter stopFilter = new StopFilter(tokenStream, russianStopWords);

        final CharTermAttribute afterStopFilter = stopFilter.addAttribute(CharTermAttribute.class);
        final StringBuilder notStopWords = new StringBuilder();
        try {
            stopFilter.reset(); // Resets this stream to the beginning. (Required)
            while (stopFilter.incrementToken()) {
                final String token = afterStopFilter.toString();
                notStopWords.append(token).append(" ");
            }
            tokenStream.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            tokenStream.close(); // Release resources associated with this stream.
        }
        stopFilter.close();
        afterStopFilter.setEmpty();
        return notStopWords.toString();
    }

    public LinkedList<String> extractLemmasAsList(String text) throws IOException {
        final LinkedList<String> lemmas = new LinkedList<>();
        Analyzer analyzer = new org.apache.lucene.morphology.russian.RussianAnalyzer();
        final List<String> tokens = Arrays.asList(text.split(" "));

        // Use each work separately because of homonyms that analyzer detects. We use first match
        for (String token : tokens) {
            TokenStream stream = analyzer.tokenStream("field", token);
            stream.reset();
            stream.incrementToken();
            String lemma = stream.getAttribute(CharTermAttribute.class).toString();
            lemmas.add(lemma);
            stream.end();
            stream.close();
        }

        return lemmas;
    }

    public String extractLemmaFromToken(String token) throws IOException {
        Analyzer analyzer = new org.apache.lucene.morphology.russian.RussianAnalyzer();

        TokenStream stream = analyzer.tokenStream("field", token);
        stream.reset();
        stream.incrementToken();
        String lemma = stream.getAttribute(CharTermAttribute.class).toString();
        stream.end();
        stream.close();

        return lemma;
    }

    public String extractLemmasAsText(String textForLemmas) throws IOException {
        LinkedList<String> lemmasAsList = extractLemmasAsList(textForLemmas);
        return String.join(" ", lemmasAsList);
    }

    public Map<String, Double> calculateTfIds(List<String> tokens, Map<TimeWindow, List<String>> tokensPerTimeWindow) {
        final Map<String, Double> termFrequency = calculateTermFrequency(tokens);
        final Map<String, Double> inverseDocumentFrequency = calculateInverseDocFrequency(tokens, tokensPerTimeWindow);
        final Map<String, Double> tfIdf = new HashMap<>();
        for (Map.Entry<String, Double> entry: termFrequency.entrySet()) {
            tfIdf.put(entry.getKey(), entry.getValue() * inverseDocumentFrequency.get(entry.getKey()));
        }
        return tfIdf;
    }

    private Map<String, Double> calculateInverseDocFrequency(List<String> tokens, Map<TimeWindow, List<String>> tokensPerTimeWindow) {
        final Map<String, Double> tokenIdfs = new HashMap<>();
        final int size = tokensPerTimeWindow.size();
        for (String token: tokens) {
            if (tokenIdfs.containsKey(token)) {
                continue;
            }
            int count = 0;
            for (Map.Entry<TimeWindow, List<String>> entry: tokensPerTimeWindow.entrySet()) {
                if (entry.getValue().stream().anyMatch(word -> word.equals(token))) {
                    count++;
                }
            }
            tokenIdfs.put(token, Math.log((double) size / count));
        }
        return tokenIdfs;
    }

    public Map<String, Double> calculateTermFrequency(List<String> tokens) {
        final Map<String, Double> tokenTfs = new HashMap<>();
        final int size = tokens.size();
        for (String token : tokens) {
            if (tokenTfs.containsKey(token)) {
                continue;
            }
            int count = 0;
            for (String s : tokens) {
                if (token.equals(s)) {
                    count++;
                }
            }
            tokenTfs.put(token, (double) count / size);
        }
        return tokenTfs;
    }

    public static Map<String, Double> sortByValue(Map<String, Double> unsortedMap, boolean descendingOrder) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(unsortedMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        if(descendingOrder){
            list.sort(Comparator.comparing(Map.Entry::getValue));
        }
        else {
            list.sort((o2, o1) -> (o1.getValue()).compareTo(o2.getValue()));
        }

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
