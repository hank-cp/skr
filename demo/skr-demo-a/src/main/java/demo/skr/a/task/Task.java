package demo.skr.a.task;

import demo.skr.model.IdBasedEntity;

import javax.persistence.Entity;

@Entity
public class Task extends IdBasedEntity {

    public String description;

}