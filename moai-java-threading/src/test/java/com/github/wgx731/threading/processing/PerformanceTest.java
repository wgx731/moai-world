package com.github.wgx731.threading.processing;

import com.github.wgx731.TestHelper;
import com.github.wgx731.threading.model.Record;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Ignore
public class PerformanceTest extends TestHelper implements SummaryMerger {

  private static Random random = new Random();

  public static final int LARGE_SAMPLE_SIZE = 8500000;
  public static final int EACH_THREAD_SIZE = 1000;

  private static List<Record> largeSamples;

  private static List<Record> getRecords(int size) {
    List<Record> samples = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      Record r = new Record();
      r.setId(String.format("sample-%d", i));
      r.setTime("2019-01-01T00:00:00");
      r.setAmount(new BigDecimal(random.nextDouble(), MathContext.DECIMAL64));
      r.setType(Record.PAY_TYPE);
      r.setCustomerName(String.format("c%d", random.nextInt(10)));
      r.setMerchantName(String.format("m%d", random.nextInt(10)));
      r.setOrgId(Record.EMPTY_ORG_ID);
      samples.add(r);
    }
    return samples;
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    largeSamples = getRecords(LARGE_SAMPLE_SIZE);
  }

  @Test
  public void testNoThreadProcess() {
    RecordProcessor p = RecordProcessor.builder()
        .records(largeSamples)
        .build();
    Map<String, List<BigDecimal>> result = p.customerSummary();
    System.out.println(result);
  }

  @Test
  public void testFixThreadPoolProcess() {
    ExecutorService executorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );
    runTest(executorService);
  }

  @Test
  public void testCachedThreadPoolProcess() {
    ExecutorService executorService = Executors.newCachedThreadPool();
    runTest(executorService);
  }

  @Test
  public void testWorkStealingPoolProcess() {
    ExecutorService executorService = Executors.newWorkStealingPool();
    runTest(executorService);
  }

  @Test
  public void testSingleThreadProcess() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    runTest(executorService);
  }

  @Test
  public void testScheduleThreadProcess() {
    ExecutorService executorService = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors()
    );
    runTest(executorService);
  }

  private void runTest(ExecutorService executorService) {
    final int divider = LARGE_SAMPLE_SIZE / EACH_THREAD_SIZE;
    List<Callable<Map<String, List<BigDecimal>>>> tasks = new ArrayList<>();
    for (int i = 0; i < EACH_THREAD_SIZE; i++) {
      final int threadNum = i;
      tasks.add(() -> {
        RecordProcessor p = RecordProcessor.builder()
            .records(largeSamples.subList(threadNum * divider, (threadNum + 1) * divider))
            .build();
        return p.customerSummary();
      });
    }
    try {
      List<Future<Map<String, List<BigDecimal>>>> futures = executorService.invokeAll(tasks);
      Map<String, List<BigDecimal>> result = futures.stream()
          .map(f -> {
            try {
              return f.get();
            } catch (InterruptedException e) {
              e.printStackTrace();
              return new HashMap<String, List<BigDecimal>>();
            } catch (ExecutionException e) {
              e.printStackTrace();
              return new HashMap<String, List<BigDecimal>>();
            }
          })
          .reduce(new HashMap<>(), this::merge);
      System.out.println(result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }
  }

}
