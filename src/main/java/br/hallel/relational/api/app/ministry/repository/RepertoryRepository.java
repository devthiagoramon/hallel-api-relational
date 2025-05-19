package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import br.hallel.relational.api.app.ministry.model.PlaylistRepertory;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepertoryRepository extends JpaRepository<RepertoryMinistry, UUID> {
    List<RepertoryMinistry> findAllByMinistry_Id(UUID ministryId);

    @Query("""
                SELECT r FROM RepertoryMinistry r
                LEFT JOIN FETCH r.danceMinistryList d
                LEFT JOIN FETCH r.musicMinistryList m
                WHERE r.id = :id
            """)
    Optional<RepertoryMinistry> findByIdWithDanceAndMusicInfos(@Param("id") UUID id);

    @Query("""
                SELECT m FROM RepertoryMinistry r
                JOIN r.musicMinistryList m
                WHERE r.id = :repertoryId
            """)
    List<MusicMinistry> findAllMusicByRepertoryId(@Param("repertoryId") UUID repertoryId);

    @Query("""
    SELECT d FROM RepertoryMinistry r
    JOIN r.danceMinistryList d
    WHERE r.id = :repertoryId
""")
    List<DanceMinistry> findAllDancesByRepertoryId(@Param("repertoryId") UUID repertoryId);

    @Query("""
    SELECT d FROM RepertoryMinistry r
    JOIN r.playlistRepertoryList d
    WHERE r.id = :repertoryId
""")
    List<PlaylistRepertory> findAllPlaylistsByRepertoryId(@Param("repertoryId") UUID repertoryId);


}
