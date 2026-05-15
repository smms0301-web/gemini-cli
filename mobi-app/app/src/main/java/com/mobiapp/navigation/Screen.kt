package com.mobiapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    // Process module
    object ProcessList : Screen("process_list")
    object ProcessDetail : Screen("process_detail/{processId}") {
        fun createRoute(id: Long) = "process_detail/$id"
    }
    object AddEditProcess : Screen("add_edit_process?processId={processId}") {
        fun createRoute(id: Long? = null) = if (id != null) "add_edit_process?processId=$id" else "add_edit_process"
    }
    object StepDetail : Screen("step_detail/{stepId}/{processId}") {
        fun createRoute(stepId: Long, processId: Long) = "step_detail/$stepId/$processId"
    }
    object AddEditStep : Screen("add_edit_step/{processId}?stepId={stepId}") {
        fun createRoute(processId: Long, stepId: Long? = null) =
            if (stepId != null) "add_edit_step/$processId?stepId=$stepId"
            else "add_edit_step/$processId"
    }
    object PdfExport : Screen("pdf_export/{processId}") {
        fun createRoute(id: Long) = "pdf_export/$id"
    }

    // Reminder module
    object ReminderList : Screen("reminder_list")
    object AddEditReminder : Screen("add_edit_reminder?reminderId={reminderId}") {
        fun createRoute(id: Long? = null) = if (id != null) "add_edit_reminder?reminderId=$id" else "add_edit_reminder"
    }

    // Prompt module
    object PromptList : Screen("prompt_list")
    object PromptDetail : Screen("prompt_detail/{promptId}") {
        fun createRoute(id: Long) = "prompt_detail/$id"
    }
    object AddEditPrompt : Screen("add_edit_prompt?promptId={promptId}") {
        fun createRoute(id: Long? = null) = if (id != null) "add_edit_prompt?promptId=$id" else "add_edit_prompt"
    }

    // Tool module
    object ToolList : Screen("tool_list")
    object ToolDetail : Screen("tool_detail/{toolId}") {
        fun createRoute(id: Long) = "tool_detail/$id"
    }
    object AddEditTool : Screen("add_edit_tool?toolId={toolId}") {
        fun createRoute(id: Long? = null) = if (id != null) "add_edit_tool?toolId=$id" else "add_edit_tool"
    }

    // Note module
    object NoteList : Screen("note_list")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(id: Long) = "note_detail/$id"
    }
    object AddEditNote : Screen("add_edit_note?noteId={noteId}") {
        fun createRoute(id: Long? = null) = if (id != null) "add_edit_note?noteId=$id" else "add_edit_note"
    }

    // Settings
    object Settings : Screen("settings")
}
