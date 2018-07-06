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
//                    .limit(100000)
                    .forEachOrdered(this::processLine);
            processLine("--"); //last entry will be processed too

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bigrams;
    }


    private int i = 0;

    private void processLine(String line) {
        if (line.startsWith("PL:")) {
            plotText.append(line.substring(3)); //erase PL:_
        } else if (line.startsWith("MV")) {
            String title = extractTitle(line);
            addBigrams(title);
        } else if (line.startsWith("-")) {
            if (plotText.length() > 0) {
                System.out.println(i++);
                String plot = plotText.toString();
                addBigrams(plot);
            }
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
        String title = line.substring(4, pos);

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
                .filter(token -> !"".equals(token))
                .collect(Collectors.toList());

        wordCounts.addAll(tokensList);

        Iterator<String> tokenIterator = tokensList.listIterator();
        String token1, token2;
        if (tokenIterator.hasNext()) {
            token2 = tokenIterator.next();

            while (tokenIterator.hasNext()) {
                token1 = token2;
                token2 = tokenIterator.next();
                if (!stopwords.contains(token2) && !stopwords.contains(token1)) {
                    bigrams.put(token1, token2);
                }
            }
        }
    }
}
