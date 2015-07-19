package htsjdk.samtools.sjam;

import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.sjam.SjamFileRecord.SubSeqOf_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.testng.annotations.Test;

public class SJAMFileTest {
	public static final String INPUT = "c:\\temp\\sim_reads_aligned.sam";
	public static final String OUTPUT = "c:\\temp\\sim_reads_aligned.sjam";
	
	@Test
	public void testDoWork() {
        final SAMFileReader reader = new SAMFileReader(new File(INPUT));
//        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), true, OUTPUT);
        SJAMFileWriter writer = null;
		try {
			writer = new SJAMFileWriter(new File(OUTPUT));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        List<Alignment> alignmentList = new ArrayList<Alignment>();
        Header header = new Header();
        
        for (final SAMRecord rec : reader) {
        	/* Alignment record */
        	Alignment alignment = new Alignment();
        	alignment.qname = new BerVisibleString(rec.getReadName());
        	alignment.flag = new BerInteger(rec.getFlags());
        	alignment.rname = new BerVisibleString(rec.getReferenceName());
        	alignment.pos = new BerInteger(rec.getAlignmentStart());
        	alignment.mapq = new BerInteger(rec.getMappingQuality());
        	alignment.cigar = new BerVisibleString(rec.getCigarString());
        	if (rec.getReferenceName() == rec.getMateReferenceName() &&
        			SAMRecord.NO_ALIGNMENT_REFERENCE_NAME != rec.getReferenceName()) {
        		alignment.rnext = new BerVisibleString("=");
        	} else {
            	alignment.rnext = new BerVisibleString(rec.getMateReferenceName());
        	}
        	alignment.pnext = new BerInteger(rec.getMateAlignmentStart());
        	alignment.tlen = new BerInteger(rec.getInferredInsertSize());
        	alignment.seq = new BerVisibleString(rec.getReadString());
        	alignment.qual = new BerVisibleString(rec.getBaseQualityString());
//          SAMBinaryTagAndValue attribute = alignment.getBinaryAttributes();
//          while (attribute != null) {
//              out.write(FIELD_SEPARATOR);
//              final String encodedTag;
//              if (attribute.isUnsignedArray()) {
//                  encodedTag = tagCodec.encodeUnsignedArray(tagUtil.makeStringTag(attribute.tag), attribute.value);
//              } else {
//                  encodedTag = tagCodec.encode(tagUtil.makeStringTag(attribute.tag), attribute.value);
//              }
//              out.write(encodedTag);
//              attribute = attribute.getNext();
//          }
        	alignmentList.add(alignment);
        }

		SjamFileRecord sjamFileRecord = new SjamFileRecord(header, new SubSeqOf_alignment(alignmentList));
		writer.writeRecord(sjamFileRecord);
        	
		reader.close();
        try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

//    /**
//     * Write the record.
//     *
//     * @param alignment SAMRecord.
//     */
//    public void writeAlignment(final SAMRecord alignment) {
//        try {
//            out.write(alignment.getReadName());
//            out.write(FIELD_SEPARATOR);
//            out.write(Integer.toString(alignment.getFlags()));
//            out.write(FIELD_SEPARATOR);
//            out.write(alignment.getReferenceName());
//            out.write(FIELD_SEPARATOR);
//            out.write(Integer.toString(alignment.getAlignmentStart()));
//            out.write(FIELD_SEPARATOR);
//            out.write(Integer.toString(alignment.getMappingQuality()));
//            out.write(FIELD_SEPARATOR);
//            out.write(alignment.getCigarString());
//            out.write(FIELD_SEPARATOR);
//
//            //  == is OK here because these strings are interned
//            if (alignment.getReferenceName() == alignment.getMateReferenceName() &&
//                    SAMRecord.NO_ALIGNMENT_REFERENCE_NAME != alignment.getReferenceName()) {
//                out.write("=");
//            } else {
//                out.write(alignment.getMateReferenceName());
//            }
//            out.write(FIELD_SEPARATOR);
//            out.write(Integer.toString(alignment.getMateAlignmentStart()));
//            out.write(FIELD_SEPARATOR);
//            out.write(Integer.toString(alignment.getInferredInsertSize()));
//            out.write(FIELD_SEPARATOR);
//            out.write(alignment.getReadString());
//            out.write(FIELD_SEPARATOR);
//            out.write(alignment.getBaseQualityString());
//            SAMBinaryTagAndValue attribute = alignment.getBinaryAttributes();
//            while (attribute != null) {
//                out.write(FIELD_SEPARATOR);
//                final String encodedTag;
//                if (attribute.isUnsignedArray()) {
//                    encodedTag = tagCodec.encodeUnsignedArray(tagUtil.makeStringTag(attribute.tag), attribute.value);
//                } else {
//                    encodedTag = tagCodec.encode(tagUtil.makeStringTag(attribute.tag), attribute.value);
//                }
//                out.write(encodedTag);
//                attribute = attribute.getNext();
//            }
//            out.write("\n");
//
//        } catch (IOException e) {
//            throw new RuntimeIOException(e);
//        }
//    }
}
