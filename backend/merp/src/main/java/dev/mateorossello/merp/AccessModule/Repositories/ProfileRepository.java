package dev.mateorossello.merp.AccessModule.Repositories;

import dev.mateorossello.merp.AccessModule.Models.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface which extends JpaRepository to provide CRUD operations for Profile entities.
 */

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    // Name related queries
    Optional<Profile> findByName(String name);
    boolean existsByName(String name);
    @EntityGraph(attributePaths = {"tasks"})
    Optional<Profile> findFullByName(String name);

    // ID related queries
    @EntityGraph(attributePaths = {"tasks"})
    Optional<Profile> findFullById(Long id);
}
