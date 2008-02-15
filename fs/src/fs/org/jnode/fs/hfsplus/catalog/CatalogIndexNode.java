package org.jnode.fs.hfsplus.catalog;

import org.apache.log4j.Logger;
import org.jnode.fs.hfsplus.tree.IndexNode;
import org.jnode.fs.hfsplus.tree.IndexRecord;
import org.jnode.fs.hfsplus.tree.Key;
import org.jnode.fs.hfsplus.tree.NodeDescriptor;

public class CatalogIndexNode extends IndexNode {
	private final Logger log = Logger.getLogger(getClass());
	public CatalogIndexNode(NodeDescriptor descriptor, byte[] nodeData, int nodeSize){
		super(descriptor, nodeData, nodeSize);
		for(int i = 0; i < records.length; ++i) {
		    int currentOffset = offsets[i];
		    Key currentKey = new CatalogKey(nodeData, currentOffset);
		    records[i] = new IndexRecord(currentKey, nodeData, currentOffset);
		    log.debug("Index record key:" + records[i].getKey().toString());
		}
	}
	/**
	 * 
	 * @param parentId
	 * @return
	 */
	public IndexRecord find(CatalogNodeId parentId){
		for(IndexRecord rec : records) {
			Key key = rec.getKey();
			if(key instanceof CatalogKey) {
				if(((CatalogKey)key).getParentId().getId() == parentId.getId())
					return rec;
			}
		}
		return null;
	}
	
	public IndexRecord find(CatalogKey key){
		IndexRecord largestMatchingRecord = null;
		for(int i = 0; i < records.length; ++i) {
			if(records[i].getKey().compareTo(key) <= 0 && 
					(largestMatchingRecord == null || records[i].getKey().compareTo(largestMatchingRecord.getKey()) > 0)) {
				largestMatchingRecord = records[i];
			}
		}
		return largestMatchingRecord;
	}
}