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

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] storageImage;

    private Boolean isPublic;

    @Column(columnDefinition = "TEXT")
    private String storageOverview;

    private Long likeCount = 0L;

    private Long movieCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.PERSIST)
    private List<MovieStorage> movieStorages = new ArrayList<>();


    @Builder
    public Storage(String storageName, byte[] storageImage, Boolean isPublic, String storageOverview, Long likeCount, Long movieCount, User user, List<MovieStorage> movieStorages) {
        this.storageName = storageName;
        this.storageImage = storageImage;
        this.isPublic = isPublic;
        this.storageOverview = storageOverview;
        this.likeCount = likeCount;
        this.movieCount = movieCount;
        this.user = user;
        this.movieStorages = movieStorages;
    }

    public void changeStatus() {
        this.isPublic = !this.isPublic;
    }
}
