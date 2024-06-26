package com.tobe.healthy.diet.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "diet_files")
@Builder
@Getter
@ToString
public class DietFiles extends BaseTimeEntity<DietFiles, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String fileUrl;

    @ColumnDefault("false")
    @Builder.Default
    @ToString.Exclude
    private Boolean delYn = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    @ToString.Exclude
    private Diet diet;

    @Enumerated(STRING)
    @ColumnDefault("'BREAKFAST'")
    private DietType type;

    private String fileName;

    public static DietFiles create(Diet diet, String fileUrl, DietType type) {
        return DietFiles.builder()
                .fileUrl(fileUrl)
                .diet(diet)
                .type(type)
                .fileName(getFileName(fileUrl))
                .build();
    }

    private static String getFileName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

    public void deleteDietFile() {
        this.delYn = true;
    }

}
