package com.example.mysihat.data

data class EmergencyContact(
    val name: String = "Nurul Huda binti Halim",
    val relationship: String = "Isteri (Spouse)",
    val phone: String = "+60 12-345 6789"
)

data class UserProfile(
    val name: String = "Ahmad Zikri bin Razak",
    val myKad: String = "920814-14-5532",
    val email: String = "ahmad.zikri@gmail.com",
    val password: String = "Zikri@2026*",
    val age: Int = 34,
    val gender: String = "Lelaki",
    val condition: String = "Diabetes Jenis 2 & Darah Tinggi",
    val sihatId: String = "MY-SIHAT-8829-KL",
    val riskStatus: String = "SIHAT (RISIKO RENDAH)",
    val vaccinationStatus: String = "LENGKAP (DOS 1 & 2)",
    val bloodType: String = "O+",
    val heightCm: Int = 175,
    val weightKg: Double = 68.5,
    val emergencyContact: EmergencyContact = EmergencyContact()
)

/**
 * Malaysian Clinical Practice Guidelines (CPG) 9 Meal Timings.
 */
enum class GlucoseTiming(val bmLabel: String, val enLabel: String, val isPostMeal: Boolean = false) {
    FASTING("Puasa (Sebelum Sarapan)", "Fasting (Pre-Breakfast)", false),
    BEFORE_BREAKFAST("Sebelum Sarapan", "Before Breakfast", false),
    POST_BREAKFAST("Selepas Sarapan", "After Breakfast", true),
    BEFORE_LUNCH("Sebelum Makan Tengah Hari", "Before Lunch", false),
    POST_LUNCH("Selepas Makan Tengah Hari", "After Lunch", true),
    MINUM_PETANG("Minum Petang", "Afternoon Tea", false),
    BEFORE_DINNER("Sebelum Makan Malam", "Before Dinner", false),
    POST_DINNER("Selepas Makan Malam", "After Dinner", true),
    BEDTIME("Sebelum Tidur", "Before Bedtime", false)
}

/**
 * 3 INDICATORS ONLY AS REQUESTED:
 * 1. LOW (Rendah)
 * 2. NORMAL (Normal)
 * 3. HIGH (Tinggi)
 */
enum class HealthSeverity(val bmLabel: String, val enLabel: String) {
    LOW("Rendah", "Low"),
    NORMAL("Normal", "Normal"),
    HIGH("Tinggi", "High")
}

data class GlucoseRecord(
    val id: String,
    val valueMmol: Double,
    val timing: GlucoseTiming,
    val dateLabel: String,
    val timeLabel: String,
    val timestampMillis: Long,
    val severity: HealthSeverity,
    val note: String = ""
)

data class BloodPressureRecord(
    val id: String,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val dateLabel: String,
    val timeLabel: String,
    val timestampMillis: Long,
    val category: String,
    val severity: HealthSeverity
)

enum class FacilityType(val bmLabel: String, val enLabel: String) {
    ALL("Semua", "All"),
    KLINIK_KESIHATAN("Klinik Kesihatan", "Health Clinic"),
    HOSPITAL("Hospital Kerajaan", "Government Hospital")
}

data class ClinicService(
    val name: String,
    val iconTag: String,
    val description: String
)

data class ClinicFacility(
    val id: String,
    val name: String,
    val type: FacilityType,
    val state: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val currentQueueNumber: String,
    val waitTimeMins: Int,
    val phoneNumber: String,
    val isOpenNow: Boolean = true,
    val operatingHours: String,
    val services: List<ClinicService>
) {
    /**
     * Real-time calculation based on Malaysian local time (UTC+8 / Asia/Kuala_Lumpur):
     * - Hospitals are open 24 Hours.
     * - Klinik Kesihatan are open 8:00 AM – 5:00 PM (Monday – Friday), closed on weekends and after 5:00 PM.
     */
    val isCurrentlyOpen: Boolean
        get() {
            if (type == FacilityType.HOSPITAL || operatingHours.contains("24 Jam", ignoreCase = true)) {
                return true
            }
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
            val day = cal.get(java.util.Calendar.DAY_OF_WEEK)
            val isWeekday = day in java.util.Calendar.MONDAY..java.util.Calendar.FRIDAY
            if (!isWeekday) return false

            val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = cal.get(java.util.Calendar.MINUTE)
            val minuteOfDay = hour * 60 + minute
            // 8:00 AM = 480 mins, 5:00 PM = 1020 mins
            return minuteOfDay in 480 until 1020
        }
}

/**
 * Public Healthcare Treatment Appointment (Patient does not set doctor name).
 */
data class KkmAppointment(
    val id: String,
    val facilityName: String,
    val clinicType: String,
    val sessionLabel: String,
    val dateDisplay: String,
    val timeDisplay: String,
    val referenceNumber: String,
    val targetTimeMillis: Long,
    val isCheckedIn: Boolean = false
)

typealias ClinicAppointment = KkmAppointment
typealias AwamAppointment = KkmAppointment

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val instruction: String,
    val pillsRemaining: Int,
    val nextDoseTime: String,
    val isTakenToday: Boolean
)
