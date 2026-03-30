ALTER TABLE public.ug_bpa_buildingplans
ADD COLUMN signed_dxf_filestore_id VARCHAR(100) NULL;

ALTER TABLE public.ug_bpa_buildingplans_audit
ADD COLUMN signed_dxf_filestore_id VARCHAR(100) NULL;