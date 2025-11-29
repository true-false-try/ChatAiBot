CREATE TABLE user_mood
(
    id        DECIMAL NOT NULL,
    user_id   DECIMAL,
    moods     VARCHAR(255),
    risk_flag VARCHAR(255),
    CONSTRAINT pk_user_mood PRIMARY KEY  (id)
);