package com.popflix.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CommonCodeId implements Serializable {
    @Column(name = "common_code_id", length = 20)
    private String commonCodeId;

    @Column(name = "group_code_id", length = 20)
    private String groupCodeId;
}
