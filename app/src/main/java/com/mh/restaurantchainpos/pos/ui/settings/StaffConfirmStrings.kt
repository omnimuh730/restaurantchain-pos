package com.mh.restaurantchainpos.pos.ui.settings

import com.mh.restaurantchainpos.R

internal fun ConfirmKind.titleRes(): Int = when (this) {
    ConfirmKind.Deactivate -> R.string.staff_confirm_deactivate_title
    ConfirmKind.Activate -> R.string.staff_confirm_activate_title
    ConfirmKind.Remove -> R.string.staff_confirm_remove_title
    ConfirmKind.ResetPin -> R.string.staff_confirm_reset_pin_title
}

internal fun ConfirmKind.descriptionRes(): Int = when (this) {
    ConfirmKind.Deactivate -> R.string.staff_confirm_deactivate_desc
    ConfirmKind.Activate -> R.string.staff_confirm_activate_desc
    ConfirmKind.Remove -> R.string.staff_confirm_remove_desc
    ConfirmKind.ResetPin -> R.string.staff_confirm_reset_pin_desc
}

internal fun ConfirmKind.buttonLabelRes(): Int = when (this) {
    ConfirmKind.Deactivate -> R.string.staff_confirm_deactivate_btn
    ConfirmKind.Activate -> R.string.staff_confirm_activate_btn
    ConfirmKind.Remove -> R.string.staff_confirm_remove_btn
    ConfirmKind.ResetPin -> R.string.staff_confirm_reset_pin_btn
}
