package com.pechenegmobilecompanyltd.honestrating.model

data class Company(
    val id: String,
    val name: String,
    val rating: Float,
    val reviews: Int,
    val industry: String,
    val about: String = "Описание компании будет здесь.",
    val logoRes: Int? = null, // можно null, если нет рисунка
    val salaryStats: List<Pair<String, Int>> = emptyList() // для графика зарплат
)

// Моковые данные для начальной отрисовки (реальные компании — позже из БД)
val mockCompanies = listOf(
    Company(
        "1", "Яндекс", 4.8f, 1210, "ИТ",
        about = "Крупнейшая ИТ-компания, лидер рынка интернет-сервисов и технологий.",
        salaryStats = listOf("QA" to 100_000, "Android" to 190_000)
    ),
    Company("2", "VK", 4.2f, 900, "ИТ"),
    Company("3", "Сбер", 3.7f, 1612, "Банки"),
    Company("4", "Tinkoff", 4.1f, 740, "Банки"),
    Company("5", "МТС", 3.9f, 688, "Телеком"),
    Company("6", "Ростелеком", 3.5f, 501, "Телеком"),
    Company("7", "Газпром", 4.0f, 346, "Топливо"),
    Company("8", "Лукойл", 3.6f, 120, "Топливо"),
)