package ar.com.old.ms_users.repositories;

import ar.com.old.ms_users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByEnabledTrue(Pageable pageable);

    Optional<User> findByIdAndEnabledTrue(Long id);

    Optional<User> findByUserNameAndEnabledTrue(String userName);

    Optional<User> findByEmailAndEnabledTrue(String email);

    @Modifying
    @Query("UPDATE User u SET u.enabled = false WHERE u.id = :id")
    void deleteLogicById(Long id);

    Optional<User> findByEmailOrUserNameAndEnabledTrue(String email, String userName);
}
