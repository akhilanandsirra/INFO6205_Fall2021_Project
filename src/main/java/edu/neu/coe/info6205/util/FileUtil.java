package edu.neu.coe.info6205.util;
import edu.neu.coe.info6205.sort.counting.MSDStringSort;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;


public class FileUtil {

    public enum SortLanguage {
        CHINESE,
        TELUGU
    }

    public static String[] getWordArray() throws FileNotFoundException {
        String filePath = ((FileUtil.getSortLanguage().equals(SortLanguage.CHINESE.toString())) ? "/shuffledChinese.txt" : "/TeluguWords.txt");
        return getWords(filePath, FileUtil::lineAsList);
    }

    static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
        Class<?> clazz = MSDStringSort.class;
        try {
            final File file = new File(Objects.requireNonNull(clazz.getResource(resource)).toURI());
            final String[] result = getWordArray(file, stringListFunction, 2);
            System.out.println("getWords: testing with " + result.length + " unique words: from " + file);
            return result;
        } catch (final URISyntaxException | NullPointerException e) {
            System.out.println("Cannot find resource: " + resource + "  relative to class: " + clazz);
            return new String[0];
        }
    }

    static String[] getWordArray(final File file, final Function<String, List<String>> stringListFunction, final int minLength) {
        try (final FileReader fr = new FileReader(file)) {
            return getWordList(fr, stringListFunction, minLength).toArray(new String[0]);
        } catch (final IOException e) {
            System.out.println("Cannot open file: " + file);
            return new String[0];
        }
    }

    private static List<String> getWordList(final FileReader fr, final Function<String, List<String>> stringListFunction, final int minLength) {
        final List<String> words = new ArrayList<>();
        for (final Object line : new BufferedReader(fr).lines().toArray())
            words.addAll(stringListFunction.apply((String) line));
        return words.stream().distinct().filter(s -> s.length() >= minLength).collect(Collectors.toList());
    }

    static List<String> lineAsList(final String line) {
        final List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    public static String getSortLanguage() {
        String lang = "";
        try {
            InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(inputStream);
            return prop.getProperty("lang");
        } catch (IOException e) {
            System.out.println("properties file not found");
        }
        return lang;
    }

}

