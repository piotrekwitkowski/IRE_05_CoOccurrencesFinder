import com.google.common.collect.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

class InputProcessor {
    private StringBuilder plotText = new StringBuilder();
    private Pattern delimiters = Pattern.compile("[ .,:!?]");
    private Multimap<String, String> bigrams = ArrayListMultimap.create();
    private HashSet<String> stopwords = new HashSet<>(Arrays.asList(Stopwords.VALUES));
    private Multiset<String> wordCounts;

    InputProcessor(Multiset<String> wordCounts) {
        this.wordCounts = wordCounts;
    }

    Multimap<String, String> processPlot(String filename) {
        try {
            Path plotList = Paths.get(filename);
            Stream<String> lines = Files.lines(plotList, ISO_8859_1);
            lines
                    .skip(15)
                    .filter(line -> line.length() > 0)
                    .forEachOrdered(this::processLine);
            processLine("--"); //last entry will be processed too

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bigrams;
    }

    private void processLine(String line) {
        if (line.charAt(0) == 'P') {
            plotText.append(line.substring(3)); //erase PL:
        } else if (line.charAt(0) == 'M') {
            addBigrams(extractTitle(line));
        } else {
            addBigrams(plotText.toString());
            clearProcessor();
        }
    }

    private String extractTitle(String line) {
        if (line.charAt(4) == '"') {
            return extractSeriesTitle(line);
        } else {
            return extractMovieTitle(line);
        }
    }

    private String extractSeriesTitle(String line) {
        int pos = line.indexOf("\"", 5);
        return line.substring(5, pos);
    }

    private String extractMovieTitle(String line) {
        int pos = line.lastIndexOf("(");
        String title = line.substring(4, pos - 1);

        //(VG) erased instead of (2011)? try this:
        int pos1 = title.lastIndexOf("(");
        int pos2 = title.lastIndexOf(")");
        if (pos1 > 0 && pos2 > pos1) {
            if (isRealYear(title.substring(pos1 + 1, pos2))) {
                title = title.substring(0, pos1 - 1);
            }
        }
        return title;
    }

    private boolean isRealYear(String year) {
        for (char c : year.toCharArray()) {
            if (!isYearChar(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isYearChar(char c) {
        return isDigit(c) ||
                c == '?' ||
                c == '/' ||
                c == 'I' ||
                c == 'V';
    }

    private void clearProcessor() {
        plotText.setLength(0);
    }

    private void addBigrams(String text) {
        List<String> tokensList = delimiters
                .splitAsStream(text.toLowerCase(Locale.ENGLISH))
                .filter(token -> token.length() != 0)
                .map(token -> (!stopwords.contains(token)) ? token : "")
                .collect(Collectors.toList());

        Iterator<String> tokenIterator = tokensList.listIterator();
        if (tokenIterator.hasNext()) {
            String token2 = tokenIterator.next();
            if (token2.length() != 0) {
                wordCounts.add(token2);
            }

            String token1;
            while (tokenIterator.hasNext()) {
                token1 = token2;
                token2 = tokenIterator.next();
                if (token2.length() != 0) {
                    wordCounts.add(token2);
                    if (token1.length() != 0) {
                        bigrams.put(token1, token2);
                    }
                }
            }
        }
    }
}
