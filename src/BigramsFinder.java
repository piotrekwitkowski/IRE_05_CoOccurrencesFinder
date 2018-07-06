import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

class BigramsFinder {

    void findBigrams(String moviePlotFile, String outputFile) {

        Multiset<String> wordCounts = HashMultiset.create();

        InputProcessor ip = new InputProcessor(wordCounts);
        Multimap<String, String> bigrams = ip.processPlot(moviePlotFile);

        OutputWriter ow = new OutputWriter(wordCounts);
        ow.writeResults(outputFile, bigrams);
    }

//    private static void saveResults(Multimap<String, String> topBigrams, Path outputFile) {
//        System.out.println("top bigrams size: " + topBigrams.size());
//        System.out.println("top bigrams keySet() size: " + topBigrams.keySet().size());
//        System.out.println("top bigrams values() size: " + topBigrams.values().size());
//
//        System.out.println("real magic...");
//        System.out.println();
//
//        BiConsumer<String, Long> print = (txt, count) -> {
//            System.out.println(txt + count);
//
//        };
//        topBigrams
//                .keySet()
//                .stream() //Iterate the `keys`
//                .map(i -> //For each key
//                        topBigrams
//                                .get(i)
//                                .stream() //stream the values.
//                                .collect( //Group and count
//                                        Collectors.groupingBy(
//                                                java.util.function.Function.identity(),
//                                                Collectors.counting()
//                                        )
//                                )
//                                .forEach(print);
//                )
//                .forEach(text -> {
//                    System.out.println(text);
//                    System.out.println();
//                });
//    }

}
