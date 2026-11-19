-- ============================================================================
-- MYSIHAT SUPABASE CLOUD SCHEMA (100% FULL CRUD TABLES & RLS POLICIES)
-- Copy & Paste this entire file into your Supabase Dashboard -> SQL Editor -> Run
-- ============================================================================

-- 1. User Profiles Table
CREATE TABLE IF NOT EXISTS public.profiles (
    email TEXT PRIMARY KEY,
    password TEXT NOT NULL DEFAULT '',
    full_name TEXT NOT NULL,
    ic_number VARCHAR(20) DEFAULT '',
    phone_number VARCHAR(25) DEFAULT '',
    age INTEGER DEFAULT 0,
    gender VARCHAR(15) DEFAULT '-',
    condition TEXT DEFAULT '-',
    sihat_id TEXT DEFAULT 'MY-SIHAT-0001',
    blood_type VARCHAR(5) DEFAULT '-',
    height_cm INTEGER DEFAULT 0,
    weight_kg NUMERIC(5,1) DEFAULT 0.0,
    kin_name TEXT DEFAULT '-',
    kin_relation TEXT DEFAULT '-',
    kin_phone TEXT DEFAULT '-',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Blood Glucose Records Table
CREATE TABLE IF NOT EXISTS public.blood_glucose_records (
    id TEXT PRIMARY KEY,
    user_email TEXT NOT NULL,
    glucose_value NUMERIC(5, 2) NOT NULL,
    meal_context TEXT NOT NULL,
    date_label TEXT NOT NULL,
    time_label TEXT NOT NULL,
    timestamp_millis BIGINT NOT NULL DEFAULT 0,
    severity TEXT NOT NULL,
    notes TEXT DEFAULT '',
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Blood Pressure Records Table
CREATE TABLE IF NOT EXISTS public.blood_pressure_records (
    id TEXT PRIMARY KEY,
    user_email TEXT NOT NULL,
    systolic INTEGER NOT NULL,
    diastolic INTEGER NOT NULL,
    pulse_rate INTEGER NOT NULL,
    date_label TEXT NOT NULL,
    time_label TEXT NOT NULL,
    timestamp_millis BIGINT NOT NULL DEFAULT 0,
    category TEXT NOT NULL,
    severity TEXT NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 4. Appointments Table
CREATE TABLE IF NOT EXISTS public.appointments (
    user_email TEXT PRIMARY KEY,
    id TEXT NOT NULL,
    facility_name TEXT NOT NULL,
    clinic_type TEXT NOT NULL,
    session_label TEXT NOT NULL,
    date_display TEXT NOT NULL,
    time_display TEXT NOT NULL,
    reference_number TEXT NOT NULL,
    target_time_millis BIGINT NOT NULL,
    is_checked_in BOOLEAN DEFAULT false,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Enable Row Level Security (RLS) with open policies for the app's publishable key
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.blood_glucose_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.blood_pressure_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.appointments ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow all profiles" ON public.profiles;
CREATE POLICY "Allow all profiles" ON public.profiles FOR ALL USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "Allow all glucose" ON public.blood_glucose_records;
CREATE POLICY "Allow all glucose" ON public.blood_glucose_records FOR ALL USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "Allow all bp" ON public.blood_pressure_records;
CREATE POLICY "Allow all bp" ON public.blood_pressure_records FOR ALL USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "Allow all appointments" ON public.appointments;
CREATE POLICY "Allow all appointments" ON public.appointments FOR ALL USING (true) WITH CHECK (true);
