package ai.elimu.kukariri.ui

import ai.elimu.kukariri.FamiliarWordsViewModel
import ai.elimu.kukariri.LetterSoundsViewModel
import ai.elimu.kukariri.LiteracySkill
import ai.elimu.kukariri.R
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * App shell: a navigation drawer with one entry per [LiteracySkill], and the screen of the
 * currently selected skill.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KukaririApp(
    letterSoundsViewModel: LetterSoundsViewModel,
    familiarWordsViewModel: FamiliarWordsViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSkill by rememberSaveable { mutableStateOf(LiteracySkill.LETTER_IDENTIFICATION) }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.app_name) + " 🧠",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                )
                LiteracySkill.entries.forEach { skill ->
                    NavigationDrawerItem(
                        label = { Text(stringResource(skill.labelResId)) },
                        selected = skill == selectedSkill,
                        onClick = {
                            selectedSkill = skill
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(selectedSkill.labelResId)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.open_navigation_drawer)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedSkill) {
                    LiteracySkill.LETTER_IDENTIFICATION -> LetterSoundsScreen(letterSoundsViewModel)
                    LiteracySkill.FAMILIAR_WORD_READING -> FamiliarWordsScreen(familiarWordsViewModel)
                }
            }
        }
    }
}
