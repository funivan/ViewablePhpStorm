package org.funivan.phpstorm.ViewablePlugin.VariableCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;

/**
 * @author Adrien Brault <adrien.brault@gmail.com>
 */
public class VariableCompletionContributor extends CompletionContributor {


    public VariableCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
//                PlatformPatterns.psiElement().withParent(
//                        PlatformPatterns.psiElement(Variable.class)
//                )
                , new VariableCompletionProvider());

    }


}

