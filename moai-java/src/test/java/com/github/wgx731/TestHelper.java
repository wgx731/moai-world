package com.github.wgx731;

import com.github.wgx731.threading.model.Record;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class TestHelper {

  @Rule
  public final Stopwatch stopwatch = new Stopwatch() {

    @Override
    protected void finished(long nanos, Description description) {
      super.finished(nanos, description);
      System.out.println(new StringBuilder()
          .append(description.getMethodName())
          .append(" finished, time taken ")
          .append(nanos)
          .append(" nano seconds or ")
          .append(TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS))
          .append(" seconds.")
          .toString());
    }

  };

  protected List<Record> sampleRecords;

  protected void setUpSampleRecords() {
    sampleRecords = new ArrayList<>();
    Record r1 = new Record();
    r1.setId("sample-01");
    r1.setTime("2019-01-01T01:00:00");
    r1.setAmount(new BigDecimal("20.22"));
    r1.setType(Record.PAY_TYPE);
    r1.setCustomerName("c1");
    r1.setMerchantName("m1");
    r1.setOrgId(Record.EMPTY_ORG_ID);
    sampleRecords.add(r1);
    Record r2 = new Record();
    r2.setId("sample-02");
    r2.setTime("2019-01-01T02:00:00");
    r2.setAmount(new BigDecimal("10.22"));
    r2.setType(Record.REFUND_TYPE);
    r2.setCustomerName("c1");
    r2.setMerchantName("m1");
    r2.setOrgId("sample-01");
    sampleRecords.add(r2);
    Record r3 = new Record();
    r3.setId("sample-03");
    r3.setTime("2019-01-01T03:00:00");
    r3.setAmount(new BigDecimal("30"));
    r3.setType(Record.PAY_TYPE);
    r3.setCustomerName("c1");
    r3.setMerchantName("m2");
    r3.setOrgId(Record.EMPTY_ORG_ID);
    sampleRecords.add(r3);
    Record r4 = new Record();
    r4.setId("sample-04");
    r4.setTime("2019-01-01T01:00:00");
    r4.setAmount(new BigDecimal("60.19"));
    r4.setType(Record.PAY_TYPE);
    r4.setCustomerName("c2");
    r4.setMerchantName("m2");
    r4.setOrgId(Record.EMPTY_ORG_ID);
    sampleRecords.add(r4);
    Record r5 = new Record();
    r5.setId("sample-05");
    r5.setTime("2019-01-01T01:30:00");
    r5.setAmount(new BigDecimal("10.19"));
    r5.setType(Record.REFUND_TYPE);
    r5.setCustomerName("c2");
    r5.setMerchantName("m2");
    r5.setOrgId("sample-04");
    sampleRecords.add(r5);
  }

  protected void tearDownSampleRecords() {
    this.sampleRecords = null;
  }

}
