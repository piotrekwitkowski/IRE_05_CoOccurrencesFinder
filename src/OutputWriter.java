import com.google.common.collect.*;
import com.google.common.primitives.Doubles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class OutputWriter {
    private final Multiset<String> wordCounts;
    private int TOP_COUNT = 1000;

    OutputWriter(Multiset<String> wordCounts) {
        this.wordCounts = wordCounts;
    }

    void writeResults(String outputFile, Multimap<String, String> bigrams) {
        printStats(bigrams);
        try {
            writeToFile(outputFile, bigrams);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String outputFile, Multimap<String, String> bigrams) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFile);

        getRatios(bigrams)
                .entrySet()
                .stream()
                .collect(Comparators.greatest(TOP_COUNT, highestCountFirst))
                .stream()
                .map(e -> e.getKey() + "\t" + e.getValue())
                .forEach(writer::println);

        writer.close();
    }

    private HashMap<String, Double> getRatios(Multimap<String, String> bigrams) {
        HashMap<String, Double> bigramRatios = new HashMap<>();

        bigrams
                .asMap()
                .entrySet()
                .stream()
                .filter(word1 -> wordCounts.count(word1.getKey()) > TOP_COUNT)
                .forEach(word1 -> word1
                        .getValue()
                        .parallelStream()
                        .filter(word2 -> wordCounts.count(word2) > TOP_COUNT)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))

                        .forEach((word2, count) ->
                                bigramRatios.put(word1.getKey() + "\t" + word2,
                                        2.0 * count / (wordCounts.count(word1.getKey()) + wordCounts
                                                .count(word2)))));
        return bigramRatios;
    }

    private Ordering<Map.Entry<String, Double>> highestCountFirst =
            new Ordering<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> e1,
                                   Map.Entry<String, Double> e2) {
                    return Doubles.compare(e1.getValue(), e2.getValue());
                }
            };

    private void printStats(Multimap<String, String> bigrams) {
        System.out.println("wordCount entrySet: " + wordCounts.entrySet().size());
        System.out.println("wordCount:          " + wordCounts.size());
        System.out.println();
        System.out.println("top bigrams keySet: " + bigrams.keySet().size());
        System.out.println("top bigrams values: " + bigrams.values().size());
    }
}
