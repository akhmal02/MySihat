package com.example.mysihat.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.StrictMode
import android.util.Log
import com.example.mysihat.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Locale

/**
 * 100% Live Supabase Cloud + Offline Cache Database Engine.
 *
 * Credentials are injected securely at compile time from local.properties via BuildConfig:
 * - BuildConfig.SUPABASE_URL
 * - BuildConfig.SUPABASE_ANON_KEY
 *
 * Performs full REST & Auth CRUD:
 * 1. Register: POST /auth/v1/signup AND POST /rest/v1/profiles (Starts 100% EMPTY with 0 records)
 * 2. Login: POST /auth/v1/token?grant_type=password AND GET /rest/v1/profiles?email=eq.<email>
 *    + Fetches live records from /rest/v1/blood_glucose_records, /rest/v1/blood_pressure_records, /rest/v1/appointments
 * 3. Create / Update: POST (Upsert with Prefer: resolution=merge-duplicates) to /rest/v1/<table>
 * 4. Delete: DELETE /rest/v1/<table>?id=eq.<id>&user_email=eq.<email>
 */
object SupabaseDatabaseManager {
    private const val TAG = "MySihatSupabaseCloud"
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    private const val DB_NAME = "mysihat_patient_cloud_v3.db"
    private const val DB_VERSION = 1

    private var dbHelper: Helper? = null
    private val ioScope = CoroutineScope(Dispatchers.IO)
    var currentAccessToken: String? = null

    fun init(context: Context) {
        if (dbHelper == null) {
            dbHelper = Helper(context.applicationContext)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            seedDemoPatientIfNotExists()
        }
    }

    private class Helper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS users (
                    email TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    mykad TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    gender TEXT NOT NULL,
                    condition TEXT NOT NULL,
                    sihat_id TEXT NOT NULL,
                    blood_type TEXT NOT NULL,
                    height_cm INTEGER NOT NULL,
                    weight_kg REAL NOT NULL,
                    kin_name TEXT NOT NULL,
                    kin_relation TEXT NOT NULL,
                    kin_phone TEXT NOT NULL,
                    supabase_uid TEXT
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS glucose_records (
                    id TEXT PRIMARY KEY,
                    user_email TEXT NOT NULL,
                    value_mmol REAL NOT NULL,
                    timing TEXT NOT NULL,
                    date_label TEXT NOT NULL,
                    time_label TEXT NOT NULL,
                    timestamp_millis INTEGER NOT NULL,
                    severity TEXT NOT NULL,
                    note TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS bp_records (
                    id TEXT PRIMARY KEY,
                    user_email TEXT NOT NULL,
                    systolic INTEGER NOT NULL,
                    diastolic INTEGER NOT NULL,
                    pulse INTEGER NOT NULL,
                    date_label TEXT NOT NULL,
                    time_label TEXT NOT NULL,
                    timestamp_millis INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    severity TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS medications (
                    id TEXT NOT NULL,
                    user_email TEXT NOT NULL,
                    name TEXT NOT NULL,
                    dosage TEXT NOT NULL,
                    frequency TEXT NOT NULL,
                    instruction TEXT NOT NULL,
                    pills_remaining INTEGER NOT NULL,
                    next_dose_time TEXT NOT NULL,
                    is_taken_today INTEGER NOT NULL,
                    PRIMARY KEY (id, user_email)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS appointments (
                    user_email TEXT PRIMARY KEY,
                    id TEXT NOT NULL,
                    facility_name TEXT NOT NULL,
                    clinic_type TEXT NOT NULL,
                    session_label TEXT NOT NULL,
                    date_display TEXT NOT NULL,
                    time_display TEXT NOT NULL,
                    reference_number TEXT NOT NULL,
                    target_time_millis INTEGER NOT NULL,
                    is_checked_in INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onCreate(db)
            // Reset any previously created non-demo accounts that had old default biometric values so new accounts are 100% empty
            db.execSQL(
                "UPDATE users SET age = 0, condition = '-', blood_type = '-', height_cm = 0, weight_kg = 0.0, kin_name = '-', kin_relation = '-', kin_phone = '-' WHERE email != 'ahmad.zikri@gmail.com' AND age = 30 AND height_cm = 170"
            )
        }
    }

    /**
     * One-way SHA-256 cryptographic password hashing so plain-text passwords are never stored in Supabase public.profiles or SQLite.
     */
    fun hashPassword(password: String): String {
        if (password.startsWith("sha256:")) return password
        return try {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
            "sha256:" + bytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            password
        }
    }

    private fun verifyPasswordMatch(inputPassword: String, storedHashOrPass: String): Boolean {
        if (storedHashOrPass.isEmpty()) return false
        return storedHashOrPass == hashPassword(inputPassword) || storedHashOrPass == inputPassword
    }

    private fun seedDemoPatientIfNotExists() {
        val db = dbHelper?.writableDatabase ?: return
        val demoEmail = "ahmad.zikri@gmail.com"
        // Clean any non-demo accounts that previously had old default biometric values (age=30, height=170, weight=65.0, blood='O+')
        try {
            db.execSQL(
                "UPDATE users SET age = 0, condition = '-', blood_type = '-', height_cm = 0, weight_kg = 0.0, kin_name = '-', kin_relation = '-', kin_phone = '-' WHERE email != ? AND age = 30 AND height_cm = 170",
                arrayOf(demoEmail)
            )
        } catch (_: Exception) {}
        val cursor = db.rawQuery("SELECT email FROM users WHERE email = ?", arrayOf(demoEmail))
        val exists = cursor.moveToFirst()
        cursor.close()
        if (exists) return

        val userValues = ContentValues().apply {
            put("email", demoEmail)
            put("password", hashPassword("Zikri@2026*"))
            put("full_name", "Ahmad Zikri bin Razak")
            put("mykad", "920814-14-5532")
            put("phone", "+60 12-345 6789")
            put("age", 34)
            put("gender", "Lelaki")
            put("condition", "Diabetes Jenis 2 & Darah Tinggi")
            put("sihat_id", "MY-SIHAT-8829-KL")
            put("blood_type", "O+")
            put("height_cm", 175)
            put("weight_kg", 68.5)
            put("kin_name", "Nurul Hidayah binti Osman")
            put("kin_relation", "Isteri")
            put("kin_phone", "+60 19-876 5432")
            put("supabase_uid", "demo-patient-8829")
        }
        db.insertWithOnConflict("users", null, userValues, SQLiteDatabase.CONFLICT_REPLACE)

        val demoGlucose = listOf(
            GlucoseRecord("G-07", 5.20, GlucoseTiming.FASTING, "18 Nov 2026", "07:15 AM", 1794986100000L, HealthSeverity.NORMAL, "Bacaan puasa pagi sebelum makan"),
            GlucoseRecord("G-06", 6.85, GlucoseTiming.POST_BREAKFAST, "17 Nov 2026", "09:45 AM", 1794908700000L, HealthSeverity.NORMAL, "Selepas sarapan pagi (normal < 10.0)"),
            GlucoseRecord("G-05", 10.80, GlucoseTiming.POST_LUNCH, "16 Nov 2026", "02:30 PM", 1794839400000L, HealthSeverity.HIGH, "Melebihi sasaran 10.0 selepas makan tengah hari"),
            GlucoseRecord("G-04", 5.60, GlucoseTiming.FASTING, "15 Nov 2026", "07:30 AM", 1794727800000L, HealthSeverity.NORMAL, "Bacaan puasa pagi normal"),
            GlucoseRecord("G-03", 8.40, GlucoseTiming.POST_DINNER, "14 Nov 2026", "09:00 PM", 1794668400000L, HealthSeverity.NORMAL, "Selepas makan malam"),
            GlucoseRecord("G-02", 4.80, GlucoseTiming.BEFORE_LUNCH, "13 Nov 2026", "12:45 PM", 1794552300000L, HealthSeverity.NORMAL, "Sebelum makan tengah hari"),
            GlucoseRecord("G-01", 6.20, GlucoseTiming.POST_BREAKFAST, "12 Nov 2026", "09:30 AM", 1794475800000L, HealthSeverity.NORMAL, "Selepas sarapan pagi")
        )
        demoGlucose.forEach { saveGlucoseLocalOnly(demoEmail, it) }

        val demoBp = listOf(
            BloodPressureRecord("BP-04", 122, 78, 72, "18 Nov 2026", "08:00 AM", 1794988800000L, "Normal", HealthSeverity.NORMAL),
            BloodPressureRecord("BP-03", 125, 82, 74, "16 Nov 2026", "08:15 AM", 1794816900000L, "Normal", HealthSeverity.NORMAL),
            BloodPressureRecord("BP-02", 128, 84, 76, "14 Nov 2026", "08:00 AM", 1794643200000L, "Normal", HealthSeverity.NORMAL),
            BloodPressureRecord("BP-01", 120, 78, 70, "12 Nov 2026", "08:30 AM", 1794472200000L, "Normal", HealthSeverity.NORMAL)
        )
        demoBp.forEach { saveBpLocalOnly(demoEmail, it) }

        val demoMeds = listOf(
            Medication("MED-01", "Metformin HCl", "500 mg", "2 kali sehari", "Ambil bersama atau selepas makan", 24, "08:00 PM", true),
            Medication("MED-02", "Gliclazide MR", "30 mg", "1 kali sehari", "Ambil semasa sarapan pagi", 12, "08:00 AM", true),
            Medication("MED-03", "Perindopril Erbumine", "4 mg", "1 kali sehari", "Ambil sebelum sarapan pagi", 18, "08:00 AM", true),
            Medication("MED-04", "Atorvastatin Calcium", "20 mg", "1 kali sehari (Malam)", "Ambil sebelum tidur", 28, "10:00 PM", false)
        )
        demoMeds.forEach { saveMedicationToSqlite(demoEmail, it) }

        val demoApt = MockDataRepository.initialAppointmentTarget
        val apt = ClinicAppointment(
            id = "APT-2026-NCD-8841",
            facilityName = "Hospital Sultan Abdul Halim (HSAH Sungai Petani)",
            clinicType = "Klinik Rawatan Diabetes & NCD",
            sessionLabel = "Sesi Pagi (09:00 AM)",
            dateDisplay = demoApt.second,
            timeDisplay = "09:00 AM",
            referenceNumber = "REF-NCD-HSAH-01",
            targetTimeMillis = demoApt.first,
            isCheckedIn = false
        )
        saveAppointmentLocalOnly(demoEmail, apt)
    }

    // ==================== SUPABASE LIVE REST & AUTH HTTP ENGINE ====================

    data class SupabaseAuthResult(
        val success: Boolean,
        val errorCode: String = "",
        val errorMessage: String = "",
        val uid: String = "",
        val accessToken: String = "",
        val fullName: String = "",
        val icNumber: String = "",
        val phone: String = "",
        val gender: String = "-"
    )

    private fun callSupabaseSignUp(email: String, password: String, fullName: String, myKad: String, phone: String, gender: String = "-"): SupabaseAuthResult {
        return try {
            val url = URL("$SUPABASE_URL/auth/v1/signup")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("apikey", SUPABASE_ANON_KEY)
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            val payload = JSONObject().apply {
                put("email", email)
                put("password", password)
                put("data", JSONObject().apply {
                    put("full_name", fullName)
                    put("ic_number", myKad)
                    put("phone_number", phone)
                    put("gender", gender)
                    put("role", "patient")
                })
            }
            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""
            conn.disconnect()

            if (code in 200..299) {
                val json = JSONObject(body)
                val uid = json.optString("id", json.optJSONObject("user")?.optString("id") ?: "")
                val token = json.optString("access_token", "")
                SupabaseAuthResult(success = true, uid = uid, accessToken = token, fullName = fullName, icNumber = myKad, phone = phone, gender = gender)
            } else {
                val errJson = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val errCode = errJson.optString("error_code", "signup_error")
                val msg = errJson.optString("msg", errJson.optString("message", "Registration error"))
                SupabaseAuthResult(success = false, errorCode = errCode, errorMessage = msg)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Supabase SignUp network exception: ${e.message}")
            SupabaseAuthResult(success = false, errorCode = "network_error", errorMessage = e.message ?: "Network error")
        }
    }

    private fun callSupabaseSignIn(email: String, password: String): SupabaseAuthResult {
        return try {
            val url = URL("$SUPABASE_URL/auth/v1/token?grant_type=password")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("apikey", SUPABASE_ANON_KEY)
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            val payload = JSONObject().apply {
                put("email", email)
                put("password", password)
            }
            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } } ?: ""
            conn.disconnect()

            if (code in 200..299) {
                val json = JSONObject(body)
                val token = json.optString("access_token", "")
                val userObj = json.optJSONObject("user")
                val uid = userObj?.optString("id") ?: ""
                val meta = userObj?.optJSONObject("user_metadata")
                val name = meta?.optString("full_name", "") ?: ""
                val ic = meta?.optString("ic_number", "") ?: ""
                val ph = meta?.optString("phone_number", "") ?: ""
                val gd = meta?.optString("gender", "-") ?: "-"
                SupabaseAuthResult(success = true, uid = uid, accessToken = token, fullName = name, icNumber = ic, phone = ph, gender = gd)
            } else {
                val errJson = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val errCode = errJson.optString("error_code", "auth_failed")
                val msg = errJson.optString("msg", errJson.optString("message", "Invalid login credentials"))
                SupabaseAuthResult(success = false, errorCode = errCode, errorMessage = msg)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Supabase SignIn network exception: ${e.message}")
            SupabaseAuthResult(success = false, errorCode = "network_error", errorMessage = e.message ?: "Network error")
        }
    }

    /**
     * Direct Supabase PostgREST GET query: returns JSONArray or null if table not created yet.
     */
    private fun supabaseRestGet(pathWithQuery: String): JSONArray? {
        return try {
            val url = URL("$SUPABASE_URL/rest/v1/$pathWithQuery")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4500
                readTimeout = 4500
                setRequestProperty("apikey", SUPABASE_ANON_KEY)
                if (!currentAccessToken.isNullOrBlank()) {
                    setRequestProperty("Authorization", "Bearer $currentAccessToken")
                }
                setRequestProperty("Accept", "application/json")
            }
            val code = conn.responseCode
            if (code in 200..299) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                conn.disconnect()
                JSONArray(body)
            } else {
                conn.disconnect()
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "supabaseRestGet ($pathWithQuery) error: ${e.message}")
            null
        }
    }

    /**
     * Direct Supabase PostgREST UPSERT (POST with Prefer: resolution=merge-duplicates).
     */
    private fun supabaseRestUpsertAsync(table: String, jsonPayload: JSONObject) {
        ioScope.launch {
            try {
                val url = URL("$SUPABASE_URL/rest/v1/$table")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("apikey", SUPABASE_ANON_KEY)
                    if (!currentAccessToken.isNullOrBlank()) {
                        setRequestProperty("Authorization", "Bearer $currentAccessToken")
                    }
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Prefer", "resolution=merge-duplicates,return=minimal")
                    doOutput = true
                }
                OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use {
                    it.write(jsonPayload.toString())
                    it.flush()
                }
                conn.responseCode
                conn.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "supabaseRestUpsertAsync ($table) error: ${e.message}")
            }
        }
    }

    /**
     * Direct Supabase PostgREST DELETE.
     */
    private fun supabaseRestDeleteAsync(pathWithFilter: String) {
        ioScope.launch {
            try {
                val url = URL("$SUPABASE_URL/rest/v1/$pathWithFilter")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("apikey", SUPABASE_ANON_KEY)
                    if (!currentAccessToken.isNullOrBlank()) {
                        setRequestProperty("Authorization", "Bearer $currentAccessToken")
                    }
                }
                conn.responseCode
                conn.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "supabaseRestDeleteAsync ($pathWithFilter) error: ${e.message}")
            }
        }
    }

    // ==================== 100% SUPABASE REGISTER, LOGIN & CLOUD FETCH ====================

    /**
     * Automatically calculates patient age from the first 2 digits (YY) of Malaysian MyKad / IC number.
     */
    fun calculateAgeFromMyKad(myKad: String): Int {
        val digits = myKad.filter { it.isDigit() }
        if (digits.length < 2) return 0
        val yy = digits.substring(0, 2).toIntOrNull() ?: return 0
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentTwoDigit = currentYear % 100
        val birthYear = if (yy <= currentTwoDigit) 2000 + yy else 1900 + yy
        val age = currentYear - birthYear
        return if (age in 1..120) age else 0
    }

    fun registerNewUser(
        name: String,
        myKad: String,
        email: String,
        password: String,
        phone: String,
        gender: String = ""
    ): Pair<Boolean, String> {
        val isEn = LanguageManager.isEnglish
        val cleanEmail = email.trim().lowercase(Locale.US)
        val cleanName = name.trim()
        val cleanMyKad = myKad.trim()
        val cleanPhone = phone.trim().ifBlank { "-" }
        val cleanGender = gender.trim().ifBlank { "-" }

        if (cleanName.isBlank() || cleanMyKad.isBlank() || cleanEmail.isBlank() || password.isBlank() || gender.isBlank()) {
            return Pair(
                false,
                if (isEn) "Registration Failed: All fields including gender and password are required!"
                else "Pendaftaran Tidak Berjaya: Sila lengkapkan Nama, MyKad, Jantina, E-mel dan Kata Laluan!"
            )
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Pair(
                false,
                if (isEn) "Registration Failed: Please enter a valid email address!"
                else "Pendaftaran Tidak Berjaya: Format alamat e-mel tidak sah!"
            )
        }
        if (password.length < 6) {
            return Pair(
                false,
                if (isEn) "Registration Failed: Password must be at least 6 characters!"
                else "Pendaftaran Tidak Berjaya: Kata laluan mestilah sekurang-kurangnya 6 aksara!"
            )
        }

        val encEmail = URLEncoder.encode(cleanEmail, "UTF-8")

        // 1. Check if email already exists in Supabase public.profiles table
        val cloudProfiles = supabaseRestGet("profiles?email=eq.$encEmail&select=email")
        if (cloudProfiles != null && cloudProfiles.length() > 0) {
            return Pair(
                false,
                if (isEn) "Registration Failed: Email '$cleanEmail' is already registered in Supabase! Please log in."
                else "Pendaftaran Tidak Berjaya: E-mel '$cleanEmail' telah wujud di Supabase! Sila log masuk."
            )
        }

        // Check local cache too
        val db = dbHelper?.writableDatabase
        if (db != null) {
            val cursor = db.rawQuery("SELECT email FROM users WHERE email = ?", arrayOf(cleanEmail))
            val alreadyExists = cursor.moveToFirst()
            cursor.close()
            if (alreadyExists) {
                return Pair(
                    false,
                    if (isEn) "Registration Failed: Email '$cleanEmail' is already registered! Please log in."
                    else "Pendaftaran Tidak Berjaya: E-mel '$cleanEmail' telah didaftarkan! Sila log masuk."
                )
            }
        }

        // 2. Register user on Live Supabase Auth (POST /auth/v1/signup)
        val supaRes = callSupabaseSignUp(cleanEmail, password, cleanName, cleanMyKad, cleanPhone, cleanGender)
        if (!supaRes.success && (supaRes.errorCode.contains("already_exists", ignoreCase = true) || supaRes.errorMessage.contains("already registered", ignoreCase = true))) {
            return Pair(
                false,
                if (isEn) "Registration Failed: Email '$cleanEmail' already exists in Supabase Auth! Please log in."
                else "Pendaftaran Tidak Berjaya: E-mel '$cleanEmail' telah wujud di Supabase Auth! Sila log masuk."
            )
        }

        val generatedSihatId = "MY-SIHAT-${(1000..9999).random()}-MY"
        val uid = supaRes.uid.ifBlank { "usr-${System.currentTimeMillis()}" }
        val autoAgeFromIc = calculateAgeFromMyKad(cleanMyKad)
        if (supaRes.accessToken.isNotBlank()) {
            currentAccessToken = supaRes.accessToken
        }

        val hashedPass = hashPassword(password)

        // 3. Upsert new user profile directly to Supabase public.profiles table (Password stored as SHA-256 hash; Biometrics start 100% EMPTY)
        supabaseRestUpsertAsync(
            "profiles",
            JSONObject().apply {
                put("email", cleanEmail)
                put("password", hashedPass)
                put("full_name", cleanName)
                put("ic_number", cleanMyKad)
                put("phone_number", cleanPhone)
                put("age", autoAgeFromIc)
                put("gender", cleanGender)
                put("condition", "-")
                put("sihat_id", generatedSihatId)
                put("blood_type", "-")
                put("height_cm", 0)
                put("weight_kg", 0.0)
                put("kin_name", "-")
                put("kin_relation", "-")
                put("kin_phone", "-")
            }
        )

        // 4. Save new user account in local SQLite with ZERO records & EMPTY biometrics (Age auto-derived from IC first 2 digits)
        if (db != null) {
            db.delete("glucose_records", "user_email = ?", arrayOf(cleanEmail))
            db.delete("bp_records", "user_email = ?", arrayOf(cleanEmail))
            db.delete("medications", "user_email = ?", arrayOf(cleanEmail))
            db.delete("appointments", "user_email = ?", arrayOf(cleanEmail))

            val cv = ContentValues().apply {
                put("email", cleanEmail)
                put("password", hashedPass)
                put("full_name", cleanName)
                put("mykad", cleanMyKad)
                put("phone", cleanPhone)
                put("age", autoAgeFromIc)
                put("gender", cleanGender)
                put("condition", "-")
                put("sihat_id", generatedSihatId)
                put("blood_type", "-")
                put("height_cm", 0)
                put("weight_kg", 0.0)
                put("kin_name", "-")
                put("kin_relation", "-")
                put("kin_phone", "-")
                put("supabase_uid", uid)
            }
            db.insertWithOnConflict("users", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }

        loadAccountState(cleanEmail)

        return Pair(
            true,
            if (isEn) "Supabase Account Created! Welcome $cleanName (New Empty Profile)."
            else "Akaun Baharu Berjaya Didaftarkan di Supabase! Selamat datang, $cleanName."
        )
    }

    fun authenticateUser(email: String, password: String): Pair<Boolean, String> {
        val isEn = LanguageManager.isEnglish
        val cleanEmail = email.trim().lowercase(Locale.US)

        if (cleanEmail.isBlank() || password.isBlank()) {
            return Pair(
                false,
                if (isEn) "Login Failed: Please enter both email and password!"
                else "Log Masuk Tidak Berjaya: Sila masukkan alamat e-mel dan kata laluan!"
            )
        }

        val encEmail = URLEncoder.encode(cleanEmail, "UTF-8")
        val db = dbHelper?.writableDatabase

        // 1. Check Live Supabase public.profiles table first if available
        val cloudProfiles = supabaseRestGet("profiles?email=eq.$encEmail&select=*")
        if (cloudProfiles != null && cloudProfiles.length() > 0) {
            val prof = cloudProfiles.getJSONObject(0)
            val cloudPass = prof.optString("password", "")
            val fullName = prof.optString("full_name", cleanEmail)

            if (cloudPass.isNotEmpty() && !verifyPasswordMatch(password, cloudPass)) {
                return Pair(
                    false,
                    if (isEn) "Login Failed: Incorrect password for '$cleanEmail'!"
                    else "Log Masuk Tidak Berjaya: Kata laluan salah bagi akaun '$cleanEmail'!"
                )
            }

            // Cache cloud profile to SQLite (empty biometric defaults when not yet filled)
            if (db != null) {
                val cv = ContentValues().apply {
                    put("email", cleanEmail)
                    put("password", hashPassword(password))
                    put("full_name", fullName)
                    put("mykad", prof.optString("ic_number", "-"))
                    put("phone", prof.optString("phone_number", "-"))
                    put("age", prof.optInt("age", 0))
                    put("gender", prof.optString("gender", "-"))
                    put("condition", prof.optString("condition", "-"))
                    put("sihat_id", prof.optString("sihat_id", "MY-SIHAT-1001"))
                    put("blood_type", prof.optString("blood_type", "-"))
                    put("height_cm", prof.optInt("height_cm", 0))
                    put("weight_kg", prof.optDouble("weight_kg", 0.0))
                    put("kin_name", prof.optString("kin_name", "-"))
                    put("kin_relation", prof.optString("kin_relation", "-"))
                    put("kin_phone", prof.optString("kin_phone", "-"))
                }
                db.insertWithOnConflict("users", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }

            loadAccountState(cleanEmail)
            return Pair(
                true,
                if (isEn) "Supabase Login Successful! Welcome back, $fullName."
                else "Log Masuk Supabase Berjaya! Selamat kembali, $fullName."
            )
        }

        // 2. Check local SQLite users table
        if (db != null) {
            val cursor = db.rawQuery("SELECT password, full_name FROM users WHERE email = ?", arrayOf(cleanEmail))
            val foundLocal = cursor.moveToFirst()
            if (foundLocal) {
                val storedPass = cursor.getString(0)
                val fullName = cursor.getString(1)
                cursor.close()

                if (!verifyPasswordMatch(password, storedPass)) {
                    return Pair(
                        false,
                        if (isEn) "Login Failed: Incorrect password for '$cleanEmail'!"
                        else "Log Masuk Tidak Berjaya: Kata laluan salah bagi akaun '$cleanEmail'!"
                    )
                }

                loadAccountState(cleanEmail)
                return Pair(
                    true,
                    if (isEn) "Login Successful! Welcome back, $fullName."
                    else "Log Masuk Berjaya! Selamat kembali, $fullName."
                )
            }
            cursor.close()
        }

        // 3. Check Live Supabase Auth (/auth/v1/token?grant_type=password)
        val supaLogin = callSupabaseSignIn(cleanEmail, password)
        val isSupabasePasswordVerified = supaLogin.success || supaLogin.errorCode == "email_not_confirmed"

        if (!isSupabasePasswordVerified) {
            return Pair(
                false,
                if (isEn) "Login Failed: Account not found or invalid password! Please register first."
                else "Log Masuk Tidak Berjaya: Akaun tidak wujud atau kata laluan salah! Sila daftar akaun baharu."
            )
        }

        if (supaLogin.accessToken.isNotBlank()) {
            currentAccessToken = supaLogin.accessToken
        }
        val resolvedName = supaLogin.fullName.ifBlank { cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val resolvedIc = supaLogin.icNumber.ifBlank { "-" }
        val resolvedPhone = supaLogin.phone.ifBlank { "-" }
        val resolvedGender = supaLogin.gender.ifBlank { "-" }

        if (db != null) {
            val cv = ContentValues().apply {
                put("email", cleanEmail)
                put("password", hashPassword(password))
                put("full_name", resolvedName)
                put("mykad", resolvedIc)
                put("phone", resolvedPhone)
                put("age", 0)
                put("gender", resolvedGender)
                put("condition", "-")
                put("sihat_id", "MY-SIHAT-${(1000..9999).random()}-MY")
                put("blood_type", "-")
                put("height_cm", 0)
                put("weight_kg", 0.0)
                put("kin_name", "-")
                put("kin_relation", "-")
                put("kin_phone", "-")
                put("supabase_uid", supaLogin.uid)
            }
            db.insertWithOnConflict("users", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }

        loadAccountState(cleanEmail)
        return Pair(
            true,
            if (isEn) "Supabase Login Successful! Welcome back, $resolvedName."
            else "Log Masuk Supabase Berjaya! Selamat kembali, $resolvedName."
        )
    }

    /**
     * Loads the user's records from Live Supabase REST tables (if created) and local SQLite cache.
     */
    fun loadAccountState(email: String) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        val encEmail = URLEncoder.encode(cleanEmail, "UTF-8")
        val db = dbHelper?.writableDatabase ?: return

        // Pull latest cloud records from Supabase REST if tables exist
        val cloudGlucose = supabaseRestGet("blood_glucose_records?user_email=eq.$encEmail&order=timestamp_millis.desc")
        if (cloudGlucose != null) {
            db.delete("glucose_records", "user_email = ?", arrayOf(cleanEmail))
            for (i in 0 until cloudGlucose.length()) {
                val obj = cloudGlucose.getJSONObject(i)
                val timingEnum = try { GlucoseTiming.valueOf(obj.optString("meal_context", "FASTING")) } catch (_: Exception) { GlucoseTiming.FASTING }
                val sevEnum = try { HealthSeverity.valueOf(obj.optString("severity", "NORMAL")) } catch (_: Exception) { HealthSeverity.NORMAL }
                saveGlucoseLocalOnly(
                    cleanEmail,
                    GlucoseRecord(
                        id = obj.optString("id", "G-$i"),
                        valueMmol = obj.optDouble("glucose_value", 5.0),
                        timing = timingEnum,
                        dateLabel = obj.optString("date_label", ""),
                        timeLabel = obj.optString("time_label", ""),
                        timestampMillis = obj.optLong("timestamp_millis", System.currentTimeMillis()),
                        severity = sevEnum,
                        note = obj.optString("notes", "")
                    )
                )
            }
        }

        val cloudBp = supabaseRestGet("blood_pressure_records?user_email=eq.$encEmail&order=timestamp_millis.desc")
        if (cloudBp != null) {
            db.delete("bp_records", "user_email = ?", arrayOf(cleanEmail))
            for (i in 0 until cloudBp.length()) {
                val obj = cloudBp.getJSONObject(i)
                val sevEnum = try { HealthSeverity.valueOf(obj.optString("severity", "NORMAL")) } catch (_: Exception) { HealthSeverity.NORMAL }
                saveBpLocalOnly(
                    cleanEmail,
                    BloodPressureRecord(
                        id = obj.optString("id", "BP-$i"),
                        systolic = obj.optInt("systolic", 120),
                        diastolic = obj.optInt("diastolic", 80),
                        pulse = obj.optInt("pulse_rate", 72),
                        dateLabel = obj.optString("date_label", ""),
                        timeLabel = obj.optString("time_label", ""),
                        timestampMillis = obj.optLong("timestamp_millis", System.currentTimeMillis()),
                        category = obj.optString("category", "Normal"),
                        severity = sevEnum
                    )
                )
            }
        }

        val cloudApt = supabaseRestGet("appointments?user_email=eq.$encEmail")
        if (cloudApt != null && cloudApt.length() > 0) {
            val obj = cloudApt.getJSONObject(0)
            saveAppointmentLocalOnly(
                cleanEmail,
                ClinicAppointment(
                    id = obj.optString("id", "APT-01"),
                    facilityName = obj.optString("facility_name", ""),
                    clinicType = obj.optString("clinic_type", ""),
                    sessionLabel = obj.optString("session_label", ""),
                    dateDisplay = obj.optString("date_display", ""),
                    timeDisplay = obj.optString("time_display", ""),
                    referenceNumber = obj.optString("reference_number", "REF-NCD-01"),
                    targetTimeMillis = obj.optLong("target_time_millis", System.currentTimeMillis()),
                    isCheckedIn = obj.optBoolean("is_checked_in", false)
                )
            )
        }

        // Now populate Compose State from synchronized SQLite for this specific user email
        val uCursor = db.rawQuery(
            "SELECT full_name, mykad, email, password, age, gender, condition, sihat_id, blood_type, height_cm, weight_kg, kin_name, kin_relation, kin_phone FROM users WHERE email = ?",
            arrayOf(cleanEmail)
        )
        if (uCursor.moveToFirst()) {
            val loadedMyKad = uCursor.getString(1)
            val storedAge = uCursor.getInt(4)
            val icAge = calculateAgeFromMyKad(loadedMyKad)
            val resolvedAge = if (icAge > 0) icAge else storedAge
            if (icAge > 0 && storedAge != icAge) {
                try {
                    db.execSQL("UPDATE users SET age = ? WHERE email = ?", arrayOf(icAge, cleanEmail))
                } catch (_: Exception) {}
            }
            MockDataRepository.currentUser.value = UserProfile(
                name = uCursor.getString(0),
                myKad = loadedMyKad,
                email = uCursor.getString(2),
                password = uCursor.getString(3),
                age = resolvedAge,
                gender = uCursor.getString(5),
                condition = uCursor.getString(6),
                sihatId = uCursor.getString(7),
                bloodType = uCursor.getString(8),
                heightCm = uCursor.getInt(9),
                weightKg = uCursor.getDouble(10),
                emergencyContact = EmergencyContact(
                    name = uCursor.getString(11),
                    relationship = uCursor.getString(12),
                    phone = uCursor.getString(13)
                )
            )
        }
        uCursor.close()

        MockDataRepository.glucoseRecords.clear()
        val gCursor = db.rawQuery(
            "SELECT id, value_mmol, timing, date_label, time_label, timestamp_millis, severity, note FROM glucose_records WHERE user_email = ? ORDER BY timestamp_millis DESC",
            arrayOf(cleanEmail)
        )
        while (gCursor.moveToNext()) {
            val timingEnum = try { GlucoseTiming.valueOf(gCursor.getString(2)) } catch (_: Exception) { GlucoseTiming.FASTING }
            val sevEnum = try { HealthSeverity.valueOf(gCursor.getString(6)) } catch (_: Exception) { HealthSeverity.NORMAL }
            MockDataRepository.glucoseRecords.add(
                GlucoseRecord(
                    id = gCursor.getString(0),
                    valueMmol = gCursor.getDouble(1),
                    timing = timingEnum,
                    dateLabel = gCursor.getString(3),
                    timeLabel = gCursor.getString(4),
                    timestampMillis = gCursor.getLong(5),
                    severity = sevEnum,
                    note = gCursor.getString(7)
                )
            )
        }
        gCursor.close()

        MockDataRepository.bpRecords.clear()
        val bpCursor = db.rawQuery(
            "SELECT id, systolic, diastolic, pulse, date_label, time_label, timestamp_millis, category, severity FROM bp_records WHERE user_email = ? ORDER BY timestamp_millis DESC",
            arrayOf(cleanEmail)
        )
        while (bpCursor.moveToNext()) {
            val sevEnum = try { HealthSeverity.valueOf(bpCursor.getString(8)) } catch (_: Exception) { HealthSeverity.NORMAL }
            MockDataRepository.bpRecords.add(
                BloodPressureRecord(
                    id = bpCursor.getString(0),
                    systolic = bpCursor.getInt(1),
                    diastolic = bpCursor.getInt(2),
                    pulse = bpCursor.getInt(3),
                    dateLabel = bpCursor.getString(4),
                    timeLabel = bpCursor.getString(5),
                    timestampMillis = bpCursor.getLong(6),
                    category = bpCursor.getString(7),
                    severity = sevEnum
                )
            )
        }
        bpCursor.close()

        MockDataRepository.medications.clear()
        val mCursor = db.rawQuery(
            "SELECT id, name, dosage, frequency, instruction, pills_remaining, next_dose_time, is_taken_today FROM medications WHERE user_email = ?",
            arrayOf(cleanEmail)
        )
        while (mCursor.moveToNext()) {
            MockDataRepository.medications.add(
                Medication(
                    id = mCursor.getString(0),
                    name = mCursor.getString(1),
                    dosage = mCursor.getString(2),
                    frequency = mCursor.getString(3),
                    instruction = mCursor.getString(4),
                    pillsRemaining = mCursor.getInt(5),
                    nextDoseTime = mCursor.getString(6),
                    isTakenToday = mCursor.getInt(7) == 1
                )
            )
        }
        mCursor.close()

        val aCursor = db.rawQuery(
            "SELECT id, facility_name, clinic_type, session_label, date_display, time_display, reference_number, target_time_millis, is_checked_in FROM appointments WHERE user_email = ?",
            arrayOf(cleanEmail)
        )
        if (aCursor.moveToFirst()) {
            MockDataRepository.hasScheduledAppointment.value = true
            MockDataRepository.nextAppointment.value = ClinicAppointment(
                id = aCursor.getString(0),
                facilityName = aCursor.getString(1),
                clinicType = aCursor.getString(2),
                sessionLabel = aCursor.getString(3),
                dateDisplay = aCursor.getString(4),
                timeDisplay = aCursor.getString(5),
                referenceNumber = aCursor.getString(6),
                targetTimeMillis = aCursor.getLong(7),
                isCheckedIn = aCursor.getInt(8) == 1
            )
        } else {
            MockDataRepository.hasScheduledAppointment.value = false
        }
        aCursor.close()
    }

    // ==================== 100% SUPABASE + LOCAL CRUD OPERATIONS ====================

    fun saveUserProfileToSqlite(profile: UserProfile) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = profile.email.trim().lowercase(Locale.US)
        val hashedPass = hashPassword(profile.password)
        val cv = ContentValues().apply {
            put("email", cleanEmail)
            put("password", hashedPass)
            put("full_name", profile.name)
            put("mykad", profile.myKad)
            put("age", profile.age)
            put("gender", profile.gender)
            put("condition", profile.condition)
            put("sihat_id", profile.sihatId)
            put("blood_type", profile.bloodType)
            put("height_cm", profile.heightCm)
            put("weight_kg", profile.weightKg)
            put("kin_name", profile.emergencyContact.name)
            put("kin_relation", profile.emergencyContact.relationship)
            put("kin_phone", profile.emergencyContact.phone)
        }
        db.update("users", cv, "email = ?", arrayOf(cleanEmail))

        // Upsert directly to Supabase public.profiles (Password stored as SHA-256 hash)
        supabaseRestUpsertAsync(
            "profiles",
            JSONObject().apply {
                put("email", cleanEmail)
                put("password", hashedPass)
                put("full_name", profile.name)
                put("ic_number", profile.myKad)
                put("age", profile.age)
                put("gender", profile.gender)
                put("condition", profile.condition)
                put("sihat_id", profile.sihatId)
                put("blood_type", profile.bloodType)
                put("height_cm", profile.heightCm)
                put("weight_kg", profile.weightKg)
                put("kin_name", profile.emergencyContact.name)
                put("kin_relation", profile.emergencyContact.relationship)
                put("kin_phone", profile.emergencyContact.phone)
            }
        )
    }

    private fun saveGlucoseLocalOnly(email: String, record: GlucoseRecord) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        val cv = ContentValues().apply {
            put("id", record.id)
            put("user_email", cleanEmail)
            put("value_mmol", record.valueMmol)
            put("timing", record.timing.name)
            put("date_label", record.dateLabel)
            put("time_label", record.timeLabel)
            put("timestamp_millis", record.timestampMillis)
            put("severity", record.severity.name)
            put("note", record.note)
        }
        db.insertWithOnConflict("glucose_records", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun saveGlucoseToSqlite(email: String, record: GlucoseRecord) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        saveGlucoseLocalOnly(cleanEmail, record)

        // Upsert directly to Supabase public.blood_glucose_records
        supabaseRestUpsertAsync(
            "blood_glucose_records",
            JSONObject().apply {
                put("id", record.id)
                put("user_email", cleanEmail)
                put("glucose_value", record.valueMmol)
                put("meal_context", record.timing.name)
                put("date_label", record.dateLabel)
                put("time_label", record.timeLabel)
                put("timestamp_millis", record.timestampMillis)
                put("severity", record.severity.name)
                put("notes", record.note)
            }
        )
    }

    fun deleteGlucoseFromSqlite(email: String, recordId: String) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        db.delete("glucose_records", "id = ? AND user_email = ?", arrayOf(recordId, cleanEmail))

        val encId = URLEncoder.encode(recordId, "UTF-8")
        val encEmail = URLEncoder.encode(cleanEmail, "UTF-8")
        supabaseRestDeleteAsync("blood_glucose_records?id=eq.$encId&user_email=eq.$encEmail")
    }

    private fun saveBpLocalOnly(email: String, record: BloodPressureRecord) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        val cv = ContentValues().apply {
            put("id", record.id)
            put("user_email", cleanEmail)
            put("systolic", record.systolic)
            put("diastolic", record.diastolic)
            put("pulse", record.pulse)
            put("date_label", record.dateLabel)
            put("time_label", record.timeLabel)
            put("timestamp_millis", record.timestampMillis)
            put("category", record.category)
            put("severity", record.severity.name)
        }
        db.insertWithOnConflict("bp_records", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun saveBpToSqlite(email: String, record: BloodPressureRecord) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        saveBpLocalOnly(cleanEmail, record)

        // Upsert directly to Supabase public.blood_pressure_records
        supabaseRestUpsertAsync(
            "blood_pressure_records",
            JSONObject().apply {
                put("id", record.id)
                put("user_email", cleanEmail)
                put("systolic", record.systolic)
                put("diastolic", record.diastolic)
                put("pulse_rate", record.pulse)
                put("date_label", record.dateLabel)
                put("time_label", record.timeLabel)
                put("timestamp_millis", record.timestampMillis)
                put("category", record.category)
                put("severity", record.severity.name)
            }
        )
    }

    fun deleteBpFromSqlite(email: String, recordId: String) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        db.delete("bp_records", "id = ? AND user_email = ?", arrayOf(recordId, cleanEmail))

        val encId = URLEncoder.encode(recordId, "UTF-8")
        val encEmail = URLEncoder.encode(cleanEmail, "UTF-8")
        supabaseRestDeleteAsync("blood_pressure_records?id=eq.$encId&user_email=eq.$encEmail")
    }

    fun saveMedicationToSqlite(email: String, med: Medication) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        val cv = ContentValues().apply {
            put("id", med.id)
            put("user_email", cleanEmail)
            put("name", med.name)
            put("dosage", med.dosage)
            put("frequency", med.frequency)
            put("instruction", med.instruction)
            put("pills_remaining", med.pillsRemaining)
            put("next_dose_time", med.nextDoseTime)
            put("is_taken_today", if (med.isTakenToday) 1 else 0)
        }
        db.insertWithOnConflict("medications", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun saveAppointmentLocalOnly(email: String, apt: ClinicAppointment) {
        val db = dbHelper?.writableDatabase ?: return
        val cleanEmail = email.trim().lowercase(Locale.US)
        val cv = ContentValues().apply {
            put("user_email", cleanEmail)
            put("id", apt.id)
            put("facility_name", apt.facilityName)
            put("clinic_type", apt.clinicType)
            put("session_label", apt.sessionLabel)
            put("date_display", apt.dateDisplay)
            put("time_display", apt.timeDisplay)
            put("reference_number", apt.referenceNumber)
            put("target_time_millis", apt.targetTimeMillis)
            put("is_checked_in", if (apt.isCheckedIn) 1 else 0)
        }
        db.insertWithOnConflict("appointments", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun saveAppointmentToSqlite(email: String, apt: ClinicAppointment) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        saveAppointmentLocalOnly(cleanEmail, apt)

        // Upsert directly to Supabase public.appointments
        supabaseRestUpsertAsync(
            "appointments",
            JSONObject().apply {
                put("user_email", cleanEmail)
                put("id", apt.id)
                put("facility_name", apt.facilityName)
                put("clinic_type", apt.clinicType)
                put("session_label", apt.sessionLabel)
                put("date_display", apt.dateDisplay)
                put("time_display", apt.timeDisplay)
                put("reference_number", apt.referenceNumber)
                put("target_time_millis", apt.targetTimeMillis)
                put("is_checked_in", apt.isCheckedIn)
            }
        )
    }
}
