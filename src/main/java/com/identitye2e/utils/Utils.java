package com.identitye2e.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    private Utils() {
        // Not used.
    }

    public static void fileWriter(File file, String html) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(html);
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    public static List<String> stringToList(String groups) {
        StringTokenizer stringTokenizer = new StringTokenizer(groups);
        List<String> groupList = new ArrayList<>();
        while(stringTokenizer.hasMoreTokens()) {
            groupList.add(stringTokenizer.nextToken());
        }
        return groupList;
    }
}
