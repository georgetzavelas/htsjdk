package htsjdk.samtools.sjam;

import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.sjam.SjamFileRecord.SubSeqOf_alignment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.testng.annotations.Test;

public class SJAMFileTest {
	public static final String INPUT_SAM = "c:\\work\\sjam\\samples\\sim_reads_aligned.sam";
	public static final String OUTPUT_SJAM = "c:\\work\\sjam\\samples\\sim_reads_aligned.sjam";
    public static final String INPUT_SJAM = OUTPUT_SJAM;
    public static final String OUTPUT_SAM = INPUT_SAM;

	@Test
	public void testWrite() {
        final SAMFileReader reader = new SAMFileReader(new File(INPUT_SAM));
//        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), true, OUTPUT);
        SJAMFileWriter writer = null;
		try {
			writer = new SJAMFileWriter(new File(OUTPUT_SJAM));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        List<Alignment> alignmentList = new ArrayList<>();
        Header header = new Header();
        
        for (final SAMRecord rec : reader) {
        	/* Alignment record */
        	Alignment alignment = new Alignment();
        	alignment.qname = new BerVisibleCompressedString(rec.getReadName());
        	alignment.flag = new BerInteger(rec.getFlags());
        	alignment.rname = new BerVisibleCompressedString(rec.getReferenceName());
        	alignment.pos = new BerInteger(rec.getAlignmentStart());
        	alignment.mapq = new BerInteger(rec.getMappingQuality());
        	alignment.cigar = new BerVisibleCompressedString(rec.getCigarString());
        	if (rec.getReferenceName() == rec.getMateReferenceName() &&
        			SAMRecord.NO_ALIGNMENT_REFERENCE_NAME != rec.getReferenceName()) {
        		alignment.rnext = new BerVisibleCompressedString("=");
        	} else {
            	alignment.rnext = new BerVisibleCompressedString(rec.getMateReferenceName());
        	}
        	alignment.pnext = new BerInteger(rec.getMateAlignmentStart());
        	alignment.tlen = new BerInteger(rec.getInferredInsertSize());
        	alignment.seq = new BerVisibleCompressedString(rec.getReadString());
        	alignment.qual = new BerVisibleCompressedString(rec.getBaseQualityString());
        	alignmentList.add(alignment);
        }

		SjamFileRecord sjamFileRecord = new SjamFileRecord(null, new SubSeqOf_alignment(alignmentList));
		writer.writeRecord(sjamFileRecord);

		reader.close();
        try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Test
	public void testRead() throws IOException {
//		final SAMFileWriter writer = new SAMFileWriter(new File(OUTPUT_SAM));
        SJAMFileReader reader = null;
        try {
            reader = new SJAMFileReader(new File(INPUT_SJAM));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SjamFileRecord sjamFileRecord = reader.readRecord();
        System.out.println(sjamFileRecord.header);

//        List<Alignment> alignmentList = new ArrayList<>();
//        Header header = new Header();
//
//        for (final SAMRecord rec : reader) {
//        	/* Alignment record */
//            Alignment alignment = new Alignment();
//            alignment.qname = new BerVisibleCompressedString(rec.getReadName());
//            alignment.flag = new BerInteger(rec.getFlags());
//            alignment.rname = new BerVisibleCompressedString(rec.getReferenceName());
//            alignment.pos = new BerInteger(rec.getAlignmentStart());
//            alignment.mapq = new BerInteger(rec.getMappingQuality());
//            alignment.cigar = new BerVisibleCompressedString(rec.getCigarString());
//            if (rec.getReferenceName() == rec.getMateReferenceName() &&
//                    SAMRecord.NO_ALIGNMENT_REFERENCE_NAME != rec.getReferenceName()) {
//                alignment.rnext = new BerVisibleCompressedString("=");
//            } else {
//                alignment.rnext = new BerVisibleCompressedString(rec.getMateReferenceName());
//            }
//            alignment.pnext = new BerInteger(rec.getMateAlignmentStart());
//            alignment.tlen = new BerInteger(rec.getInferredInsertSize());
//            alignment.seq = new BerVisibleCompressedString(rec.getReadString());
//            alignment.qual = new BerVisibleCompressedString(rec.getBaseQualityString());
//            alignmentList.add(alignment);
//        }
//
//        SjamFileRecord sjamFileRecord = new SjamFileRecord(header, new SubSeqOf_alignment(alignmentList));
//        writer.writeRecord(sjamFileRecord);

        reader.close();
//        try {
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}

    @Test
    public void testCompression() throws IOException {
        String test = "This is a test!";
        BerByteArrayOutputStream bos = new BerByteArrayOutputStream(100);
        BerVisibleCompressedString bvcsEncoder = new BerVisibleCompressedString(test);
        int encodeCount = bvcsEncoder.encode(bos, true);
        BerVisibleCompressedString bvcsDecoder = new BerVisibleCompressedString();
        int decodeCount = bvcsDecoder.decode(new ByteArrayInputStream(bos.getArray()), true);
        System.out.println("Origninal:" + test);
        System.out.println("Decoded:" + bvcsDecoder.toString());
    }
}
