package com.mobiapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobiapp.ui.home.HomeScreen
import com.mobiapp.ui.note.*
import com.mobiapp.ui.process.*
import com.mobiapp.ui.prompt.*
import com.mobiapp.ui.reminder.*
import com.mobiapp.ui.settings.SettingsScreen
import com.mobiapp.ui.tool.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navigate: (String) -> Unit = { navController.navigate(it) }
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(onNavigate = navigate)
        }

        // ─── Process module ───────────────────────────────────
        composable(Screen.ProcessList.route) {
            ProcessListScreen(onNavigate = navigate, onBack = goBack)
        }
        composable(
            Screen.ProcessDetail.route,
            arguments = listOf(navArgument("processId") { type = NavType.LongType })
        ) { entry ->
            ProcessDetailScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                onNavigate = navigate,
                onBack = goBack
            )
        }
        composable(
            Screen.AddEditProcess.route,
            arguments = listOf(navArgument("processId") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            val pid = entry.arguments?.getLong("processId").takeIf { it != -1L }
            AddEditProcessScreen(
                processId = pid,
                onBack = goBack,
                onSaved = { id ->
                    navController.popBackStack()
                    if (pid == null) navController.navigate(Screen.ProcessDetail.createRoute(id))
                }
            )
        }
        composable(
            Screen.StepDetail.route,
            arguments = listOf(
                navArgument("stepId") { type = NavType.LongType },
                navArgument("processId") { type = NavType.LongType }
            )
        ) { entry ->
            StepDetailScreen(
                stepId = entry.arguments?.getLong("stepId") ?: 0L,
                processId = entry.arguments?.getLong("processId") ?: 0L,
                onNavigate = navigate,
                onBack = goBack
            )
        }
        composable(
            Screen.AddEditStep.route,
            arguments = listOf(
                navArgument("processId") { type = NavType.LongType },
                navArgument("stepId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) { entry ->
            AddEditStepScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                stepId = entry.arguments?.getLong("stepId").takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }
        composable(
            Screen.PdfExport.route,
            arguments = listOf(navArgument("processId") { type = NavType.LongType })
        ) { entry ->
            PdfExportScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                onBack = goBack
            )
        }

        // ─── Reminder module ──────────────────────────────────
        composable(Screen.ReminderList.route) {
            ReminderListScreen(onNavigate = navigate, onBack = goBack)
        }
        composable(
            Screen.AddEditReminder.route,
            arguments = listOf(navArgument("reminderId") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditReminderScreen(
                reminderId = entry.arguments?.getLong("reminderId").takeIf { it != -1L },
                onBack = goBack
            )
        }

        // ─── Prompt module ────────────────────────────────────
        composable(Screen.PromptList.route) {
            PromptListScreen(onNavigate = navigate, onBack = goBack)
        }
        composable(
            Screen.PromptDetail.route,
            arguments = listOf(navArgument("promptId") { type = NavType.LongType })
        ) { entry ->
            PromptDetailScreen(
                promptId = entry.arguments?.getLong("promptId") ?: 0L,
                onNavigate = navigate,
                onBack = goBack
            )
        }
        composable(
            Screen.AddEditPrompt.route,
            arguments = listOf(navArgument("promptId") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditPromptScreen(
                promptId = entry.arguments?.getLong("promptId").takeIf { it != -1L },
                onBack = goBack
            )
        }

        // ─── Tool module ──────────────────────────────────────
        composable(Screen.ToolList.route) {
            ToolListScreen(onNavigate = navigate, onBack = goBack)
        }
        composable(
            Screen.ToolDetail.route,
            arguments = listOf(navArgument("toolId") { type = NavType.LongType })
        ) { entry ->
            ToolDetailScreen(
                toolId = entry.arguments?.getLong("toolId") ?: 0L,
                onNavigate = navigate,
                onBack = goBack
            )
        }
        composable(
            Screen.AddEditTool.route,
            arguments = listOf(navArgument("toolId") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditToolScreen(
                toolId = entry.arguments?.getLong("toolId").takeIf { it != -1L },
                onBack = goBack
            )
        }

        // ─── Note module ──────────────────────────────────────
        composable(Screen.NoteList.route) {
            NoteListScreen(onNavigate = navigate, onBack = goBack)
        }
        composable(
            Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { entry ->
            NoteDetailScreen(
                noteId = entry.arguments?.getLong("noteId") ?: 0L,
                onNavigate = navigate,
                onBack = goBack
            )
        }
        composable(
            Screen.AddEditNote.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditNoteScreen(
                noteId = entry.arguments?.getLong("noteId").takeIf { it != -1L },
                onBack = goBack,
                onSaved = { goBack() }
            )
        }

        // ─── Settings ─────────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = goBack)
        }
    }
}
