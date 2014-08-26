package htsjdk.samtools.util;

import htsjdk.samtools.SAMRecord;

/**
 * A little class to store the unique index associated with this record.  The index is determined as records are read in, so is in fact
 * gives the ordinal of the record in the input file.  All sub-classes should have a default constructor.
 */
public abstract class SamRecordIndex {
    private SAMRecord record;
    private long recordIndex;

    public SamRecordIndex() {
        this.record = null;
        this.recordIndex = -1;
    }

    public SamRecordIndex(final SAMRecord record, final long recordIndex) {
        this.record = record;
        this.recordIndex = recordIndex;
    }

    public SAMRecord getRecord() { return this.record; }
    public void setRecord(final SAMRecord record) { this.record = record; }
    public long getRecordIndex() { return this.recordIndex; }
    public void setRecordIndex(final long recordIndex) { this.recordIndex = recordIndex; }

    abstract public void setExaminedState(final boolean examinedState);
}
