package com.vag.lmsapp.util

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.InputType
import android.util.Size
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import android.widget.TextView
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vag.lmsapp.R
import com.vag.lmsapp.databinding.AlertDialogBinding
import com.vag.lmsapp.databinding.AlertDialogTextInputBinding
import com.vag.lmsapp.model.EnumProductType
import com.vag.lmsapp.model.EnumWashType
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@BindingAdapter("app:errorText")
fun setErrorText(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if(isVisible) { View.VISIBLE } else { View.GONE }
}

@BindingAdapter("app:required")
fun setRequired(view: TextInputLayout, required: Boolean) {
    val originalHint = view.hint?.toString() ?: ""

    if (required && !originalHint.contains("*")) {
        view.hint = "*$originalHint"
    }
}

@BindingAdapter("android:text")
fun setText(view: TextInputEditText, washType: EnumWashType?) {
    view.setText(washType?.toString())
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getWashTypeEnum(view: TextInputEditText) : EnumWashType? {
    return EnumWashType.fromName(view.text.toString())
}

@BindingAdapter("android:text")
fun setText(view: TextInputEditText, productType: EnumProductType?) {
    view.setText(productType?.toString())
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getProductType(view: TextInputEditText) : EnumProductType? {
    return EnumProductType.fromName(view.text.toString())
}

@BindingAdapter("android:text")
fun setText(view: TextView, dateTime: Instant?) {
    if(dateTime != null) {
        val now = Instant.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
            .withZone(ZoneId.systemDefault())

        val formattedDateTime = if (dateTime.atZone(ZoneId.systemDefault()).toLocalDate() == now.atZone(ZoneId.systemDefault()).toLocalDate()) {
            // If the date is today, display just the time
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            "Today, " + timeFormatter.format(dateTime.atZone(ZoneId.systemDefault()))
        } else {
            formatter.format(dateTime)
        }
        view.text = formattedDateTime
    } else {
        view.text = ""
    }
}

@BindingAdapter("android:date")
fun setDate(view: TextView, dateTime: Instant?) {
    if(dateTime != null) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            .withZone(ZoneId.systemDefault())
        view.text = formatter.format(dateTime)
    }
}

@BindingAdapter("android:dateTime")
fun setDateTime(view: TextView, dateTime: Instant?) {
    if(dateTime != null) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a")
            .withZone(ZoneId.systemDefault())
        view.text = formatter.format(dateTime)
    }
}

@BindingAdapter("android:localDate")
fun setDate(view: TextView, date: LocalDate?) {
    try {
        if(date != null) {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withZone(ZoneId.systemDefault())
            view.text = formatter.format(date)
        } else {
            view.text = ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("android:time")
fun setTime(view: TextView, dateTime: Instant?) {
    if(dateTime != null) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            .withZone(ZoneId.systemDefault())
        view.text = formatter.format(dateTime)
    }
}

@BindingAdapter("android:peso")
fun setPeso(view: TextView, value: Float?) {
    val amount = value ?: 0f
    view.text = "P %s".format(NumberFormat.getNumberInstance(Locale.US).format(amount))
}
@BindingAdapter("android:peso")
fun setPeso(view: TextView, value: String?) {
    val amount = value?.toFloatOrNull() ?: 0f
    view.text = "P %s".format(NumberFormat.getNumberInstance(Locale("en", "PH")).format(amount))
}

fun View.hideKeyboard() : Boolean {
    val imm = ContextCompat.getSystemService(this.context, InputMethodManager::class.java) as InputMethodManager
    return imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun String?.isUUID(): Boolean {
    return try {
        UUID.fromString(this)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

fun String?.toUUID() : UUID? {
    if(this?.isUUID() == true) {
        return UUID.fromString(this)
    }
    return null
}

fun Instant.isToday(): Boolean {
    val currentZone = ZoneId.systemDefault()
    val currentDate = LocalDate.now(currentZone)

    val currentStartOfDay = currentDate.atStartOfDay(currentZone).toInstant()
    val currentEndOfDay = currentDate.plusDays(1).atStartOfDay(currentZone).toInstant()

    return this.isAfter(currentStartOfDay) && this.isBefore(currentEndOfDay)
}


//@BindingAdapter("app:selectedPaymentMethod")
//fun setPaymentMethod(radioGroup: RadioGroup, paymentMethod: EnumPaymentMethod?) {
//    val selectedId = when (paymentMethod) {
//        EnumPaymentMethod.CASH -> R.id.radio_cash
//        EnumPaymentMethod.CASHLESS -> R.id.radio_cashless
//        else -> View.NO_ID
//    }
//    if (radioGroup.checkedRadioButtonId != selectedId) {
//        radioGroup.check(selectedId)
//    }
//}
//
//@InverseBindingAdapter(attribute = "app:selectedPaymentMethod", event = "android:checkedButtonAttrChanged")
//fun getPaymentMethod(radioGroup: RadioGroup): EnumPaymentMethod? {
//    return when (radioGroup.checkedRadioButtonId) {
//        R.id.radio_cash -> EnumPaymentMethod.CASH
//        R.id.radio_cashless -> EnumPaymentMethod.CASHLESS
//        else -> null
//    }
//}


@BindingAdapter("android:checkedButtonAttrChanged")
fun setCheckedButtonListener(radioGroup: RadioGroup, listener: InverseBindingListener?) {
    radioGroup.setOnCheckedChangeListener { _, _ ->
        listener?.onChange()
    }
}

@BindingAdapter("app:momentAgo")
fun setMomentAgo(view: TextView, dateTime: Instant?) {
    if(dateTime == null) {
        view.text = ""
        return
    }
    val now = Instant.now()
    val duration = Duration.between(dateTime, now).abs()

    val text = when {
        duration.toDays() >= 365 -> {
            val years = duration.toDays() / 365
            "$years ${if (years == 1L) "year ago" else "years ago"}"
        }
        duration.toDays() >= 30 -> {
            val months = duration.toDays() / 30
            "$months ${if (months == 1L) "month ago" else "months ago"}"
        }
        duration.toDays() >= 7 -> {
            val weeks = duration.toDays() / 7
            "$weeks ${if (weeks == 1L) "week ago" else "weeks ago"}"
        }
        duration.toDays() >= 1 -> {
            val days = duration.toDays()
            "$days ${if (days == 1L) "day ago" else "days ago"}"
        }
        duration.toHours() >= 1 -> {
            val hours = duration.toHours()
            "$hours ${if (hours == 1L) "hour ago" else "hours ago"}"
        }
        duration.toMinutes() >= 1 -> {
            val minutes = duration.toMinutes()
            "$minutes ${if (minutes == 1L) "minute ago" else "minutes ago"}"
        }
        else -> "just now"
    }

    view.text = text
}

fun Context.showDeleteConfirmationDialog(title: String? = "Delete this item", message: String? = "Are you sure you want to proceed?", onDeleteConfirmed: () -> Unit) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("Yes") { _, _ ->
            onDeleteConfirmed()
        }
        setNegativeButton("Cancel") { _, _ ->

        }
        create()
    }.show()
}

fun Context.showMessageDialog(title: String?, message: String?, onOk: (() -> Unit)? = null) {
    AlertDialog.Builder(this).apply {
        title?.let {
            setTitle(it)
        }
        message?.let {
            setMessage(it)
        }
        setNeutralButton("OK") { _, _ ->
            onOk?.invoke()
        }
        create()
    }.show()
}
fun Context.showConfirmationDialog(title: String?, message: String?, onOk: (() -> Unit)?) {
    AlertDialog.Builder(this).apply {
        title?.let {
            setTitle(it)
        }
        message?.let {
            setMessage(it)
        }
        setNeutralButton("OK") { _, _ ->
            onOk?.invoke()
        }
        setNegativeButton("Cancel") {_, _ ->}
        create()
    }.show()
}

inline fun <reified T> Context.showTextInputDialog(
    title: String?,
    message: String?,
    initialValue: T?,
    crossinline onOk: (value: T?) -> Unit
) {
    val binding: AlertDialogTextInputBinding = DataBindingUtil.inflate(
        LayoutInflater.from(this),
        R.layout.alert_dialog_text_input,
        null,
        false
    )

    // Set the inputType based on T
    when (T::class) {
        String::class -> {
            binding.textInput.inputType = InputType.TYPE_CLASS_TEXT
            println("input type text")
        }
        Float::class, Int::class, Long::class, Number::class -> {
            binding.textInput.inputType = InputType.TYPE_CLASS_NUMBER
            println("input type number")
        }
        Boolean::class -> {
            // For boolean, you may want to handle it differently, e.g., as a checkbox
            // Adjust based on your specific UI design
        }
    }

    binding.textInput.setText(initialValue?.toString())
    binding.textHint.text = message

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setView(binding.root)
        setPositiveButton("Ok") { _, _ ->
            val inputText = binding.textInput.text.toString()

            val typedValue = when (T::class) {
                Number::class -> inputText as T
                String::class -> inputText as T
                Float::class -> inputText.toFloatOrNull() as? T ?: initialValue
                Int::class -> inputText.toIntOrNull() as? T ?: initialValue
                Boolean::class -> inputText.toBoolean() as T
                Long::class -> inputText.toLong() as T ?: initialValue
                else -> null
            }

            onOk(typedValue/*, key ?: ""*/)
        }
        create()
    }.show()
}


//fun View.showDialog(message: String, duration: Int = Snackbar.LENGTH_SHORT, actionText: String? = null, actionCallback: (() -> Unit)? = null) {
//    val snackBar = Snackbar.make(this, message, duration)
//
//    actionText?.let {
//        snackBar.setAction(actionText) {
//            actionCallback?.invoke()
//        }
//    }
//
//    snackBar.show()
//}

fun Context.showDialog(title: String, message: String? = null, callback: (() -> Unit)? = null) {
    val binding: AlertDialogBinding = DataBindingUtil.inflate(
        LayoutInflater.from(this),
        R.layout.alert_dialog,
        null,
        false
    )
    binding.textMessage.text = message

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setView(binding.root)
        setPositiveButton("Ok") { _, _ ->
            callback?.invoke()
        }
        create()
    }.show()
}

fun Context.loadThumbnailOrBitmap(uri: Uri, dimension: Int): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        println("get thumbnail")
        contentResolver.loadThumbnail(uri, Size(dimension, dimension), null)
    } else {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        var bitmap: Bitmap? = null

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath = it.getString(columnIndex)
                bitmap = BitmapFactory.decodeFile(imagePath)
            }
        }

        cursor?.close()
        bitmap
    }
}

fun Context.calculateSpanCount(
    columnWidthId: Int,
    horizontalMarginId: Int? = null
): Int {
    val columnWidth = resources.getDimensionPixelSize(columnWidthId)

    val margin = if(horizontalMarginId == null) 0 else (resources.getDimension(horizontalMarginId) * 2)

    val parentWidthDp = resources.displayMetrics.widthPixels - margin.toInt()

    val spanCount = (parentWidthDp / columnWidth)
    return if (spanCount > 0) spanCount else 1
}

fun Context.calculateSpanCount(
    columnWidth: Dp,
    horizontalMargin: Dp? = null
): Int {
    val columnWidthPx = columnWidth.toPixels(this)

    val marginPx = if (horizontalMargin == null) 0 else (horizontalMargin.toPixels(this) * 2)

    val parentWidthPx = resources.displayMetrics.widthPixels - marginPx

    val spanCount = (parentWidthPx / columnWidthPx)
    return if (spanCount > 0) spanCount else 1
}

// Extension function for Dp to convert to pixels
fun Dp.toPixels(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.value, context.resources.displayMetrics).toInt()
}
fun RecyclerView.calculateSpanCount(
    context: Context,
    columnWidth: Dp,
    horizontalMargin: Dp? = null
): Int {
    val columnWidthPx = columnWidth.toPixels(context)

    val marginPx = if (horizontalMargin == null) 0 else (horizontalMargin.toPixels(context) * 2)

    val parentWidthPx = (this.parent as View).width - marginPx //resources.displayMetrics.widthPixels - marginPx

    val spanCount = (parentWidthPx / columnWidthPx)
    return if (spanCount > 0) spanCount else 1
}

fun RecyclerView.setGridLayout(
    context: Context,
    columnWidth: Dp,
    horizontalMargin: Dp? = null
) {
    val parent = this.parent as View
    val recycler = this
    val columnWidthPx = columnWidth.toPixels(context)
    // val marginPx = if (horizontalMargin == null) 0 else (horizontalMargin.toPixels(context) * 2)

    parent.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {

            // val parentWidthPx = parent.width - marginPx
            val spanCount = (recycler.width / (columnWidthPx)).toInt() // Adjust for margins
            val col = maxOf(spanCount, 1) // Ensure at least 1 column

            recycler.layoutManager = GridLayoutManager(context, col)
            parent.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun Context.file(fileName: UUID?) : File {
    return File(filesDir, Constants.PICTURES_DIR + fileName)
}

fun LocalDate.toShort(): String {
    val today = LocalDate.now()
    val yearFormat = if(this.year != today.year) { ", yyyy" } else { "" }
    val formatter = DateTimeFormatter.ofPattern("MMM dd$yearFormat") // You can change the pattern to your desired format
    return this.format(formatter)
}

fun TextInputEditText.selectAllOnFocus() {
    this.setOnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.post {
                this.selectAll()
            }
        }
    }
}

fun Instant.toShort() : String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

fun Float.spToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
}

fun Float.dpToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}

fun View.show() {
    this.visibility = View.VISIBLE
}
fun View.hide() {
    this.visibility = View.INVISIBLE
}
fun View.remove() {
    this.visibility = View.GONE
}
fun today() : Long {
//    return Instant.now()
    val zonedDateTime = ZonedDateTime.now()
    return zonedDateTime.toInstant().toEpochMilli()
}

fun <T> MutableList<T>.addOrRemove(item: T) {
    if (!this.contains(item)) {
        this.add(item)
    } else {
        this.remove(item)
    }
}

fun Int?.greaterThan(value: Int?) : Boolean {
    return this.let {
        it != null && value != null && it > value
    }
}

fun String?.isNotEmpty() : Boolean {
    return this.let {
        it != null && it.trim() == ""
    }
}