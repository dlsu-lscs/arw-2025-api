DROP TABLE IF EXISTS org_pubs;
DROP TABLE IF EXISTS orgs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS clusters;
DROP TABLE IF EXISTS refresh_tokens;

-- Table for Clusters
CREATE TABLE IF NOT EXISTS clusters (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT
);

-- Table for Users
CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    display_picture VARCHAR(255),
    name VARCHAR(255)
);

-- Table for Organizations
CREATE TABLE IF NOT EXISTS orgs (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    short_name VARCHAR(20),
    about TEXT,
    cluster_id INT,
    fee DECIMAL(10, 2),
    bundle_fee DECIMAL(10, 2),
    gforms_url TEXT,
    facebook_url TEXT,
    mission TEXT,
    vision TEXT,
    tagline TEXT,
    FOREIGN KEY (cluster_id) REFERENCES clusters(id)
);

-- Table for Organization Publications/Media
CREATE TABLE IF NOT EXISTS org_pubs (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    org_id INT,
    main_pub_url TEXT,
    fee_pub_url TEXT,
    logo_url TEXT,
    sub_logo_url TEXT,
    org_vid_url TEXT,
    FOREIGN KEY (org_id) REFERENCES orgs(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id INT UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
