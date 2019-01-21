package org.skr.a.task;

import org.skr.model.BaseEntity;

import javax.persistence.Entity;

@Entity
public class Task extends BaseEntity {

    public String description;

}
