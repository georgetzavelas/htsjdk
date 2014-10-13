package htsjdk.samtools.sjam;

import htsjdk.samtools.util.BinaryCodec;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

class SJAMFileWriter implements Closeable {

    private final BinaryCodec outputBinaryCodec;

    public SJAMFileWriter(final File path) throws FileNotFoundException {
        outputBinaryCodec = new BinaryCodec(path, true);
    }

    /** @return absolute path, or null if arg is null.  */
    public String getPathString(final File path){
        return (path != null) ? path.getAbsolutePath() : null;
    }

    protected void finish() {
        outputBinaryCodec.close();
    }

    /** @return absolute path, or null if this writer does not correspond to a file.  */
    protected String getFilename() {
        return outputBinaryCodec.getOutputFileName();
    }

    public void writeRecord(final SjamFileRecord sjamFileRecord) {
        outputBinaryCodec.writeBytes(sjamFileRecord.code);
    }

	@Override
	public void close() throws IOException {
		finish();
	}
}
