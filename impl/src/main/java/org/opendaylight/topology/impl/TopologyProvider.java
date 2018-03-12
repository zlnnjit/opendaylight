/*
 * Copyright © 2017 ZebraDecoder and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.topology.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.ListLinksInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.ListLinksInfoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.ListPortsInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.ListPortsInfoOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.TopologyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.list.links.info.output.LinksInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.list.links.info.output.LinksInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.list.ports.info.output.PortsInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.rev180307.list.ports.info.output.PortsInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class TopologyProvider implements TopologyService {

	private static final Logger LOG = LoggerFactory.getLogger(TopologyProvider.class);
	private final DataBroker dataBroker;
	private static final String FLOWID = "flow:1";

	public TopologyProvider(final DataBroker dataBroker) {
		this.dataBroker = dataBroker;
	}

	public void init() {
		LOG.info("TopologyProvider Session Initiated");
	}

	public void close() {
		LOG.info("TopologyProvider Closed");
	}

	@Override
	public Future<RpcResult<ListLinksInfoOutput>> listLinksInfo() {

		final SettableFuture<RpcResult<ListLinksInfoOutput>> futureResult = SettableFuture.create();
		ListLinksInfoOutputBuilder outputBuilder = new ListLinksInfoOutputBuilder();

		final InstanceIdentifier.InstanceIdentifierBuilder<Topology> topologyId = InstanceIdentifier.builder(NetworkTopology.class).
				child(Topology.class, new TopologyKey(new TopologyId(new Uri(FLOWID))));
		InstanceIdentifier<Topology> topologyIId = topologyId.build();
		Topology topology = read(LogicalDatastoreType.OPERATIONAL, topologyIId);

		if (topology == null || topology.getLink() == null || topology.getLink().size() < 1) {
			futureResult.set(RpcResultBuilder.success(outputBuilder.build()).build());
			return futureResult;
		}
		List<LinksInfo> linkInfos = new ArrayList<>();
		topology.getLink().forEach(temp -> {
			LinksInfoBuilder lib = new LinksInfoBuilder();

			lib.setLinkId(temp.getLinkId())
					.setSrcDevice(temp.getSource().getSourceNode())
					.setDstPort(getPort(temp.getSource().getSourceTp().getValue()))
					.setDstDevice(temp.getDestination().getDestNode())
					.setDstPort(getPort(temp.getDestination().getDestTp().getValue()));
			linkInfos.add(lib.build());
		});

		outputBuilder.setLinksInfo(linkInfos);
		futureResult.set(RpcResultBuilder.success(outputBuilder.build()).build());
		return futureResult;
	}


	@Override
	public Future<RpcResult<ListPortsInfoOutput>> listPortsInfo() {

		final SettableFuture<RpcResult<ListPortsInfoOutput>> futureResult = SettableFuture.create();
		ListPortsInfoOutputBuilder listPortsInfoOutputBuilder = new ListPortsInfoOutputBuilder();
		Nodes nodes = queryAllNode(LogicalDatastoreType.OPERATIONAL);
		if (nodes == null || nodes.getNode() == null || nodes.getNode().size() < 1) {
			futureResult.set(RpcResultBuilder.success(listPortsInfoOutputBuilder.build()).build());
			return futureResult;
		}

		List<PortsInfo> portsInfos = new ArrayList<>();
		nodes.getNode().forEach(tempNode -> {
			List<NodeConnector> nodeConnectors = filterNodeConnectors(tempNode);
			if (nodeConnectors == null || nodeConnectors.size() < 1) {
				return;
			}
			nodeConnectors.forEach(tempPort -> {
				PortsInfoBuilder pi = new PortsInfoBuilder();
				FlowCapableNodeConnector augmentation = tempPort.getAugmentation(FlowCapableNodeConnector.class);
				pi.setDeviceId(tempNode.getId())
						.setPortNumber(new String(augmentation.getPortNumber().getValue()))
						.setPortName(augmentation.getName())
						.setHardwareAddress(augmentation.getHardwareAddress().getValue())
						.setLinkDown(augmentation.getState().isLinkDown())
						.setMaximumSpeed(augmentation.getMaximumSpeed())
						.setCurrentSpeed(augmentation.getCurrentSpeed());

				portsInfos.add(pi.build());
			});
		});

		listPortsInfoOutputBuilder.setPortsInfo(portsInfos);
		futureResult.set(RpcResultBuilder.success(listPortsInfoOutputBuilder.build()).build());
		return futureResult;
	}


	private <D extends DataObject> D read(final LogicalDatastoreType store, final InstanceIdentifier<D> path) {
		D result = null;
		final ReadOnlyTransaction transaction = dataBroker.newReadOnlyTransaction();
		Optional<D> optionalDataObject;
		final CheckedFuture<Optional<D>, ReadFailedException> future = transaction.read(store, path);
		try {
			optionalDataObject = future.checkedGet();
			if (optionalDataObject.isPresent()) {
				result = optionalDataObject.get();
			} else {
				LOG.debug("{}: Failed to read {}", Thread.currentThread().getStackTrace()[1], path);
			}
		} catch (final ReadFailedException e) {
			LOG.warn("Failed to read {} ", path, e);
		}
		transaction.close();
		return result;
	}

	private int getPort(String tp) {
		return Integer.parseInt(tp.split(":")[2]);
	}

	private Nodes queryAllNode(LogicalDatastoreType configuration) {
		final InstanceIdentifier<Nodes> identifierNodes = InstanceIdentifier.create(Nodes.class);
		return read(configuration, identifierNodes);
	}

	private List<NodeConnector> filterNodeConnectors(Node node) {
		final List<NodeConnector> connectors = Lists.newArrayList();
		final List<NodeConnector> list = node.getNodeConnector();
		if (list != null && list.size() > 0) {
			for (final NodeConnector nodeConnector : list) {

				if (!nodeConnector.getId().getValue().endsWith("LOCAL")) {
					connectors.add(nodeConnector);
				}
			}
		}
		return connectors;
	}
}