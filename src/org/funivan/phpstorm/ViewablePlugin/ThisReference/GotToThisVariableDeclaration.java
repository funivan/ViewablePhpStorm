package org.funivan.phpstorm.ViewablePlugin.ThisReference;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import org.funivan.phpstorm.Helper.Helper;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by funivan on 11/12/14.
 */
public class GotToThisVariableDeclaration implements GotoDeclarationHandler {

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement psiElement, int i, Editor editor) {


        if (psiElement.getText().equals("$this") == false) {
            return null;
        }

        Boolean isVariable = PlatformPatterns.psiElement(PhpTokenTypes.VARIABLE).accepts(psiElement);

        if (!isVariable) {
            return null;
        }

        PsiElement parent = psiElement.getParent();
        if (parent == null || (parent instanceof VariableImpl) == false) {
            return null;
        }
        Variable variable = (Variable) parent;
        if (variable == null) {
            return null;
        }

        //@todo check if variable unresolved
        if (variable.getSignature().equals("#Vthis") == false) {
            return null;
        }

        PsiFile file = psiElement.getContainingFile();
        if (file == null) {
            return null;
        }


        List<MethodReference> methodReferences = ViewHelper.getViewMethodReferences(file);
        Iterator<MethodReference> iterator = methodReferences.iterator();

        List<PsiElement> elements = new ArrayList<PsiElement>();

        while (iterator.hasNext()) {
            MethodReference methodReference = iterator.next();
            PsiElement argument = Helper.getMethodReferenceArgument(methodReference, 0);
            if (argument == null) {
                continue;
            }
            elements.add(argument);
        }

        PsiElement[] result = new PsiElement[elements.size()];
        elements.toArray(result);
        return result;

    }

    @Nullable
    @Override

    public String getActionText(DataContext dataContext) {
        return null;
    }
}
