package org.skr.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class IdBasedEntity extends BaseEntity implements Cloneable {

    @Id
    @GeneratedValue(generator="native")
    @GenericGenerator(name="native", strategy="native")
    public long id;

}
