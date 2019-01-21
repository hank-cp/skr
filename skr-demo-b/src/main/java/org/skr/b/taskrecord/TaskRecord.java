package org.skr.b.taskrecord;

import org.skr.model.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class TaskRecord extends BaseEntity {

    @NotNull
    public long taskId;

    public String operation;

}
