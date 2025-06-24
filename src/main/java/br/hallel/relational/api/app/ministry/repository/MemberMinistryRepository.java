package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberMinistryRepository
        extends JpaRepository<MemberMinistry, MemberMinistryId> {


    @Query(
            value = """
                    SELECT u.* from "user" u
                    join member_ministry mm on u.id = mm.user_id
                    where mm.ministry_id = :ministry_id""",
            countQuery = """
                    SELECT count(*) from member_ministry mm
                    where mm.ministry_id = :ministry_id"""
            , nativeQuery = true)
    Page<User> findAllMembersFromMinistry(
            @Param("ministry_id") UUID ministryId, Pageable pageable);

    @Query(
            value = """
                    SELECT m.* from ministry m
                    join member_ministry mm on m.id = mm.ministry_id
                    where mm.user_id = :user_id""", nativeQuery = true
    )
    List<Ministry> listMinistryThatUserParticipateByUserId(
            @Param("user_id") UUID userId);

    MemberMinistry findMemberMinistryById(MemberMinistryId ministryId);


    List<MemberMinistry> findMemberMinistriesByMinistry_Id(UUID ministryId
//            , Pageable pageable
    );

    MemberMinistryId id(MemberMinistryId id);

    @Query("""
    SELECT m FROM MemberMinistry m
    WHERE m.ministry.id = :ministryId
    AND NOT EXISTS (
        SELECT mes FROM MemberEventScale mes
        WHERE mes.user.id = m.user.id
        AND mes.eventScale.id = :eventScaleId
    )
""")
    List<MemberMinistry> findAvailableMembersToInvite(UUID ministryId, UUID eventScaleId);

}
