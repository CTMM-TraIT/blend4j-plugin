package com.github.jmchilton.blend4j.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.OutputDataset;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class TestHelpers {

  static File getTestFile() {
    try {
      final File tempFile = File.createTempFile("galxtest", ".txt");
      final FileWriter writer = new FileWriter(tempFile);
      try {
        writer.write("Hello World!!!");
      } finally {
          writer.close();
      }
      return tempFile;
    } catch(final IOException ioException) {
      throw new RuntimeException(ioException);
    }
  }
  
  static OutputDataset testUpload(final GalaxyInstance galaxyInstance, final String historyId, final File testFile) {
    final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(historyId, testFile);
    final ToolExecution execution = galaxyInstance.getToolsClient().upload(request);
    return execution.getOutputs().get(0);
  }
  
  static String getTestHistoryId(final GalaxyInstance instance) {
    final History testHistory = new History();
    testHistory.setName("blend4j Test History");
    final History newHistory = instance.getHistoriesClient().create(testHistory);
    final String historyId = newHistory.getId();
    return historyId;
  }
  
  static void waitForHistory(final HistoriesClient client, final String historyId) throws InterruptedException {
    HistoryDetails details = null;
    while(true) {
      details = client.showHistory(historyId);
      if(details.isReady()) {
        break;
      }
    }
    final String state = details.getState();
    if(!state.equals("ok")) {
      final String message = "History no longer running, but not in 'ok' state. State is - " + state;
      throw new RuntimeException(message);
    }
    Thread.sleep(200L);
  }
  
  static List<String> populateTestDatasets(final GalaxyInstance instance, final String historyId, final int count) throws InterruptedException {
    final List<String> ids = Lists.newArrayListWithCapacity(count);
    for(int i = 0; i < count; i++) {
      final File input = getTestFile();
      OutputDataset dataset = testUpload(instance, historyId, input);
      ids.add(dataset.getId());
    }
    waitForHistory(instance.getHistoriesClient(), historyId);
    return ids;
  }
  
}
