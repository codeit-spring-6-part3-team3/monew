package com.team03.monew.interest.domain;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
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

    @Column(length = 100)
    private String name;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar(100)[]")
    private List<String> keywords;

    @Column(length = 100)
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

    void keywordAdd(String keyword) {
        this.keywords.add(keyword);
    }

    void keywordRemove(String keyword) { this.keywords.remove(keyword); }

    void subscribeAdd() {this.subscribeCount = this.subscribeCount + 1; }

    void subscribeRemove() {this.subscribeCount = this.subscribeCount - 1; }
    
    //관심사 유사도 검사 메소드
    boolean nameEquals(String name) {
        int nameLength = this.name.length();
        int targetLength = name.length();
        return name.contains(this.name) && (targetLength * 0.8) <= nameLength;
    }
}
