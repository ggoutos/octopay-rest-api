package eu.octopay.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Operation extends BaseEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();

    @NotNull
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @NotNull
    @Column
    private BigDecimal amount;

    @NotNull
    @Column
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column
    private String comment;

    public Operation(Account account, OperationType type, BigDecimal amount, String comment) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Operation operation = (Operation) o;
        return Objects.equals(id, operation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
