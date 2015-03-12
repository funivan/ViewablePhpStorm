package org.funivan.phpstorm.ViewablePlugin.ThisReference;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.funivan.phpstorm.Helper.Helper;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;


/**
 * @author funivan <dev@funivan.com>
 */
public class ThisTypeProvider implements PhpTypeProvider2 {

    final static char TRIM_KEY = '\u0180';

    @Override
    public char getKey() {
        return '\u0153';
    }


    @Nullable
    @Override
    public String getType(PsiElement psiElement) {


        if (psiElement.getText().equals("$this") == false) {
            return null;
        }

        System.out.println("psiElement.getText()::" + psiElement.getText());

        PsiFile psiFile = psiElement.getContainingFile();

        if (psiFile == null) {
            return null;
        }

        boolean isView = ViewHelper.isViewFile(psiFile);

        if (!isView) {
//            System.out.println("Not view file");
            return null;
        }


        //System.out.println("psiElement.getContainingFile():" + psiElement.getContainingFile());
        List<MethodReference> methodReferences = ViewHelper.getViewMethodReferences(psiFile);

        if (methodReferences == null || methodReferences.size() == 0) {
//            System.out.println("empty method references");
            return null;
        }

        MethodReference methodReference = methodReferences.iterator().next();


        PsiElement variable = methodReference.getFirstChild();

        if (!(variable instanceof VariableImpl)) {
            //System.out.println("not variable call:" + psiElement.getText());
            return null;
        }
        Variable var = (Variable) variable;

        return var.getSignature();
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        Collection<? extends PhpNamedElement> elements = phpIndex.getBySignature(s);

        return elements;
    }

}
