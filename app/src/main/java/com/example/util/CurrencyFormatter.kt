package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {

    private val numberFormat = DecimalFormat("#,##0")
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val cryptoFormat = DecimalFormat("#,##0.000000")
    private val percentFormat = DecimalFormat("0.0")

    fun formatCurrency(
        amount: Double,
        currency: String = "تومان",
        usePersianDigits: Boolean = false,
        isHidden: Boolean = false
    ): String {
        if (isHidden) {
            val placeholder = "••••••••"
            val result = if (currency.isNotBlank()) "$placeholder $currency" else placeholder
            return if (usePersianDigits) toPersianDigits(result) else result
        }
        val rounded = Math.round(amount)
        val formatted = numberFormat.format(rounded)
        val result = "$formatted $currency"
        return if (usePersianDigits) toPersianDigits(result) else result
    }

    fun formatNumber(value: Double, usePersianDigits: Boolean = false, isHidden: Boolean = false): String {
        if (isHidden) return "••••••"
        val formatted = if (value == 0.0) {
            "0"
        } else if (value % 1.0 == 0.0) {
            numberFormat.format(value)
        } else if (value < 0.0001) {
            // For tiny numbers e.g. 0.0000222, format up to 10 decimal digits without scientific notation
            val df = DecimalFormat("#,##0.##########")
            df.format(value)
        } else if (value < 0.01) {
            val df = DecimalFormat("#,##0.######")
            df.format(value)
        } else {
            decimalFormat.format(value)
        }
        return if (usePersianDigits) toPersianDigits(formatted) else formatted
    }

    fun formatQuantity(
        value: Double,
        symbol: String = "",
        usePersianDigits: Boolean = false,
        isHidden: Boolean = false
    ): String {
        if (isHidden) {
            val placeholder = "••••"
            val withUnit = if (symbol.isNotBlank()) "$placeholder $symbol" else placeholder
            return if (usePersianDigits) toPersianDigits(withUnit) else withUnit
        }
        val formatted = if (value == 0.0) {
            "0"
        } else if (value % 1.0 == 0.0) {
            numberFormat.format(value)
        } else {
            // Output high precision floating point format up to 10 decimal places, stripping trailing zeros
            val df = DecimalFormat("#,##0.##########")
            df.format(value)
        }
        val withUnit = if (symbol.isNotBlank()) "$formatted $symbol" else formatted
        return if (usePersianDigits) toPersianDigits(withUnit) else withUnit
    }

    fun formatPercent(percent: Double, usePersianDigits: Boolean = false): String {
        val formatted = "${percentFormat.format(percent)}%"
        return if (usePersianDigits) toPersianDigits(formatted) else formatted
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun toPersianDigits(text: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
