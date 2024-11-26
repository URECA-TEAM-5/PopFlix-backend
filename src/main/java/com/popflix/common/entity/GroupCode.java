package com.popflix.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GroupCode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCode extends BaseTimeEntity {
    @Id
    @Column(name = "group_code_id", length = 20)
    private String groupCodeId;

    @Column(name = "group_code_name", length = 20)
    private String groupCodeName;

    @Column(name = "group_code_desc", length = 50)
    private String groupCodeDesc;

    @OneToMany(mappedBy = "groupCode")
    private List<CommonCode> commonCodes = new ArrayList<>();
}