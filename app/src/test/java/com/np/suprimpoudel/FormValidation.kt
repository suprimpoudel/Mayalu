package com.np.suprimpoudel

import androidx.core.util.PatternsCompat
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FormValidation {
    @Test
    fun `invalid email fails test`() {
        val email = "abc@.com"
        val matched = PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
        assertThat(matched).isFalse()
    }

    @Test
    fun `valid email pass test`() {
        val email = "abc@gmail.com"
        val matched = PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
        assertThat(matched).isTrue()
    }
}