package eu.octopay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.octopay.domain.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OperationRequest {
    @JsonIgnore
    private String accountId;
    @JsonIgnore
    private OperationType type;
    @JsonIgnore
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private String comment;
}
