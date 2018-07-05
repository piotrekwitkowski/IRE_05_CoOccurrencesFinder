import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

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


    private static void saveResults(Multiset<String> topBigrams, Path
            outputFile) {

        System.out.println("top bigrams size: " + topBigrams.size());
        System.out.println("generating copyHighestCountFirst...");

//        topBigrams.entrySet().stream().limit(20).forEach(System.out::println);

        System.out.println("real magic...");
        System.out.println();

        Ordering<Multiset.Entry<String>> highestCountFirst =
                new Ordering<Multiset.Entry<String>>() {
                    @Override
                    public int compare(Multiset.Entry<String> e1, Multiset.Entry<String> e2) {
                        return Ints.compare(e1.getCount(), e2.getCount());
                    }
                };

        ImmutableMultiset.Builder<String> top100Builder = ImmutableMultiset.builder();
        for (Multiset.Entry<String> topEntry :
                highestCountFirst.greatestOf(topBigrams.entrySet(), 10)) {
            top100Builder.addCopies(topEntry.getElement(), topEntry.getCount());
        }

        ImmutableMultiset<String> results = top100Builder.build();
//        for (Multiset.Entry<String> e : results.entrySet()) {
//            System.out.println(e);
//        }
        results.entrySet().stream().forEachOrdered(System.out::println);

//        Iterable<Multiset.Entry<String>> entriesSortedByCount =
//                Multisets.copyHighestCountFirst(topBigrams).entrySet();
//
//
//        Multisets
//                .copyHighestCountFirst(topBigrams)
//                .entrySet()
//                .stream()
//                .limit(100)
//                .forEachOrdered(System.out::println);

//        PriorityQueue<String> pq= new PriorityQueue<AbstractMap.SimpleImmutableEntry<String, String>>(100,
// Comparator.comparing(String::length));


//        ImmutableMultiset<AbstractMap.SimpleImmutableEntry<String, String>> results =
//                copyHighestCountFirst(topBigrams);

//        results.entrySet().stream().limit(100).forEachOrdered(System.out::println);

//        for (int i = 0; i < 100; i++) {
//        }
//
//        for (Multiset.Entry<AbstractMap.SimpleImmutableEntry<String, String>> e : topBigrams.entrySet()) {
//            System.out.println(e.getElement() + " " + e.getCount());
//        }
//        topBigrams = null;


    }

}
