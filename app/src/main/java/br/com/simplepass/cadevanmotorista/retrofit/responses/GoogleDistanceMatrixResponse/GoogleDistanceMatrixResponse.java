package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse;

import java.util.List;

/**
 * Created by leandro on 3/22/16.
 */
public class GoogleDistanceMatrixResponse {
    private String status;
    private List<Row> rows;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
