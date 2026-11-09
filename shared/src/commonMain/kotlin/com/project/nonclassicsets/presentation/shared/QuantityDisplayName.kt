package com.project.nonclassicsets.presentation.shared

import androidx.compose.runtime.Composable
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.add_default_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun FuzzyQuantity.displayName(): String = name ?: stringResource(Res.string.add_default_name, id)
