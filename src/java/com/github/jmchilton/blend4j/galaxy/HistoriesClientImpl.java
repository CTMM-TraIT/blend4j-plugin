package com.github.jmchilton.blend4j.galaxy;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.sun.jersey.api.client.ClientResponse;

class HistoriesClientImpl extends Client implements HistoriesClient {
  HistoriesClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "histories");
  }

  public ClientResponse createRequest(final History history) {
    return super.create(history);
  }

  public History create(final History history) {
    return createRequest(history).getEntity(History.class);
  }

  public List<History> getHistories() {
    final TypeReference<List<History>> typeReference = new TypeReference<List<History>>() {
    };
    return get(typeReference);
  }

  public ClientResponse showHistoryRequest(String id) {
    return super.show(id, ClientResponse.class);
  }

  public HistoryDetails showHistory(String id) {
    return super.show(id, HistoryDetails.class);
  }

  public List<HistoryContents> showHistoryContents(String historyId) {
    return get(getWebResourceContents(historyId), new TypeReference<List<HistoryContents>>() {
    });
  }

  public Dataset showDataset(String historyId, String datasetId) {
    return setGalaxyUrl(getWebResourceContents(historyId).path(datasetId).get(Dataset.class));
  }

  public HistoryContentsProvenance showProvenance(String historyId, String datasetId) {
    return getWebResourceContents(historyId).path(datasetId).path("provenance").get(HistoryContentsProvenance.class);
  }


  public HistoryDetails createHistoryDataset(String historyId, HistoryDataset hd) {
    final ClientResponse response = super.create(super.path(historyId).path("contents"), hd);
    return response.getEntity(HistoryDetails.class);
  }
}
