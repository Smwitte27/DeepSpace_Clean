package frc.team2606.lib.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Writes data to a CSV file
 * @param <T>
 */
public class CSVWriter<T> {
    ConcurrentLinkedDeque<String> lines = new ConcurrentLinkedDeque<>();
    PrintWriter output = null;
    Field[] fields;

    public CSVWriter(String fileName, Class<T> typeClass) {
        fields = typeClass.getFields();
        try {
            output = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write field names
        StringBuffer line = new StringBuffer();
        for (Field field : fields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            line.append(field.getName());
        }
        writeLine(line.toString());
    }

    public void add(T value) {
        StringBuffer line = new StringBuffer();
        for (Field field : fields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            try {
                if (CSVWritable.class.isAssignableFrom(field.getType())) {
                    line.append(((CSVWritable) field.get(value)).toCSV());
                } else {
                    line.append(field.get(value).toString());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        lines.add(line.toString());
    }

    protected synchronized void writeLine(String line) {
        if (output != null) {
            output.println(line);
        }
    }

    /**
     * Call this periodically from any thread to write to disk.
     */
    public void write() {
        while (true) {
            String val = lines.pollFirst();
            if (val == null) {
                break;
            }
            writeLine(val);
        }
    }

    public synchronized void flush() {
        if (output != null) {
            write();
            output.flush();
        }
    }
}
