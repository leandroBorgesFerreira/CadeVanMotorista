package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse;

import java.util.List;

import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.Row;

/**
 * Created by leandro on 4/12/16.
 */
public class GoogleGeocodeResponse {
    private String status;
    private List<Result> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
