package com.ilyaevteev.productmonitoring.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DateIntervalPriceHandler {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    private static Set<String> getDatesBetween(Date startDate, Date endDate) {
        Supplier<Set<String>> datesBetween = TreeSet::new;
        LocalDate startLocalDate = new java.sql.Date(startDate.getTime()).toLocalDate();
        LocalDate endLocalDate = new java.sql.Date(endDate.getTime()).toLocalDate().plusDays(1);

        return startLocalDate.datesUntil(endLocalDate)
                .map(el -> el.format(dateTimeFormatter))
                .collect(Collectors.toCollection(datesBetween));
    }

    public static List<Map<String, String>> getIntervalPrices(Map<String, List<Long>> groupedPrices, Date startDate, Date endDate) {
        List<Map<String, String>> intervalPrices = new ArrayList<>();
        Set<String> datesBetween = getDatesBetween(startDate, endDate);

        final String[] previousPriceValue = {"0"};
        datesBetween.forEach(el -> {
            List<Long> prices = groupedPrices.get(el);
            if (prices != null) {
                previousPriceValue[0] = String.valueOf(Math.round(prices.stream().mapToDouble(lng -> lng).average().getAsDouble()));
            }
            intervalPrices.add(Map.of("date", el, "price", previousPriceValue[0]));
        });

        return intervalPrices;
    }
}