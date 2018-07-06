import com.google.common.collect.*;
import com.google.common.primitives.Doubles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        ImmutableMap<String, Double> topBigrams = getTopBigrams(bigrams);
        highestCountFirst.greatestOf(topBigrams.entrySet(), TOP_COUNT).forEach(e ->
                writer.println(e.getKey() + "\t" + e.getValue()));
        writer.close();
    }

    private ImmutableMap<String, Double> getTopBigrams(Multimap<String, String> bigrams) {
        ImmutableMap.Builder<String, Double> topBigrams = ImmutableMap.builder();

        bigrams
                .asMap()
                .entrySet()
                .stream()
                .filter(word1 -> wordCounts.count(word1.getKey()) > TOP_COUNT)
                .forEach(word1 -> {
                    word1
                            .getValue()
                            .stream()
                            .filter(word2 -> wordCounts.count(word2) > TOP_COUNT)
                            .collect(Collectors.groupingBy(
                                    Function.identity(),
                                    Collectors.counting()
                            )).forEach((word2, count) -> {

                        double bigramRatio =
                                2 * (double) count / (wordCounts.count(word1.getKey()) + wordCounts.count(word2));


//                        System.out.print(
//                                "2 * " + count + "\t/ " +
//                                " (" +
//                                wordCounts.count(word1.getKey()) + " + " +
//                                wordCounts.count(word2) + ")\t= "
//                        );
//                        System.out.println(bigramRatio);
//                        System.out.println(count);

                        topBigrams.put(word1.getKey() + "\t" + word2, bigramRatio);
                    });
                });
        return topBigrams.build();
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
        System.out.println("top bigrams size: " + bigrams.size());
        System.out.println("top bigrams keySet() size: " + bigrams.keySet().size());
        System.out.println("top bigrams values() size: " + bigrams.values().size());
    }
}

