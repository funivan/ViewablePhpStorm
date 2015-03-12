package org.funivan.phpstorm.ViewablePlugin.ViewIdReference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ivan on 3/10/15.
 */
public class ViewPsiReference implements PsiReference {

    /**
     * The template string element
     */
    private StringLiteralExpression viewIdElement;

    /**
     * Project reference, which is needed e.x. for the
     * FilenameIndex.
     */
    private Project project;

    /**
     * Lazy cache for a cleaned string - which means a string without quotations.
     * Real view ID
     */
    private String viewId;

    /**
     *
     */
    private TextRange textRange;

    /**
     * @param viewIdElement
     * @param project
     */
    public ViewPsiReference(final StringLiteralExpression viewIdElement, Project project) {
        this.viewIdElement = viewIdElement;
        this.project = project;
        this.viewId = ViewHelper.getViewIdFromElement(viewIdElement);
        this.textRange = new TextRange(1, viewIdElement.getTextLength() - 1);
    }

    /**
     * @return the full source element
     * @see com.intellij.psi.PsiReference#getElement()
     */
    @Override
    public PsiElement getElement() {
        return viewIdElement;
    }

    /**
     * @return the full range incl. quotes.
     * @see com.intellij.psi.PsiReference#getRangeInElement()
     */
    @Override
    public TextRange getRangeInElement() {
        return textRange;
    }

    /**
     * @return the resolved template file or null
     * @see com.intellij.psi.PsiReference#resolve()
     */
    @Override
    public PsiElement resolve() {
        return ViewHelper.getViewFileForElement(this.getElement(), viewId);
    }

    /**
     * @return Plain text representation.
     * @see com.intellij.psi.PsiReference#getCanonicalText()
     */
    @NotNull
    @Override
    public String getCanonicalText() {
        return this.viewIdElement.getText();
    }

    /**
     * @param newElementName
     * @return the new string literal with the new text
     * @throws com.intellij.util.IncorrectOperationException
     * @see com.intellij.psi.PsiReference#handleElementRename(String)
     */
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        ASTNode node = viewIdElement.getNode();
        StringLiteralExpressionImpl se = new StringLiteralExpressionImpl(node);
        String viewId = newElementName.replaceAll("\\.html$", "");
        se.updateText(viewId);
        this.viewIdElement = se;
        return viewIdElement;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return resolve();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {


        if ((element instanceof PsiFile) == false) {
            return false;
        }

        if (ViewHelper.isViewFile(element) == false) {
            return false;
        }

        PsiFile templateFile = (PsiFile) element;

        String name = ViewHelper.getViewIdFromFile(templateFile);

        if (name.equals(viewId) == false) {
            // different names
            return false;
        }

        // check containing file
        PsiElement file = this.resolve();
        if (file == null) {
            return false;
        }

        if (file.equals(templateFile) == false) {
            // files in different locations
            return false;
        }

        return true;

    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    @Override
    public boolean isSoft() {
        return true;
    }


}
