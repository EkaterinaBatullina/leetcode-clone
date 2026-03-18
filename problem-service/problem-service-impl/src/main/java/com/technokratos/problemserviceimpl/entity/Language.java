package com.technokratos.problemserviceimpl.entity;

import jakarta.persistence.*;
import lombok.*;

<<<<<<< HEAD
import java.util.UUID;

=======
>>>>>>> feature/problem-and-submission-service
@Entity
@Table(name = "language")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    @Id
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String extension;
=======
    private int id;  // Judge0 language ID

    private String name;
>>>>>>> feature/problem-and-submission-service
}
