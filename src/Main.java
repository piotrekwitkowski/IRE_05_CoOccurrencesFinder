import com.google.common.base.Stopwatch;

public class Main {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: java –jar CoOccurrencesFinder.jar <plot-file> <output-file>");
            System.exit(-1);
        }

        Stopwatch timer = Stopwatch.createStarted();

        BigramsFinder bf = new BigramsFinder();
        bf.findBigrams(args[0], args[1]);

        System.out.println("CoOccurrencesFinder took: " + timer.stop());
    }

}