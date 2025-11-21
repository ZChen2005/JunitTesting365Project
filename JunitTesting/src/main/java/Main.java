import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static HT convert(String url) throws IOException {
        Document doc = (Document) Jsoup.connect(url).get();


        StringBuilder sb = new StringBuilder();
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            sb.append(p.text()).append(" ");
        }
        String text = sb.toString();
        //System.out.println(text);

        // remove footnotes
        text = text.replaceAll("\\[\\d+\\]", "");

        // remove everything that is not a letter
        text = text.replaceAll("[^a-zA-Z\\s]", " ");
        //System.out.println(text);

        // split words by white space
        String[] words = text.toLowerCase().split("\\s+");


        HT test = new HT();
        for (String word : words) {
            if (word.length()>1&& !stopwords.contains(word))
            {test.add(word);}
        }
        //test.printAll();
        return test;


    }
    static final Set<String> stopwords = new HashSet<>(Arrays.asList(
            "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers",
            "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
            "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are",
            "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does",
            "did", "doing",  "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on",
            "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where",
            "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no",
            "nor", "not", "only", "own", "same", "so", "than", "too", "very", "can", "will",
            "just", "don", "should", "now"
    ));

    public static double cosineSimilarity(HT doc1, HT doc2) {
        double dot = 0.0;
        double mag1 = 0.0;
        double mag2 = 0.0;

        // dot productc + mag of doc1
        for (int i = 0; i < doc1.table.length; i++) {
            for (HT.Node e1 = doc1.table[i]; e1 != null; e1 = e1.next) {
                double v1 =  e1.value;
                mag1 += v1 * v1;

                HT.Node e2 = doc2.getNode(e1.key);
                if (e2 != null) {
                    dot += v1 *  e2.value;
                }
            }
        }

        // mag of doc2
        for (int i = 0; i < doc2.table.length; i++) {
            for (HT.Node e2 = doc2.table[i]; e2 != null; e2 = e2.next) {
                double v2 = e2.value;
                mag2 += v2 * v2;
            }
        }

        double denom = Math.sqrt(mag1) * Math.sqrt(mag2);
        if (denom == 0) {
            return 0.0;
        } else {
            return dot / denom;
        }
    }

    public static void TF(HT doc) {
        int twc = doc.getTotalWordCount();
        for (int j = 0; j < doc.table.length; j++) {
            for (HT.Node e = doc.table[j]; e != null; e = e.next) {
                int count = e.count;
                double tf = (double) count / twc;
                e.value = tf;
            }
        }
    }

    public static void IDF(HT doc, List<HT> FT) {
        int tnd = FT.size();
        for (int j = 0; j < doc.table.length; j++) {
            for (HT.Node e = doc.table[j]; e != null; e = e.next) {
                Object term = e.key;
                int c = 0;
                for (HT t : FT) {
                    if (t.contains(term)) {
                        c++;
                    }
                }
                if (c > 0) {
                    double idf = Math.log((double) tnd / (double) c);
                    e.value = e.value * idf;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        List<String> urls = Files.readAllLines(Paths.get("src/wiki.txt"));
        List<HT> FT = new ArrayList<>();
        for (String url : urls) {
            //System.out.println("Processing: " + url);
            HT newT = convert(url);
            FT.add(newT);
        }

        for (HT ht : FT) {
            TF(ht);
        }

        for (HT ht : FT) {
            IDF(ht, FT);
        }

        SwingUtilities.invokeLater(() -> {
            new GUI(FT, urls).setVisible(true);
        });

    }
}