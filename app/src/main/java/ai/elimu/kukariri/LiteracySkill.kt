package ai.elimu.kukariri

import androidx.annotation.StringRes

/**
 * The literacy skills that can be inspected in the app. Each skill is a destination in the
 * navigation drawer.
 */
enum class LiteracySkill(@StringRes val labelResId: Int) {
    LETTER_IDENTIFICATION(R.string.skill_letter_identification),
    FAMILIAR_WORD_READING(R.string.skill_familiar_word_reading)
}
