DROP TABLE IF EXISTS org_pubs;
DROP TABLE IF EXISTS orgs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS colleges;
DROP TABLE IF EXISTS clusters;

-- Table for Clusters
CREATE TABLE clusters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT
);

-- Table for Colleges
CREATE TABLE colleges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT
);

-- Table for Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    display_picture VARCHAR(255),
    name VARCHAR(255)
);

-- Table for Organizations
CREATE TABLE orgs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    short_name VARCHAR(20),
    about TEXT,
    cluster_id INT,
    college_id INT,
    fee DECIMAL(10, 2),
    bundle_fee DECIMAL(10, 2),
    gforms_url TEXT,
    facebook_url TEXT,
    mission TEXT,
    vision TEXT,
    tagline TEXT,
    FOREIGN KEY (cluster_id) REFERENCES clusters(id),
    FOREIGN KEY (college_id) REFERENCES colleges(id)
);

-- Table for Organization Publications/Media
CREATE TABLE org_pubs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    org_id INT,
    main_pub_url TEXT,
    fee_pub_url TEXT,
    logo_url TEXT,
    sub_logo_url TEXT,
    org_vid_url TEXT,
    FOREIGN KEY (org_id) REFERENCES orgs(id)
);
