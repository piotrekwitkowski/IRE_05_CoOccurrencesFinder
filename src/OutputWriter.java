import com.google.common.collect.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class OutputWriter {
    private final Multiset<String> wordCounts;


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
        ArrayList<String> topBigrams = getTopBigrams(bigrams);
        topBigrams.stream().limit(1000).forEach(writer::println);
        writer.close();
    }

    private ArrayList<String> getTopBigrams(Multimap<String, String> bigrams) {
        ImmutableMap.Builder<String, Integer> topBigrams = ImmutableMap.builder();

//        bigrams
//                .keySet()
//                .stream() //Iterate the `keys`
//                .filter(key -> wordCounts.count(key) > 10000)
//                .forEach(key -> {
//                    HashMultiset<Object> objects = HashMultiset.create();
//                    bigrams.asMap().entrySet()
//                });

        bigrams
                .asMap()
                .entrySet()
                .stream()
                .filter(key -> wordCounts.count(key.getKey()) > 50000)
                .forEach(key -> {
                    System.out.println(key);
                    Map<String, Long> values = key
                            .getValue()
                            .stream()
                            .filter(value -> wordCounts.count(value) > 50000)
                            .collect(Collectors.groupingBy(
                                    Function.identity(),
                                    Collectors.counting()
                            ));

                    for (Map.Entry e : values.entrySet()) System.out.println("   " + e);
                });



//        for (String key : bigrams.keySet()) {
//            if (wordCounts.count(key) > 1000) {
//                Map<String, Long> collect = bigrams
//                        .get(key)
//                        .stream()
//                        .collect( //Group and count
//                                Collectors.groupingBy(
//                                        Function.identity(),
//                                        Collectors.counting()
//                                )
//                        );
//                collect.forEach(this::print);
//            }
//        }

//        bigrams
//                .keySet()
//                .stream() //Iterate the `keys`
//                .filter(key -> wordCounts.count(key) > 10000)
//
//                .map(i -> i + " : " +  //For each key
//                        bigrams.get(i)
//                                .stream() //stream the values.
//                                .collect( //Group and count
//                                        Collectors.groupingBy(
//                                                java.util.function.Function.identity(),
//                                                Collectors.counting()
//                                        )
//                                )
//                )
//                .forEach(topBigrams::add)
//        ;
//
        return null;
    }


//        bigrams
//                .keySet()
//                .stream() //Iterate the `keys`
//                .filter(wordCounts.count(key) < 1000)
//                .map(i -> i + " : " +  //For each key
//                        bigrams.get(i)
//                                .stream() //stream the values.
//                                .collect( //Group and count
//                                        Collectors.groupingBy(
//                                                java.util.function.Function.identity(),
//                                                Collectors.counting()
//                                        )
//                                )
//                )
//                .forEach(topBigrams::add)
//        ;


    private void print(String s, Long aLong) {
        System.out.println(s + " ," + aLong);
    }


    private void printStats(Multimap<String, String> bigrams) {
        System.out.println("top bigrams size: " + bigrams.size());
        System.out.println("top bigrams keySet() size: " + bigrams.keySet().size());
        System.out.println("top bigrams values() size: " + bigrams.values().size());
    }
}

