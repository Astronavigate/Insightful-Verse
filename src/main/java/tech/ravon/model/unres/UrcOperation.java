package tech.ravon.model.unres;

import lombok.Data;

import java.time.Instant;

@Data
public class UrcOperation {
    private Long id;
    private String urc;
    private String type;
    private String factoryId;
    private String operationId;
    private String authCode;
    private Instant createdAt;


    public UrcOperation() {
        id = null;
        urc = null;
        type = null;
        factoryId = null;
        operationId = null;
        authCode = null;
        createdAt = null;
    }

    public UrcOperation(Long id, String urc, String type, String factoryId,
                        String operationId, String authCode, Instant createdAt) {
        this.id = id;
        this.urc = urc;
        this.type = type;
        this.factoryId = factoryId;
        this.operationId = operationId;
        this.authCode = authCode;
        this.createdAt = createdAt;
    }

    public void setTime() {
        createdAt = Instant.now();
    }
}