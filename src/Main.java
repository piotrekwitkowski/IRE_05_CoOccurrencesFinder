import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Main {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: java –jar CoOccurrencesFinder.jar <plot-file> <output-file>");
            System.exit(-1);
        }

        Path moviePlotFile = Paths.get(args[0]);
        Path outputFile = Paths.get(args[1]);


        Stopwatch timer = Stopwatch.createStarted();
        InputProcessor ip = new InputProcessor();
        processPlotAsStreamOrdered(ip, moviePlotFile);
        saveResults(ip.getBigrams(), outputFile);
        System.out.println("CoOccurrencesFinder took: " + timer.stop());
    }

    private static void processPlotAsStreamOrdered(InputProcessor ip, Path filename) {
        try {
            Stream<String> lines = Files.lines(filename, ISO_8859_1);
            lines.forEachOrdered(ip::consume);
            ip.consume("--"); //last entry will be processed too

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void saveResults(Multimap<String, String> topBigrams, Path outputFile) {
        System.out.println("top bigrams size: " + topBigrams.size());
        System.out.println("top bigrams keySet() size: " + topBigrams.keySet().size());
        System.out.println("top bigrams values() size: " + topBigrams.values().size());

        System.out.println("real magic...");
        System.out.println();

        topBigrams
                .keySet()
                .stream() //Iterate the `keys`
//                .filter(key -> !stopwords.contains(key) || wordcounts)
                .map(i -> i + " : " +  //For each key
                        topBigrams.get(i)
                                .stream() //stream the values.
                                .collect( //Group and count
                                        Collectors.groupingBy(
                                                java.util.function.Function.identity(),
                                                Collectors.counting()
                                        )
                                )
                )
//                .forEach(System.out::println)
        ;

//        topBigrams.asMap().entrySet().stream() //Iterate the `keys`
//                .map(i -> i.getKey() + " : " +  //For each key
//                        i.getValue()
//                                .stream() //stream the values.
//                                .collect( //Group and count
//                                        Collectors.groupingBy(
//                                                java.util.function.Function.identity(),
//                                                Collectors.counting()
//                                        )
//                                )
//                )
////                .forEach(System.out::println)
//        ;


//        Ordering<Multiset.Entry<String>> highestCountFirst =
//                new Ordering<Multiset.Entry<String>>() {
//                    @Override
//                    public int compare(Multiset.Entry<String> e1, Multiset.Entry<String> e2) {
//                        return Ints.compare(e1.getCount(), e2.getCount());
//                    }
//                };
//
//
//
//        ImmutableMultiset.Builder<String> top100Builder = ImmutableMultiset.builder();
//        for (Multiset.Entry<String> topEntry :
//                highestCountFirst.greatestOf(topBigrams.entrySet(), 10)) {
//            top100Builder.addCopies(topEntry.getElement(), topEntry.getCount());
//        }

//        Ordering<Multiset.Entry<AbstractMap.SimpleImmutableEntry>> highestCountFirst =
//                new Ordering<Multiset.Entry<AbstractMap.SimpleImmutableEntry>>() {
//                    @Override
//                    public int compare(Multiset.Entry<AbstractMap.SimpleImmutableEntry> e1,
//                                       Multiset.Entry<AbstractMap.SimpleImmutableEntry> e2) {
//                        return Ints.compare(e1.getCount(), e2.getCount());
//                    }
//                };

//        topBigrams
//                .entrySet()
//                .stream()
//                .sorted(Comparator.comparing(Multiset.Entry::getCount))
//                .forEach(System.out::println);
//
//        topBigrams
//                .entrySet()
//                .stream()
//                .collect(greatest(10, Comparator.comparingInt(Multiset.Entry::getCount)))
//                .forEach(System.out::println);

//        ImmutableMultiset.Builder<AbstractMap.SimpleImmutableEntry> top100Builder = ImmutableMultiset.builder();
//        for (Multiset.Entry<AbstractMap.SimpleImmutableEntry> topEntry :
//                highestCountFirst.greatestOf(topBigrams.entrySet(), 100)) {
//            top100Builder.addCopies(topEntry.getElement(), topEntry.getCount());
//        }

//        top100Builder
//                .build()
//                .entrySet()
//                .forEach(System.out::println);

    }

}
