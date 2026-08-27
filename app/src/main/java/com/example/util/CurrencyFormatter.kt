package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {

    private val numberFormat = DecimalFormat("#,##0")
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
        val result = if (currency.isNotBlank()) "$formatted $currency" else formatted
        return if (usePersianDigits) toPersianDigits(result) else result
    }

    fun formatSmartFloat(value: Double): String {
        if (value == 0.0) return "0"
        val sign = if (value < 0) "-" else ""
        val absVal = Math.abs(value)
        val integerPart = absVal.toLong()
        val fraction = absVal - integerPart

        if (fraction == 0.0) {
            return sign + numberFormat.format(integerPart)
        }

        if (integerPart > 0) {
            val df = DecimalFormat("#,##0.###")
            return sign + df.format(absVal)
        }

        // For fractional values < 1 (e.g., 0.00045678):
        // Count leading zeroes after decimal point and show 3 significant digits
        val rawStr = String.format(Locale.US, "%.10f", fraction)
        val afterDot = if (rawStr.contains('.')) rawStr.substringAfter('.') else ""
        var leadingZeros = 0
        while (leadingZeros < afterDot.length && afterDot[leadingZeros] == '0') {
            leadingZeros++
        }

        if (leadingZeros >= afterDot.length) return "0"

        val decimalsToShow = Math.min(10, leadingZeros + 3)
        val pattern = "0." + "#".repeat(decimalsToShow)
        val df = DecimalFormat(pattern)
        return sign + df.format(absVal)
    }

    fun formatNumber(value: Double, usePersianDigits: Boolean = false, isHidden: Boolean = false): String {
        if (isHidden) return "••••••"
        val formatted = formatSmartFloat(value)
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
        val formatted = formatSmartFloat(value)
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
