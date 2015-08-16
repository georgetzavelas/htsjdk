package htsjdk.samtools.sjam;

import htsjdk.samtools.util.RuntimeIOException;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.SyncFailedException;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

class SJAMFileWriter implements Closeable {

	private File path;
	private OutputStream outputStream;
    private final BerByteArrayOutputStream berByteArrayOutputStream;

    public SJAMFileWriter(final File path) throws FileNotFoundException {
    	this.path = path;
        this.outputStream = new FileOutputStream(path);
        berByteArrayOutputStream = new BerByteArrayOutputStream(1000, true);
    }

    /** @return absolute path, or null if arg is null.  */
    public String getPathString(final File path){
        return (path != null) ? path.getAbsolutePath() : null;
    }

    protected void finish() {
        try {
        	// To the degree possible, make sure the bytes get forced to the file system,
            // or else cause an exception to be thrown.
            if (this.outputStream instanceof FileOutputStream) {
            	this.outputStream.flush();
                FileOutputStream fos = (FileOutputStream)this.outputStream;
                try {
                    fos.getFD().sync();
                } catch (SyncFailedException e) {
                    // Since the sync is belt-and-suspenders anyway, don't throw an exception if it fails,
                    // because on some OSs it will fail for some types of output.  E.g. writing to /dev/null
                    // on some Unixes.
                }
            }
            this.outputStream.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e.getMessage(), e);
        }
    }

    public void writeBytes(final byte[] bytes, final int startOffset, final int numBytes) {
        try {
            outputStream.write(bytes, startOffset, numBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** @return absolute path, or null if this writer does not correspond to a file.  */
    protected String getFilename() {
        return path.getPath();
    }

    public void writeRecord(final SjamFileRecord sjamFileRecord) {
    	try {
			sjamFileRecord.encode(berByteArrayOutputStream, false);
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(berByteArrayOutputStream.getArray());
            fos.flush();
            fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }

	@Override
	public void close() throws IOException {
		finish();
	}
}
