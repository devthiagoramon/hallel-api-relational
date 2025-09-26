package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.MessageScaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageScaleStatusRepository extends JpaRepository<MessageScaleStatus, UUID> {


    Optional<MessageScaleStatus> findByMessage_IdAndChatParticipant_MemberEventScale_MemberMinistry_User_Id(
            UUID messageId, UUID chatParticipantMemberEventScaleMemberMinistryUserId);
}
