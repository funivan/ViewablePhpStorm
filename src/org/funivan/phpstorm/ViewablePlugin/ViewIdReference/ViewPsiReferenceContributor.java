package org.funivan.phpstorm.ViewablePlugin.ViewIdReference;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;

/**
 * Created by ivan on 3/10/15.
 */
public class ViewPsiReferenceContributor extends PsiReferenceContributor {

    /**
     * Registers the reference provider for any string in php files that match the regex for
     * a single or double quoted string like: "AcmeBundle:Default:index.html.twig".
     *
     * @param registrar
     */
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        PsiElementPattern.Capture<StringLiteralExpression> psiElementCapture = ViewHelper.methodWithFirstStringPattern();


        registrar.registerReferenceProvider(psiElementCapture, new ViewPsiReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY);
    }


}
