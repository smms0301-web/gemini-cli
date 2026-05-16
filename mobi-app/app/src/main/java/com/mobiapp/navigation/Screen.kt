package com.mobiapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object ProcessList : Screen("process_list")
    object ProcessDetail : Screen("process_detail/{id}") {
        fun route(id: Long) = "process_detail/$id"
    }
    object AddEditProcess : Screen("add_edit_process?id={id}") {
        fun route(id: Long? = null) = if (id != null) "add_edit_process?id=$id" else "add_edit_process"
    }
    object StepDetail : Screen("step_detail/{processId}/{stepId}") {
        fun route(processId: Long, stepId: Long) = "step_detail/$processId/$stepId"
    }
    object AddEditStep : Screen("add_edit_step/{processId}?stepId={stepId}") {
        fun route(processId: Long, stepId: Long? = null) =
            if (stepId != null) "add_edit_step/$processId?stepId=$stepId"
            else "add_edit_step/$processId"
    }
    object PdfExport : Screen("pdf_export/{processId}") {
        fun route(processId: Long) = "pdf_export/$processId"
    }

    object ReminderList : Screen("reminder_list")
    object AddEditReminder : Screen("add_edit_reminder?id={id}") {
        fun route(id: Long? = null) = if (id != null) "add_edit_reminder?id=$id" else "add_edit_reminder"
    }

    object PromptList : Screen("prompt_list")
    object PromptDetail : Screen("prompt_detail/{id}") {
        fun route(id: Long) = "prompt_detail/$id"
    }
    object AddEditPrompt : Screen("add_edit_prompt?id={id}") {
        fun route(id: Long? = null) = if (id != null) "add_edit_prompt?id=$id" else "add_edit_prompt"
    }

    object ToolList : Screen("tool_list")
    object ToolDetail : Screen("tool_detail/{id}") {
        fun route(id: Long) = "tool_detail/$id"
    }
    object AddEditTool : Screen("add_edit_tool?id={id}") {
        fun route(id: Long? = null) = if (id != null) "add_edit_tool?id=$id" else "add_edit_tool"
    }

    object NoteList : Screen("note_list")
    object NoteDetail : Screen("note_detail/{id}") {
        fun route(id: Long) = "note_detail/$id"
    }
    object AddEditNote : Screen("add_edit_note?id={id}") {
        fun route(id: Long? = null) = if (id != null) "add_edit_note?id=$id" else "add_edit_note"
    }

    object Settings : Screen("settings")
}
