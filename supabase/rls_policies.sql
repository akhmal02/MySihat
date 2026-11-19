-- ====================================================================
-- MySihat Healthcare Application - Row Level Security (RLS) Policies
-- Version: 1.0.0
-- Target: Supabase (PostgreSQL)
-- Description: Enforces strict HIPAA / PDPA (Malaysia) compliant access
--              control across all 4 user roles:
--              1. Patient
--              2. Healthcare Staff
--              3. Administrator
--              4. Caregiver
-- ====================================================================

-- 1. Enable RLS on ALL tables
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.caregiver_links ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.blood_glucose_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.blood_pressure_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.hba1c_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.medications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.medication_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.appointments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.medical_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.healthcare_facilities ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

-- Helper function: Check current user role
CREATE OR REPLACE FUNCTION public.get_current_role()
RETURNS user_role AS $$
  SELECT role FROM public.profiles WHERE id = auth.uid();
$$ LANGUAGE sql SECURITY DEFINER;

-- Helper function: Check if current user is active caregiver for patient
CREATE OR REPLACE FUNCTION public.is_caregiver_for(p_patient_id UUID)
RETURNS BOOLEAN AS $$
  SELECT EXISTS (
    SELECT 1 FROM public.caregiver_links
    WHERE caregiver_id = auth.uid()
      AND patient_id = p_patient_id
      AND status = 'active'
  );
$$ LANGUAGE sql SECURITY DEFINER;

-- ====================================================================
-- PROFILES POLICIES
-- ====================================================================
CREATE POLICY "Users can view own profile"
  ON public.profiles FOR SELECT
  USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
  ON public.profiles FOR UPDATE
  USING (auth.uid() = id);

CREATE POLICY "Healthcare staff & admins can view all patient profiles"
  ON public.profiles FOR SELECT
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view linked patient profiles"
  ON public.profiles FOR SELECT
  USING (is_caregiver_for(id));

-- Trigger to automatically create profile on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.profiles (id, email, full_name, role)
  VALUES (
    new.id,
    new.email,
    COALESCE(new.raw_user_meta_data->>'full_name', 'MySihat User'),
    COALESCE((new.raw_user_meta_data->>'role')::user_role, 'patient')
  );
  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();

-- ====================================================================
-- CAREGIVER LINKS POLICIES
-- ====================================================================
CREATE POLICY "Caregivers and patients can view their own links"
  ON public.caregiver_links FOR SELECT
  USING (auth.uid() = caregiver_id OR auth.uid() = patient_id OR get_current_role() = 'admin');

CREATE POLICY "Patients can create or manage caregiver links"
  ON public.caregiver_links FOR ALL
  USING (auth.uid() = patient_id OR auth.uid() = caregiver_id);

-- ====================================================================
-- BLOOD GLUCOSE RECORDS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own blood glucose"
  ON public.blood_glucose_records FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can view all blood glucose"
  ON public.blood_glucose_records FOR SELECT
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view and add linked patient blood glucose"
  ON public.blood_glucose_records FOR SELECT
  USING (is_caregiver_for(patient_id));

CREATE POLICY "Caregivers can insert linked patient blood glucose"
  ON public.blood_glucose_records FOR INSERT
  WITH CHECK (is_caregiver_for(patient_id));

-- ====================================================================
-- BLOOD PRESSURE RECORDS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own blood pressure"
  ON public.blood_pressure_records FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can view all blood pressure"
  ON public.blood_pressure_records FOR SELECT
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view and add linked patient blood pressure"
  ON public.blood_pressure_records FOR SELECT
  USING (is_caregiver_for(patient_id));

CREATE POLICY "Caregivers can insert linked patient blood pressure"
  ON public.blood_pressure_records FOR INSERT
  WITH CHECK (is_caregiver_for(patient_id));

-- ====================================================================
-- HBA1C RECORDS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own HbA1c"
  ON public.hba1c_records FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can view and insert HbA1c"
  ON public.hba1c_records FOR ALL
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view linked patient HbA1c"
  ON public.hba1c_records FOR SELECT
  USING (is_caregiver_for(patient_id));

-- ====================================================================
-- MEDICATIONS & LOGS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own medications"
  ON public.medications FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can manage all medications"
  ON public.medications FOR ALL
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view and log medication for patient"
  ON public.medications FOR SELECT
  USING (is_caregiver_for(patient_id));

CREATE POLICY "Patients manage own medication logs"
  ON public.medication_logs FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Caregivers can insert medication logs"
  ON public.medication_logs FOR INSERT
  WITH CHECK (is_caregiver_for(patient_id));

-- ====================================================================
-- APPOINTMENTS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own appointments"
  ON public.appointments FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can view and update appointments"
  ON public.appointments FOR ALL
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view linked patient appointments"
  ON public.appointments FOR SELECT
  USING (is_caregiver_for(patient_id));

-- ====================================================================
-- MEDICAL DOCUMENTS POLICIES
-- ====================================================================
CREATE POLICY "Patients manage own documents"
  ON public.medical_documents FOR ALL
  USING (auth.uid() = patient_id);

CREATE POLICY "Staff can view all medical documents"
  ON public.medical_documents FOR SELECT
  USING (get_current_role() IN ('staff', 'admin'));

CREATE POLICY "Caregivers can view linked patient documents"
  ON public.medical_documents FOR SELECT
  USING (is_caregiver_for(patient_id));

-- ====================================================================
-- HEALTHCARE FACILITIES POLICIES
-- ====================================================================
CREATE POLICY "Everyone can read verified facilities"
  ON public.healthcare_facilities FOR SELECT
  TO authenticated, anon
  USING (is_verified = true);

CREATE POLICY "Admins can manage facilities"
  ON public.healthcare_facilities FOR ALL
  USING (get_current_role() = 'admin');

-- ====================================================================
-- AUDIT LOGS POLICIES
-- ====================================================================
CREATE POLICY "Admins can view audit logs"
  ON public.audit_logs FOR SELECT
  USING (get_current_role() = 'admin');

CREATE POLICY "Users can create audit logs"
  ON public.audit_logs FOR INSERT
  WITH CHECK (auth.uid() = user_id);
