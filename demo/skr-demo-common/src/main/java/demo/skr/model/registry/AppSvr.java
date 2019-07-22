package demo.skr.model.registry;

import demo.skr.model.CodeBasedEntity;
import lombok.Getter;
import org.skr.registry.model.AppSvrRegistry;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public class AppSvr extends CodeBasedEntity implements AppSvrRegistry {

    public static final String REGISTRY_APPSVR = "AppSvr";

    @NotNull
    @Column(unique = true)
    public String name;

    @Override
    public String getCode() {
        return code;
    }

}
