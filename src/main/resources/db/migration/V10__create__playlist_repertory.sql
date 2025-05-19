CREATE TABLE playlist_repertory_music
(
    playlist_repertory_id UUID NOT NULL,
    music_ministry_id     UUID NOT NULL,
    PRIMARY KEY (playlist_repertory_id, music_ministry_id),
    FOREIGN KEY (playlist_repertory_id) REFERENCES "playlist_repertory" (id) ON DELETE CASCADE,
    FOREIGN KEY (music_ministry_id) REFERENCES "music_ministry" (id) ON DELETE CASCADE
);

CREATE TABLE playlist_repertory_dance
(
    playlist_repertory_id UUID NOT NULL,
    dance_ministry_id     UUID NOT NULL,
    PRIMARY KEY (playlist_repertory_id, dance_ministry_id),
    FOREIGN KEY (playlist_repertory_id) REFERENCES "playlist_repertory" (id) ON DELETE CASCADE,
    FOREIGN KEY (dance_ministry_id) REFERENCES "dance_ministry" (id) ON DELETE CASCADE
);

