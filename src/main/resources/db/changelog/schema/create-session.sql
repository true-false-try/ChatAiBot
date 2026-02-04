CREATE TABLE session
(
    id         UUID NOT NULL,
    user_id    BIGINT,
    created_at BIGINT,
    updated_at BIGINT,
    CONSTRAINT pk_session PRIMARY KEY (id)
);

ALTER TABLE session
    ADD CONSTRAINT FK_SESSION_ON_USER FOREIGN KEY (user_id) REFERENCES "main_user" (id);