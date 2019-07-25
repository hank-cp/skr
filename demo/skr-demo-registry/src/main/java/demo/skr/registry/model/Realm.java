package demo.skr.registry.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Realm extends demo.skr.model.registry.Realm {

    public boolean enabled;
}
