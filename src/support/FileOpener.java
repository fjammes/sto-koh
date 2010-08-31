package support;

/**
 * Generic Data File Reader
 *  @author K.A.J.Doherty
 *  @version 1.1 23 November 2007 Added <Generics> to silence compiler > 1.5 bitching about unsafes and casts
 */

import java.io.*;
import java.util.*;
import structures.*;

public class FileOpener {
    private Vector<Object> v = new Vector<Object>();
    private Vertex[] data;
    private int dataDim;
    private boolean labelled;
    private String mode;

    /**
     * The class fileOpener opens the specified data file and reads in and
     * formats the data in accordance with the data_dim and label_dim values.
     * The data is stored in either RAW or NORMALISED form.
     *  @param file The file to open
     *  @param data_dim The arity of the data fields
     *  @param labelled Whether thie data is labelled or not
     *  @param Mode The mode in which the data is processed. Mode takes values RAW | NORMALISED
     *  @exception FileNotFoundException Indicates file does not exist
     *  @exception IOException Indicates a general IO error
     */
    public FileOpener(final String file, final int data_dim, final boolean labelled, final String Mode) {
        dataDim = data_dim;
        this.labelled = labelled;
        mode = Mode;
        try {
            readFile(file);
            populateData();
        }
        catch (FileNotFoundException e) {
            handleException(e);
        }
        catch (IOException e) {
            handleException(e);
        }
    }

    final private void populateData() {
        int MAXINPUTS;
        int labelDim;
        if (labelled) {
            labelDim = 1;
        } else {
            labelDim = 0;
        }
        MAXINPUTS = v.size() / (dataDim + labelDim);
        this.data = new Vertex[MAXINPUTS];
        int i, j;
        double[] dm = new double[dataDim];
        String label = "";
        for (i = 0; i < MAXINPUTS; i++) {
            for (j = 0; j < dataDim; j++) {
                try {
                    dm[j] = ((Double)v.get(i * (dataDim + labelDim) + j)).doubleValue();
                }
                catch (NumberFormatException e) {
                    System.err.println("Exception: " + e);
                }
            }
            for (j = dataDim; j < dataDim + labelDim; j++) {
                try {
                    Object o = v.get(i * (dataDim + labelDim) + j);
                    if (o instanceof String) {
                        label = o.toString();
                    } else if (o instanceof Double) {
                        label = (((Double)o).toString());
                    }
                }
                catch (NumberFormatException e) {
                    System.err.println("Exception: " + e);
                }
            }
            data[i] = new Vertex((double[]) dm.clone(), label);
        }
        // PREPROCESS THE INPUT
        if (mode.equals("RAW")) {
            //
            // do nothing as data [] is good
            //
        }
        else if (mode.equals("NORMALISED")) {
            double[] max = new double[dataDim];
            double[] min = new double[dataDim];
            double[] temp;
            // Initialise max and min values
            for (i = 0; i < dataDim; i++) {
                max[i] = Double.MIN_VALUE;
                min[i] = Double.MAX_VALUE;
            }
            // Find the max and min for each attribute
            for (i = 0; i < data.length; i++) {
                temp = data[i].getPosition();
                for (j = 0; j < dataDim; j++) {
                    if (temp[j] > max[j]) {
                        max[j] = temp[j];
                    }
                    if (temp[j] < min[j]) {
                        min[j] = temp[j];
                    }
                }
            }
            // Write ranged values into gower datum array
            for (i = 0; i < data.length; i++) {
                temp = data[i].getPosition();
                for (j = 0; j < dataDim; j++) {
                    temp[j] = (temp[j] - min[j]) / (max[j] - min[j]);
                }
                data[i].setPosition(temp);
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported Mode: " + mode);
        }
    }

    /**
     * Get the fileOpener data
     * @return datum [] populated with the data taken from the fileOpener file parameter.  The returned data is pre- processed
     * in accordance with the Mode parameter
     */
    final public Vertex[] getData() {
        return data;
    }

    /**
     * Get the arithmetic mean of the fileOpener data
     * @return doubles [] (length equal to data_dim constructor parameter),
     * each element containing the arithmetic mean of all data items
     */
    final public double[] getMean() {
        // Sample Mean
        int i, j;
        double[] pos;
        double[] mean = new double[dataDim];
        for (i = 0; i < data.length; i++) {
            pos = data[i].getPosition();
            for (j = 0; j < dataDim; j++) {
                mean[j] += pos[j];
            }
        }
        for (i = 0; i < dataDim; i++) {
            mean[i] /= data.length;
        }
        return mean;
    }

    final private void readFile(final String in) throws IOException {
        int t;
        final File data_file = new File(in);
        FileInputStream data_file_stream = null;
        InputStreamReader input_stream_reader = null;
        BufferedReader buffered_reader = null;
        StreamTokenizer tokenizer = null;
        try {
            data_file_stream = new FileInputStream(data_file);
            input_stream_reader = new InputStreamReader(data_file_stream);
            buffered_reader = new BufferedReader(input_stream_reader);
            tokenizer = new StreamTokenizer(buffered_reader);
            while ((t = tokenizer.nextToken()) != tokenizer.TT_EOF) {
                if (t == tokenizer.TT_NUMBER) {
                    v.addElement(new Double(tokenizer.nval));
                } else {
                    v.addElement(tokenizer.sval);
                }
            }
        }
        catch (IOException e) {
            handleException(e);
        }
        finally {
            if (data_file_stream != null) {
                try {
                    data_file_stream.close();
                }
                catch (IOException e) {
                    handleException(e);
                }
                ;
            }
            if (input_stream_reader != null) {
                try {
                    input_stream_reader.close();
                }
                catch (IOException e) {
                    handleException(e);
                }
                ;
            }
            if (buffered_reader != null) {
                try {
                    buffered_reader.close();
                }
                catch (IOException e) {
                    handleException(e);
                }
                ;
            }
        }
    }

    final private static void handleException(Exception e) {
        System.err.println(e);
        System.exit(-1);
    }
}
