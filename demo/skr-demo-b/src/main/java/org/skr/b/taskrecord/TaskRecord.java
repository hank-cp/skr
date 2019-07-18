package org.skr.b.taskrecord;

import org.skr.model.IdBasedEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class TaskRecord extends IdBasedEntity {

    @NotNull
    public long taskId;

    public String operation;

}
