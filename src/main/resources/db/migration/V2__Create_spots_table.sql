CREATE TABLE spots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sector_name VARCHAR(255) NOT NULL,
    lat DOUBLE NOT NULL,
    lng DOUBLE NOT NULL,
    occupied BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_spots_sector FOREIGN KEY (sector_name) REFERENCES sectors(name)
);

CREATE INDEX idx_spots_sector_name ON spots(sector_name);
CREATE INDEX idx_spots_coordinates ON spots(lat, lng);

