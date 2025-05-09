package br.hallel.relational.api.app.global.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GoogleBucketUtils {
    public static String getImageName(String idDocument,
                                      String simpleClassName,
                                      String... extras) {
        return idDocument + "-" + simpleClassName + Arrays.stream(extras)
                                                          .map(extra -> "-" + extra)
                                                          .collect(Collectors.joining());
    }
}
