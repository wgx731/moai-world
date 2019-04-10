package com.github.wgx731.threading.processing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SummaryMerger {

  default Map<String, List<BigDecimal>> merge(
      Map<String, List<BigDecimal>> left,
      Map<String, List<BigDecimal>> right
  ) {
    return Stream.of(left, right)
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (v1, v2) -> {
              List<BigDecimal> combined = new ArrayList<>();
              combined.add(v1.get(0).add(v2.get(0)));
              combined.add(v1.get(1).add(v2.get(1)));
              combined.add(v1.get(2).add(v2.get(2)));
              return combined;
            }
        ));
  }

  default Map<String, List<BigDecimal>> merge(
      List<Map<String, List<BigDecimal>>> all
  ) {
    return all.stream()
        .reduce(new HashMap<>(), this::merge);
  }

}
