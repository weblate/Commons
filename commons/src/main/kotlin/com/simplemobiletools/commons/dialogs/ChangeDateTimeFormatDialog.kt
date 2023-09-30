package com.simplemobiletools.commons.dialogs

import android.app.Activity
import android.text.format.DateFormat
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.compose.alert_dialog.AlertDialogState
import com.simplemobiletools.commons.compose.alert_dialog.rememberAlertDialogState
import com.simplemobiletools.commons.compose.extensions.MyDevices
import com.simplemobiletools.commons.compose.extensions.NoRippleTheme
import com.simplemobiletools.commons.compose.extensions.rememberMutableInteractionSource
import com.simplemobiletools.commons.compose.settings.SettingsHorizontalDivider
import com.simplemobiletools.commons.compose.theme.AppThemeSurface
import com.simplemobiletools.commons.compose.theme.preferenceLabelColor
import com.simplemobiletools.commons.databinding.DialogChangeDateTimeFormatBinding
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.getAlertDialogBuilder
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.helpers.*
import java.util.Calendar
import java.util.Locale
import kotlinx.collections.immutable.toImmutableList

class ChangeDateTimeFormatDialog(val activity: Activity, val callback: () -> Unit) {
    private val view = DialogChangeDateTimeFormatBinding.inflate(activity.layoutInflater, null, false)
    private val sampleTS = 1613422500000    // February 15, 2021

    init {
        view.apply {
            changeDateTimeDialogRadioOne.text = formatDateSample(DATE_FORMAT_ONE)
            changeDateTimeDialogRadioTwo.text = formatDateSample(DATE_FORMAT_TWO)
            changeDateTimeDialogRadioThree.text = formatDateSample(DATE_FORMAT_THREE)
            changeDateTimeDialogRadioFour.text = formatDateSample(DATE_FORMAT_FOUR)
            changeDateTimeDialogRadioFive.text = formatDateSample(DATE_FORMAT_FIVE)
            changeDateTimeDialogRadioSix.text = formatDateSample(DATE_FORMAT_SIX)
            changeDateTimeDialogRadioSeven.text = formatDateSample(DATE_FORMAT_SEVEN)
            changeDateTimeDialogRadioEight.text = formatDateSample(DATE_FORMAT_EIGHT)

            changeDateTimeDialog24Hour.isChecked = activity.baseConfig.use24HourFormat

            val formatButton = when (activity.baseConfig.dateFormat) {
                DATE_FORMAT_ONE -> changeDateTimeDialogRadioOne
                DATE_FORMAT_TWO -> changeDateTimeDialogRadioTwo
                DATE_FORMAT_THREE -> changeDateTimeDialogRadioThree
                DATE_FORMAT_FOUR -> changeDateTimeDialogRadioFour
                DATE_FORMAT_FIVE -> changeDateTimeDialogRadioFive
                DATE_FORMAT_SIX -> changeDateTimeDialogRadioSix
                DATE_FORMAT_SEVEN -> changeDateTimeDialogRadioSeven
                else -> changeDateTimeDialogRadioEight
            }
            formatButton.isChecked = true
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view.root, this)
            }
    }

    private fun dialogConfirmed() {
        activity.baseConfig.dateFormat = when (view.changeDateTimeDialogRadioGroup.checkedRadioButtonId) {
            view.changeDateTimeDialogRadioOne.id -> DATE_FORMAT_ONE
            view.changeDateTimeDialogRadioTwo.id -> DATE_FORMAT_TWO
            view.changeDateTimeDialogRadioThree.id -> DATE_FORMAT_THREE
            view.changeDateTimeDialogRadioFour.id -> DATE_FORMAT_FOUR
            view.changeDateTimeDialogRadioFive.id -> DATE_FORMAT_FIVE
            view.changeDateTimeDialogRadioSix.id -> DATE_FORMAT_SIX
            view.changeDateTimeDialogRadioSeven.id -> DATE_FORMAT_SEVEN
            else -> DATE_FORMAT_EIGHT
        }

        activity.baseConfig.use24HourFormat = view.changeDateTimeDialog24Hour.isChecked
        callback()
    }

    private fun formatDateSample(format: String): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = sampleTS
        return DateFormat.format(format, cal).toString()
    }
}


@Composable
fun ChangeDateTimeFormatAlertDialog(
    alertDialogState: AlertDialogState,
    modifier: Modifier = Modifier,
    is24HourChecked: Boolean,
    callback: (selectedFormat: String, is24HourChecked: Boolean) -> Unit
) {
    val context = LocalContext.current
    val selections = remember {
        mapOf(
            Pair(DATE_FORMAT_ONE, formatDateSample(DATE_FORMAT_ONE)),
            Pair(DATE_FORMAT_TWO, formatDateSample(DATE_FORMAT_TWO)),
            Pair(DATE_FORMAT_THREE, formatDateSample(DATE_FORMAT_THREE)),
            Pair(DATE_FORMAT_FOUR, formatDateSample(DATE_FORMAT_FOUR)),
            Pair(DATE_FORMAT_FIVE, formatDateSample(DATE_FORMAT_FIVE)),
            Pair(DATE_FORMAT_SIX, formatDateSample(DATE_FORMAT_SIX)),
            Pair(DATE_FORMAT_SEVEN, formatDateSample(DATE_FORMAT_SEVEN)),
            Pair(DATE_FORMAT_EIGHT, formatDateSample(DATE_FORMAT_EIGHT)),
        )
    }
    val kinds = remember {
        selections.values.toImmutableList()
    }
    val initiallySelected = remember {
        requireNotNull(selections[context.baseConfig.dateFormat]){
            "Incorrect format, please check selections"
        }
    }
    AlertDialog(
        onDismissRequest = alertDialogState::hide,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(dialogContainerColor, dialogShape)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            var is24HoursSelected by remember { mutableStateOf(is24HourChecked) }

            val (selected, setSelected) = remember { mutableStateOf(initiallySelected) }
            RadioGroup(
                items = kinds, selected = selected,
                setSelected = setSelected,
                modifier = Modifier.padding(
                    vertical = 16.dp,
                    horizontal = 11.dp
                )
            )
            SettingsHorizontalDivider()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SettingsCheckBoxChangeDateTimeFormat(
                    label = stringResource(id = R.string.use_24_hour_time_format),
                    initialValue = is24HoursSelected,
                    onChange = { is24HoursSelected = it })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                TextButton(onClick = {
                    alertDialogState.hide()
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }

                TextButton(onClick = {
                    alertDialogState.hide()
                    callback(selections.filterValues { it == selected }.keys.first(), is24HoursSelected)
                }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        }
    }
}

@Composable
private fun RadioGroup(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: String,
    setSelected: (selected: String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items.forEach { item ->
            val interactionSource = rememberMutableInteractionSource()
            val indication = LocalIndication.current
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { setSelected(item) },
                        interactionSource = interactionSource,
                        indication = indication
                    )
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = null,
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    ),
                )
                Text(
                    text = item.uppercase(),
                    modifier = Modifier.padding(start = 8.dp),
                    color = dialogTextColor
                )
            }
        }
    }
}

private const val timeSample = 1613422500000    // February 15, 2021

private fun formatDateSample(format: String): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = timeSample
    return DateFormat.format(format, cal).toString()
}

@Composable
private fun SettingsCheckBoxChangeDateTimeFormat(
    modifier: Modifier = Modifier,
    label: String,
    initialValue: Boolean = false,
    isPreferenceEnabled: Boolean = true,
    onChange: ((Boolean) -> Unit)? = null,
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        checkmarkColor = MaterialTheme.colorScheme.surface,
    )
) {
    val interactionSource = rememberMutableInteractionSource()
    val indication = LocalIndication.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onChange?.invoke(!initialValue) },
                interactionSource = interactionSource,
                indication = indication
            ),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = label,
                color = preferenceLabelColor(isEnabled = isPreferenceEnabled),
                fontSize = with(LocalDensity.current) {
                    dimensionResource(id = R.dimen.normal_text_size).toSp()
                },
                textAlign = TextAlign.End
            )
        }
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            Checkbox(
                checked = initialValue,
                onCheckedChange = { onChange?.invoke(it) },
                enabled = isPreferenceEnabled,
                colors = checkboxColors,
                interactionSource = interactionSource
            )
        }
    }
}

@Composable
@MyDevices
private fun ChangeDateTimeFormatAlertDialogPreview() {
    AppThemeSurface {
        ChangeDateTimeFormatAlertDialog(alertDialogState = rememberAlertDialogState(), callback = { _, _ -> }, is24HourChecked = true)
    }
}
