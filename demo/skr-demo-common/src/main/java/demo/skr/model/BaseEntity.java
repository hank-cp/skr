package demo.skr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.UUID;

@TypeDefs({
        @TypeDef(name = "datetime", typeClass = PersistentDateTime.class)
})
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity implements Cloneable {

    @Type(type="uuid-char")
    @Column(name = "`uid`")
    public UUID uid = UUID.randomUUID();

    @CreatedBy
    public String createdBy;

    @CreatedDate
    @Type(type="datetime")
    public DateTime createdAt;

    @LastModifiedBy
    public String updatedBy;

    @LastModifiedDate
    @Type(type="datetime")
    public DateTime updatedAt;

    @Version
    public int ver = 0;

    //*************************************************************************
    // Entity Listener
    //*************************************************************************

    @PrePersist
    @PreUpdate
    public void beforeSaved() {
        if (uid == null) uid = UUID.randomUUID();
    }

}
