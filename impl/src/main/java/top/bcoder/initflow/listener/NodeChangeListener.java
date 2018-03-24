package top.bcoder.initflow.listener;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeChangeListener extends DataTreeListener<Node> {

	private static final Logger LOG = LoggerFactory.getLogger(NodeChangeListener.class);


	public NodeChangeListener(final DataBroker db) {
		super(new NodeDataProcessor(db) , db, new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL,
				InstanceIdentifier.create(NetworkTopology.class).child(Topology.class).child(Node.class)));
		LOG.error("Listener on Node initialed");
	}
}
