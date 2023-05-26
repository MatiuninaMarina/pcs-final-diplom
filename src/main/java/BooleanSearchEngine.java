import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> countPerPagePerWord = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        List<String> paths = Arrays.asList(pdfsDir.listFiles()).stream().map(x -> x.getAbsolutePath()).collect(Collectors.toList());
        for (String fileName : paths) {

            try (var document = new PdfDocument(new PdfReader(fileName))) {
                for (int i = 1; i <= document.getNumberOfPages(); i++) {
                    Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                    String text = PdfTextExtractor.getTextFromPage(document.getPage(i));

                    for (var word : text.split("\\P{IsAlphabetic}+")) { // перебираем слова
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                    for (String word : freqs.keySet()) {
                        if (countPerPagePerWord.containsKey(word)) {
                            countPerPagePerWord.get(word)
                                    .add(new PageEntry(new File(fileName).getName(), i, freqs.get(word)));
                        } else {
                            List list = new ArrayList<>();
                            list.add(new PageEntry(new File(fileName).getName(), i, freqs.get(word)));
                            countPerPagePerWord.put(word, list);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        List<PageEntry> pageEntries = countPerPagePerWord.get(word.toLowerCase());
        if (pageEntries == null) {
            return Collections.emptyList();
        } else {
            pageEntries.sort(PageEntry::compareTo);
            return pageEntries;
        }
    }
}
