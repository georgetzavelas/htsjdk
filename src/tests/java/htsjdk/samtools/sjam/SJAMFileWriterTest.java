package htsjdk.samtools.sjam;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordSetBuilder;
import htsjdk.samtools.util.CloseableIterator;

import java.io.File;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class SJAMFileWriterTest {

	/**
    * Parse some SAM text into a SAM object, then write as BAM.  If SAM text was presorted, then the BAM file can
    * be read and compared with the SAM object.
    * @param samRecordSetBuilder source of input SAMFileReader to be written and compared with
    * @param sortOrder How the BAM should be written
    * @param presorted If true, samText is in the order specified by sortOrder
    */
   private void testHelper(final SAMRecordSetBuilder samRecordSetBuilder, final SAMFileHeader.SortOrder sortOrder, final boolean presorted) throws Exception {
       SAMFileReader samReader = samRecordSetBuilder.getSamReader();
       final File sjamFile = File.createTempFile("test.", ".sjam");
       sjamFile.deleteOnExit();
       samReader.getFileHeader().setSortOrder(sortOrder);
       final SJAMFileWriter sjamWriter = new SJAMFileWriter(sjamFile);
//       final SAMFileWriter bamWriter = new SAMFileWriterFactory().makeSAMOrBAMWriter(samReader.getFileHeader(), presorted, bamFile);
       CloseableIterator<SAMRecord> it = samReader.iterator();
       while (it.hasNext()) {
//       	sjamWriter.addAlignment(it.next());
       }
       sjamWriter.close();
       it.close();
       samReader.close();
   }
   
   private SAMRecordSetBuilder getSAMReader(final boolean sortForMe, final SAMFileHeader.SortOrder sortOrder) {
       final SAMRecordSetBuilder ret = new SAMRecordSetBuilder(sortForMe, sortOrder);
       ret.addPair("readB", 20, 200, 300);
       ret.addPair("readA", 20, 100, 150);
       ret.addFrag("readC", 20, 140, true);
       ret.addFrag("readD", 20, 140, false);
       return ret;
   }

   @DataProvider(name = "test1")
   public Object[][] createTestData() {
       return new Object[][] {
               {"coordinate sorted", getSAMReader(false, SAMFileHeader.SortOrder.unsorted), SAMFileHeader.SortOrder.coordinate, false},
/*               {"query sorted", getSAMReader(false, SAMFileHeader.SortOrder.unsorted), SAMFileHeader.SortOrder.queryname, false},
               {"unsorted", getSAMReader(false, SAMFileHeader.SortOrder.unsorted), SAMFileHeader.SortOrder.unsorted, false},
               {"coordinate presorted", getSAMReader(true, SAMFileHeader.SortOrder.coordinate), SAMFileHeader.SortOrder.coordinate, true},
               {"query presorted", getSAMReader(true, SAMFileHeader.SortOrder.queryname), SAMFileHeader.SortOrder.queryname, true},
*/       };
   }
   
   @Test(dataProvider = "test1")
   public void testPositive(final String testName, final SAMRecordSetBuilder samRecordSetBuilder, final SAMFileHeader.SortOrder order, final boolean presorted) throws Exception {

       testHelper(samRecordSetBuilder, order, presorted);
   }
}
