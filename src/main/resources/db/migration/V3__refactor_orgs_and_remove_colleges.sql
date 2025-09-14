-- Remove the college_id foreign key from the orgs table
ALTER TABLE orgs DROP COLUMN IF EXISTS college_id;

-- Drop the colleges table entirely
DROP TABLE IF EXISTS colleges;

-- Remove the bundle_fee column from the orgs table
ALTER TABLE orgs DROP COLUMN IF EXISTS bundle_fee;

-- Change the fee column from a numeric type to TEXT
ALTER TABLE orgs ALTER COLUMN fee TYPE TEXT;
