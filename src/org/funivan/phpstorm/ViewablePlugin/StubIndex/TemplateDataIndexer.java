package org.funivan.phpstorm.ViewablePlugin.StubIndex;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.funivan.phpstorm.Helper.Helper;
import org.funivan.phpstorm.ViewablePlugin.ViewHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TemplateDataIndexer implements DataIndexer<String, List<String>, FileContent> {
    public static final String DELIMITER = ":::";

    @NotNull
    @Override
    public Map<String, List<String>> map(@NotNull FileContent inputData) {

        final Map<String, List<String>> map = new THashMap<String, List<String>>();

        PsiFile psiFile = inputData.getPsiFile();


        if (psiFile == null || TemplateUsageStubIndex.isValidForIndex(inputData, psiFile) == false) {
            return map;
        }

        String relativePath = VfsUtil.getRelativePath(inputData.getFile(), psiFile.getProject().getBaseDir(), '/');
        if (relativePath == null) {
            return map;
        }


        PsiElement[] elements = PsiTreeUtil.collectElements(psiFile, new PsiElementFilter() {
            @Override
            public boolean isAccepted(PsiElement psiElement) {
                if (!(psiElement instanceof MethodReference)) {
                    return false;
                }

                MethodReference methodReference = (MethodReference) psiElement;
                String methodName = methodReference.getName();
                if (!methodName.equals("renderView")) {
                    return false;
                }

                PsiElement argument = Helper.getMethodReferenceArgument(methodReference, 0);

                if (argument == null) {
                    return false;
                }

                if (ViewHelper.methodWithFirstStringPattern().accepts(argument) == false) {
                    return false;
                }

                return true;
            }
        });

        if (elements.length == 0) {
            return map;
        }


        String directory = VfsUtil.getParentDir(relativePath);


        for (PsiElement el : elements) {
            MethodReference methodRef = (MethodReference) el;
            if (methodRef == null) {
                continue;

            }
            PsiElement argument = Helper.getMethodReferenceArgument(methodRef, 0);

            if (argument == null) {
                continue;
            }


            PhpClass phpClass = PsiTreeUtil.getParentOfType(methodRef, PhpClass.class);
            if (phpClass == null) {
                continue;
            }

            String viewId = ViewHelper.getViewIdFromElement(argument);
            String key = getKey(directory, viewId);

            List<String> data = map.get(key);


            if (data == null) {
                data = new ArrayList<String>();
            }


            String rowForStorage = getRowForStorage(phpClass, methodRef);
//            System.out.println("rowForStorage:" + rowForStorage);
            data.add(rowForStorage);

            map.put(key, data);

        }

        return map;
    }

    public static String getKey(String directory, String viewId) {
        return directory + DELIMITER + viewId;
    }

    public static String getRowForStorage(PhpClass phpClass, MethodReference methodReference) {
        int startOffsetInParent = methodReference.getNode().getStartOffset();
        return phpClass.getFQN() + DELIMITER + Integer.toString(startOffsetInParent);
    }

    @Nullable
    public static PhpClass getPhpClassFromRow(String row, Project project) {
        String[] elements = row.split(DELIMITER);

        if (elements.length != 2) {
            return null;
        }

        PhpClass phpClass = Helper.getClassByFqn(elements[0], project);

        if (phpClass == null) {
            return null;
        }

        return phpClass;
    }

    @Nullable
    public static Integer getOffsetFromRow(String row) {
        String[] elements = row.split(DELIMITER);

        if (elements.length != 2) {
            return null;
        }

        return Integer.valueOf(elements[1]);

    }


}
