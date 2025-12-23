DROP TABLE IF EXISTS eg_userrole;
ALTER TABLE eg_role ADD COLUMN IF NOT EXISTS roleid bigint NOT NULL DEFAULT 0;
UPDATE eg_role SET roleid = id WHERE roleid IS NULL OR roleid <> id;
ALTER TABLE eg_role ALTER COLUMN roleid DROP DEFAULT;
ALTER TABLE eg_role DROP COLUMN IF EXISTS id;
DO $$ BEGIN IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='eg_role' AND column_name='roleid') AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='eg_role' AND column_name='id') THEN ALTER TABLE eg_role RENAME COLUMN roleid TO id; END IF; END$$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='eg_role_pk') THEN ALTER TABLE eg_role ADD CONSTRAINT eg_role_pk PRIMARY KEY (id, tenantid); END IF; END$$;
CREATE TABLE IF NOT EXISTS eg_userrole(roleid bigint NOT NULL, 
roleidtenantid character varying(256) NOT NULL, 
userid bigint NOT NULL, 
tenantid character varying(256) NOT NULL, 
FOREIGN KEY (roleid, roleidtenantid) REFERENCES eg_role(id, tenantid),
FOREIGN KEY (userid, tenantid) REFERENCES eg_user(id, tenantid));
