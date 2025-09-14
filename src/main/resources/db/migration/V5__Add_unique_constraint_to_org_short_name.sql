ALTER TABLE orgs ADD CONSTRAINT orgs_short_name_unique UNIQUE (short_name);
