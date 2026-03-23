CREATE TABLE sectors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    base_price DECIMAL(10, 2) NOT NULL,
    max_capacity INT NOT NULL,
    open_hour VARCHAR(255) NOT NULL,
    close_hour VARCHAR(255) NOT NULL,
    duration_limit_minutes INT NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uk_sectors_name UNIQUE (name)
);

CREATE INDEX idx_sectors_name ON sectors(name);

