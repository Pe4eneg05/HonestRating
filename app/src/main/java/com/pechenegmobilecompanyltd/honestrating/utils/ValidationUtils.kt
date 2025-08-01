package com.pechenegmobilecompanyltd.honestrating.utils

// Проверка валидности email
fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return emailRegex.matches(email)
}

// Проверка корпоративного email
fun isValidCorporateEmail(email: String): Boolean {
    val domain = email.substringAfterLast("@", "")
    val freeDomains = listOf(
        "gmail.com", "yandex.ru", "mail.ru", "icloud.com",
        "yahoo.com", "outlook.com", "hotmail.com", "protonmail.com"
    )
    return !freeDomains.any { domain.contains(it, ignoreCase = true) }
}

// Проверка валидности пароля
fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}