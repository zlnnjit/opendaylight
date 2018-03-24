package top.bcoder.initflow.listener;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bcoder.initflow.impl.FlowAdd;
import top.bcoder.initflow.utils.DataStoreUtils;

public class NodeDataProcessor implements DataProcessor<Node> {
	private static final Logger LOG = LoggerFactory.getLogger(NodeDataProcessor.class);

	private final DataStoreUtils dataStoreUtils;

	public NodeDataProcessor(DataBroker db) {
		dataStoreUtils = new DataStoreUtils(db);
	}

	@Override
	public void remove(InstanceIdentifier<Node> identifier, Node del) {
		LOG.info("Remove Node Process : {}", del);
	}

	@Override
	public void update(InstanceIdentifier<Node> identifier, Node original, Node update) {
		LOG.info("Update Node Process : {}", update);
	}

	@Override
	public void add(InstanceIdentifier<Node> identifier, Node add) {
		LOG.error("Add Node Process : {}", add);

		FlowAdd flowAdd = new FlowAdd(dataStoreUtils);
		boolean result = flowAdd.service(add);
		if (result) {
			LOG.error("Add Basic Flows Success!");
		} else {
			LOG.error("Add Basic Flows Failed");
		}
	}
}
