package giahuypro.jwtrefreshtokenaccesstoken.repository.model;

import giahuypro.jwtrefreshtokenaccesstoken.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}