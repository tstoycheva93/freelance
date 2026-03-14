-- =============================================================================
-- Sample Offers Data
-- =============================================================================
-- Run postings.sql first
-- =============================================================================

INSERT INTO offer (
    id,
    created_at,
    updated_at,
    activity_status,
    posting_id,
    freelancer_id,
    price,
    status,
    delivery_time,
    message
) VALUES
-- Offer 1: Freelancer 1 offers on Posting 1 (Senior React Developer)
(UNHEX(REPLACE('o1111111-1111-1111-1111-111111111111', '-', '')),
 NOW(), NOW(), 'ACTIVE',
 UNHEX(REPLACE('2ce3d155-7333-4f67-8295-96f6d8d900be', '-', '')),  -- Posting 1
 UNHEX(REPLACE('1ebc4c45-c1b5-48ca-998e-ada6bb897a3a', '-', '')),  -- Freelancer 1
 120000,
 'PENDING',
 '2026-04-01',
 'Hi! I am very interested in this project. With my 6 years of React experience, I can deliver high-quality work within your timeline.'
),

-- Offer 2: Freelancer 2 offers on Posting 1 (Senior React Developer)
(UNHEX(REPLACE('o2222222-2222-2222-2222-222222222222', '-', '')),
 NOW(), NOW(), 'ACTIVE',
 UNHEX(REPLACE('d5ed0e08-7791-497c-83d2-698f5137e2c9', '-', '')),  -- Posting 1
 UNHEX(REPLACE('1ebc4c45-c1b5-48ca-998e-ada6bb897a3a', '-', '')),  -- Freelancer 2
 135000,
 'PENDING',
 '2026-04-05',
 'I would love to work on this project. I have extensive experience with TypeScript and Redux.'
),

-- Offer 3: Freelancer 1 offers on Posting 2 (UI/UX Designer)
(UNHEX(REPLACE('o3333333-3333-3333-3333-333333333333', '-', '')),
 NOW(), NOW(), 'ACTIVE',
 UNHEX(REPLACE('8f66136f-c4f2-499c-9b89-ca519057ec11', '-', '')),  -- Posting 2
 UNHEX(REPLACE('e14f47e7-448c-400e-83f9-9b4a3788f264', '-', '')),  -- Freelancer 1
 75000,
 'SHORTLISTED',
 '2026-04-20',
 'Your project sounds exciting! I have designed similar mobile apps before and can provide a great UI/UX.'
),

-- Offer 4: Freelancer 2 offers on Posting 3 (Full Stack Developer)
(UNHEX(REPLACE('o4444444-4444-4444-4444-444444444444', '-', '')),
 NOW(), NOW(), 'ACTIVE',
 UNHEX(REPLACE('c9ad9907-f5bd-4538-aa9f-686400d91a27', '-', '')),  -- Posting 3
 UNHEX(REPLACE('aa82707c-7f09-4794-a2b4-c74edfd62b46', '-', '')),  -- Freelancer 2
 110000,
 'PENDING',
 '2026-04-25',
 'I have built several full-stack applications using Node.js and React. I can start immediately.'
),

-- Offer 5: Freelancer 3 offers on Posting 1 (Senior React Developer)
(UNHEX(REPLACE('o5555555-5555-5555-5555-555555555555', '-', '')),
 NOW(), NOW(), 'ACTIVE',
 UNHEX(REPLACE('8f66136f-c4f2-499c-9b89-ca519057ec11', '-', '')),  -- Posting 1
 UNHEX(REPLACE('aa82707c-7f09-4794-a2b4-c74edfd62b46', '-', '')),  -- Freelancer 3
 140000,
 'ACCEPTED',
 '2026-03-28',
 'I am very excited about this opportunity! I have 8 years of experience and have worked on similar fintech applications.'
);

-- =============================================================================
-- Offer Attachments (Collection Table)
-- =============================================================================

INSERT INTO offers_attachments (offer_id, attachment_url) VALUES
-- Attachments for Offer 1
(UNHEX(REPLACE('o1111111-1111-1111-1111-111111111111', '-', '')), 'https://example.com/offers/offer1-portfolio.pdf'),
(UNHEX(REPLACE('o1111111-1111-1111-1111-111111111111', '-', '')), 'https://example.com/offers/offer1-resume.pdf'),

-- Attachments for Offer 2
(UNHEX(REPLACE('o2222222-2222-2222-2222-222222222222', '-', '')), 'https://example.com/offers/offer2-project-links.pdf'),

-- Attachments for Offer 3
(UNHEX(REPLACE('o3333333-3333-3333-3333-333333333333', '-', '')), 'https://example.com/offers/offer3-design-samples.pdf'),

-- Attachments for Offer 4
(UNHEX(REPLACE('o4444444-4444-4444-4444-444444444444', '-', '')), 'https://example.com/offers/offer4-github.pdf');
