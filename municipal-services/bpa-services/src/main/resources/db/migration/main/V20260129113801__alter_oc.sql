ALTER TABLE public.ug_oc_details
ADD COLUMN IF NOT EXISTS signed_oc_filestore_id VARCHAR(100),
ADD COLUMN IF NOT EXISTS is_panality_applicable BOOLEAN DEFAULT FALSE;