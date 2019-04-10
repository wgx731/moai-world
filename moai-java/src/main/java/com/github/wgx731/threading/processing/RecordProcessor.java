package com.github.wgx731.threading.processing;

import com.github.wgx731.threading.model.Record;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public class RecordProcessor implements RecordCalculator {

  @NonNull
  private List<Record> records;

  public Map<String, List<BigDecimal>> customerSummary() {
    return records.stream()
        .collect(Collectors.groupingBy(
            Record::getCustomerName
        )).entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> summary(e.getValue())
        ));
  }

  public Map<String, List<BigDecimal>> merchantSummary() {
    return records.stream()
        .collect(Collectors.groupingBy(
            Record::getMerchantName
        )).entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> summary(e.getValue())
        ));
  }

}
