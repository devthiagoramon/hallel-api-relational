package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
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
            SELECT
            new br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse(
                scm.id,
                scale.id,
                sender.id,
                senderUser,
                scm.content,
                scm.contentType,
                scm.sentAt,
                scm.updatedAt,
                mss.status,
                scm.visibility
            ) FROM ScaleChatMessage scm
            JOIN scm.scale scale
            JOIN scm.memberChatSender sender
            JOIN sender.memberEventScale memberEventScale
            JOIN memberEventScale.memberMinistry mm
            JOIN mm.user senderUser
            LEFT JOIN MessageScaleStatus mss ON
                        mss.message.id = scm.id AND
                        senderUser.id = :currentUser AND
                        mss.chatParticipant.memberEventScale.memberMinistry.user.id = :currentUser
            where scale.id = :scaleId and senderUser.id = :currentUser
            ORDER BY scm.sentAt DESC
            """)
    Page<ScaleChatMessageResponse> listMessagesWithStatus(@Param("scaleId") UUID scaleId, @Param("currentUser") UUID userId, Pageable pageable);

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
