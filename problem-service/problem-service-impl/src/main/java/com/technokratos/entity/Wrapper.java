package com.technokratos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Table(
        name = "wrapper",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"problem_id", "language_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wrapper {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(name = "method_signature", nullable = false, columnDefinition = "TEXT")
    private String methodSignature;

    @Column(name = "wrapper", nullable = false, columnDefinition = "TEXT")
    private String wrapper;
}

