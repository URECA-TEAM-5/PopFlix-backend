package com.popflix.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CommonCode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode extends BaseTimeEntity {
    @EmbeddedId
    private CommonCodeId commonCodeId;

    @Column(name = "common_code_name", length = 50)
    private String commonCodeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code_id", insertable = false, updatable = false)
    private GroupCode groupCode;
}
