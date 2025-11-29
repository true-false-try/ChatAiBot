CREATE TABLE "user"
(
    id               DECIMAL NOT NULL,
    first_name       VARCHAR(100),
    last_name        VARCHAR(100),
    user_name        VARCHAR(100),
    created_at       BIGINT  NOT NULL,
    last_interaction BIGINT,
    age_group        VARCHAR(20),
    user_mood_id     DECIMAL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_user_mood UNIQUE (user_mood_id);

ALTER TABLE "user"
    ADD CONSTRAINT FK_USER_ON_USER_MOOD FOREIGN KEY (user_mood_id) REFERENCES user_mood (id);