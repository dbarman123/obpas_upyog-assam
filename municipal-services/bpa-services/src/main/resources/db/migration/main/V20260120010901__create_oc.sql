DROP TABLE IF EXISTS public.ug_oc_details;

CREATE TABLE public.ug_oc_details (
    id                        VARCHAR(64) PRIMARY KEY,
    tenant_id                 VARCHAR(256),
    application_no            VARCHAR(64),
    application_date          BIGINT,
    land_id                   VARCHAR(64),
    bpa_application_no        VARCHAR(64),
    name_of_master_plan       VARCHAR(256),
    name_of_ulb_panchayat     VARCHAR(256),
    name_of_applicant         VARCHAR(256),
    status                    VARCHAR(64),
    approval_no               VARCHAR(64),
    approval_date             BIGINT,
    occupancy_certificate_no  VARCHAR(64),
    noc_no                    VARCHAR(64),
    noc_date                  VARCHAR(64),
    proposed_use_of_building  VARCHAR(256),
    no_of_floors              VARCHAR(32),
    is_payment_done           BOOLEAN DEFAULT FALSE,
    additional_details        JSONB,
    oc_file_store_id          VARCHAR(64),
    business_service          VARCHAR(64),
    created_by                VARCHAR(64),
    last_modified_by          VARCHAR(64),
    created_time              BIGINT,
    last_modified_time        BIGINT
);


DROP TABLE IF EXISTS public.ug_oc_details_audit;

CREATE TABLE public.ug_oc_details_audit (
    id                        VARCHAR(64) PRIMARY KEY,
    oc_id                     VARCHAR(64),
    tenant_id                 VARCHAR(256),
    application_no            VARCHAR(64),
    application_date          BIGINT,
    land_id                   VARCHAR(64),
    bpa_application_no        VARCHAR(64),
    name_of_master_plan       VARCHAR(256),
    name_of_ulb_panchayat     VARCHAR(256),
    name_of_applicant         VARCHAR(256),
    status                    VARCHAR(64),
    approval_no               VARCHAR(64),
    approval_date             BIGINT,
    occupancy_certificate_no  VARCHAR(64),
    noc_no                    VARCHAR(64),
    noc_date                  VARCHAR(64),
    proposed_use_of_building  VARCHAR(256),
    no_of_floors              VARCHAR(32),
    is_payment_done           BOOLEAN DEFAULT FALSE,
    additional_details        JSONB,
    oc_file_store_id          VARCHAR(64),
    business_service          VARCHAR(64),
    created_by                VARCHAR(64),
    last_modified_by          VARCHAR(64),
    created_time              BIGINT,
    last_modified_time        BIGINT
);
