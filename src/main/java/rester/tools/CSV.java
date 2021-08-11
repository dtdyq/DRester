package rester.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CSV {
    public static Map<String, List<String>> read(String path) {
        Map<String, List<String>> ret = new HashMap<>();
        Reader in;
        try {
            in = new FileReader(path);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().withTrim().parse(in);
            for (CSVRecord record : records) {
                record.getParser().getHeaderNames().forEach(head -> {
                    if (!ret.containsKey(head)) {
                        ret.put(head, new ArrayList<>());
                    }
                    ret.get(head).add(record.get(head));
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
