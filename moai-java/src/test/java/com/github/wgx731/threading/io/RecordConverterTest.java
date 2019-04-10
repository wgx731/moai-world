package com.github.wgx731.threading.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wgx731.TestHelper;
import com.github.wgx731.threading.model.Record;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordConverterTest extends TestHelper {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private RecordConverter converter;

  private File testFile;

  @Before
  public void setUp() throws Exception {
    super.setUpSampleRecords();
    converter = RecordConverter.builder()
        .mapper(new ObjectMapper())
        .charset(Charset.forName("UTF8"))
        .build();
    testFile = tempFolder.newFile();
  }

  @After
  public void tearDown() throws Exception {
    super.tearDownSampleRecords();
    converter = null;
    testFile = null;
  }

  @Test
  public void testConverter() throws IOException {
    assertThat(converter.writeToFile(sampleRecords, testFile.toPath())).isTrue();
    List<Record> results = converter.readFromFile(testFile.toPath());
    assertThat(results).isEqualTo(sampleRecords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testException() throws IOException {
    try {
      converter.readFromFile(tempFolder.newFolder().toPath());
      Assert.fail();
    } catch (IOException e) {
      assertThat(e).hasMessageContaining("wrong path");
    }
    assertThat(converter.writeToFile(
        (List<Record>) Mockito.mock(List.class),
        tempFolder.newFile().toPath())
    ).isFalse();
    assertThat(converter.writeToFile(
        sampleRecords, tempFolder.newFolder().toPath())
    ).isFalse();
  }

}
