CREATE TABLE "main_user"
(
    id               DECIMAL NOT NULL,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    user_name        VARCHAR(255),
    created_at       BIGINT,
    last_interaction BIGINT,
    age_group        VARCHAR(255),
    current_mood     VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (id)
);