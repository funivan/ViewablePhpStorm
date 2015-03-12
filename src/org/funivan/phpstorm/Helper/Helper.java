package org.funivan.phpstorm.Helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


/**
 * Created by ivan on 3/10/15.
 */
public class Helper {

    @Nullable
    public static PsiElement getMethodReferenceArgument(MethodReference methodRef, Integer argumentIndex) {

        PsiElement[] parameterList = methodRef.getParameters();

        if (parameterList.length == 0) {
            return null;
        }


        if (parameterList[argumentIndex] == null) {
            return null;
        }

        return parameterList[argumentIndex];
    }

    public static boolean isCallTo(PsiElement e, Method method) {
        if (!(e instanceof MethodReference)) {
            return false;
        }

        MethodReference methodRef = (MethodReference) e;

        if (method.getName().equals(methodRef.getName()) == false) {
            return false;
        }

        PsiReference psiReference = methodRef.getReference();
        if (null == psiReference) {
            return false;
        }


        PsiElement resolvedReference = psiReference.resolve();
        if ((resolvedReference instanceof Method) == false) {
            return false;
        }

        Method resolvedMethod = (Method) resolvedReference;

        return resolvedMethod.getFQN().equals(method.getFQN());
    }

    @Nullable
    static public Method getTraitMethod(Project project, String traitFqn, String methodName) {
        for (PhpClass phpClass : PhpIndex.getInstance(project).getTraitsByFQN(traitFqn)) {
            Method method = phpClass.findMethodByName(methodName);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    @Nullable
    public static String getElementFilePath(PsiElement e) {
        PsiFile containingFile = e.getOriginalElement().getContainingFile();

        if (containingFile == null) {
            return null;
        }

        PsiFile originalFile = containingFile.getOriginalFile();

        if (originalFile == null) {
            return null;
        }

        VirtualFile virtualFile = originalFile.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }

        String path = virtualFile.getPath();
        if (path == null) {
            return null;
        }

        return path;
    }

    @Nullable
    public static PhpClass getClassByFqn(String fqn, Project project) {
        Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN(fqn);

        if (classes.size() == 0) {
            return null;
        }

        return classes.iterator().next();
    }
}
