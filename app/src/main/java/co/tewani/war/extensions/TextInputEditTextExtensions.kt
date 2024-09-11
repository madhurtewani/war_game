package co.tewani.war.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.isNullOrEmpty(error: String): Boolean {
    if (this.editText?.text.isNullOrEmpty()) {
        this.showError(error)
        return false
    }
    this.hideError()
    return true
}

fun TextInputLayout.showError(error: String) {
    this.isErrorEnabled = true
    this.error = error
}

fun TextInputLayout.hideError() {
    this.isErrorEnabled = false
    this.error = null
}