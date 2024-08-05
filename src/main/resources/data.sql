CREATE TABLE IF NOT EXISTS trade (
    trade_id VARCHAR(255) NOT NULL,
    version INT NOT NULL,
    counter_party_id VARCHAR(255) NOT NULL,
    book_id VARCHAR(255) NOT NULL,
    maturity_date DATE NOT NULL,
    created_date DATE NOT NULL,
    expired CHAR(1) NOT NULL,
    PRIMARY KEY (trade_id, version)
);

INSERT INTO trade (trade_id, version, counter_party_id, book_id, maturity_date, created_date, expired)
VALUES ('T1', 1, 'CP-1', 'B1', '2025-05-20', CURRENT_DATE, 'N');
