package student.ed.tracecode.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import student.ed.tracecode.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccountAndStatus(String account, Byte status);
}
