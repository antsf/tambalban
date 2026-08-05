-- TambalBan - Supabase Database Schema (REFERENCE)
-- Canonical schema shared by the Android app (`tambalban/`) and the web app
-- (`tambalban-web/`). Run this SQL in your Supabase SQL Editor to create the
-- required tables on a FRESH project.
--
-- NOTE: the live project's `tambal_ban` table has more columns than this file's
-- original version — this file is the source of truth for NEW setups. The live
-- project was migrated incrementally (see tambalban-web/supabase/migrations/).
--
-- One product, two front doors, one source of truth: `tambal_ban`.

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- Table: tambal_ban  (the ONE workshop table)
-- =============================================
-- Both apps read/write this table. Visibility is controlled by `verified`:
--   - verified = true   -> shown on the public map (both apps)
--   - verified = false  -> hidden; only its owner (user_id) can see it
-- `source` records provenance: 'osm' (OSM scraper) or 'user' (manual submit).
CREATE TABLE tambal_ban (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    address TEXT,
    city TEXT,
    district TEXT,
    province TEXT,
    phone TEXT,
    whatsapp TEXT,
    website TEXT,
    instagram TEXT,
    opening_hours TEXT,
    rating DOUBLE PRECISION DEFAULT 0.0,
    total_reviews INTEGER DEFAULT 0,
    image_url TEXT,
    source TEXT DEFAULT 'osm',
    verified BOOLEAN DEFAULT false,
    verified_at TIMESTAMPTZ,
    user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    osm_id BIGINT,
    osm_tags JSONB,
    motorcycle_tyres BOOLEAN NOT NULL DEFAULT false,
    car_tyres BOOLEAN NOT NULL DEFAULT false,
    truck_tyres BOOLEAN NOT NULL DEFAULT false,
    tubeless_repair BOOLEAN NOT NULL DEFAULT false,
    vulcanizer BOOLEAN NOT NULL DEFAULT false,
    balancing BOOLEAN NOT NULL DEFAULT false,
    spooring BOOLEAN NOT NULL DEFAULT false,
    roadside_service BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_tambal_ban_location ON tambal_ban (lat, lon);
CREATE INDEX idx_tambal_ban_name ON tambal_ban (name);
CREATE INDEX idx_tambal_ban_source ON tambal_ban (source);
CREATE INDEX idx_tambal_ban_user ON tambal_ban (user_id);
CREATE UNIQUE INDEX idx_tambal_ban_osm_id ON tambal_ban (osm_id) WHERE osm_id IS NOT NULL;

-- =============================================
-- Table: reviews
-- =============================================
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workshop_id UUID REFERENCES tambal_ban(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_reviews_workshop ON reviews (workshop_id);

-- =============================================
-- Table: users_profile
-- =============================================
CREATE TABLE users_profile (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT,
    full_name TEXT,
    email TEXT,
    phone TEXT,
    avatar_url TEXT,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- Row Level Security (RLS)
-- =============================================
ALTER TABLE tambal_ban ENABLE ROW LEVEL SECURITY;
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE users_profile ENABLE ROW LEVEL SECURITY;

-- ---- tambal_ban ----
-- Public can read verified rows only.
CREATE POLICY public_read_verified ON tambal_ban
    FOR SELECT USING (verified);

-- Authenticated users can submit; the row must belong to them (the
-- set_tambal_ban_user_id trigger fills user_id from the JWT automatically).
CREATE POLICY user_insert ON tambal_ban
    FOR INSERT WITH CHECK (auth.role() = 'authenticated' AND user_id = auth.uid());

-- A user can see their own unverified submissions (verified rows are covered by
-- public_read_verified). This is per-user — not "any authenticated user".
CREATE POLICY user_read_own_unverified ON tambal_ban
    FOR SELECT USING (verified OR (source = 'user' AND user_id = auth.uid()));

-- Admin review = UPDATE tambal_ban SET verified = true (service_role key or
-- SQL editor). No dedicated admin policy — the service role bypasses RLS.

-- ---- reviews ----
CREATE POLICY public_read_reviews ON reviews
    FOR SELECT USING (true);

CREATE POLICY user_insert_review ON reviews
    FOR INSERT WITH CHECK (auth.role() = 'authenticated' AND user_id = auth.uid());

CREATE POLICY user_update_own_review ON reviews
    FOR UPDATE USING (auth.role() = 'authenticated' AND user_id = auth.uid());

-- NOTE: live project has admin_delete_review (DELETE for any authenticated
-- user). Intent is admin-only; do NOT ship that as-is on a fresh setup without
-- a real admin gate (e.g. service_role key / app-layer check).
CREATE POLICY admin_delete_review ON reviews
    FOR DELETE USING (auth.role() = 'authenticated');

-- ---- users_profile ----
CREATE POLICY "Users can view their own profile" ON users_profile
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile" ON users_profile
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Public profiles are viewable by everyone" ON users_profile
    FOR SELECT USING (true);

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
    )
    ON CONFLICT (id) DO NOTHING; -- avoid errors if the profile already exists
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION handle_new_user();

-- =============================================
-- updated_at triggers
-- =============================================
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

CREATE OR REPLACE FUNCTION set_tambal_ban_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER update_tambal_ban_updated_at
    BEFORE UPDATE ON tambal_ban
    FOR EACH ROW EXECUTE FUNCTION set_tambal_ban_updated_at();

-- =============================================
-- Stamp the submitter on insert (tambal_ban)
-- =============================================
CREATE OR REPLACE FUNCTION set_tambal_ban_user_id()
RETURNS TRIGGER AS $$
BEGIN
    NEW.user_id = COALESCE(NEW.user_id, auth.uid());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER set_tambal_ban_user_id
    BEFORE INSERT ON tambal_ban
    FOR EACH ROW EXECUTE FUNCTION set_tambal_ban_user_id();

-- =============================================
-- Storage
-- =============================================
-- Public bucket `workshops` for workshop images. Created via dashboard or:
--   INSERT INTO storage.buckets (id, name, public) VALUES ('workshops', 'workshops', true);
-- Path convention: `{userId}/{uuid}.jpg`; store the public CDN URL in image_url.
-- User avatars live in a separate bucket (see Android auth feature).

-- =============================================
-- Bounding box queries
-- =============================================
-- SELECT * FROM tambal_ban
-- WHERE verified
--   AND lat BETWEEN :south AND :north
--   AND lon BETWEEN :west AND :east
-- ORDER BY rating DESC
-- LIMIT 200;
