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

}
