package org.skr.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class RegisterBatch {

    public RealmRegistry realm;
    public List<PermissionRegistry> permissions;
    public List<EndPointRegistry> endPoints;

}
