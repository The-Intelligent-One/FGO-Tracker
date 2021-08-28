package com.github.theintelligentone.fgotracker.service.filemanagement;

import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ImportFileService {
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.3.csv";
    private static final int LINES_TO_SKIP_IN_ROSTER_CSV = 2;
    private static final int LINES_TO_SKIP_IN_LT_CSV = 12;

    public List<String[]> importRosterCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_ROSTER_CSV);
    }

    public List<String[]> importPlannerCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_LT_CSV);
    }

    public Map<String, Integer> importInventoryCsv(File sourceFile) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile, Charset.defaultCharset());
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(7)
                    .build();
            strings.add(csvReader.readNext());
            csvReader.readNext();
            csvReader.readNext();
            strings.add(csvReader.readNext());
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return transformToInventoryMap(strings);
    }

    private List<String[]> importServantsFromCsv(File sourceFile, int linesToSkip) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile, Charset.defaultCharset());
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(linesToSkip)
                    .build();
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return strings;
    }

    private Map<String, Integer> transformToInventoryMap(List<String[]> strings) {
        Map<String, Integer> result = new HashMap<>();
        for (int i = 13; i < strings.get(0).length; i++) {
            if (!strings.get(1)[i].isEmpty()) {
                String amountAsString = strings.get(0)[i].isEmpty() ? "0" : strings.get(0)[i].replaceAll("[\\D.]", "");
                result.put(strings.get(1)[i], Integer.parseInt(amountAsString));
            }
        }
        return result;
    }

    public List<ManagerServant> loadManagerLookupTable() {
        Reader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(MANAGER_DB_PATH)),
                        Charset.defaultCharset()));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> strings = new ArrayList<>();
        try {
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return strings.stream().map(this::buildLookupObject).collect(Collectors.toList());
    }

    private ManagerServant buildLookupObject(String... strings) {
        return new ManagerServant(Integer.parseInt(strings[1]), strings[0]);
    }
}
