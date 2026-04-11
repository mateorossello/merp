package dev.mateorossello.merp.AccessModule.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/**
 * Entity representing a profile in the access system.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @NotBlank
    @Column(name = "profile_name", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "profiles_tasks", joinColumns = @JoinColumn(name = "profile_id"),inverseJoinColumns = @JoinColumn(name = "task_id"))
    private Set<Task> tasks = new HashSet<>();

    public void updateTasks(Set<Task> newTasks) {
        if (newTasks == null) {
            this.tasks.clear();
            return;
        }

        this.tasks.retainAll(newTasks);
        this.tasks.addAll(newTasks);
    }
}
