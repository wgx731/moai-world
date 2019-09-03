package com.github.wgx731.threading.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.wgx731.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordProcessorTest extends TestHelper implements SummaryMerger {


  private RecordProcessor processor;

  @Before
  public void setUp() throws Exception {
    setUpFixSampleRecords();
    processor = RecordProcessor.builder()
        .records(sampleRecords)
        .build();
  }

  @After
  public void tearDown() throws Exception {
    tearDownSampleRecords();
    this.processor = null;
  }

  @Test
  public void customerSummary() {
    Map<String, List<BigDecimal>> result = processor.customerSummary();
    assertThat(result.keySet()).containsOnly("c1", "c2");
    assertThat(result.get("c1")).containsExactly(
        new BigDecimal("40.00"),
        new BigDecimal("50.22"),
        new BigDecimal("10.22")
    );
    assertThat(result.get("c2")).containsExactly(
        new BigDecimal("50.00"),
        new BigDecimal("60.19"),
        new BigDecimal("10.19")
    );
  }

  @Test
  public void merchantSummary() throws JsonProcessingException {
    Map<String, List<BigDecimal>> result = processor.merchantSummary();
    assertThat(result.keySet()).containsOnly("m1", "m2");
    assertThat(result.get("m1")).containsExactly(
        new BigDecimal("10.00"),
        new BigDecimal("20.22"),
        new BigDecimal("10.22")
    );
    assertThat(result.get("m2")).containsExactly(
        new BigDecimal("80.00"),
        new BigDecimal("90.19"),
        new BigDecimal("10.19")
    );
  }

  @Test
  public void testMergeSummary() {
    List<Map<String, List<BigDecimal>>> all = new ArrayList<>();
    all.add(processor.customerSummary());
    all.add(processor.merchantSummary());
    all.add(processor.merchantSummary());
    Map<String, List<BigDecimal>> merged = merge(all);
    assertThat(merged.keySet()).containsOnly("c1", "m1", "c2", "m2");
    assertThat(merged.get("m1")).containsOnly(
        new BigDecimal("20.00"),
        new BigDecimal("40.44"),
        new BigDecimal("20.44")
    );
    assertThat(merged.get("m2")).containsOnly(
        new BigDecimal("160.00"),
        new BigDecimal("180.38"),
        new BigDecimal("20.38")
    );
  }

}
