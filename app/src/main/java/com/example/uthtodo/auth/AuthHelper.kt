package com.example.uthtodo.auth

import android.content.Context
import android.content.Intent
import com.example.uthtodo.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object AuthHelper {

    private val auth get() = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    private fun googleClient(context: Context) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

    /** Intent mở hộp chọn tài khoản Google. */
    fun googleSignInIntent(context: Context): Intent = googleClient(context).signInIntent

    /** Nhận kết quả từ hộp chọn tài khoản, lấy ID token rồi đổi lấy user của Firebase. */
    suspend fun finishGoogleSignIn(data: Intent?) {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
        val idToken = account.idToken
            ?: throw IllegalStateException("Không lấy được ID token từ Google")
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    /** Dùng thử không cần tài khoản (đăng nhập ẩn danh). */
    suspend fun signInAnonymously() {
        auth.signInAnonymously().await()
    }

    suspend fun signOut(context: Context) {
        auth.signOut()
        try {
            googleClient(context).signOut().await()
        } catch (_: Exception) {
            // không quan trọng: user Firebase đã được đăng xuất
        }
    }
}

fun authErrorMessage(e: Exception): String = when (e) {
    is ApiException -> when (e.statusCode) {
        12501 -> "Bạn đã huỷ đăng nhập"
        7 -> "Không có kết nối mạng, vui lòng thử lại"
        10, 12500 -> "Cấu hình đăng nhập Google chưa đúng (mã ${e.statusCode}), kiểm tra SHA-1 và google-services.json"
        else -> "Đăng nhập Google thất bại (mã ${e.statusCode})"
    }
    is FirebaseNetworkException -> "Không có kết nối mạng, vui lòng thử lại"
    else -> "Đăng nhập thất bại: ${e.localizedMessage ?: "lỗi không xác định"}"
}

fun FirebaseUser?.displayLabel(): String {
    val name = this?.displayName?.takeIf { it.isNotBlank() }
    return name ?: this?.email?.substringBefore('@')?.takeIf { it.isNotBlank() } ?: "Khách"
}

fun initialsOf(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(1).uppercase()
        else -> (words.first().take(1) + words.last().take(1)).uppercase()
    }
}

fun FirebaseUser?.isGoogleLinked(): Boolean =
    this?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true
