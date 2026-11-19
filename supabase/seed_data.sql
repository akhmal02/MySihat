-- ====================================================================
-- MySihat Healthcare Application - Verified Seed Data & Synthetic Test Data
-- Version: 1.0.0
-- Target: Supabase (PostgreSQL)
-- Description: Verified facilities for Klinik Kesihatan Langkap & Hilir Perak,
--              plus synthetic test records for academic demonstration.
-- ====================================================================

-- 1. Insert Verified Healthcare Facilities
INSERT INTO public.healthcare_facilities (id, name, facility_type, address, phone, operating_hours, services, diabetes_services, latitude, longitude, is_verified)
VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'Klinik Kesihatan Langkap',
    'Klinik Kesihatan',
    'Jalan Chui Chak, 36700 Langkap, Perak Darul Ridzuan',
    '05-659 1233',
    'Isnin - Jumaat: 8:00 AM - 5:00 PM | Sabtu - Ahad: Tutup',
    ARRAY['Pesakit Luar (Outpatient)', 'Kesihatan Ibu & Anak (MCH)', 'Farmasi Klinikal', 'Makmal Diagnostik', 'Kecemasan Ringan'],
    ARRAY['Pusat Sumber Diabetes', 'Pemeriksaan Kaki Diabetik (Foot Care)', 'Saringan Fundus Mata (Eye Screening)', 'Kaunseling Diet & Pemakanan', 'Pemantauan Glukosa & HbA1c'],
    4.0722000,
    101.1542000,
    true
),
(
    '22222222-2222-2222-2222-222222222222',
    'Hospital Teluk Intan',
    'Hospital',
    'Jalan Changkat Jong, 36000 Teluk Intan, Perak Darul Ridzuan',
    '05-621 3333',
    'Kecemasan: 24 Jam | Klinik Pakar: Isnin - Jumaat 8:00 AM - 5:00 PM',
    ARRAY['Klinik Pakar Perubatan', 'Jabatan Kecemasan & Trauma 24/7', 'Nefrologi & Dialisis', 'Oftalmologi', 'Farmasi Hospital'],
    ARRAY['Pakar Endokrinologi & Diabetes', 'Klinik Penjagaan Luka Kronik & Ulser Kaki', 'Dialisis Renal Pesakit Diabetes', 'Laser & Rawatan Retinopati Diabetik'],
    4.0089000,
    101.0347000,
    true
),
(
    '33333333-3333-3333-3333-333333333333',
    'Klinik Desa Pelawan',
    'Klinik Desa',
    'Jalan Pelawan, 36700 Langkap, Perak Darul Ridzuan',
    '05-659 1400',
    'Isnin - Jumaat: 8:00 AM - 4:30 PM',
    ARRAY['Penjagaan Kesihatan Asas', 'Pemeriksaan Tekanan Darah', 'Saringan Kesihatan Komuniti'],
    ARRAY['Ujian Pantas Glukosa Darah', 'Konsultasi Penjagaan Diabetes Asas'],
    4.0934000,
    101.1689000,
    true
),
(
    '44444444-4444-4444-4444-444444444444',
    'Klinik Kesihatan Chikus',
    'Klinik Kesihatan',
    'Jalan Chikus, 36750 Chikus, Perak Darul Ridzuan',
    '05-659 2110',
    'Isnin - Jumaat: 8:00 AM - 5:00 PM',
    ARRAY['Pesakit Luar', 'Kesihatan Komuniti', 'Farmasi Asas'],
    ARRAY['Klinik NCD (Penyakit Tidak Berjangkit)', 'Ujian Glukosa Berkala', 'Rujukan Klinik Kesihatan Langkap'],
    4.0456000,
    101.1923000,
    true
),
(
    '55555555-5555-5555-5555-555555555555',
    'Klinik Kesihatan Chenderong Balai',
    'Klinik Kesihatan',
    'Pekan Chenderong Balai, 36600 Chenderong Balai, Perak',
    '05-656 1222',
    'Isnin - Jumaat: 8:00 AM - 5:00 PM',
    ARRAY['Pesakit Luar', 'Kesihatan Ibu & Anak', 'Farmasi'],
    ARRAY['Klinik Rawatan Diabetes', 'Ujian Makmal HbA1c Berkala'],
    4.1489000,
    101.1045000,
    true
)
ON CONFLICT (id) DO NOTHING;

-- 2. Synthetic Test Accounts (Demonstration Data)
-- Test Patient ID: 'aaaa0001-0000-0000-0000-000000000001'
-- Test Staff ID:   'bbbb0002-0000-0000-0000-000000000002'
-- Test Caregiver:  'cccc0003-0000-0000-0000-000000000003'
-- Test Admin:      'dddd0004-0000-0000-0000-000000000004'

INSERT INTO public.profiles (id, email, full_name, ic_number, phone_number, role, primary_facility_name, date_of_birth, gender)
VALUES
(
    'aaaa0001-0000-0000-0000-000000000001',
    'ahmad.patient@mysihat.my',
    'Ahmad bin Abdullah (Pesakit Demonstrasi)',
    '620514-08-5431',
    '012-588 4321',
    'patient',
    'Klinik Kesihatan Langkap',
    '1962-05-14',
    'Lelaki'
),
(
    'bbbb0002-0000-0000-0000-000000000002',
    'dr.noraini@mysihat.my',
    'Dr. Noraini binti Yusof (Pegawai Perubatan)',
    '850920-08-5678',
    '019-456 7890',
    'staff',
    'Klinik Kesihatan Langkap',
    '1985-09-20',
    'Perempuan'
),
(
    'cccc0003-0000-0000-0000-000000000003',
    'siti.caregiver@mysihat.my',
    'Siti Aminah binti Ahmad (Penjaga)',
    '910312-08-6224',
    '017-345 6789',
    'caregiver',
    'Klinik Kesihatan Langkap',
    '1991-03-12',
    'Perempuan'
),
(
    'dddd0004-0000-0000-0000-000000000004',
    'admin@mysihat.my',
    'Pentadbir Sistem MySihat',
    '880101-08-9999',
    '011-123 4567',
    'admin',
    'Klinik Kesihatan Langkap',
    '1988-01-01',
    'Lelaki'
)
ON CONFLICT (id) DO NOTHING;

-- 3. Caregiver Link: Siti Aminah cares for Ahmad bin Abdullah
INSERT INTO public.caregiver_links (caregiver_id, patient_id, status)
VALUES
('cccc0003-0000-0000-0000-000000000003', 'aaaa0001-0000-0000-0000-000000000001', 'active')
ON CONFLICT (caregiver_id, patient_id) DO NOTHING;

-- 4. Synthetic Blood Glucose Records (Trend showing diurnal pattern)
INSERT INTO public.blood_glucose_records (patient_id, glucose_value, meal_context, recorded_at, notes)
VALUES
('aaaa0001-0000-0000-0000-000000000001', 5.8, 'fasting',   NOW() - INTERVAL '3 days' + INTERVAL '7 hours',  'Bangun pagi, puasa 10 jam. Bacaan baik.'),
('aaaa0001-0000-0000-0000-000000000001', 7.4, 'post_meal', NOW() - INTERVAL '3 days' + INTERVAL '14 hours', '2 jam selepas makan tengah hari (nasi separuh, ikan bakar).'),
('aaaa0001-0000-0000-0000-000000000001', 6.2, 'bedtime',   NOW() - INTERVAL '3 days' + INTERVAL '22 hours', 'Sebelum tidur.'),
('aaaa0001-0000-0000-0000-000000000001', 6.0, 'fasting',   NOW() - INTERVAL '2 days' + INTERVAL '7 hours',  'Pagi sebelum sarapan.'),
('aaaa0001-0000-0000-0000-000000000001', 8.1, 'post_meal', NOW() - INTERVAL '2 days' + INTERVAL '14 hours', 'Selepas makan mi sup.'),
('aaaa0001-0000-0000-0000-000000000001', 6.5, 'bedtime',   NOW() - INTERVAL '2 days' + INTERVAL '22 hours', 'Malam tenang.'),
('aaaa0001-0000-0000-0000-000000000001', 5.6, 'fasting',   NOW() - INTERVAL '1 days' + INTERVAL '7 hours',  'Puasa pagi. Terasa cergas.'),
('aaaa0001-0000-0000-0000-000000000001', 7.2, 'post_meal', NOW() - INTERVAL '1 days' + INTERVAL '14 hours', 'Selepas makan siang.'),
('aaaa0001-0000-0000-0000-000000000001', 5.9, 'fasting',   NOW() - INTERVAL '2 hours',                      'Ujian glukosa pagi ini.');

-- 5. Synthetic Blood Pressure Records
INSERT INTO public.blood_pressure_records (patient_id, systolic, diastolic, pulse_rate, recorded_at, notes)
VALUES
('aaaa0001-0000-0000-0000-000000000001', 124, 78, 72, NOW() - INTERVAL '3 days' + INTERVAL '8 hours', 'Tekanan darah normal selepas rehat 10 minit.'),
('aaaa0001-0000-0000-0000-000000000001', 128, 82, 75, NOW() - INTERVAL '2 days' + INTERVAL '8 hours', 'Pagi, bacaan stabil.'),
('aaaa0001-0000-0000-0000-000000000001', 122, 79, 70, NOW() - INTERVAL '1 days' + INTERVAL '8 hours', 'Bacaan pra-sarapan.'),
('aaaa0001-0000-0000-0000-000000000001', 125, 80, 71, NOW() - INTERVAL '1 hours',                     'Pagi ini di rumah.');

-- 6. Synthetic HbA1c Records (Demonstrates positive clinical progression)
INSERT INTO public.hba1c_records (patient_id, hba1c_percentage, lab_facility, recorded_at, notes)
VALUES
('aaaa0001-0000-0000-0000-000000000001', 7.8, 'Makmal Klinik Kesihatan Langkap', CURRENT_DATE - INTERVAL '6 months', 'Saringan awal, rawatan dos metformin ditingkatkan.'),
('aaaa0001-0000-0000-0000-000000000001', 7.1, 'Makmal Klinik Kesihatan Langkap', CURRENT_DATE - INTERVAL '3 months', 'Penambahbaikan gaya hidup dan kawalan diet.'),
('aaaa0001-0000-0000-0000-000000000001', 6.6, 'Makmal Klinik Kesihatan Langkap', CURRENT_DATE - INTERVAL '2 weeks',  'Kawalan diabetes sangat memuaskan (sasaran < 7.0% dicapai).');

-- 7. Synthetic Medications
INSERT INTO public.medications (id, patient_id, name, dosage, frequency, instructions, is_insulin, insulin_type, stock_count, is_active)
VALUES
(
    'm001-0000-0000-0000-000000000001',
    'aaaa0001-0000-0000-0000-000000000001',
    'Metformin HCl',
    '500 mg',
    '2 kali sehari',
    'Ambil selepas makan sarapan dan makan malam',
    false,
    null,
    45,
    true
),
(
    'm002-0000-0000-0000-000000000002',
    'aaaa0001-0000-0000-0000-000000000001',
    'Gliclazide Modified Release',
    '60 mg',
    '1 kali sehari',
    'Ambil waktu pagi bersama sarapan',
    false,
    null,
    20,
    true
),
(
    'm003-0000-0000-0000-000000000003',
    'aaaa0001-0000-0000-0000-000000000001',
    'Insulin Glargine (Basal)',
    '14 Unit',
    '1 kali sehari pada waktu malam',
    'Suntik secara subkutan pada bahagian perut sebelum tidur',
    true,
    'Basal',
    15,
    true
)
ON CONFLICT (id) DO NOTHING;

-- 8. Synthetic Appointments
INSERT INTO public.appointments (patient_id, facility_name, doctor_name, appointment_date, appointment_time, purpose, status, reminder_minutes_before, notes)
VALUES
(
    'aaaa0001-0000-0000-0000-000000000001',
    'Klinik Kesihatan Langkap',
    'Dr. Noraini binti Yusof',
    CURRENT_DATE + INTERVAL '5 days',
    '09:30:00',
    'Pemeriksaan Susulan Diabetes & Ujian Makmal',
    'scheduled',
    1440,
    'Bawa buku rekod glukosa dan kad pengenalan. Berpuasa dari jam 12 tengah malam.'
),
(
    'aaaa0001-0000-0000-0000-000000000001',
    'Klinik Kesihatan Langkap',
    'Jururawat Kesihatan Awam',
    CURRENT_DATE + INTERVAL '12 days',
    '11:00:00',
    'Pemeriksaan Kaki Diabetik & Saringan Fundus',
    'scheduled',
    1440,
    'Klinik Penjagaan Kaki Diabetik KK Langkap.'
),
(
    'aaaa0001-0000-0000-0000-000000000001',
    'Hospital Teluk Intan',
    'Dr. Lim (Pakar Endokrin)',
    CURRENT_DATE - INTERVAL '40 days',
    '10:00:00',
    'Konsultasi Pakar Rujukan NCD',
    'completed',
    1440,
    'Keadaan terkawal. Diteruskan rawatan di KK Langkap.'
);
