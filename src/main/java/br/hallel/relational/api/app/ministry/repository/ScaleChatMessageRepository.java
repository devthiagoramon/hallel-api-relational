package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponseView;
import br.hallel.relational.api.app.ministry.dto.StatusReadingMessageUserResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScaleChatMessageRepository extends JpaRepository<ScaleChatMessage, UUID> {

    @Query("""
                SELECT v
                FROM ScaleChatMessageResponseView v
                WHERE v.eventScaleId = :scaleId
                ORDER BY v.sentAt DESC
            """)
    Page<ScaleChatMessageResponseView> listMessagesWithStatus(@Param("scaleId") UUID scaleId,
                                                              @Param("currentScaleChatParticipantId") UUID scaleChatParticipant,
                                                              Pageable pageable);

    @Query("""
            SELECT new
            br.hallel.relational.api.app.ministry.dto.StatusReadingMessageUserResponse(
                    user, mss.status
                    )
            FROM MessageScaleStatus mss
            JOIN mss.chatParticipant chatParticipant
            JOIN chatParticipant.memberEventScale mes
            JOIN mes.memberMinistry mm
            JOIN mm.user user
            WHERE mss.message.id = :messageId
            """)
    List<StatusReadingMessageUserResponse> listStatusDeliverPerUser(@Param("messageId") UUID messageId);


}
