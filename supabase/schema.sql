-- ====================================================================
-- MySihat Healthcare Application - PostgreSQL Database Schema
-- Version: 1.0.0
-- Target: Supabase (PostgreSQL 15+)
-- Description: Comprehensive database schema supporting 4 user roles,
--              diabetes monitoring, medical documents, appointments,
--              and Malaysian healthcare directory.
-- ====================================================================

-- 1. Custom Types & Enums
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('patient', 'staff', 'admin', 'caregiver');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE meal_context AS ENUM ('fasting', 'post_meal', 'bedtime', 'random');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE appointment_status AS ENUM ('scheduled', 'completed', 'cancelled', 'rescheduled');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE document_type AS ENUM ('prescription', 'lab_report', 'clinical_summary', 'other');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE caregiver_status AS ENUM ('pending', 'active', 'revoked');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 2. User Profiles Table (extends auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    ic_number VARCHAR(14), -- Malaysian IC e.g. 750101-08-5555
    phone_number VARCHAR(20),
    role user_role NOT NULL DEFAULT 'patient',
    primary_facility_name TEXT DEFAULT 'Klinik Kesihatan Langkap',
    date_of_birth DATE,
    gender VARCHAR(10),
    consent_given BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 3. Caregiver Links Table
CREATE TABLE IF NOT EXISTS public.caregiver_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caregiver_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    status caregiver_status NOT NULL DEFAULT 'active',
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    CONSTRAINT unique_caregiver_patient UNIQUE (caregiver_id, patient_id)
);

-- 4. Blood Glucose Records Table
CREATE TABLE IF NOT EXISTS public.blood_glucose_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    glucose_value NUMERIC(4, 1) NOT NULL, -- in mmol/L (Malaysian standard)
    meal_context meal_context NOT NULL DEFAULT 'fasting',
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 5. Blood Pressure Records Table
CREATE TABLE IF NOT EXISTS public.blood_pressure_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    systolic INTEGER NOT NULL, -- mmHg
    diastolic INTEGER NOT NULL, -- mmHg
    pulse_rate INTEGER NOT NULL, -- bpm
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 6. HbA1c Records Table (Excludes cholesterol as required)
CREATE TABLE IF NOT EXISTS public.hba1c_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    hba1c_percentage NUMERIC(3, 1) NOT NULL, -- e.g. 6.8%
    lab_facility TEXT NOT NULL DEFAULT 'Makmal Klinik Kesihatan Langkap',
    recorded_at DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 7. Medications & Insulin Table
CREATE TABLE IF NOT EXISTS public.medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    dosage TEXT NOT NULL, -- e.g. "500 mg" or "10 IU"
    frequency TEXT NOT NULL, -- e.g. "Twice a day", "Once at bedtime"
    instructions TEXT, -- e.g. "Take after food"
    is_insulin BOOLEAN NOT NULL DEFAULT false,
    insulin_type TEXT, -- 'Basal', 'Bolus', 'Premixed' if insulin
    stock_count INTEGER DEFAULT 30,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 8. Medication Logs (Adherence Tracking)
CREATE TABLE IF NOT EXISTS public.medication_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medication_id UUID NOT NULL REFERENCES public.medications(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    scheduled_time TIMESTAMPTZ NOT NULL,
    taken_time TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'taken', -- 'taken', 'skipped', 'missed'
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 9. Appointments Table
CREATE TABLE IF NOT EXISTS public.appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    facility_name TEXT NOT NULL DEFAULT 'Klinik Kesihatan Langkap',
    doctor_name TEXT,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    purpose TEXT NOT NULL, -- e.g. "Pemeriksaan Diabetes", "Fundus Eye Screening", "Diabetic Foot Care"
    status appointment_status NOT NULL DEFAULT 'scheduled',
    reminder_minutes_before INTEGER DEFAULT 1440, -- 1440 mins = 24 hours
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 10. Medical Documents Table (Google ML Kit OCR text stored)
CREATE TABLE IF NOT EXISTS public.medical_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    document_type document_type NOT NULL DEFAULT 'prescription',
    file_path TEXT NOT NULL,
    file_url TEXT,
    ocr_extracted_text TEXT,
    extracted_data JSONB, -- structured fields parsed from OCR
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 11. Healthcare Facilities Directory (Verified Langkap & Nearby Hilir Perak)
CREATE TABLE IF NOT EXISTS public.healthcare_facilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    facility_type TEXT NOT NULL, -- 'Klinik Kesihatan', 'Hospital', 'Klinik Desa'
    address TEXT NOT NULL,
    phone TEXT NOT NULL,
    operating_hours TEXT NOT NULL,
    services TEXT[] NOT NULL DEFAULT '{}',
    diabetes_services TEXT[] NOT NULL DEFAULT '{}',
    latitude NUMERIC(10, 7) NOT NULL,
    longitude NUMERIC(10, 7) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 12. Security Audit Logs Table
CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    action TEXT NOT NULL, -- e.g. 'LOGIN', 'RECORD_CREATE', 'RECORD_DELETE', 'OCR_UPLOAD'
    table_name TEXT,
    record_id TEXT,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 13. Create Indexes for High Performance Queries
CREATE INDEX IF NOT EXISTS idx_glucose_patient_date ON public.blood_glucose_records(patient_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_bp_patient_date ON public.blood_pressure_records(patient_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_hba1c_patient_date ON public.hba1c_records(patient_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_meds_patient ON public.medications(patient_id) WHERE is_active = true;
CREATE INDEX IF NOT EXISTS idx_appointments_patient_date ON public.appointments(patient_id, appointment_date ASC);
CREATE INDEX IF NOT EXISTS idx_documents_patient ON public.medical_documents(patient_id, uploaded_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_user_date ON public.audit_logs(user_id, created_at DESC);
