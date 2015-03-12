package org.funivan.phpstorm.Helper;

import com.intellij.util.io.DataExternalizer;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * com.jetbrains.php.lang.psi.stubs.indexes.PhpTraitUsageIndex
 */
public class ListOfStringsDataExternalizer implements DataExternalizer<List<String>> {

    @Override
    public void save(@NotNull DataOutput out, List<String> value) throws IOException {
        out.writeInt(value.size());
        for (String data : value) {
            out.writeUTF(data);
        }
    }

    @Override
    public List<String> read(@NotNull DataInput in) throws IOException {
        final int size = in.readInt();
        final List<String> result = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            result.add(in.readUTF());
        }
        return result;
    }

}
