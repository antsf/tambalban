-- TambalBan - Supabase Database Schema
-- Run this SQL in your Supabase SQL Editor to create the required tables

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- Table: workshops
-- =============================================
CREATE TABLE IF NOT EXISTS workshops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    phone TEXT,
    address TEXT,
    open_time TEXT,
    close_time TEXT,
    is_24h BOOLEAN DEFAULT false,
    rating_avg DOUBLE PRECISION DEFAULT 0.0,
    rating_count INTEGER DEFAULT 0,
    source TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create index for geospatial queries
CREATE INDEX IF NOT EXISTS idx_workshops_location ON workshops (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_workshops_name ON workshops (name);

-- =============================================
-- Table: users_profile
-- =============================================
CREATE TABLE IF NOT EXISTS users_profile (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT,
    email TEXT,
    phone TEXT,
    avatar_url TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- =============================================
-- Table: reviews
-- =============================================
CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workshop_id UUID NOT NULL REFERENCES workshops(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_reviews_workshop ON reviews(workshop_id);

-- =============================================
-- Table: workshop_submissions
-- =============================================
CREATE TABLE IF NOT EXISTS workshop_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_submissions_status ON workshop_submissions(status);

-- =============================================
-- Table: workshop_reports
-- =============================================
CREATE TABLE IF NOT EXISTS workshop_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workshop_id UUID NOT NULL REFERENCES workshops(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    reason TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_reports_workshop ON workshop_reports(workshop_id);

-- =============================================
-- Row Level Security (RLS)
-- =============================================
-- Enable RLS on all tables
ALTER TABLE workshops ENABLE ROW LEVEL SECURITY;
ALTER TABLE users_profile ENABLE ROW LEVEL SECURITY;
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE workshop_submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE workshop_reports ENABLE ROW LEVEL SECURITY;

-- Public read access for workshops
CREATE POLICY "Public can read workshops" ON workshops
    FOR SELECT USING (true);

-- Public read access for reviews
CREATE POLICY "Public can read reviews" ON reviews
    FOR SELECT USING (true);

-- Anyone can submit workshop
CREATE POLICY "Anyone can submit workshop" ON workshop_submissions
    FOR INSERT WITH CHECK (true);

-- Anyone can read submissions
CREATE POLICY "Public can read submissions" ON workshop_submissions
    FOR SELECT USING (true);

-- Anyone can create reports
CREATE POLICY "Anyone can create report" ON workshop_reports
    FOR INSERT WITH CHECK (true);

-- User Profile Policies
CREATE POLICY "Users can view their own profile" ON users_profile
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile" ON users_profile
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Public profiles are viewable by everyone" ON users_profile
    FOR SELECT USING (true);

-- =============================================
-- Sample Data (for testing)
-- =============================================
INSERT INTO workshops (name, latitude, longitude, phone, address, is_24h, rating_avg, rating_count, source)
VALUES
    ('Tambal Ban Jakarta Pusat', -6.1751, 106.8650, '+62 21 1234567', 'Jl. Sudirman No. 1, Jakarta Pusat', true, 4.5, 25, 'manual'),
    ('Tambal Ban Maju Jaya', -6.2088, 106.8456, '+62 21 9876543', 'Jl. Thamrin No. 10, Jakarta Pusat', true, 4.2, 18, 'manual'),
    ('Tambal Ban Sejahtera', -6.9147, 107.6098, '+62 22 5551234', 'Jl. Braga No. 50, Bandung', false, 4.8, 32, 'manual'),
    ('Bengkel Tambal Ban Selamat', -6.9175, 107.6191, '+62 22 6667890', 'Jl. Asia Afrika No. 15, Bandung', true, 4.3, 21, 'manual'),
    ('Tambal Ban 24 Jam', -7.5755, 110.8243, '+62 24 1112223', 'Jl. Ahmad Yani No. 25, Semarang', true, 4.6, 45, 'manual');

-- =============================================
-- Functions for updating rating
-- =============================================
CREATE OR REPLACE FUNCTION update_workshop_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE workshops
    SET
        rating_avg = (
            SELECT COALESCE(AVG(rating), 0)
            FROM reviews
            WHERE workshop_id = NEW.workshop_id
        ),
        rating_count = (
            SELECT COUNT(*)
            FROM reviews
            WHERE workshop_id = NEW.workshop_id
        )
    WHERE id = NEW.workshop_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update workshop rating when a review is added
CREATE TRIGGER trigger_update_rating
    AFTER INSERT OR UPDATE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_workshop_rating();

-- =============================================
-- Auto-create profile on signup
-- =============================================
CREATE OR REPLACE FUNCTION handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.users_profile (id, full_name, email, avatar_url)
    VALUES (
        NEW.id,
        NEW.raw_user_meta_data->>'full_name',
        NEW.email,
        NEW.raw_user_meta_data->>'avatar_url'
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION handle_new_user();

-- Update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER update_users_profile_updated_at
    BEFORE UPDATE ON users_profile
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- Note for Bounding Box Queries
-- =============================================
-- To query workshops within a bounding box:
-- SELECT * FROM workshops
-- WHERE latitude BETWEEN :south AND :north
-- AND longitude BETWEEN :west AND :east
-- LIMIT 200;

-- Example for Jakarta area:
-- SELECT * FROM workshops
-- WHERE latitude BETWEEN -6.3 AND -6.1
-- AND longitude BETWEEN 106.7 AND 107.0
-- LIMIT 200;
