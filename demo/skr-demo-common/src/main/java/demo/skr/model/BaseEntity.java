/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@TypeDefs({
        @TypeDef(name = "datetime", typeClass = PersistentDateTime.class)
})
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity implements Serializable, Cloneable {

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
