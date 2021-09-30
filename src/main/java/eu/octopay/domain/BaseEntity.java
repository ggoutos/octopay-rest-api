package eu.octopay.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Setter
@Getter
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Version
    private Long version;
}
