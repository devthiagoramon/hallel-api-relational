package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.FunctionMinistryMember;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FunctionMinistryMemberRepository extends
        JpaRepository<FunctionMinistryMember, FunctionMinistryMemberId> {

    @Query("""
        SELECT fmm
        from function_ministry_member fmm
        join fetch fmm.functionMinistry
        where fmm.user.id in :userIds
        """)
    List<FunctionMinistryMember> listAllByUserIds(@Param("userIds")List<UUID> userIds);
}
