package com.example.myapplication

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.ArrayMap
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews

class MyAutofillService : AutofillService() {
    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val structure =
            request.fillContexts[request.fillContexts.size - 1].structure //accessing the last assist structure
        //contains all the views and nodes
        val fields = parseAutofillableFields(structure)

        val response = FillResponse.Builder()


        //create the dataset for our response
        for (i in 1..3) {
            val set = Dataset.Builder()
            for ((hint, id) in fields) {
                val value = i.toString() + " " + hint
                val view = createRemoteViews(value)
                set.setValue(id, AutofillValue.forText(value), view)
            }

            response.addDataset(set.build())
        }

        callback.onSuccess(response.build())
    }

    private fun createRemoteViews(text: CharSequence): RemoteViews {
        val presentation = RemoteViews(packageName, R.layout.autofill_list_view)
        presentation.setTextViewText(R.id.text, text)
        return presentation
    }

    private fun parseAutofillableFields(structure: AssistStructure): Map<String, AutofillId> {
        val autoFillFields = ArrayMap<String, AutofillId>()
        for (i in 0 until structure.windowNodeCount) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(autoFillFields, node)
        }

        return autoFillFields

    }

    private fun addAutofillableFields(autoFillFields: MutableMap<String, AutofillId>, node: AssistStructure.ViewNode) {
        val hints = node.autofillHints
        val id = node.autofillId
        if (hints != null && id != null) {
            val hint = hints[0].toLowerCase()

            if (!autoFillFields.containsKey(hint)) {
                autoFillFields[hint] = id
            }
        }
        for (i in 0 until node.childCount) {
            addAutofillableFields(autoFillFields, node.getChildAt(i))
        }
    }

}