CREATE TABLE parking_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(255) NOT NULL,
    sector_name VARCHAR(255) NOT NULL,
    spot_id BIGINT,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    applied_price DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2),
    status VARCHAR(255) NOT NULL,
    active_key VARCHAR(255) NULL,
    CONSTRAINT fk_parking_records_sector FOREIGN KEY (sector_name) REFERENCES sectors(name),
    CONSTRAINT fk_parking_records_spot FOREIGN KEY (spot_id) REFERENCES spots(id)
);

CREATE INDEX idx_parking_records_license_plate ON parking_records(license_plate);
CREATE INDEX idx_parking_records_sector_name ON parking_records(sector_name);
CREATE INDEX idx_parking_records_status ON parking_records(status);
CREATE INDEX idx_parking_records_entry_time ON parking_records(entry_time);
CREATE UNIQUE INDEX idx_parking_records_active_key ON parking_records(active_key);

