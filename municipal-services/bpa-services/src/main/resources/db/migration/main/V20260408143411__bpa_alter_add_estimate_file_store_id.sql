
ALTER TABLE public.ug_bpa_buildingplans
ADD COLUMN IF NOT EXISTS estimated_cost_of_construction NUMERIC(12,2) NULL,
ADD COLUMN IF NOT EXISTS estimate_file_store_id VARCHAR(256) NULL;

ALTER TABLE public.ug_bpa_buildingplans_audit
ADD COLUMN IF NOT EXISTS estimated_cost_of_construction NUMERIC(12,2) NULL,
ADD COLUMN IF NOT EXISTS estimate_file_store_id VARCHAR(256) NULL;