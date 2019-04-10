package com.github.wgx731.threading.processing;

import com.github.wgx731.threading.model.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface RecordCalculator {

  default BigDecimal totalPayment(List<Record> records) {
    return records.stream()
        .filter(r -> r.getType().equals(Record.PAY_TYPE))
        .map(r -> r.getAmount())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  default BigDecimal totalRefund(List<Record> records) {
    return records.stream()
        .filter(r -> r.getType().equals(Record.REFUND_TYPE))
        .map(r -> r.getAmount())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  default List<BigDecimal> summary(List<Record> records) {
    List<BigDecimal> results = new ArrayList<>();
    BigDecimal totalPayment = totalPayment(records);
    BigDecimal totalRefund = totalRefund(records);
    results.add(totalPayment.subtract(totalRefund));
    results.add(totalPayment);
    results.add(totalRefund);
    return results;
  }

}
