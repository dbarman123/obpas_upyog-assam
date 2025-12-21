-- Create ShedLock table for distributed scheduler locking
-- This table is used by ShedLock to ensure that scheduled jobs run only on one instance

CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

