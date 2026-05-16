package com.mobiapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobiapp.ui.home.HomeScreen
import com.mobiapp.ui.note.AddEditNoteScreen
import com.mobiapp.ui.note.NoteDetailScreen
import com.mobiapp.ui.note.NoteListScreen
import com.mobiapp.ui.process.AddEditProcessScreen
import com.mobiapp.ui.process.AddEditStepScreen
import com.mobiapp.ui.process.PdfExportScreen
import com.mobiapp.ui.process.ProcessDetailScreen
import com.mobiapp.ui.process.ProcessListScreen
import com.mobiapp.ui.process.StepDetailScreen
import com.mobiapp.ui.prompt.AddEditPromptScreen
import com.mobiapp.ui.prompt.PromptDetailScreen
import com.mobiapp.ui.prompt.PromptListScreen
import com.mobiapp.ui.reminder.AddEditReminderScreen
import com.mobiapp.ui.reminder.ReminderListScreen
import com.mobiapp.ui.settings.SettingsScreen
import com.mobiapp.ui.tool.AddEditToolScreen
import com.mobiapp.ui.tool.ToolDetailScreen
import com.mobiapp.ui.tool.ToolListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProcess = { navController.navigate(Screen.ProcessList.route) },
                onNavigateToReminder = { navController.navigate(Screen.ReminderList.route) },
                onNavigateToPrompt = { navController.navigate(Screen.PromptList.route) },
                onNavigateToTool = { navController.navigate(Screen.ToolList.route) },
                onNavigateToNote = { navController.navigate(Screen.NoteList.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.ProcessList.route) {
            ProcessListScreen(
                onBack = goBack,
                onAddProcess = { navController.navigate(Screen.AddEditProcess.route()) },
                onProcessClick = { id -> navController.navigate(Screen.ProcessDetail.route(id)) }
            )
        }
        composable(
            route = Screen.ProcessDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            ProcessDetailScreen(
                processId = entry.arguments?.getLong("id") ?: 0L,
                onBack = goBack,
                onEdit = { id -> navController.navigate(Screen.AddEditProcess.route(id)) },
                onAddStep = { pid -> navController.navigate(Screen.AddEditStep.route(pid)) },
                onStepClick = { pid, sid -> navController.navigate(Screen.StepDetail.route(pid, sid)) },
                onExportPdf = { pid -> navController.navigate(Screen.PdfExport.route(pid)) }
            )
        }
        composable(
            route = Screen.AddEditProcess.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditProcessScreen(
                processId = entry.arguments?.getLong("id")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }
        composable(
            route = Screen.StepDetail.route,
            arguments = listOf(
                navArgument("processId") { type = NavType.LongType },
                navArgument("stepId") { type = NavType.LongType }
            )
        ) { entry ->
            StepDetailScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                stepId = entry.arguments?.getLong("stepId") ?: 0L,
                onBack = goBack,
                onEdit = { pid, sid -> navController.navigate(Screen.AddEditStep.route(pid, sid)) }
            )
        }
        composable(
            route = Screen.AddEditStep.route,
            arguments = listOf(
                navArgument("processId") { type = NavType.LongType },
                navArgument("stepId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) { entry ->
            AddEditStepScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                stepId = entry.arguments?.getLong("stepId")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }
        composable(
            route = Screen.PdfExport.route,
            arguments = listOf(navArgument("processId") { type = NavType.LongType })
        ) { entry ->
            PdfExportScreen(
                processId = entry.arguments?.getLong("processId") ?: 0L,
                onBack = goBack
            )
        }

        composable(Screen.ReminderList.route) {
            ReminderListScreen(
                onBack = goBack,
                onAddReminder = { navController.navigate(Screen.AddEditReminder.route()) },
                onEditReminder = { id -> navController.navigate(Screen.AddEditReminder.route(id)) }
            )
        }
        composable(
            route = Screen.AddEditReminder.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditReminderScreen(
                reminderId = entry.arguments?.getLong("id")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }

        composable(Screen.PromptList.route) {
            PromptListScreen(
                onBack = goBack,
                onAddPrompt = { navController.navigate(Screen.AddEditPrompt.route()) },
                onPromptClick = { id -> navController.navigate(Screen.PromptDetail.route(id)) }
            )
        }
        composable(
            route = Screen.PromptDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            PromptDetailScreen(
                promptId = entry.arguments?.getLong("id") ?: 0L,
                onBack = goBack,
                onEdit = { id -> navController.navigate(Screen.AddEditPrompt.route(id)) }
            )
        }
        composable(
            route = Screen.AddEditPrompt.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditPromptScreen(
                promptId = entry.arguments?.getLong("id")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }

        composable(Screen.ToolList.route) {
            ToolListScreen(
                onBack = goBack,
                onAddTool = { navController.navigate(Screen.AddEditTool.route()) },
                onToolClick = { id -> navController.navigate(Screen.ToolDetail.route(id)) }
            )
        }
        composable(
            route = Screen.ToolDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            ToolDetailScreen(
                toolId = entry.arguments?.getLong("id") ?: 0L,
                onBack = goBack,
                onEdit = { id -> navController.navigate(Screen.AddEditTool.route(id)) }
            )
        }
        composable(
            route = Screen.AddEditTool.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditToolScreen(
                toolId = entry.arguments?.getLong("id")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }

        composable(Screen.NoteList.route) {
            NoteListScreen(
                onBack = goBack,
                onAddNote = { navController.navigate(Screen.AddEditNote.route()) },
                onNoteClick = { id -> navController.navigate(Screen.NoteDetail.route(id)) }
            )
        }
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            NoteDetailScreen(
                noteId = entry.arguments?.getLong("id") ?: 0L,
                onBack = goBack,
                onEdit = { id -> navController.navigate(Screen.AddEditNote.route(id)) }
            )
        }
        composable(
            route = Screen.AddEditNote.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { entry ->
            AddEditNoteScreen(
                noteId = entry.arguments?.getLong("id")?.takeIf { it != -1L },
                onBack = goBack,
                onSaved = goBack
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = goBack)
        }
    }
}
