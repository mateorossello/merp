package dev.mateorossello.merp.modules.access.repositories;

import dev.mateorossello.merp.modules.access.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Interface which extends JpaRepository to provide CRUD operations for User entities.
 */

public interface UserRepository extends JpaRepository<User,Long> {
    // Username related queries
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @EntityGraph(attributePaths = {"profile", "profile.tasks"})
    Optional<User> findFullByUsername(String username);

    // Profile related queries
    List<User> findAllByProfileId(Long id);
    boolean existsByProfileId(Long id);

    @Query("SELECT user.profile.permissionsVersion FROM User user WHERE user.id = :id")
    Optional<Long> findPermissionsVersionByUserId(@Param("id") Long id);
}
