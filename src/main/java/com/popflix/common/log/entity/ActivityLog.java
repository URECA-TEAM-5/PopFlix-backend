package com.popflix.common.log.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "ActivityLog")
@Getter
public class ActivityLog extends BaseSoftDeleteEntity {
    @Id
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    @Column(name = "user_id")
    private Long userId;
}
