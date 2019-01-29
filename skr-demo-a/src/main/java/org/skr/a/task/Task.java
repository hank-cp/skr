package org.skr.a.task;

import org.skr.model.IdBasedEntity;

import javax.persistence.Entity;

@Entity
public class Task extends IdBasedEntity {

    public String description;

}
