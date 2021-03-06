package com.minuminu.haruu.kirinapp.view.evalformgroupdetails

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.kirinapp.db.AppDatabase
import com.minuminu.haruu.kirinapp.db.data.EvalForm
import com.minuminu.haruu.kirinapp.db.data.EvalFormGroup
import com.minuminu.haruu.kirinapp.db.data.EvalFormGroupViewData
import com.minuminu.haruu.kirinapp.db.data.EvalFormViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EvalFormGroupDetailsViewModel : ViewModel() {
    private var db: AppDatabase? = null

    // db - viewModel
    val evalFormGroupLiveData = MutableLiveData<EvalFormGroup>()
    val evalFormListLiveData = MutableLiveData<List<EvalFormViewData>>()

    // viewModel - view
    val isEditing = ObservableField(false)
    val name = ObservableField<String>()
    val description = ObservableField<String>()
    val evalFormList: ObservableList<EvalFormViewData> = ObservableArrayList()

    fun init(db: AppDatabase) {
        this.db = db
    }

    fun loadEvalFormGroup(evalFormGroupId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.evalFormGroupDao()?.getOneById(evalFormGroupId)?.let { evalFormGroup ->
                evalFormGroupLiveData.postValue(evalFormGroup)
            }
        }
    }

    fun loadEvalFormList(evalFormGroupId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.evalFormDao()?.getAllByEvalFormGroupId(evalFormGroupId)?.let { evalFormList ->
                evalFormListLiveData.postValue(evalFormList.map {
                    EvalFormViewData(
                        it.id,
                        it.category,
                        it.num.toString(),
                        it.content,
                        it.method,
                        it.weight.toString(),
                        it.evalFormGroupId
                    )
                })
            }
        }
    }

    suspend fun saveItem(): EvalFormGroup {
        val evalFormGroupWithevalForms = EvalFormGroupViewData(
            evalFormGroup = EvalFormGroup(
                evalFormGroupLiveData.value?.id,
                name.get(),
                description.get(),
            ),
            evalForms = evalFormList.filter { !it.deleted }.mapIndexed { index, evalForm ->
                EvalForm(
                    evalForm.id,
                    evalForm.category,
                    index + 1,
                    evalForm.content,
                    evalForm.method,
                    evalForm.weight.toFloat(),
                    null
                )
            },
        )

        return withContext(Dispatchers.IO) {
            // DB Insert
            evalFormGroupWithevalForms.apply {
                if (evalFormGroup.id == null) { // Add
                    val ids = db?.evalFormGroupDao()?.insertAll(evalFormGroup)
                    Log.d(javaClass.name, "inserted evalFormGroup ${ids?.get(0)}")

                    ids?.get(0)?.let { id ->
                        for (evalForm in evalForms) {
                            evalForm.evalFormGroupId = id
                            val evalFormIds = db?.evalFormDao()?.insertAll(evalForm)
                            Log.d(javaClass.name, "inserted evalForm ${evalFormIds?.get(0)}")
                        }
                    }
                } else { // Update
                    val cnt = db?.evalFormGroupDao()?.updateAll(evalFormGroup)
                    Log.d(javaClass.name, "updated evalFormGroup $cnt")

                    if (cnt != null) {
                        for (evalForm in evalForms) {
                            evalForm.evalFormGroupId = evalFormGroup.id
                            val evalFormCnt = db?.evalFormDao()?.updateAll(evalForm)
                            Log.d(javaClass.name, "updated evalForm $evalFormCnt")
                        }
                    }
                }
            }.evalFormGroup
        }
    }
}