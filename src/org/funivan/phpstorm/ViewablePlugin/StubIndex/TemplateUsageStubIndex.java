package org.funivan.phpstorm.ViewablePlugin.StubIndex;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import org.funivan.phpstorm.Helper.ListOfStringsDataExternalizer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TemplateUsageStubIndex extends FileBasedIndexExtension<String, List<String>> {

    public static final ID<String, List<String>> KEY = ID.create("org.funivan.phpstorm.ViewablePlugin.render_view_usage");

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();
    private static int MAX_FILE_BYTE_SIZE = 2097152;


    @NotNull
    @Override
    public ID<String, List<String>> getName() {
        return KEY;
    }

    @NotNull

    public DataIndexer<String, List<String>, FileContent> getIndexer() {
        return new TemplateDataIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }


    @NotNull
    public DataExternalizer<List<String>> getValueExternalizer() {
        return new ListOfStringsDataExternalizer();
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return PhpConstantNameIndex.PHP_INPUT_FILTER;
    }


    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 3;
    }

    public static boolean isValidForIndex(FileContent inputData, PsiFile psiFile) {
        //src

        String fileName = psiFile.getName();
        if (fileName.startsWith(".")) {
            return false;
        }


        // is Test file in path name
        String relativePath = VfsUtil.getRelativePath(inputData.getFile(), psiFile.getProject().getBaseDir(), '/');
        if (relativePath == null) {
            return false;
        }
        // only from src folder php files
        //@todo move to config folder
        if (relativePath.matches("^lib/.*\\.php$") == false) {
            return false;
        }
//        System.out.println("scan:" + relativePath);

        return inputData.getFile().getLength() < MAX_FILE_BYTE_SIZE;
    }


    public static List<String> getKeys(Project project, String key) {
        final List<String> result = new ArrayList<String>();
        FileBasedIndex instance = FileBasedIndex.getInstance();

        if (instance == null) {
            return result;
        }

        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);
        if (globalSearchScope == null) {
            return result;
        }


        List<List<String>> values = instance.getValues(KEY, key, globalSearchScope);

        Collection<String> allLKeys = FileBasedIndex.getInstance().getAllKeys(KEY, project);

        if (values == null) {
            return result;
        }

        for (List<String> list : values) {
            result.addAll(list);
        }
        return result;
    }

}



