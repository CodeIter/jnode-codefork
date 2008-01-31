package org.jnode.fs.hfsplus.tree;

import org.jnode.util.BigEndian;

public class LeafNode {
	protected NodeDescriptor descriptor;
	protected int[] offsets;
	protected LeafRecord[] records;
	/**
	 * 
	 * @param descriptor
	 * @param nodeData
	 * @param nodeSize
	 */
	public LeafNode(NodeDescriptor descriptor, byte[] nodeData, int nodeSize){
		this.descriptor = descriptor;
		offsets = new int[descriptor.getNumRecords()+1];
		for(int i = 0; i < offsets.length; ++i) {
		    offsets[i] = BigEndian.getInt16(nodeData, nodeSize-((i+1)*2));
		}
		records = new LeafRecord[offsets.length-1];
	}
	
	public NodeDescriptor getDescriptor() {
		return descriptor;
	}
	public int[] getOffsets() {
		return offsets;
	}
	public LeafRecord[] getRecords() {
		return records;
	}
}
