package com.example.mysihat.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object MockDataRepository {

    // ==================== OFFICIAL MALAYSIAN HEALTHCARE CLINICAL SERVICES ====================
    // Standard Official KKM Specialist Hospital Departments (Rujuk Direktori Hospital Awam KKM)
    val officialHospitalServices = listOf(
        ClinicService("Jabatan Pesakit Luar (Outpatient Department - OPD)", "🩺", "Klinik perubatan am pesakit luar, semakan susulan berkala & saringan"),
        ClinicService("Jabatan Ortopedik (Orthopaedics)", "🦴", "Pembedahan tulang, sendi, trauma fraktur & rekonstruksi ulser kaki diabetik"),
        ClinicService("Jabatan Oftalmologi (Ophthalmology)", "👁️", "Rawatan mata, katarak, glaukoma, vitreoretina & laser retinopati diabetik"),
        ClinicService("Jabatan Perubatan & Endokrinologi", "🩸", "Pakar rujukan kencing manis, terapi insulin intensif, tiroid & metabolik"),
        ClinicService("Jabatan Nefrologi & Hemodialisis", "🫘", "Unit hemodialisis pesakit kencing manis kegagalan ginjal kronik (CKD)"),
        ClinicService("Jabatan Kardiologi & Vaskular", "❤️", "Penyiasatan fungsi kardiovaskular, ECG, ekokardiogram (ECHO) & hipertensi"),
        ClinicService("Jabatan Pembedahan Am (General Surgery)", "🏥", "Pembedahan am, surgeri laparoskopik, biopsi dan rawatan tisu lembut"),
        ClinicService("Jabatan Kecemasan & Trauma (A&E)", "🚨", "Pusat rawatan kecemasan resusitasi, strok, kemalangan & trauma 24 jam"),
        ClinicService("Jabatan Farmasi Pesakit Luar & SPUB", "💊", "Pengambilan ubat berkala bulanan, farmasi pandu lalu & Ubat Melalui Pos"),
        ClinicService("Jabatan Radiologi & Pengimejan Diagnostik", "🔬", "Sinar-X digital, imbasan CT scan, MRI & pengimejan ultrasound")
    )

    // Standard Official KKM Klinik Kesihatan Services (Rujuk Direktori Klinik Kesihatan Awam KKM)
    val officialClinicServices = listOf(
        ClinicService("Klinik Pesakit Luar (Outpatient Care - OPD)", "🩺", "Rawatan pesakit luar penyakit akut, demam, batuk & pemeriksaan am"),
        ClinicService("Klinik Rawatan Diabetes & NCD", "🩸", "Pemantauan glukosa darah harian, tekanan darah & saringan HbA1c berjadual"),
        ClinicService("Klinik Penjagaan Kaki Diabetik (Diabetic Foot Care)", "🦶", "Ujian deria sentuhan monofilamen 10g, balutan luka & pencegahan ulser"),
        ClinicService("Saringan Oftalmologi Retinopati (Kamera Fundus)", "👁️", "Imbasan foto fundus retina mata pesakit diabetes bagi rujukan awal pakar"),
        ClinicService("Klinik Kesihatan Ibu & Anak (MCH)", "👶", "Penjagaan antenatal, saringan diabetes gestasi (GDM) & imunisasi bayi"),
        ClinicService("Klinik Pergigian Komuniti (Dental)", "🦷", "Pemeriksaan oral pesakit diabetes, rawatan periodontik & pembersihan karang"),
        ClinicService("Perkhidmatan Dietetik & Kaunseling Pemakanan", "🥗", "Kaunseling nutrisi oleh Pegawai Dietetik bagi pelan makan sihat & kalori"),
        ClinicService("Farmasi Pesakit Luar Komuniti", "💊", "Pembekalan ubat bulanan berjadual, jarum pen insulin & kaunseling ubat"),
        ClinicService("Makmal Diagnostik Asas", "🔬", "Ujian glukosa darah kapilari pantas, profil lipid & analisis air kencing UFEME")
    )

    // National Database: Government Hospitals & Health Clinics Across ALL Malaysian States & Federal Territories
    val clinicFacilities = mutableStateListOf(
        // ==================== PERAK ====================
        ClinicFacility(
            id = "FAC-HOSP-TAPAH",
            name = "Hospital Tapah",
            type = FacilityType.HOSPITAL,
            state = "Perak",
            address = "Jalan Hospital, 35000 Tapah, Perak",
            latitude = 4.1950,
            longitude = 101.2580,
            distanceKm = 5.2,
            currentQueueNumber = "TP-1022",
            waitTimeMins = 15,
            phoneNumber = "+60 5-401 1333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-BIDOR",
            name = "Klinik Kesihatan Bidor",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perak",
            address = "Jalan Persatuan, 35500 Bidor, Perak",
            latitude = 4.1120,
            longitude = 101.2880,
            distanceKm = 10.4,
            currentQueueNumber = "BD-3310",
            waitTimeMins = 12,
            phoneNumber = "+60 5-434 1333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-TAPAH",
            name = "Klinik Kesihatan Tapah",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perak",
            address = "Jalan Bidor Lama, 35000 Tapah, Perak",
            latitude = 4.1880,
            longitude = 101.2610,
            distanceKm = 4.8,
            currentQueueNumber = "TP-4011",
            waitTimeMins = 14,
            phoneNumber = "+60 5-401 1222",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-TELUKINTAN",
            name = "Hospital Teluk Intan",
            type = FacilityType.HOSPITAL,
            state = "Perak",
            address = "Jalan Changkat Jong, 36000 Teluk Intan, Perak",
            latitude = 3.9980,
            longitude = 101.0380,
            distanceKm = 29.8,
            currentQueueNumber = "TI-5520",
            waitTimeMins = 25,
            phoneNumber = "+60 5-621 2222",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-IPOH",
            name = "Hospital Raja Permaisuri Bainun (HRPB Ipoh)",
            type = FacilityType.HOSPITAL,
            state = "Perak",
            address = "Jalan Raja Ashman Shah, 30450 Ipoh, Perak",
            latitude = 4.6038,
            longitude = 101.0903,
            distanceKm = 52.0,
            currentQueueNumber = "IP-8841",
            waitTimeMins = 32,
            phoneNumber = "+60 5-208 5000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-GREENTOWN",
            name = "Klinik Kesihatan Greentown Ipoh",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perak",
            address = "Jalan Sultan Abdul Jalil, Greentown, 30450 Ipoh, Perak",
            latitude = 4.5990,
            longitude = 101.0950,
            distanceKm = 51.5,
            currentQueueNumber = "GT-2104",
            waitTimeMins = 18,
            phoneNumber = "+60 5-242 6333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-TAIPING",
            name = "Hospital Taiping",
            type = FacilityType.HOSPITAL,
            state = "Perak",
            address = "Jalan Taming Sari, 34000 Taiping, Perak",
            latitude = 4.8550,
            longitude = 100.7410,
            distanceKm = 94.0,
            currentQueueNumber = "TP-7712",
            waitTimeMins = 28,
            phoneNumber = "+60 5-820 4000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-MANJUNG",
            name = "Hospital Seri Manjung",
            type = FacilityType.HOSPITAL,
            state = "Perak",
            address = "Jalan Dato Yu Neh Huat, 32040 Seri Manjung, Perak",
            latitude = 4.1980,
            longitude = 100.6650,
            distanceKm = 72.0,
            currentQueueNumber = "MJ-3390",
            waitTimeMins = 22,
            phoneNumber = "+60 5-689 6600",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KAMPAR",
            name = "Klinik Kesihatan Kampar",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perak",
            address = "Jalan Hospital, 31900 Kampar, Perak",
            latitude = 4.3120,
            longitude = 101.1550,
            distanceKm = 18.0,
            currentQueueNumber = "KP-1188",
            waitTimeMins = 10,
            phoneNumber = "+60 5-465 1333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== KEDAH ====================
        ClinicFacility(
            id = "FAC-HOSP-HSAH",
            name = "Hospital Sultan Abdul Halim (HSAH Sungai Petani)",
            type = FacilityType.HOSPITAL,
            state = "Kedah",
            address = "Jalan Lencongan Timur, Bandar Amanjaya, 08000 Sungai Petani, Kedah",
            latitude = 5.6668,
            longitude = 100.5228,
            distanceKm = 175.0,
            currentQueueNumber = "SP-4410",
            waitTimeMins = 30,
            phoneNumber = "+60 4-445 7333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-BANDARSP",
            name = "Klinik Kesihatan Bandar Sungai Petani",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kedah",
            address = "Jalan Sekerat, Bandar Sungai Petani, 08000 Sungai Petani, Kedah",
            latitude = 5.6432,
            longitude = 100.4895,
            distanceKm = 172.0,
            currentQueueNumber = "SP-1192",
            waitTimeMins = 16,
            phoneNumber = "+60 4-421 2333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-BAKARARANG",
            name = "Klinik Kesihatan Bakar Arang Sungai Petani",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kedah",
            address = "Kawasan Perusahaan Bakar Arang, 08000 Sungai Petani, Kedah",
            latitude = 5.6280,
            longitude = 100.4810,
            distanceKm = 170.0,
            currentQueueNumber = "BA-2055",
            waitTimeMins = 14,
            phoneNumber = "+60 4-423 4555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-HSB",
            name = "Hospital Sultanah Bahiyah (HSB Alor Setar)",
            type = FacilityType.HOSPITAL,
            state = "Kedah",
            address = "KM 6, Jalan Langgar, 05460 Alor Setar, Kedah",
            latitude = 6.1492,
            longitude = 100.4075,
            distanceKm = 228.0,
            currentQueueNumber = "AS-9921",
            waitTimeMins = 35,
            phoneNumber = "+60 4-740 6000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-ALORSETAR",
            name = "Klinik Kesihatan Bandar Alor Setar",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kedah",
            address = "Jalan Putra, Bandar Alor Setar, 05100 Alor Setar, Kedah",
            latitude = 6.1220,
            longitude = 100.3650,
            distanceKm = 225.0,
            currentQueueNumber = "AS-3320",
            waitTimeMins = 15,
            phoneNumber = "+60 4-733 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-KULIM",
            name = "Hospital Kulim",
            type = FacilityType.HOSPITAL,
            state = "Kedah",
            address = "Jalan Hospital, 09000 Kulim, Kedah",
            latitude = 5.3710,
            longitude = 100.5610,
            distanceKm = 142.0,
            currentQueueNumber = "KM-4422",
            waitTimeMins = 22,
            phoneNumber = "+60 4-490 3333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KULIM",
            name = "Klinik Kesihatan Kulim",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kedah",
            address = "Jalan Tunku Putra, 09000 Kulim, Kedah",
            latitude = 5.3650,
            longitude = 100.5520,
            distanceKm = 141.0,
            currentQueueNumber = "KM-1120",
            waitTimeMins = 12,
            phoneNumber = "+60 4-490 5555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== PULAU PINANG ====================
        ClinicFacility(
            id = "FAC-HOSP-PENANG",
            name = "Hospital Pulau Pinang (HPP Georgetown)",
            type = FacilityType.HOSPITAL,
            state = "Pulau Pinang",
            address = "Jalan Residensi, 10990 George Town, Pulau Pinang",
            latitude = 5.4172,
            longitude = 100.3113,
            distanceKm = 148.0,
            currentQueueNumber = "PP-8811",
            waitTimeMins = 34,
            phoneNumber = "+60 4-222 5333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SEBERANGJAYA",
            name = "Hospital Seberang Jaya",
            type = FacilityType.HOSPITAL,
            state = "Pulau Pinang",
            address = "Jalan Tun Hussein Onn, 13700 Seberang Jaya, Pulau Pinang",
            latitude = 5.3942,
            longitude = 100.4078,
            distanceKm = 142.0,
            currentQueueNumber = "SJ-4402",
            waitTimeMins = 28,
            phoneNumber = "+60 4-387 7777",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-JLNPERAK",
            name = "Klinik Kesihatan Jalan Perak Georgetown",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Pulau Pinang",
            address = "Jalan Perak, 10150 George Town, Pulau Pinang",
            latitude = 5.4050,
            longitude = 100.3150,
            distanceKm = 147.0,
            currentQueueNumber = "JP-1240",
            waitTimeMins = 16,
            phoneNumber = "+60 4-282 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-BUTTERWORTH",
            name = "Klinik Kesihatan Butterworth",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Pulau Pinang",
            address = "Jalan Bagan Luar, 12000 Butterworth, Pulau Pinang",
            latitude = 5.3980,
            longitude = 100.3690,
            distanceKm = 144.0,
            currentQueueNumber = "BW-2289",
            waitTimeMins = 15,
            phoneNumber = "+60 4-333 4444",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== PERLIS ====================
        ClinicFacility(
            id = "FAC-HOSP-HTF",
            name = "Hospital Tuanku Fauziah (HTF Kangar)",
            type = FacilityType.HOSPITAL,
            state = "Perlis",
            address = "Jalan Kolam, 01000 Kangar, Perlis",
            latitude = 6.4414,
            longitude = 100.1912,
            distanceKm = 275.0,
            currentQueueNumber = "PR-6611",
            waitTimeMins = 25,
            phoneNumber = "+60 4-973 8000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KANGAR",
            name = "Klinik Kesihatan Kangar",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perlis",
            address = "Jalan Raja Syed Alwi, 01000 Kangar, Perlis",
            latitude = 6.4380,
            longitude = 100.1980,
            distanceKm = 274.0,
            currentQueueNumber = "KG-1122",
            waitTimeMins = 12,
            phoneNumber = "+60 4-976 1333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-ARAU",
            name = "Klinik Kesihatan Arau",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Perlis",
            address = "Jalan Padang Nyu, 02600 Arau, Perlis",
            latitude = 6.4310,
            longitude = 100.2750,
            distanceKm = 268.0,
            currentQueueNumber = "AR-2201",
            waitTimeMins = 10,
            phoneNumber = "+60 4-986 1222",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== KUALA LUMPUR ====================
        ClinicFacility(
            id = "FAC-HOSP-HKL",
            name = "Hospital Kuala Lumpur (HKL)",
            type = FacilityType.HOSPITAL,
            state = "Kuala Lumpur",
            address = "Jalan Pahang, 50586 Kuala Lumpur, Wilayah Persekutuan",
            latitude = 3.1717,
            longitude = 101.7019,
            distanceKm = 118.0,
            currentQueueNumber = "KL-9901",
            waitTimeMins = 40,
            phoneNumber = "+60 3-2615 5555",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-HUKM",
            name = "Hospital Canselor Tuanku Muhriz (HUKM Cheras)",
            type = FacilityType.HOSPITAL,
            state = "Kuala Lumpur",
            address = "Jalan Yaacob Latif, Bandar Tun Razak, 56000 Cheras, Kuala Lumpur",
            latitude = 3.0998,
            longitude = 101.7247,
            distanceKm = 124.0,
            currentQueueNumber = "UKM-3321",
            waitTimeMins = 35,
            phoneNumber = "+60 3-9145 5555",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KL",
            name = "Klinik Kesihatan Kuala Lumpur (KKKL Jalan Fletcher)",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kuala Lumpur",
            address = "Jalan Fletcher, Titiwangsa, 53200 Kuala Lumpur",
            latitude = 3.1755,
            longitude = 101.7088,
            distanceKm = 119.0,
            currentQueueNumber = "FL-2210",
            waitTimeMins = 18,
            phoneNumber = "+60 3-2698 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-TANGLIN",
            name = "Klinik Kesihatan Tanglin",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kuala Lumpur",
            address = "Jalan Cenderasari, Perdana Botanical Gardens, 50480 Kuala Lumpur",
            latitude = 3.1480,
            longitude = 101.6890,
            distanceKm = 116.0,
            currentQueueNumber = "TG-1102",
            waitTimeMins = 15,
            phoneNumber = "+60 3-2698 8655",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-CHERAS",
            name = "Klinik Kesihatan Cheras",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kuala Lumpur",
            address = "Jalan Yaacob Latif, Bandar Tun Razak, 56000 Cheras, Kuala Lumpur",
            latitude = 3.1040,
            longitude = 101.7210,
            distanceKm = 123.0,
            currentQueueNumber = "CH-4412",
            waitTimeMins = 16,
            phoneNumber = "+60 3-9131 5555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== PUTRAJAYA ====================
        ClinicFacility(
            id = "FAC-HOSP-PUTRA",
            name = "Hospital Putrajaya",
            type = FacilityType.HOSPITAL,
            state = "Putrajaya",
            address = "Pusat Pentadbiran Kerajaan Persekutuan, Presint 7, 62250 Putrajaya",
            latitude = 2.9288,
            longitude = 101.6742,
            distanceKm = 142.0,
            currentQueueNumber = "PJ-2089",
            waitTimeMins = 30,
            phoneNumber = "+60 3-8312 4200",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-P9",
            name = "Klinik Kesihatan Putrajaya Presint 9",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Putrajaya",
            address = "Jalan P9F, Presint 9, 62250 Putrajaya, Wilayah Persekutuan",
            latitude = 2.9372,
            longitude = 101.6784,
            distanceKm = 141.0,
            currentQueueNumber = "P9-1042",
            waitTimeMins = 12,
            phoneNumber = "+60 3-8888 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-P18",
            name = "Klinik Kesihatan Putrajaya Presint 18",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Putrajaya",
            address = "Jalan P18, Presint 18, 62150 Putrajaya, Wilayah Persekutuan",
            latitude = 2.9150,
            longitude = 101.6880,
            distanceKm = 144.0,
            currentQueueNumber = "P18-3011",
            waitTimeMins = 14,
            phoneNumber = "+60 3-8888 5555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== SELANGOR ====================
        ClinicFacility(
            id = "FAC-HOSP-HTAR",
            name = "Hospital Tengku Ampuan Rahimah (HTAR Klang)",
            type = FacilityType.HOSPITAL,
            state = "Selangor",
            address = "Jalan Langat, 41200 Klang, Selangor",
            latitude = 3.0210,
            longitude = 101.4390,
            distanceKm = 132.0,
            currentQueueNumber = "KLG-7711",
            waitTimeMins = 38,
            phoneNumber = "+60 3-3375 7000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SELAYANG",
            name = "Hospital Selayang",
            type = FacilityType.HOSPITAL,
            state = "Selangor",
            address = "Lebuhraya Selayang-Kepong, 68100 Batu Caves, Selangor",
            latitude = 3.2425,
            longitude = 101.6468,
            distanceKm = 109.0,
            currentQueueNumber = "SL-5510",
            waitTimeMins = 32,
            phoneNumber = "+60 3-6126 3333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SERDANG",
            name = "Hospital Sultan Idris Shah Serdang",
            type = FacilityType.HOSPITAL,
            state = "Selangor",
            address = "Jalan Puchong, 43000 Kajang, Selangor",
            latitude = 2.9760,
            longitude = 101.7210,
            distanceKm = 138.0,
            currentQueueNumber = "SD-8812",
            waitTimeMins = 30,
            phoneNumber = "+60 3-8947 5555",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SUNGAIBULOH",
            name = "Hospital Sungai Buloh",
            type = FacilityType.HOSPITAL,
            state = "Selangor",
            address = "Jalan Hospital, 47000 Sungai Buloh, Selangor",
            latitude = 3.2180,
            longitude = 101.5830,
            distanceKm = 110.0,
            currentQueueNumber = "SB-2299",
            waitTimeMins = 28,
            phoneNumber = "+60 3-6145 4333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-SHAHALAM",
            name = "Klinik Kesihatan Shah Alam Seksyen 7",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Selangor",
            address = "Persiaran Kayangan, Seksyen 7, 40000 Shah Alam, Selangor",
            latitude = 3.0780,
            longitude = 101.4920,
            distanceKm = 126.0,
            currentQueueNumber = "SA-1105",
            waitTimeMins = 16,
            phoneNumber = "+60 3-5510 5555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-KELANAJAYA",
            name = "Klinik Kesihatan Kelana Jaya",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Selangor",
            address = "Jalan SS 6/3, Kelana Jaya, 47301 Petaling Jaya, Selangor",
            latitude = 3.1020,
            longitude = 101.5990,
            distanceKm = 124.0,
            currentQueueNumber = "KJ-3318",
            waitTimeMins = 15,
            phoneNumber = "+60 3-7804 5555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-KAJANG",
            name = "Klinik Kesihatan Kajang",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Selangor",
            address = "Jalan Semenyih, 43000 Kajang, Selangor",
            latitude = 2.9910,
            longitude = 101.7910,
            distanceKm = 139.0,
            currentQueueNumber = "KJ-4490",
            waitTimeMins = 14,
            phoneNumber = "+60 3-8736 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== NEGERI SEMBILAN ====================
        ClinicFacility(
            id = "FAC-HOSP-HTJ",
            name = "Hospital Tuanku Ja'afar (HTJ Seremban)",
            type = FacilityType.HOSPITAL,
            state = "Negeri Sembilan",
            address = "Jalan Rasah, 70300 Seremban, Negeri Sembilan",
            latitude = 2.7092,
            longitude = 101.9427,
            distanceKm = 175.0,
            currentQueueNumber = "NS-8812",
            waitTimeMins = 32,
            phoneNumber = "+60 6-768 4000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-SEREMBAN",
            name = "Klinik Kesihatan Seremban (Jalan Rasah)",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Negeri Sembilan",
            address = "Jalan Rasah, 70300 Seremban, Negeri Sembilan",
            latitude = 2.7120,
            longitude = 101.9380,
            distanceKm = 174.0,
            currentQueueNumber = "SR-2201",
            waitTimeMins = 14,
            phoneNumber = "+60 6-762 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-PD",
            name = "Hospital Port Dickson",
            type = FacilityType.HOSPITAL,
            state = "Negeri Sembilan",
            address = "KM 11, Jalan Pantai, 71050 Port Dickson, Negeri Sembilan",
            latitude = 2.4550,
            longitude = 101.8650,
            distanceKm = 198.0,
            currentQueueNumber = "PD-3312",
            waitTimeMins = 20,
            phoneNumber = "+60 6-646 3333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),

        // ==================== MELAKA ====================
        ClinicFacility(
            id = "FAC-HOSP-MELAKA",
            name = "Hospital Melaka",
            type = FacilityType.HOSPITAL,
            state = "Melaka",
            address = "Jalan Mufti Haji Khalil, 75400 Melaka",
            latitude = 2.2174,
            longitude = 102.2612,
            distanceKm = 245.0,
            currentQueueNumber = "MK-7721",
            waitTimeMins = 30,
            phoneNumber = "+60 6-289 2344",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-PERINGGIT",
            name = "Klinik Kesihatan Peringgit Melaka",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Melaka",
            address = "Peringgit, 75400 Melaka",
            latitude = 2.2210,
            longitude = 102.2540,
            distanceKm = 244.0,
            currentQueueNumber = "PR-1120",
            waitTimeMins = 12,
            phoneNumber = "+60 6-282 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-AYERKEROH",
            name = "Klinik Kesihatan Ayer Keroh",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Melaka",
            address = "Lebuh Ayer Keroh, 75450 Ayer Keroh, Melaka",
            latitude = 2.2710,
            longitude = 102.2850,
            distanceKm = 240.0,
            currentQueueNumber = "AK-4401",
            waitTimeMins = 15,
            phoneNumber = "+60 6-232 4444",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== JOHOR ====================
        ClinicFacility(
            id = "FAC-HOSP-HSA",
            name = "Hospital Sultanah Aminah (HSA Johor Bahru)",
            type = FacilityType.HOSPITAL,
            state = "Johor",
            address = "Jalan Persiaran Abu Bakar Sultan, 80100 Johor Bahru, Johor",
            latitude = 1.4583,
            longitude = 103.7460,
            distanceKm = 398.0,
            currentQueueNumber = "JB-9904",
            waitTimeMins = 36,
            phoneNumber = "+60 7-225 7000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-HSI",
            name = "Hospital Sultan Ismail (HSI Pandan JB)",
            type = FacilityType.HOSPITAL,
            state = "Johor",
            address = "Jalan Persiaran Mutiara Emas Utama, Taman Mount Austin, 81100 Johor Bahru, Johor",
            latitude = 1.5458,
            longitude = 103.7942,
            distanceKm = 392.0,
            currentQueueNumber = "SI-4421",
            waitTimeMins = 30,
            phoneNumber = "+60 7-356 5000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-MAHMOODIAH",
            name = "Klinik Kesihatan Mahmoodiah Johor Bahru",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Johor",
            address = "Jalan Mahmoodiah, 80100 Johor Bahru, Johor",
            latitude = 1.4650,
            longitude = 103.7410,
            distanceKm = 397.0,
            currentQueueNumber = "MH-2210",
            waitTimeMins = 16,
            phoneNumber = "+60 7-223 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-MUAR",
            name = "Hospital Pakar Sultanah Fatimah (HPSF Muar)",
            type = FacilityType.HOSPITAL,
            state = "Johor",
            address = "Jalan Salleh, 84000 Muar, Johor",
            latitude = 2.0520,
            longitude = 102.5760,
            distanceKm = 285.0,
            currentQueueNumber = "MR-3301",
            waitTimeMins = 24,
            phoneNumber = "+60 6-952 1002",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),

        // ==================== PAHANG ====================
        ClinicFacility(
            id = "FAC-HOSP-HTAA",
            name = "Hospital Tengku Ampuan Afzan (HTAA Kuantan)",
            type = FacilityType.HOSPITAL,
            state = "Pahang",
            address = "Jalan Tanah Putih, 25100 Kuantan, Pahang",
            latitude = 3.8055,
            longitude = 103.3242,
            distanceKm = 248.0,
            currentQueueNumber = "KT-8802",
            waitTimeMins = 32,
            phoneNumber = "+60 9-513 3333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-HOSHAS",
            name = "Hospital Sultan Haji Ahmad Shah (HOSHAS Temerloh)",
            type = FacilityType.HOSPITAL,
            state = "Pahang",
            address = "Jalan Maran, 28000 Temerloh, Pahang",
            latitude = 3.4520,
            longitude = 102.4310,
            distanceKm = 185.0,
            currentQueueNumber = "TM-4411",
            waitTimeMins = 26,
            phoneNumber = "+60 9-295 5333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KUANTAN",
            name = "Klinik Kesihatan Bandar Kuantan",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Pahang",
            address = "Jalan Gambut, 25000 Kuantan, Pahang",
            latitude = 3.8120,
            longitude = 103.3280,
            distanceKm = 247.0,
            currentQueueNumber = "KT-1190",
            waitTimeMins = 15,
            phoneNumber = "+60 9-514 2222",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== TERENGGANU ====================
        ClinicFacility(
            id = "FAC-HOSP-HSNZ",
            name = "Hospital Sultanah Nur Zahirah (HSNZ Kuala Terengganu)",
            type = FacilityType.HOSPITAL,
            state = "Terengganu",
            address = "Jalan Sultan Mahmud, 20400 Kuala Terengganu, Terengganu",
            latitude = 5.3282,
            longitude = 103.1492,
            distanceKm = 330.0,
            currentQueueNumber = "TR-6612",
            waitTimeMins = 28,
            phoneNumber = "+60 9-621 2121",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-HILIRAN",
            name = "Klinik Kesihatan Hiliran Kuala Terengganu",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Terengganu",
            address = "Jalan Hiliran, 20300 Kuala Terengganu, Terengganu",
            latitude = 5.3180,
            longitude = 103.1380,
            distanceKm = 328.0,
            currentQueueNumber = "HL-3310",
            waitTimeMins = 14,
            phoneNumber = "+60 9-622 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-KEMAMAN",
            name = "Hospital Kemaman",
            type = FacilityType.HOSPITAL,
            state = "Terengganu",
            address = "Jalan Hospital, 24000 Chukai, Kemaman, Terengganu",
            latitude = 4.2380,
            longitude = 103.4180,
            distanceKm = 295.0,
            currentQueueNumber = "KM-5501",
            waitTimeMins = 22,
            phoneNumber = "+60 9-859 3333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),

        // ==================== KELANTAN ====================
        ClinicFacility(
            id = "FAC-HOSP-HRPZ",
            name = "Hospital Raja Perempuan Zainab II (HRPZ II Kota Bharu)",
            type = FacilityType.HOSPITAL,
            state = "Kelantan",
            address = "15586 Kota Bharu, Kelantan",
            latitude = 6.1265,
            longitude = 102.2472,
            distanceKm = 295.0,
            currentQueueNumber = "KB-9912",
            waitTimeMins = 34,
            phoneNumber = "+60 9-745 2000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-HUSM",
            name = "Hospital Universiti Sains Malaysia (HUSM Kubang Kerian)",
            type = FacilityType.HOSPITAL,
            state = "Kelantan",
            address = "Jalan Raja Perempuan Zainab II, 16150 Kubang Kerian, Kelantan",
            latitude = 6.0950,
            longitude = 102.2850,
            distanceKm = 298.0,
            currentQueueNumber = "USM-4410",
            waitTimeMins = 32,
            phoneNumber = "+60 9-767 3000",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-KOTABHARU",
            name = "Klinik Kesihatan Bandar Kota Bharu",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Kelantan",
            address = "Jalan Pengkalan Chepa, 15400 Kota Bharu, Kelantan",
            latitude = 6.1350,
            longitude = 102.2410,
            distanceKm = 294.0,
            currentQueueNumber = "KB-2211",
            waitTimeMins = 16,
            phoneNumber = "+60 9-748 3333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== SABAH ====================
        ClinicFacility(
            id = "FAC-HOSP-QEH",
            name = "Hospital Queen Elizabeth (QEH Kota Kinabalu)",
            type = FacilityType.HOSPITAL,
            state = "Sabah",
            address = "Jalan Penampang, 88200 Kota Kinabalu, Sabah",
            latitude = 5.9525,
            longitude = 116.0712,
            distanceKm = 1620.0,
            currentQueueNumber = "SB-8822",
            waitTimeMins = 35,
            phoneNumber = "+60 88-517 555",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-QEH2",
            name = "Hospital Queen Elizabeth II (QEH II Damai)",
            type = FacilityType.HOSPITAL,
            state = "Sabah",
            address = "Lorong Bersatu, Off Jalan Damai, Luyang, 88300 Kota Kinabalu, Sabah",
            latitude = 5.9620,
            longitude = 116.0950,
            distanceKm = 1622.0,
            currentQueueNumber = "SB-5501",
            waitTimeMins = 28,
            phoneNumber = "+60 88-324 600",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SANDAKAN",
            name = "Hospital Duchess of Kent (HDOK Sandakan)",
            type = FacilityType.HOSPITAL,
            state = "Sabah",
            address = "KM 3.2, Jalan Utara, 90000 Sandakan, Sabah",
            latitude = 5.8520,
            longitude = 118.1050,
            distanceKm = 1820.0,
            currentQueueNumber = "SDK-4412",
            waitTimeMins = 26,
            phoneNumber = "+60 89-212 111",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-TAWAU",
            name = "Hospital Tawau",
            type = FacilityType.HOSPITAL,
            state = "Sabah",
            address = "Peti Surat 67, Tanjung Batu, 91007 Tawau, Sabah",
            latitude = 4.2510,
            longitude = 117.8890,
            distanceKm = 1870.0,
            currentQueueNumber = "TW-6610",
            waitTimeMins = 25,
            phoneNumber = "+60 89-773 533",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-LUYANG",
            name = "Klinik Kesihatan Luyang Kota Kinabalu",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sabah",
            address = "Jalan Kolam, Luyang, 88300 Kota Kinabalu, Sabah",
            latitude = 5.9450,
            longitude = 116.0820,
            distanceKm = 1621.0,
            currentQueueNumber = "LY-2033",
            waitTimeMins = 16,
            phoneNumber = "+60 88-234 400",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-PENAMPANG",
            name = "Klinik Kesihatan Penampang",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sabah",
            address = "Jalan Penampang Lama, 89500 Penampang, Sabah",
            latitude = 5.9120,
            longitude = 116.1150,
            distanceKm = 1625.0,
            currentQueueNumber = "PN-1145",
            waitTimeMins = 15,
            phoneNumber = "+60 88-711 555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-SANDAKAN",
            name = "Klinik Kesihatan Sandakan",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sabah",
            address = "Batu 1 1/2, Jalan Utara, 90000 Sandakan, Sabah",
            latitude = 5.8450,
            longitude = 118.1120,
            distanceKm = 1818.0,
            currentQueueNumber = "SD-3302",
            waitTimeMins = 14,
            phoneNumber = "+60 89-223 333",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== SARAWAK ====================
        ClinicFacility(
            id = "FAC-HOSP-SGH",
            name = "Hospital Umum Sarawak (SGH Kuching)",
            type = FacilityType.HOSPITAL,
            state = "Sarawak",
            address = "Jalan Hospital, 93586 Kuching, Sarawak",
            latitude = 1.5435,
            longitude = 110.3421,
            distanceKm = 970.0,
            currentQueueNumber = "SW-9910",
            waitTimeMins = 35,
            phoneNumber = "+60 82-276 666",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-SIBU",
            name = "Hospital Sibu",
            type = FacilityType.HOSPITAL,
            state = "Sarawak",
            address = "Batu 5 1/2, Jalan Oya, 96000 Sibu, Sarawak",
            latitude = 2.3080,
            longitude = 111.8790,
            distanceKm = 1080.0,
            currentQueueNumber = "SB-4412",
            waitTimeMins = 28,
            phoneNumber = "+60 84-343 333",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-MIRI",
            name = "Hospital Miri",
            type = FacilityType.HOSPITAL,
            state = "Sarawak",
            address = "Jalan Cahaya, 98000 Miri, Sarawak",
            latitude = 4.3750,
            longitude = 113.9890,
            distanceKm = 1350.0,
            currentQueueNumber = "MR-7711",
            waitTimeMins = 30,
            phoneNumber = "+60 85-420 033",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-HOSP-BINTULU",
            name = "Hospital Bintulu",
            type = FacilityType.HOSPITAL,
            state = "Sarawak",
            address = "Jalan Nyabau, 97000 Bintulu, Sarawak",
            latitude = 3.2080,
            longitude = 113.0750,
            distanceKm = 1220.0,
            currentQueueNumber = "BT-3320",
            waitTimeMins = 24,
            phoneNumber = "+60 86-255 555",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-TANAHPUTEH",
            name = "Klinik Kesihatan Tanah Puteh Kuching",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sarawak",
            address = "Jalan Tanah Puteh, 93450 Kuching, Sarawak",
            latitude = 1.5520,
            longitude = 110.3680,
            distanceKm = 972.0,
            currentQueueNumber = "TP-1089",
            waitTimeMins = 16,
            phoneNumber = "+60 82-331 100",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-PETRAJAYA",
            name = "Klinik Kesihatan Petra Jaya Kuching",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sarawak",
            address = "Jalan Siol Kanan, Petra Jaya, 93050 Kuching, Sarawak",
            latitude = 1.5780,
            longitude = 110.3490,
            distanceKm = 973.0,
            currentQueueNumber = "PJ-3311",
            waitTimeMins = 15,
            phoneNumber = "+60 82-442 222",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),
        ClinicFacility(
            id = "FAC-KK-MIRI",
            name = "Klinik Kesihatan Miri",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Sarawak",
            address = "Jalan Merbau, 98000 Miri, Sarawak",
            latitude = 4.3980,
            longitude = 113.9920,
            distanceKm = 1352.0,
            currentQueueNumber = "MR-2201",
            waitTimeMins = 14,
            phoneNumber = "+60 85-414 444",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        ),

        // ==================== LABUAN ====================
        ClinicFacility(
            id = "FAC-HOSP-LABUAN",
            name = "Hospital Labuan",
            type = FacilityType.HOSPITAL,
            state = "Labuan",
            address = "Peti Surat 81006, 87020 Wilayah Persekutuan Labuan",
            latitude = 5.2831,
            longitude = 115.2308,
            distanceKm = 1550.0,
            currentQueueNumber = "LB-4401",
            waitTimeMins = 20,
            phoneNumber = "+60 87-423 919",
            isOpenNow = true,
            operatingHours = "24 Jam",
            services = officialHospitalServices
        ),
        ClinicFacility(
            id = "FAC-KK-MEMBEDAI",
            name = "Klinik Kesihatan Membedai Labuan",
            type = FacilityType.KLINIK_KESIHATAN,
            state = "Labuan",
            address = "Jalan Membedai, 87000 Wilayah Persekutuan Labuan",
            latitude = 5.2910,
            longitude = 115.2410,
            distanceKm = 1551.0,
            currentQueueNumber = "MB-1102",
            waitTimeMins = 12,
            phoneNumber = "+60 87-412 555",
            isOpenNow = true,
            operatingHours = "8:00 AM – 5:00 PM",
            services = officialClinicServices
        )
    )

    /**
     * Accurate 7-Day Chronological Diabetic Records (Nov 12 - Nov 18, 2026)
     * Formatted to 2 Decimal Places (e.g. 5.20, 6.85, 10.80)
     * Rules:
     * - If post-meal: < 10.0 is NORMAL, >= 10.0 is HIGH
     * - If fasting: < 4.0 is LOW, 4.0 - 7.0 is NORMAL, >= 7.0 is HIGH
     */
    val glucoseRecords = mutableStateListOf(
        GlucoseRecord(
            id = "G-07",
            valueMmol = 5.20,
            timing = GlucoseTiming.FASTING,
            dateLabel = "18 Nov 2026",
            timeLabel = "07:15 AM",
            timestampMillis = 1794986100000L,
            severity = HealthSeverity.NORMAL,
            note = "Bacaan puasa pagi sebelum makan"
        ),
        GlucoseRecord(
            id = "G-06",
            valueMmol = 6.85,
            timing = GlucoseTiming.POST_BREAKFAST,
            dateLabel = "17 Nov 2026",
            timeLabel = "09:45 AM",
            timestampMillis = 1794908700000L,
            severity = HealthSeverity.NORMAL,
            note = "Selepas sarapan pagi (normal < 10.0)"
        ),
        GlucoseRecord(
            id = "G-05",
            valueMmol = 10.80,
            timing = GlucoseTiming.POST_LUNCH,
            dateLabel = "16 Nov 2026",
            timeLabel = "02:30 PM",
            timestampMillis = 1794839400000L,
            severity = HealthSeverity.HIGH,
            note = "Melebihi sasaran 10.0 selepas makan tengah hari"
        ),
        GlucoseRecord(
            id = "G-04",
            valueMmol = 5.60,
            timing = GlucoseTiming.FASTING,
            dateLabel = "15 Nov 2026",
            timeLabel = "07:30 AM",
            timestampMillis = 1794727800000L,
            severity = HealthSeverity.NORMAL,
            note = "Bacaan puasa pagi normal"
        ),
        GlucoseRecord(
            id = "G-03",
            valueMmol = 8.40,
            timing = GlucoseTiming.POST_DINNER,
            dateLabel = "14 Nov 2026",
            timeLabel = "09:00 PM",
            timestampMillis = 1794668400000L,
            severity = HealthSeverity.NORMAL,
            note = "Selepas makan malam"
        ),
        GlucoseRecord(
            id = "G-02",
            valueMmol = 4.80,
            timing = GlucoseTiming.BEFORE_LUNCH,
            dateLabel = "13 Nov 2026",
            timeLabel = "12:45 PM",
            timestampMillis = 1794552300000L,
            severity = HealthSeverity.NORMAL,
            note = "Sebelum makan tengah hari"
        ),
        GlucoseRecord(
            id = "G-01",
            valueMmol = 6.20,
            timing = GlucoseTiming.POST_BREAKFAST,
            dateLabel = "12 Nov 2026",
            timeLabel = "09:30 AM",
            timestampMillis = 1794475800000L,
            severity = HealthSeverity.NORMAL,
            note = "Selepas sarapan pagi"
        )
    )

    val bpRecords = mutableStateListOf(
        BloodPressureRecord(
            id = "BP-04",
            systolic = 122,
            diastolic = 78,
            pulse = 72,
            dateLabel = "18 Nov 2026",
            timeLabel = "08:00 AM",
            timestampMillis = 1794988800000L,
            category = "Normal",
            severity = HealthSeverity.NORMAL
        ),
        BloodPressureRecord(
            id = "BP-03",
            systolic = 125,
            diastolic = 82,
            pulse = 74,
            dateLabel = "16 Nov 2026",
            timeLabel = "08:15 AM",
            timestampMillis = 1794816900000L,
            category = "Normal",
            severity = HealthSeverity.NORMAL
        ),
        BloodPressureRecord(
            id = "BP-02",
            systolic = 128,
            diastolic = 84,
            pulse = 76,
            dateLabel = "14 Nov 2026",
            timeLabel = "08:00 AM",
            timestampMillis = 1794643200000L,
            category = "Normal",
            severity = HealthSeverity.NORMAL
        ),
        BloodPressureRecord(
            id = "BP-01",
            systolic = 120,
            diastolic = 78,
            pulse = 70,
            dateLabel = "12 Nov 2026",
            timeLabel = "08:30 AM",
            timestampMillis = 1794472200000L,
            category = "Normal",
            severity = HealthSeverity.NORMAL
        )
    )

    val bloodPressureRecords get() = bpRecords

    val medications = mutableStateListOf(
        Medication(
            id = "MED-01",
            name = "Metformin HCl",
            dosage = "500 mg",
            frequency = "2 kali sehari",
            instruction = "Ambil bersama atau selepas makan bagi mengurangkan ketidakselesaan perut",
            pillsRemaining = 24,
            nextDoseTime = "08:00 PM",
            isTakenToday = true
        ),
        Medication(
            id = "MED-02",
            name = "Gliclazide MR",
            dosage = "30 mg",
            frequency = "1 kali sehari",
            instruction = "Ambil semasa sarapan pagi",
            pillsRemaining = 12,
            nextDoseTime = "08:00 AM",
            isTakenToday = true
        ),
        Medication(
            id = "MED-03",
            name = "Perindopril Erbumine",
            dosage = "4 mg",
            frequency = "1 kali sehari",
            instruction = "Ambil sebelum sarapan pagi untuk perlindungan buah pinggang dan kawalan tekanan darah",
            pillsRemaining = 18,
            nextDoseTime = "08:00 AM",
            isTakenToday = true
        ),
        Medication(
            id = "MED-04",
            name = "Atorvastatin Calcium",
            dosage = "20 mg",
            frequency = "1 kali sehari (Malam)",
            instruction = "Ambil pada waktu malam sebelum tidur bagi kawalan kolesterol",
            pillsRemaining = 28,
            nextDoseTime = "10:00 PM",
            isTakenToday = false
        )
    )

    val currentUser = mutableStateOf(
        UserProfile(
            name = "Ahmad Zikri bin Razak",
            myKad = "920814-14-5532",
            email = "ahmad.zikri@gmail.com",
            age = 34,
            gender = "Lelaki",
            bloodType = "O+",
            emergencyContact = EmergencyContact(
                name = "Nurul Hidayah binti Osman",
                relationship = "Isteri",
                phone = "+60 19-876 5432"
            )
        )
    )

    private fun getTomorrowDateAt9Am(): Pair<Long, String> {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala Lumpur"), Locale("ms", "MY"))
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val target = cal.timeInMillis
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("ms", "MY"))
        return Pair(target, sdf.format(cal.time))
    }

    val initialAppointmentTarget = getTomorrowDateAt9Am()

    val nextAppointment = mutableStateOf(
        ClinicAppointment(
            id = "APT-2026-NCD-8841",
            facilityName = "Hospital Sultan Abdul Halim (HSAH Sungai Petani)",
            clinicType = "Klinik Rawatan Diabetes & NCD",
            sessionLabel = "Sesi Pagi (09:00 AM)",
            dateDisplay = initialAppointmentTarget.second,
            timeDisplay = "09:00 AM",
            referenceNumber = "REF-NCD-HSAH-01",
            targetTimeMillis = initialAppointmentTarget.first,
            isCheckedIn = false
        )
    )

    fun calculateTargetMillis(dateStr: String, sessionStr: String = "", timeStr: String = ""): Long {
        val cleanDate = dateStr.trim()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala Lumpur"), Locale("ms", "MY"))
        val currentYear = cal.get(Calendar.YEAR)

        var targetHour = 9
        var targetMinute = 0

        if (timeStr.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("hh:mm a", Locale.US)
                val parsed = sdf.parse(timeStr.trim())
                if (parsed != null) {
                    val tCal = Calendar.getInstance()
                    tCal.time = parsed
                    targetHour = tCal.get(Calendar.HOUR_OF_DAY)
                    targetMinute = tCal.get(Calendar.MINUTE)
                }
            } catch (_: Exception) {}
        } else if (sessionStr.contains("Petang", ignoreCase = true) || sessionStr.contains("Afternoon", ignoreCase = true)) {
            targetHour = 14
            targetMinute = 30
        }

        if (cleanDate.equals("esok", ignoreCase = true) || cleanDate.equals("tomorrow", ignoreCase = true)) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        } else if (cleanDate.equals("lusa", ignoreCase = true)) {
            cal.add(Calendar.DAY_OF_YEAR, 2)
        } else {
            val slashMatch = Regex("""^(\d{1,2})/(\d{1,2})(?:/(\d{2,4}))?""").find(cleanDate)
            if (slashMatch != null) {
                val d = slashMatch.groupValues[1].toIntOrNull() ?: 1
                val m = (slashMatch.groupValues[2].toIntOrNull() ?: 1) - 1
                val y = slashMatch.groupValues[3].toIntOrNull() ?: currentYear
                val finalYear = if (y < 100) 2000 + y else y
                cal.set(Calendar.YEAR, finalYear)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
            } else {
                val formats = listOf("dd MMM yyyy", "dd MMMM yyyy", "d MMM yyyy", "d MMMM yyyy", "dd-MM-yyyy", "yyyy-MM-dd")
                var parsedDate: java.util.Date? = null
                for (fmt in formats) {
                    try {
                        val sdf = SimpleDateFormat(fmt, Locale("ms", "MY"))
                        parsedDate = sdf.parse(cleanDate)
                        if (parsedDate != null) break
                    } catch (_: Exception) {}
                    try {
                        val sdfEn = SimpleDateFormat(fmt, Locale.US)
                        parsedDate = sdfEn.parse(cleanDate)
                        if (parsedDate != null) break
                    } catch (_: Exception) {}
                }
                if (parsedDate != null) {
                    val pCal = Calendar.getInstance()
                    pCal.time = parsedDate
                    cal.set(Calendar.YEAR, if (pCal.get(Calendar.YEAR) < 2000) currentYear else pCal.get(Calendar.YEAR))
                    cal.set(Calendar.MONTH, pCal.get(Calendar.MONTH))
                    cal.set(Calendar.DAY_OF_MONTH, pCal.get(Calendar.DAY_OF_MONTH))
                } else {
                    val numberOnlyMatch = Regex("""\b(\d{1,2})\b""").find(cleanDate)
                    if (numberOnlyMatch != null) {
                        val dayNum = numberOnlyMatch.groupValues[1].toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                        cal.set(Calendar.DAY_OF_MONTH, dayNum)
                    }
                }
            }
        }

        cal.set(Calendar.HOUR_OF_DAY, targetHour)
        cal.set(Calendar.MINUTE, targetMinute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val now = System.currentTimeMillis()
        if (cal.timeInMillis < now - 3600_000L) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return cal.timeInMillis
    }

    private fun formatStandardDate(millis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala Lumpur"), Locale("ms", "MY"))
        cal.timeInMillis = millis
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("ms", "MY"))
        return sdf.format(cal.time)
    }

    val isLoggedIn = mutableStateOf(false)
    val hasScheduledAppointment = mutableStateOf(false)

    fun getTodayDateString(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"), Locale("ms", "MY"))
        return SimpleDateFormat("dd MMM yyyy", Locale("ms", "MY")).format(cal.time)
    }

    fun getNowTimeString(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"), Locale.US)
        return SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)
    }

    fun login(email: String, pass: String): Boolean {
        val (success, msg) = SupabaseDatabaseManager.authenticateUser(email, pass)
        if (!success) {
            NotificationService.showError(msg)
            return false
        }
        isLoggedIn.value = true
        NotificationService.showSuccess(msg)
        return true
    }

    fun loginDemoAccount(): Boolean {
        return login("ahmad.zikri@gmail.com", "Zikri@2026*")
    }

    fun logout() {
        val isEn = LanguageManager.isEnglish
        isLoggedIn.value = false
        SupabaseDatabaseManager.currentAccessToken = null
        NotificationService.showSuccess(
            if (isEn) "Logged Out Successfully! Your session has been safely closed."
            else "Log Keluar Berjaya! Sesi akaun anda telah ditamatkan dengan selamat."
        )
    }

    fun register(
        name: String,
        myKad: String,
        email: String,
        password: String,
        phone: String,
        gender: String = ""
    ): Boolean {
        val (success, msg) = SupabaseDatabaseManager.registerNewUser(
            name = name,
            myKad = myKad,
            email = email,
            password = password,
            phone = phone,
            gender = gender
        )
        if (!success) {
            NotificationService.showError(msg)
            return false
        }
        isLoggedIn.value = true
        NotificationService.showSuccess(msg)
        return true
    }

    fun updateUserProfile(
        name: String,
        myKad: String,
        email: String,
        pass: String,
        age: Int,
        gender: String,
        condition: String,
        bloodType: String,
        heightCm: Int,
        weightKg: Double
    ) {
        val isEn = LanguageManager.isEnglish
        if (name.isBlank() || myKad.isBlank() || email.isBlank()) {
            NotificationService.showError(
                if (isEn) "Update Profile Failed: Name, MyKad and Email cannot be empty!"
                else "Kemaskini Profil Tidak Berjaya: Nama, MyKad & E-mel wajib diisi!"
            )
            return
        }
        val updated = currentUser.value.copy(
            name = name,
            myKad = myKad,
            email = email,
            password = pass.ifBlank { currentUser.value.password },
            age = age,
            gender = gender,
            condition = condition,
            bloodType = bloodType,
            heightCm = heightCm,
            weightKg = weightKg
        )
        currentUser.value = updated
        SupabaseDatabaseManager.saveUserProfileToSqlite(updated)
        NotificationService.showSuccess(
            if (isEn) "Profile Updated Successfully! Saved to database."
            else "Kemaskini Profil Berjaya! Maklumat telah disimpan ke pangkalan data."
        )
    }

    fun addGlucoseReading(
        value: Double,
        timing: GlucoseTiming,
        date: String = "",
        time: String = "",
        note: String = ""
    ) {
        val isEn = LanguageManager.isEnglish
        if (value < 0.6 || value > 45.0) {
            NotificationService.showError(
                if (isEn) "Add Record Failed: Glucose value must be between 0.6 and 45.0 mmol/L!"
                else "Tambah Rekod Tidak Berjaya: Nilai glukosa mestilah antara 0.6 hingga 45.0 mmol/L!"
            )
            return
        }
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"), Locale("ms", "MY"))
        val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale("ms", "MY"))
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.US)

        val severity = when {
            timing.isPostMeal -> {
                if (value >= 10.0) HealthSeverity.HIGH else HealthSeverity.NORMAL
            }
            else -> {
                when {
                    value < 4.0 -> HealthSeverity.LOW
                    value <= 7.0 -> HealthSeverity.NORMAL
                    else -> HealthSeverity.HIGH
                }
            }
        }

        val record = GlucoseRecord(
            id = "G-${System.currentTimeMillis() % 100000}",
            valueMmol = value,
            timing = timing,
            dateLabel = date.ifBlank { dateFmt.format(cal.time) },
            timeLabel = time.ifBlank { timeFmt.format(cal.time) },
            timestampMillis = System.currentTimeMillis(),
            severity = severity,
            note = note.trim()
        )

        glucoseRecords.add(0, record)
        SupabaseDatabaseManager.saveGlucoseToSqlite(currentUser.value.email, record)
        val valStr = String.format(Locale.US, "%.2f", value)
        NotificationService.showSuccess(
            if (isEn) "Add Record Successful: Glucose $valStr mmol/L (${timing.enLabel}) saved to DB!"
            else "Tambah Rekod Berjaya: Bacaan glukosa $valStr mmol/L (${timing.bmLabel}) disimpan!"
        )
    }

    fun addGlucoseRecord(value: Double, timing: GlucoseTiming, note: String = "") {
        addGlucoseReading(value, timing, note = note)
    }

    fun updateGlucoseRecord(
        id: String,
        newValue: Double,
        newTiming: GlucoseTiming,
        newNote: String
    ): Boolean {
        val isEn = LanguageManager.isEnglish
        if (newValue < 0.6 || newValue > 45.0) {
            NotificationService.showError(
                if (isEn) "Update Failed: Please enter a valid glucose value (0.6 – 45.0 mmol/L)!"
                else "Kemaskini Tidak Berjaya: Sila masukkan nilai glukosa yang sah (0.6 – 45.0 mmol/L)!"
            )
            return false
        }
        val index = glucoseRecords.indexOfFirst { it.id == id }
        if (index == -1) {
            NotificationService.showError(
                if (isEn) "Update Failed: Glucose record not found!"
                else "Kemaskini Tidak Berjaya: Rekod glukosa tidak dijumpai!"
            )
            return false
        }
        val severity = when {
            newTiming.isPostMeal -> if (newValue >= 10.0) HealthSeverity.HIGH else HealthSeverity.NORMAL
            else -> when {
                newValue < 4.0 -> HealthSeverity.LOW
                newValue <= 7.0 -> HealthSeverity.NORMAL
                else -> HealthSeverity.HIGH
            }
        }
        val updated = glucoseRecords[index].copy(
            valueMmol = newValue,
            timing = newTiming,
            severity = severity,
            note = newNote.trim()
        )
        glucoseRecords[index] = updated
        SupabaseDatabaseManager.saveGlucoseToSqlite(currentUser.value.email, updated)
        val valStr = String.format(Locale.US, "%.2f", newValue)
        NotificationService.showSuccess(
            if (isEn) "Update Successful: Glucose record updated to $valStr mmol/L (${newTiming.enLabel})."
            else "Kemaskini Berjaya: Rekod glukosa dikemaskini kepada $valStr mmol/L (${newTiming.bmLabel})."
        )
        return true
    }

    fun deleteGlucoseRecord(id: String): Boolean {
        val isEn = LanguageManager.isEnglish
        val index = glucoseRecords.indexOfFirst { it.id == id }
        if (index == -1) {
            NotificationService.showError(
                if (isEn) "Delete Failed: Glucose record not found!"
                else "Padam Tidak Berjaya: Rekod glukosa tidak dijumpai!"
            )
            return false
        }
        val removed = glucoseRecords.removeAt(index)
        SupabaseDatabaseManager.deleteGlucoseFromSqlite(currentUser.value.email, id)
        val valStr = String.format(Locale.US, "%.2f", removed.valueMmol)
        NotificationService.showSuccess(
            if (isEn) "Delete Successful: Glucose record ($valStr mmol/L • ${removed.dateLabel}) removed."
            else "Padam Rekod Berjaya: Bacaan glukosa ($valStr mmol/L • ${removed.dateLabel}) telah dipadam."
        )
        return true
    }

    fun addBpReading(sys: Int, dia: Int, pulse: Int, date: String = "", time: String = "") {
        val isEn = LanguageManager.isEnglish
        if (sys !in 50..280 || dia !in 30..180 || sys <= dia) {
            NotificationService.showError(
                if (isEn) "Add Record Failed: Invalid Systolic/Diastolic values (Systolic must be > Diastolic)!"
                else "Tambah Rekod Tidak Berjaya: Nilai Sistolik & Diastolik tidak sah (Sistolik > Diastolik)!"
            )
            return
        }
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"), Locale("ms", "MY"))
        val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale("ms", "MY"))
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.US)

        val severity = when {
            sys >= 140 || dia >= 90 -> HealthSeverity.HIGH
            sys < 90 || dia < 60 -> HealthSeverity.LOW
            else -> HealthSeverity.NORMAL
        }

        val category = if (severity == HealthSeverity.NORMAL) "Normal" else if (severity == HealthSeverity.HIGH) "Tinggi" else "Rendah"
        val record = BloodPressureRecord(
            id = "BP-${System.currentTimeMillis() % 100000}",
            systolic = sys,
            diastolic = dia,
            pulse = pulse,
            dateLabel = date.ifBlank { dateFmt.format(cal.time) },
            timeLabel = time.ifBlank { timeFmt.format(cal.time) },
            timestampMillis = System.currentTimeMillis(),
            category = category,
            severity = severity
        )

        bpRecords.add(0, record)
        SupabaseDatabaseManager.saveBpToSqlite(currentUser.value.email, record)
        NotificationService.showSuccess(
            if (isEn) "Add Record Successful: Blood pressure $sys/$dia mmHg (Pulse: $pulse bpm) saved!"
            else "Tambah Rekod Berjaya: Tekanan darah $sys/$dia mmHg (Nadi: $pulse bpm) telah disimpan!"
        )
    }

    fun addBloodPressureRecord(systolic: Int, diastolic: Int, pulse: Int) {
        addBpReading(systolic, diastolic, pulse)
    }

    fun updateBpRecord(id: String, newSys: Int, newDia: Int, newPulse: Int): Boolean {
        val isEn = LanguageManager.isEnglish
        if (newSys !in 50..280 || newDia !in 30..180 || newSys <= newDia) {
            NotificationService.showError(
                if (isEn) "Update Failed: Invalid Systolic/Diastolic values!"
                else "Kemaskini Tidak Berjaya: Nilai Sistolik & Diastolik tidak sah!"
            )
            return false
        }
        val index = bpRecords.indexOfFirst { it.id == id }
        if (index == -1) {
            NotificationService.showError(
                if (isEn) "Update Failed: Blood pressure record not found!"
                else "Kemaskini Tidak Berjaya: Rekod tekanan darah tidak dijumpai!"
            )
            return false
        }
        val severity = when {
            newSys >= 140 || newDia >= 90 -> HealthSeverity.HIGH
            newSys < 90 || newDia < 60 -> HealthSeverity.LOW
            else -> HealthSeverity.NORMAL
        }
        val category = if (severity == HealthSeverity.NORMAL) "Normal" else if (severity == HealthSeverity.HIGH) "Tinggi" else "Rendah"
        val updated = bpRecords[index].copy(
            systolic = newSys,
            diastolic = newDia,
            pulse = newPulse,
            category = category,
            severity = severity
        )
        bpRecords[index] = updated
        SupabaseDatabaseManager.saveBpToSqlite(currentUser.value.email, updated)
        NotificationService.showSuccess(
            if (isEn) "Update Successful: Blood pressure updated to $newSys/$newDia mmHg (Pulse: $newPulse bpm)."
            else "Kemaskini Berjaya: Rekod tekanan darah dikemaskini kepada $newSys/$newDia mmHg (Nadi: $newPulse bpm)."
        )
        return true
    }

    fun deleteBpRecord(id: String): Boolean {
        val isEn = LanguageManager.isEnglish
        val index = bpRecords.indexOfFirst { it.id == id }
        if (index == -1) {
            NotificationService.showError(
                if (isEn) "Delete Failed: Blood pressure record not found!"
                else "Padam Tidak Berjaya: Rekod tekanan darah tidak dijumpai!"
            )
            return false
        }
        val removed = bpRecords.removeAt(index)
        SupabaseDatabaseManager.deleteBpFromSqlite(currentUser.value.email, id)
        NotificationService.showSuccess(
            if (isEn) "Delete Successful: BP record (${removed.systolic}/${removed.diastolic} mmHg • ${removed.dateLabel}) removed."
            else "Padam Rekod Berjaya: Tekanan darah (${removed.systolic}/${removed.diastolic} mmHg • ${removed.dateLabel}) telah dipadam."
        )
        return true
    }

    fun updateAppointment(
        facility: String = "",
        clinicType: String = "",
        session: String = "",
        date: String = "",
        time: String = ""
    ) {
        val isEn = LanguageManager.isEnglish
        if (facility.isBlank() || date.isBlank()) {
            NotificationService.showError(
                if (isEn) "Update Appointment Failed: Please specify facility and appointment date!"
                else "Kemaskini Temujanji Tidak Berjaya: Sila lengkapkan fasiliti dan tarikh temujanji!"
            )
            return
        }
        val chosenDateStr = date.ifBlank { nextAppointment.value.dateDisplay }
        val chosenSessionStr = session.ifBlank { nextAppointment.value.sessionLabel }
        val chosenTimeStr = time.ifBlank { nextAppointment.value.timeDisplay }
        val targetMillis = calculateTargetMillis(chosenDateStr, chosenSessionStr, chosenTimeStr)
        val normalizedDateDisplay = formatStandardDate(targetMillis)

        val updatedApt = nextAppointment.value.copy(
            facilityName = facility.ifBlank { nextAppointment.value.facilityName },
            clinicType = clinicType.ifBlank { nextAppointment.value.clinicType },
            sessionLabel = chosenSessionStr,
            dateDisplay = normalizedDateDisplay,
            timeDisplay = chosenTimeStr,
            targetTimeMillis = targetMillis,
            isCheckedIn = false
        )
        nextAppointment.value = updatedApt
        hasScheduledAppointment.value = true
        SupabaseDatabaseManager.saveAppointmentToSqlite(currentUser.value.email, updatedApt)
        NotificationService.showSuccess(
            if (isEn) "Appointment Updated Successfully: $normalizedDateDisplay ($chosenTimeStr). Countdown started!"
            else "Kemaskini Temujanji Berjaya: $normalizedDateDisplay ($chosenTimeStr). Kira detik bermula!"
        )
    }

    fun resetAppointment() {
        val isEn = LanguageManager.isEnglish
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"), Locale("ms", "MY"))
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val defaultTarget = cal.timeInMillis
        val formattedDate = formatStandardDate(defaultTarget)

        val apt = ClinicAppointment(
            id = "APT-${System.currentTimeMillis() % 1000}",
            facilityName = "Hospital Sultan Abdul Halim (HSAH Sungai Petani)",
            clinicType = "Klinik Rawatan Diabetes & NCD",
            sessionLabel = "Sesi Pagi (09:00 AM)",
            dateDisplay = formattedDate,
            timeDisplay = "09:00 AM",
            referenceNumber = "REF-NCD-RESET-01",
            targetTimeMillis = defaultTarget,
            isCheckedIn = false
        )
        nextAppointment.value = apt
        hasScheduledAppointment.value = true
        SupabaseDatabaseManager.saveAppointmentToSqlite(currentUser.value.email, apt)
        NotificationService.showInfo(
            if (isEn) "Appointment reset to default ($formattedDate)."
            else "Temujanji telah ditetapkan semula untuk $formattedDate."
        )
    }

    fun checkInAppointment() {
        val isEn = LanguageManager.isEnglish
        val updatedApt = nextAppointment.value.copy(isCheckedIn = true)
        nextAppointment.value = updatedApt
        SupabaseDatabaseManager.saveAppointmentToSqlite(currentUser.value.email, updatedApt)
        NotificationService.showSuccess(
            if (isEn) "QR Check-In Successful! Your attendance at ${nextAppointment.value.facilityName} is verified."
            else "Daftar Masuk QR Berjaya! Kehadiran anda di ${nextAppointment.value.facilityName} telah disahkan."
        )
    }

    fun markCheckInSuccess() {
        checkInAppointment()
    }

    fun updateEmergencyContact(name: String, relation: String, phone: String) {
        val isEn = LanguageManager.isEnglish
        if (name.isBlank() || phone.isBlank()) {
            NotificationService.showError(
                if (isEn) "Update Next-of-Kin Failed: Please provide name and phone number!"
                else "Kemaskini Waris Tidak Berjaya: Sila masukkan nama dan nombor telefon waris!"
            )
            return
        }
        val updated = currentUser.value.copy(
            emergencyContact = EmergencyContact(
                name = name.ifBlank { currentUser.value.emergencyContact.name },
                relationship = relation.ifBlank { currentUser.value.emergencyContact.relationship },
                phone = phone.ifBlank { currentUser.value.emergencyContact.phone }
            )
        )
        currentUser.value = updated
        SupabaseDatabaseManager.saveUserProfileToSqlite(updated)
        NotificationService.showSuccess(
            if (isEn) "Next-of-Kin Updated Successfully! Emergency contact ($name) saved."
            else "Kemaskini Waris Berjaya! Maklumat waris kecemasan ($name) telah disimpan."
        )
    }

    fun toggleMedicationTaken(id: String) {
        val isEn = LanguageManager.isEnglish
        val index = medications.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = medications[index]
            val newTaken = !current.isTakenToday
            val updatedMed = current.copy(isTakenToday = newTaken)
            medications[index] = updatedMed
            SupabaseDatabaseManager.saveMedicationToSqlite(currentUser.value.email, updatedMed)
            if (newTaken) {
                NotificationService.showSuccess(
                    if (isEn) "Medication Updated: ${current.name} (${current.dosage}) marked as TAKEN."
                    else "Kemaskini Ubat Berjaya: Dos ${current.name} (${current.dosage}) ditandakan DIAMBIL."
                )
            } else {
                NotificationService.showInfo(
                    if (isEn) "Medication Updated: ${current.name} dose status unmarked."
                    else "Kemaskini Ubat: Status pengambilan ${current.name} telah dibatalkan."
                )
            }
        } else {
            NotificationService.showError(
                if (isEn) "Update Medication Failed: Medication item not found!"
                else "Kemaskini Ubat Tidak Berjaya: Rekod ubat tidak dijumpai!"
            )
        }
    }

    fun updateUserDistances(userLat: Double, userLng: Double) {
        val r = 6371.0
        for (i in clinicFacilities.indices) {
            val f = clinicFacilities[i]
            val dLat = Math.toRadians(f.latitude - userLat)
            val dLng = Math.toRadians(f.longitude - userLng)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(f.latitude)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val dist = String.format(Locale.US, "%.1f", r * c).toDoubleOrNull() ?: 1.0
            clinicFacilities[i] = f.copy(distanceKm = dist)
        }
    }
}
