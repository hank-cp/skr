package demo.skr.model.registry;

import demo.skr.model.CodeBasedEntity;
import lombok.Getter;
import org.skr.registry.model.RealmRegistry;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public class Realm extends CodeBasedEntity implements RealmRegistry {

    @NotNull
    @Column(unique = true)
    public String name;

    @Override
    public String getCode() {
        return code;
    }

}
