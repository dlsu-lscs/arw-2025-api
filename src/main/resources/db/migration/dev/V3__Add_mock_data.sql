-- Mock Data for ARW 2025 API

-- Clusters
INSERT INTO clusters (name, description) VALUES
('Technology', 'Organizations focused on technology, software development, and engineering.'),
('Business', 'Organizations related to business, finance, and entrepreneurship.'),
('Arts & Media', 'Organizations for the creative and performing arts, and media production.');

-- Colleges
INSERT INTO colleges (name, description) VALUES
('College of Computer Studies', 'The premier institution for computer science and information technology.'),
('Ramon V. del Rosario College of Business', 'Leading business school in the country.'),
('College of Liberal Arts', 'Center for humanities and social sciences.');

-- Users (for linking refresh tokens)
INSERT INTO users (email, display_picture, name) VALUES
('test.user.one@example.com', 'https://example.com/pic1.jpg', 'Test User One'),
('test.user.two@example.com', 'https://example.com/pic2.jpg', 'Test User Two');

-- Organizations
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('Computer Society', 'CS', 'The Computer Society of De La Salle University is a student organization dedicated to the pursuit of excellence in the field of computer science.', 1, 1, 150.00, 120.00, 'https://forms.gle/cs', 'https://facebook.com/cs', 'To promote computer science.', 'To be the best.', 'Excellence in CS.'),
('Business Management Society', 'BMS', 'An organization for business management students.', 2, 2, 120.00, 100.00, 'https://forms.gle/bms', 'https://facebook.com/bms', 'To promote business management.', 'To lead in business.', 'Lead with us.'),
('La Salle Film Society', 'LSFS', 'A group for film enthusiasts and creators.', 3, 3, 100.00, 80.00, 'https://forms.gle/lsfs', 'https://facebook.com/lsfs', 'To create and appreciate film.', 'A cinematic future.', 'Create. Appreciate. Animate.');

-- Publications
INSERT INTO org_pubs (org_id, main_pub_url, fee_pub_url, logo_url, sub_logo_url, org_vid_url)
VALUES
(1, 'https://example.com/cs_main.jpg', 'https://example.com/cs_fee.jpg', 'https://example.com/cs_logo.png', 'https://example.com/cs_sublogo.png', 'https://youtube.com/cs_vid'),
(2, 'https://example.com/bms_main.jpg', 'https://example.com/bms_fee.jpg', 'https://example.com/bms_logo.png', 'https://example.com/bms_sublogo.png', 'https://youtube.com/bms_vid'),
(3, 'https://example.com/lsfs_main.jpg', 'https://example.com/lsfs_fee.jpg', 'https://example.com/lsfs_logo.png', 'https://example.com/lsfs_sublogo.png', 'https://youtube.com/lsfs_vid');
