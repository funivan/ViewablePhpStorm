package org.funivan.phpstorm.ViewablePlugin.ViewIdReference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl;
import org.funivan.phpstorm.Helper.Helper;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ivan on 3/10/15.
 */
public class ViewPsiReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

        PsiElement el = element.getParent().getParent();

        if ((el instanceof MethodReferenceImpl) == false) {
            return PsiReference.EMPTY_ARRAY;
        }

        Method method = ViewHelper.getRenderViewMethod(element.getProject());

        if (method == null || Helper.isCallTo(el, method) == false) {
            return PsiReference.EMPTY_ARRAY;
        }

        StringLiteralExpression se = (StringLiteralExpression) element;

        ViewPsiReference psiReference = new ViewPsiReference(se, element.getProject());

        if (psiReference.resolve() != null) {
            return new PsiReference[]{psiReference};
        }
        return PsiReference.EMPTY_ARRAY;
    }


}

