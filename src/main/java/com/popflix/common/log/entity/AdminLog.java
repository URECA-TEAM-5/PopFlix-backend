package com.popflix.common.log.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "AdminLog")
@Getter
public class AdminLog extends BaseSoftDeleteEntity {
    @Id
    @Column(name = "admin_log_id")
    private Long adminLogId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "target_id")
    private Long targetId;
}
