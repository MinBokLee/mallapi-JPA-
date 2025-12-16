package org.zerock.mallapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mallapi.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String>{

@EntityGraph(attributePaths = {"memberRoleList"})
@Query("select m from Member m where m.email = :email")
Member getWithRols(@Param("email") String email);

}

/**
     * 특정 이메일을 가진 회원을 조회하며, 관련된 'memberRoleList'를 함께 로딩합니다.
     * * @EntityGraph(attributePaths = {"memberRoleList"})
     * - JPA의 N+1 문제(지연 로딩으로 인한 성능 저하)를 해결하기 위해 사용됩니다.
     * - 'memberRoleList' 속성(회원의 역할 목록)을 즉시 로딩(Eager Loading)하도록 지정합니다.
     * 즉, 회원 엔티티를 조회할 때 관련 역할 정보도 JOIN을 통해 한 번의 쿼리로 가져옵니다.
     * * @Query("select m from Member m where m.email = :email")
     * - 직접 JPQL(Java Persistence Query Language)을 정의하여 실행할 쿼리를 지정합니다.
     * - ':email'은 @Param을 통해 전달받을 파라미터입니다.
     */
