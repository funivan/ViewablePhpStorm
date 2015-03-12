package org.funivan.phpstorm.ViewablePlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.funivan.phpstorm.Helper.Helper;
import org.funivan.phpstorm.ViewablePlugin.StubIndex.TemplateDataIndexer;
import org.funivan.phpstorm.ViewablePlugin.StubIndex.TemplateUsageStubIndex;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 3/10/15.
 */
public class ViewHelper {
    protected static Method viewRenderMethod = null;

    @Nullable
    public static PsiFile getViewFileForElement(PsiElement element) {
        String viewId = getViewIdFromElement(element);

        return getViewFileForElement(element, viewId);
    }

    @Nullable
    public static PsiFile getViewFileForElement(PsiElement element, String viewId) {
        PsiFile file = element.getOriginalElement().getContainingFile();
        PsiDirectory containingDirectory = file.getContainingDirectory();
        if (containingDirectory == null) {
            return null;
        }
        PsiDirectory viewsDirectory = containingDirectory.findSubdirectory("views");

        if (viewsDirectory == null) {
            return null;
        }

        PsiFile viewFile = viewsDirectory.findFile(viewId + ".html");

        if (viewFile == null) {
            return null;
        }

        return viewFile;
    }

    public static String getViewIdFromElement(PsiElement element) {
        return element.getText().replace("\"", "").replace("'", "");
    }

    @Nullable
    public static Method getRenderViewMethod(Project project) {
        if (viewRenderMethod == null) {
            viewRenderMethod = Helper.getTraitMethod(project, "\\Funivan\\Viewable\\Viewable", "renderView");
        }
        return viewRenderMethod;
    }

    public static String getViewIdFromFile(PsiFile templateFile) {
        return templateFile.getName().replaceAll("\\.html$", "");
    }

    public static boolean isViewFile(PsiElement psiElement) {
        if ((psiElement instanceof PsiFile) == false) {
            return false;
        }

        String filePath = Helper.getElementFilePath(psiElement);
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        if (filePath.matches("^.+/views/.+\\.html$") == false) {
            return false;
        }

        return true;
    }

    static public PsiElementPattern.Capture<StringLiteralExpression> methodWithFirstStringPattern() {
        return PlatformPatterns
                .psiElement(StringLiteralExpression.class)
                .withParent(
                        PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST)
                                .withFirstChild(
                                        PlatformPatterns.psiElement(PhpElementTypes.STRING)
                                )
                                .withParent(
                                        PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE)
                                )
                )
                .withLanguage(PhpLanguage.INSTANCE);
    }


    @Nullable
    public static List<MethodReference> getViewMethodReferences(@Nullable PsiFile file) {
        if (file == null) {
            return null;
        }
        PsiDirectory dir = file.getOriginalFile().getParent().getParent();
        if (dir == null) {
            return null;
        }

        VirtualFile virtualFile = dir.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }
        String directory = VfsUtil.getRelativePath(virtualFile, file.getProject().getBaseDir(), '/');

        String viewId = ViewHelper.getViewIdFromFile(file);

        String key = TemplateDataIndexer.getKey(directory, viewId);


//        System.out.println("access by key:" + key);
        List<String> values = TemplateUsageStubIndex.getKeys(file.getProject(), key);

        //System.out.println("values::" + values.size());

        List<MethodReference> methodReferences = new ArrayList<MethodReference>();

        for (String rowFromStorage : values) {
//            System.out.println("rowFromStorage < : " + rowFromStorage);
            Integer offset = TemplateDataIndexer.getOffsetFromRow(rowFromStorage);

            if (offset == null) {
                continue;
            }

            PhpClass phpClass = TemplateDataIndexer.getPhpClassFromRow(rowFromStorage, file.getProject());

            if (phpClass == null) {
                continue;
            }

//            PsiElement findElement = phpClass.findElementAt(offset);
//            if (findElement == null) {
//                continue;
//            }
//
//            PsiElement methodReference = findElement.getParent().getOriginalElement();
//
//            if (!(methodReference instanceof MethodReference)) {
//                continue;
//            }
//
//            methodReferences.add((MethodReference) methodReference);


//            PsiElement findElement = phpClass.findElementAt(offset);
            PsiElement findElement = phpClass.getContainingFile().findElementAt(offset);
//            System.out.println("findElement::" + findElement);
            if (findElement == null) {
                continue;
            }

//            System.out.println("findElement::" + findElement.getText());
            PsiElement methodReference = findElement.getParent().getParent();

//            System.out.println("methodReference::" + methodReference);

            if (!(methodReference instanceof MethodReference)) {
                continue;
            }

            methodReferences.add((MethodReference) methodReference);

        }

        return methodReferences;
    }
}
