package com.example.stayeasehotel.service

import android.util.Log
import com.example.stayeasehotel.model.StaffEntity
import com.example.stayeasehotel.model.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // NEW: Check if email already exists in either Users or Staff collection
    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            // Check in Users collection
            val userQuery = db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .await()

            // Check in Staff collection
            val staffQuery = db.collection("Staff")
                .whereEqualTo("email", email)
                .get()
                .await()

            // Return true if email exists in either collection
            !userQuery.isEmpty || !staffQuery.isEmpty
        } catch (e: Exception) {
            Log.e("AuthService", "Error checking email existence", e)
            false
        }
    }

    // Register User with Authentication
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phoneNum: String,
        gender: String,
        dateOfBirth: String
    ): Result<String> {
        return try {
            // 1. Create authentication account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // 2. Update user profile with name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            authResult.user?.updateProfile(profileUpdates)?.await()

            // 3. Save user data to Firestore
            val userData = hashMapOf(
                "userId" to userId,
                "name" to name,
                "email" to email,
                "phoneNum" to phoneNum,
                "gender" to gender,
                "dateOfBirth" to dateOfBirth
            )

            db.collection("Users").document(userId).set(userData).await()

            Result.success(userId)
        } catch (e: Exception) {
            Log.e("AuthService", "User registration failed", e)
            Result.failure(e)
        }
    }

    // Register Staff with Authentication
    suspend fun registerStaff(
        email: String,
        password: String,
        name: String,
        phoneNum: String,
        gender: String,
        post: String
    ): Result<String> {
        return try {
            // 1. Create authentication account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val staffId = authResult.user?.uid ?: throw Exception("Staff ID is null")

            // 2. Save staff data to Firestore
            val staffData = hashMapOf(
                "staffId" to staffId,
                "name" to name,
                "email" to email,
                "phoneNum" to phoneNum,
                "gender" to gender,
                "post" to post
            )

            db.collection("Staff").document(staffId).set(staffData).await()

            Result.success(staffId)
        } catch (e: Exception) {
            Log.e("AuthService", "Staff registration failed", e)
            Result.failure(e)
        }
    }

    // Login function for both user and staff
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Login failed")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e("AuthService", "Login failed", e)
            Result.failure(e)
        }
    }

    // Check if current user is authenticated
    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    // Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Logout function
    fun logout() {
        auth.signOut()
    }

    // Get user type (user or staff)
    suspend fun getUserType(userId: String): String {
        return try {
            // Check if user exists in Users collection
            val userDoc = db.collection("Users").document(userId).get().await()
            if (userDoc.exists()) {
                "user"
            } else {
                // Check if user exists in Staff collection
                val staffDoc = db.collection("Staff").document(userId).get().await()
                if (staffDoc.exists()) {
                    "staff"
                } else {
                    "unknown"
                }
            }
        } catch (e: Exception) {
            "unknown"
        }
    }

    suspend fun getUserDetails(userId: String): UserEntity? {
        return try {
            val userDoc = db.collection("Users").document(userId).get().await()
            if (userDoc.exists()) {
                val data = userDoc.data!!
                UserEntity(
                    userId = userId,
                    name = data["name"] as? String ?: "",
                    email = data["email"] as? String ?: "",
                    phoneNum = data["phoneNum"] as? String ?: "",
                    gender = data["gender"] as? String ?: "",
                    dateOfBirth = data["dateOfBirth"] as? String ?: ""
                    // UI-only fields like password, confirmPassword will remain default
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error getting user details", e)
            null
        }
    }

    suspend fun getStaffDetails(staffId: String): StaffEntity? {
        return try {
            val staffDoc = db.collection("Staff").document(staffId).get().await()
            if (staffDoc.exists()) {
                val data = staffDoc.data!!
                StaffEntity(
                    staffId = staffId,
                    name = data["name"] as? String ?: "",
                    email = data["email"] as? String ?: "",
                    phoneNum = data["phoneNum"] as? String ?: "",
                    gender = data["gender"] as? String ?: "",
                    post = data["post"] as? String ?: ""
                 )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error getting staff details", e)
            null
        }
    }






}