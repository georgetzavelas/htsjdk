package htsjdk.samtools.sjam;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class BerVisibleCompressedString extends BerVisibleString {
    public BerVisibleCompressedString() {
        super();
    }

    public BerVisibleCompressedString(String string) {
        super(string);
    }

    public int encode(BerByteArrayOutputStream berOStream, boolean explicit) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        deflater.setInput(octetString);
        deflater.finish();

        byte[] buffer = new byte[1024];
        int bytesCompressed = deflater.deflate(buffer);
        int codeLength = 0;

        try {
            /* write in reverse so the decoding will work */
            for (int i = 1; i <= bytesCompressed; i++) {
                berOStream.write(buffer[bytesCompressed - i]);
            }

            codeLength += BerLength.encodeLength(berOStream, bytesCompressed);
            if(explicit) {
                codeLength += this.id.encode(berOStream);
            }
            //close the output stream
            berOStream.close();
        } catch(IOException ioe) {
            System.out.println("Error while closing the stream : " + ioe);
        }

        return codeLength;
    }

    public int decode(InputStream iStream, boolean explicit) throws IOException {
        int codeLength = 0;
        if(explicit) {
            codeLength += this.id.decodeAndCheck(iStream);
        }

        BerLength length = new BerLength();
        codeLength += length.decode(iStream);
        byte compressedInput[] = new byte[length.val];
        if(length.val != 0) {
            iStream.read(compressedInput);
            codeLength += length.val;
        }

        Inflater inflater = new Inflater();
        inflater.setInput(compressedInput);

        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                inflater.inflate(buffer);
            }
            this.octetString = buffer;
        } catch (DataFormatException e) {
            e.printStackTrace();
        } finally {
            inflater.end();
        }

        return codeLength;
    }
}