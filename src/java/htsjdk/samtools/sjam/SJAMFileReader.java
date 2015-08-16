/*
 * The MIT License
 *
 * Copyright (c) 2009 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package htsjdk.samtools.sjam;


import htsjdk.samtools.BAMIndex;
import htsjdk.samtools.BAMRecordCodec;
import htsjdk.samtools.DefaultSAMRecordFactory;
import htsjdk.samtools.QueryInterval;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMFileSpan;
import htsjdk.samtools.SAMFormatException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordFactory;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SAMTextHeaderCodec;
import htsjdk.samtools.SAMValidationError;
import htsjdk.samtools.SamFiles;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.util.BinaryCodec;
import htsjdk.samtools.util.BlockCompressedInputStream;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.CoordMath;
import htsjdk.samtools.util.StringLineReader;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class SJAMFileReader implements Closeable {

    private File path;
    private FileInputStream inputStream;
    private ByteArrayInputStream byteArrayInputStream;

    public SJAMFileReader(final File path) throws FileNotFoundException {
        this.path = path;
        this.inputStream = new FileInputStream(path);
    }

    public SjamFileRecord readRecord() throws IOException {
        byte[] result = new byte[(int) path.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(inputStream);
                while (totalBytesRead < result.length) {
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0) {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
                System.out.println("Num bytes read: " + totalBytesRead);
            } finally {
                System.out.println("Closing input stream.");
                input.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println(ex);
        }
        byteArrayInputStream = new ByteArrayInputStream(result);
        SjamFileRecord sjamFileRecord = new SjamFileRecord();
        sjamFileRecord.decode(byteArrayInputStream, false);

        System.out.println(sjamFileRecord);

        return sjamFileRecord;
    }

    @Override
    public void close() throws IOException {

    }
}
