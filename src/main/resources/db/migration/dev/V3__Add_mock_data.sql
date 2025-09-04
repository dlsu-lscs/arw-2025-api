-- Mock Data for ARW 2025 API

-- Clear existing data to ensure fresh start for mock data
TRUNCATE TABLE org_pubs RESTART IDENTITY CASCADE;
TRUNCATE TABLE orgs RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE colleges RESTART IDENTITY CASCADE;
TRUNCATE TABLE clusters RESTART IDENTITY CASCADE;

-- Clusters
INSERT INTO clusters (name, description) VALUES
('Engage', 'Engineering Alliance Geared Towards Excellence'),
('CAP13', 'College of Liberal Arts Organizations'),
('ASPIRE', 'College of Education and Special Interest and Socio-Civic Organizations'),
('PROBE', 'Professional Organizations of Business and Economics'),
('ASO', 'Alliance of Science Organizations');

-- Colleges (re-inserting existing ones, assuming their IDs are 1, 2, 3)
INSERT INTO colleges (name, description) VALUES
('College of Computer Studies', 'The premier institution for computer science and information technology.'),
('Ramon V. del Rosario College of Business', 'Leading business school in the country.'),
('College of Liberal Arts', 'Center for humanities and social sciences.');

-- Users (for linking refresh tokens)
INSERT INTO users (email, display_picture, name) VALUES
('test.user.one@example.com', 'https://example.com/pic1.jpg', 'Test User One'),
('test.user.two@example.com', 'https://example.com/pic2.jpg', 'Test User Two');

-- Organizations (15 organizations)
-- Using subqueries to get cluster_id and college_id dynamically

-- Org 1: Engage - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Computer Engineering Society', 'DLSU CoES', 'Dedicated to the advancement of computer engineering.',
(SELECT id FROM clusters WHERE name = 'Engage'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'),
180.00, 150.00, 'https://forms.gle/coes', 'https://facebook.com/coes', 'To foster excellence in CoE.', 'To be a leading CoE org.', 'Innovate. Create. Engineer.');

-- Org 2: Engage - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Electronics and Communications Engineering Society', 'DLSU ECE', 'Promoting ECE excellence.',
(SELECT id FROM clusters WHERE name = 'Engage'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'),
170.00, 140.00, 'https://forms.gle/ece', 'https://facebook.com/ece', 'To advance ECE knowledge.', 'To be the best ECE org.', 'Connect. Communicate. Create.');

-- Org 3: CAP13 - College of Liberal Arts
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Literary Guild', 'DLSU LitG', 'Cultivating literary arts.',
(SELECT id FROM clusters WHERE name = 'CAP13'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'),
100.00, 80.00, 'https://forms.gle/litg', 'https://facebook.com/litg', 'To promote reading and writing.', 'To be a hub for literature.', 'Read. Write. Inspire.');

-- Org 4: CAP13 - College of Liberal Arts
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Philosophy Circle', 'DLSU Philo', 'Exploring philosophical thought.',
(SELECT id FROM clusters WHERE name = 'CAP13'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'),
90.00, 70.00, 'https://forms.gle/philo', 'https://facebook.com/philo', 'To engage in critical thinking.', 'To foster philosophical discourse.', 'Think. Question. Understand.');

-- Org 5: ASPIRE - College of Education
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Education Society', 'DLSU EdSoc', 'Empowering future educators.',
(SELECT id FROM clusters WHERE name = 'ASPIRE'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'), -- Assuming College of Liberal Arts for now, as no specific Education College
110.00, 90.00, 'https://forms.gle/edsoc', 'https://facebook.com/edsoc', 'To develop effective teachers.', 'To lead in educational reform.', 'Educate. Inspire. Transform.');

-- Org 6: PROBE - Ramon V. del Rosario College of Business
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Marketing Association', 'DLSU MA', 'Shaping marketing leaders.',
(SELECT id FROM clusters WHERE name = 'PROBE'),
(SELECT id FROM colleges WHERE name = 'Ramon V. del Rosario College of Business'),
160.00, 130.00, 'https://forms.gle/ma', 'https://facebook.com/ma', 'To hone marketing skills.', 'To be the top marketing org.', 'Market. Innovate. Lead.');

-- Org 7: PROBE - Ramon V. del Rosario College of Business
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Management of Financial Institutions', 'DLSU MFI', 'Mastering financial management.',
(SELECT id FROM clusters WHERE name = 'PROBE'),
(SELECT id FROM colleges WHERE name = 'Ramon V. del Rosario College of Business'),
175.00, 145.00, 'https://forms.gle/mfi', 'https://facebook.com/mfi', 'To excel in finance.', 'To be a financial powerhouse.', 'Manage. Invest. Grow.');

-- Org 8: ASO - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Biology Society', 'DLSU BioSoc', 'Advancing biological sciences.',
(SELECT id FROM clusters WHERE name = 'ASO'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'), -- Assuming for now, as no specific Science College
120.00, 100.00, 'https://forms.gle/biosoc', 'https://facebook.com/biosoc', 'To explore life sciences.', 'To be a center for biology.', 'Discover. Learn. Live.');

-- Org 9: Engage - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Civil Engineering Society', 'DLSU CES', 'Building the future.',
(SELECT id FROM clusters WHERE name = 'Engage'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'),
190.00, 160.00, 'https://forms.gle/ces', 'https://facebook.com/ces', 'To construct sustainable solutions.', 'To be a leader in civil engineering.', 'Build. Design. Sustain.');

-- Org 10: CAP13 - College of Liberal Arts
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU History Society', 'DLSU HistSoc', 'Preserving the past, understanding the present.',
(SELECT id FROM clusters WHERE name = 'CAP13'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'),
95.00, 75.00, 'https://forms.gle/histsoc', 'https://facebook.com/histsoc', 'To study historical events.', 'To enlighten through history.', 'Remember. Learn. Progress.');

-- Org 11: ASPIRE - College of Liberal Arts
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Psychology Society', 'DLSU PsychSoc', 'Understanding the human mind.',
(SELECT id FROM clusters WHERE name = 'ASPIRE'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'),
105.00, 85.00, 'https://forms.gle/psychsoc', 'https://facebook.com/psychsoc', 'To explore psychological concepts.', 'To promote mental well-being.', 'Mind. Explore. Grow.');

-- Org 12: PROBE - Ramon V. del Rosario College of Business
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Accountancy Association', 'DLSU AA', 'Nurturing ethical accountants.',
(SELECT id FROM clusters WHERE name = 'PROBE'),
(SELECT id FROM colleges WHERE name = 'Ramon V. del Rosario College of Business'),
165.00, 135.00, 'https://forms.gle/aa', 'https://facebook.com/aa', 'To develop accounting professionals.', 'To be the premier accountancy org.', 'Count. Audit. Succeed.');

-- Org 13: ASO - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Chemistry Society', 'DLSU ChemSoc', 'Unraveling chemical mysteries.',
(SELECT id FROM clusters WHERE name = 'ASO'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'),
115.00, 95.00, 'https://forms.gle/chemsoc', 'https://facebook.com/chemsoc', 'To advance chemical knowledge.', 'To be a leader in chemistry.', 'React. Discover. Transform.');

-- Org 14: Engage - College of Computer Studies
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Industrial Engineering Society', 'DLSU IES', 'Optimizing systems for efficiency.',
(SELECT id FROM clusters WHERE name = 'Engage'),
(SELECT id FROM colleges WHERE name = 'College of Computer Studies'),
185.00, 155.00, 'https://forms.gle/ies', 'https://facebook.com/ies', 'To improve processes and systems.', 'To be the forefront of IE.', 'Optimize. Innovate. Deliver.');

-- Org 15: CAP13 - College of Liberal Arts
INSERT INTO orgs (name, short_name, about, cluster_id, college_id, fee, bundle_fee, gforms_url, facebook_url, mission, vision, tagline)
VALUES
('DLSU Political Science Society', 'DLSU PolSci', 'Engaging in political discourse.',
(SELECT id FROM clusters WHERE name = 'CAP13'),
(SELECT id FROM colleges WHERE name = 'College of Liberal Arts'),
100.00, 80.00, 'https://forms.gle/polsci', 'https://facebook.com/polsci', 'To analyze political systems.', 'To foster civic engagement.', 'Govern. Debate. Participate.');


-- Publications (corresponding to the 15 organizations)
-- Using subqueries to get org_id dynamically
INSERT INTO org_pubs (org_id, main_pub_url, fee_pub_url, logo_url, sub_logo_url, org_vid_url)
VALUES
((SELECT id FROM orgs WHERE short_name = 'DLSU CoES'), 'https://example.com/coes_main.jpg', 'https://example.com/coes_fee.jpg', 'https://example.com/coes_logo.png', 'https://example.com/coes_sublogo.png', 'https://youtube.com/coes_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU ECE'), 'https://example.com/ece_main.jpg', 'https://example.com/ece_fee.jpg', 'https://example.com/ece_logo.png', 'https://example.com/ece_sublogo.png', 'https://youtube.com/ece_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU LitG'), 'https://example.com/litg_main.jpg', 'https://example.com/litg_fee.jpg', 'https://example.com/litg_logo.png', 'https://example.com/litg_sublogo.png', 'https://youtube.com/litg_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU Philo'), 'https://example.com/philo_main.jpg', 'https://example.com/philo_fee.jpg', 'https://example.com/philo_logo.png', 'https://example.com/philo_sublogo.png', 'https://youtube.com/philo_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU EdSoc'), 'https://example.com/edsoc_main.jpg', 'https://example.com/edsoc_fee.jpg', 'https://example.com/edsoc_logo.png', 'https://example.com/edsoc_sublogo.png', 'https://youtube.com/edsoc_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU MA'), 'https://example.com/ma_main.jpg', 'https://example.com/ma_fee.jpg', 'https://example.com/ma_logo.png', 'https://example.com/ma_sublogo.png', 'https://youtube.com/ma_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU MFI'), 'https://example.com/mfi_main.jpg', 'https://example.com/mfi_fee.jpg', 'https://example.com/mfi_logo.png', 'https://example.com/mfi_sublogo.png', 'https://youtube.com/mfi_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU BioSoc'), 'https://example.com/biosoc_main.jpg', 'https://example.com/biosoc_fee.jpg', 'https://example.com/biosoc_logo.png', 'https://example.com/biosoc_sublogo.png', 'https://youtube.com/biosoc_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU CES'), 'https://example.com/ces_main.jpg', 'https://example.com/ces_fee.jpg', 'https://example.com/ces_logo.png', 'https://example.com/ces_sublogo.png', 'https://youtube.com/ces_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU HistSoc'), 'https://example.com/histsoc_main.jpg', 'https://example.com/histsoc_fee.jpg', 'https://example.com/histsoc_logo.png', 'https://example.com/histsoc_sublogo.png', 'https://youtube.com/histsoc_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU PsychSoc'), 'https://example.com/psychsoc_main.jpg', 'https://example.com/psychsoc_fee.jpg', 'https://example.com/psychsoc_logo.png', 'https://example.com/psychsoc_sublogo.png', 'https://youtube.com/psychsoc_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU AA'), 'https://example.com/aa_main.jpg', 'https://example.com/aa_fee.jpg', 'https://example.com/aa_logo.png', 'https://example.com/aa_sublogo.png', 'https://youtube.com/aa_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU ChemSoc'), 'https://example.com/chemsoc_main.jpg', 'https://example.com/chemsoc_fee.jpg', 'https://example.com/chemsoc_logo.png', 'https://example.com/chemsoc_sublogo.png', 'https://youtube.com/chemsoc_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU IES'), 'https://example.com/ies_main.jpg', 'https://example.com/ies_fee.jpg', 'https://example.com/ies_logo.png', 'https://example.com/ies_sublogo.png', 'https://youtube.com/ies_vid'),
((SELECT id FROM orgs WHERE short_name = 'DLSU PolSci'), 'https://example.com/polsci_main.jpg', 'https://example.com/polsci_fee.jpg', 'https://example.com/polsci_logo.png', 'https://example.com/polsci_sublogo.png', 'https://youtube.com/polsci_vid');