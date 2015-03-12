package org.funivan.phpstorm.ViewablePlugin.VariableCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Created by ivan on 3/12/15.
 */
public class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

        PsiElement psiElement = completionParameters.getPosition().getOriginalElement();

        if (!(psiElement.getParent() instanceof VariableImpl)) {
            return;
        }
//        System.out.println("completion init");

        PsiFile file = psiElement.getContainingFile();

        if (file == null) {
//            System.out.println("Empty file:");
            return;
        }


        if (!ViewHelper.isViewFile(file)) {
//            System.out.println("Not view file");
            return;
        }

        List<MethodReference> methodReferences = ViewHelper.getViewMethodReferences(file);


        if (methodReferences.size() == 0) {
//            System.out.println("methodReferences:0");
            return;
        }


//        completionResultSet.addElement(LookupElementBuilder.create(phpClass, "$this->"));



        MethodReference methodReference = methodReferences.iterator().next();

        PhpPsiElement variable = methodReference.getFirstPsiChild();
        if (variable instanceof VariableImpl) {
            Collection<? extends PhpNamedElement> classElements = PhpIndex.getInstance(variable.getProject()).getBySignature(((VariableImpl) variable).getSignature());

            if (classElements.size() > 0) {

                PhpNamedElement namedElement = classElements.iterator().next();
                completionResultSet.addElement(LookupElementBuilder.create(namedElement, "$this->"));
            }

        }
//        methodReference.getSignature();
//        for (String s : values) {
//            String[] elements = s.split(":::");
//
//            String classFqn = elements[0];
//            Integer offset = Integer.valueOf(elements[1]);
//
//            PhpClass phpClass = Helper.getClassByFqn(classFqn, file.getProject());
//
//            if (phpClass == null) {
//                continue;
//            }
////
////                    PsiElement findElement = phpClass.findElementAt(offset);
////                    if (findElement == null) {
////                        continue;
////                    }
//
////                    //System.out.println("element:" + findElement.getParent().getFirstChild());
//
//            completionResultSet.addElement(LookupElementBuilder.create(phpClass, "$this->"));
//            //@todo add variable
//            break;
//
//        }

    }
}
