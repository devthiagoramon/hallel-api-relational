package br.hallel.relational.api.app.ministry.interfaces;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberMinistryRepository extends JpaRepository<MemberMinistry, MemberMinistryId> {

    @Query(
            value = """
                    SELECT u.* from "user" u
                    join member_ministry mm on u.id = mm.user_id
                    where mm.ministry_id = :ministry_id""",
            countQuery = """
                    SELECT count(*) from member_ministry mm
                    where mm.ministry_id = :ministry_id"""
            , nativeQuery = true)
    Page<User> findAllMembersFromMinistry(@Param("ministry_id") UUID ministryId, Pageable pageable);
}
