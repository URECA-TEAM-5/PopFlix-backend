package com.popflix.domain.storage.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import com.popflix.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Storage extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storageName;

    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL)
    private List<MovieStorage> movieStorages = new ArrayList<>();


    @Builder
    public Storage(String storageName, Boolean isPublic, User user, List<MovieStorage> movieStorages) {
        this.storageName = storageName;
        this.isPublic = isPublic;
        this.user = user;
        this.movieStorages = movieStorages;
    }
}
