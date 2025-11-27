package com.team03.monew.interest.domain;

import com.team03.monew.interest.util.SimilarityCheck;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar(50)[] NOT NULL")
    private List<String> keywords;

    @ColumnDefault("0")
    private int subscribeCount;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Interest() {}

    @Builder
    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
        this.subscribeCount = 0;
    }

    public void keywordAdd(String keyword) {this.keywords.add(keyword);}

    public void keywordRemove(String keyword) { this.keywords.remove(keyword); }

    public void subscribeAdd() {this.subscribeCount = this.subscribeCount + 1; }

    public void subscribeRemove() {this.subscribeCount = this.subscribeCount - 1; }
    
    //관심사 유사도 검사 메소드
    public boolean nameEquals(String name) {
        return SimilarityCheck.isSimilar(this.name, name);
    }
}
