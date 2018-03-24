package top.bcoder.initflow.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bcoder.initflow.utils.DataStoreUtils;
import top.bcoder.initflow.utils.LLDPFlowUtils;


public class FlowAdd {
	private final static Logger LOG = LoggerFactory.getLogger(FlowAdd.class);
	private final static long ETHERTYPE_LLDP = 0x88ccL;
	private final static int MAX_LENGTH = 0xffff;
	private static DataStoreUtils dataStoreUtils;

	public FlowAdd(DataStoreUtils dataStoreUtils) {
		this.dataStoreUtils = dataStoreUtils;
	}

	public boolean service(Node add) {
		boolean result = LLDPFlowUtils.addLLDPFlow(dataStoreUtils,new NodeId(add.getNodeId()));
		return result;
	}
}
