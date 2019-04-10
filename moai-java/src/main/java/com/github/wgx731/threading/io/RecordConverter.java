package com.github.wgx731.threading.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wgx731.threading.model.Record;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Builder
@Slf4j
public class RecordConverter {

  @NonNull
  private ObjectMapper mapper;

  @NonNull
  private Charset charset;

  public boolean writeToFile(List<Record> records, Path location) {
    try {
      String jsonString = mapper.writeValueAsString(records);
      Files.write(location, jsonString.getBytes(charset));
    } catch (JsonProcessingException e) {
      log.warn("can't serialize records.", e);
      return false;
    } catch (IOException e) {
      log.warn("can't write json string.", e);
      return false;
    }
    return true;
  }


  public List<Record> readFromFile(Path location) throws IOException {
    if (!Files.isRegularFile(location)) {
      throw new IOException(String.format("wrong path: %s", location));
    }
    List<Record> records = mapper.readValue(
        location.toFile(),
        new TypeReference<List<Record>>() {
        }
    );
    return records;
  }

}
