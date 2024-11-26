package com.popflix.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
public abstract class BaseSoftDeleteEntity extends BaseTimeEntity {
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
    }
}
