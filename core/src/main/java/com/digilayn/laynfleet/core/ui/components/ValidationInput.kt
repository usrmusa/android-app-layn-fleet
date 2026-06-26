package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.digilayn.laynfleet.core.validation.ValidationRule
import com.digilayn.laynfleet.core.validation.validate

@Composable
fun ValidationInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    rules: List<ValidationRule> = emptyList(),
    showErrors: Boolean = true,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val error = validate(value, rules)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = { Text(label) },
        isError = showErrors && error != null,
        supportingText = if (showErrors) error?.let { { Text(it) } } else null,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
    )
}

@Composable
fun PasswordVisibilityButton(
    visible: Boolean,
    onVisibilityChange: () -> Unit,
    showContentDescription: String,
    hideContentDescription: String,
) {
    IconButton(onClick = onVisibilityChange) {
        Icon(
            imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (visible) hideContentDescription else showContentDescription,
        )
    }
}
