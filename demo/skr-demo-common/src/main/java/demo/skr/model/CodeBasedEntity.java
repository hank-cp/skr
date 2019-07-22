package demo.skr.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class CodeBasedEntity extends BaseEntity {

    @Id
    @NotNull
    public String code;

}
