package ar.com.old.ms_users.services;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    Page<User> findAll(Pageable pageable);

    User findOne(Long id);

    User create(UserRequestDTO dto);

    User update(UserRequestDTO dto);

    void delete(Long id);
}
