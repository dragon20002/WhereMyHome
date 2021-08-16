package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.Embedded
import androidx.room.Relation

class EvalFormGroupViewData(
    @Embedded
    val evalFormGroup: EvalFormGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "eval_form_group_id",
    )
    val evalForms: List<EvalForm>,
)