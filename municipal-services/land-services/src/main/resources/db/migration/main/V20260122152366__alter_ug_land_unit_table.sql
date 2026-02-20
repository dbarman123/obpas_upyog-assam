ALTER TABLE ug_land_unit
    ADD COLUMN IF NOT EXISTS specify_usage VARCHAR(1000) NULL;

ALTER TABLE ug_land_unit_audit_details
    ADD COLUMN IF NOT EXISTS specify_usage VARCHAR(1000) NULL;
