package dev.mateorossello.merp.AccessModule.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_name", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private TaskType name;
}
