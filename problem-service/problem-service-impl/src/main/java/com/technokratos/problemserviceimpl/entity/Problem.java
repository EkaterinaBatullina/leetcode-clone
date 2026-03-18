package com.technokratos.problemserviceimpl.entity;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
<<<<<<< HEAD
=======
@Builder
@NoArgsConstructor
@AllArgsConstructor
>>>>>>> feature/problem-and-submission-service
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String constraints;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

<<<<<<< HEAD
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;

    @Column(name = "ready_for_publish", nullable = false)
    private Boolean readyForPublish;

    @Column(name = "publish_status", nullable = false)
=======
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ready_for_publish")
    private Boolean readyForPublish;

>>>>>>> feature/problem-and-submission-service
    @Enumerated(EnumType.STRING)
    private PublishStatus publishStatus;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Testcase> testcases = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "problem_tag",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

}
