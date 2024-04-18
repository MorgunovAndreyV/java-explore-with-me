CREATE TABLE IF NOT EXISTS EVENTS (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        title varchar(120) NOT NULL,
        annotation varchar(2000) NOT NULL,
        category_id BIGINT NOT NULL,
        initiator_id BIGINT NOT NULL,
        location_id BIGINT NOT NULL,
        state varchar(15),
        event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        paid boolean NOT NULL,
        created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        published_on TIMESTAMP WITHOUT TIME ZONE,
        description varchar(7000),
        request_moderation BOOLEAN,
        participant_limit BIGINT,
        confirmed_Requests BIGINT
        CHECK (LENGTH(annotation) >= 20),
        CHECK (LENGTH(description) >= 20),
        CHECK (LENGTH(title) >= 3)
);

CREATE TABLE IF NOT EXISTS USERS (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name varchar(250) NOT NULL,
        email varchar(254) NOT NULL,
        UNIQUE(name)
);


CREATE TABLE IF NOT EXISTS CATEGORIES (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name varchar(50) NOT NULL,
        UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        lat BIGINT NOT NULL,
        lon BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS REQUESTS (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        event_id BIGINT NOT NULL,
        requester_id BIGINT NOT NULL,
        created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        state varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        title varchar(50) NOT NULL,
        pinned boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS (
        compilation_id BIGINT NOT NULL,
        event_id BIGINT NOT NULL,
        PRIMARY KEY(compilation_id, event_id)
);