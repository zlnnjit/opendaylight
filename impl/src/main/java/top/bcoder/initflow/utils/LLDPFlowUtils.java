package top.bcoder.initflow.utils;

import com.google.common.collect.Lists;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class LLDPFlowUtils {

	private final static Logger LOG = LoggerFactory.getLogger(LLDPFlowUtils.class);
	private final static long ETHERTYPE_LLDP = 0x88ccL;
	private final static int MAX_LENGTH = 0xffff;


	public static boolean addLLDPFlow(DataStoreUtils dataStoreUtils,NodeId nodeId) {
		//buildMatch
		Match match = buildMatch();

		//buildInstructions
		Instructions instructions = buildInstructions(nodeId);

		//
		Flow flow = buildFlow(nodeId, match, instructions, (short) 0, 100);

		NodeBuilder nodeBuilder = new NodeBuilder();
		nodeBuilder.setId(nodeId);
		nodeBuilder.setKey(new NodeKey(nodeId));

		return writeFlow(dataStoreUtils,flow, nodeBuilder.build());
	}



	private static Match buildMatch() {
		EthernetTypeBuilder ethernetTypeBuilder = new EthernetTypeBuilder();
		ethernetTypeBuilder.setType(new EtherType(ETHERTYPE_LLDP));

		EthernetMatchBuilder ethernetMatchBuilder = new EthernetMatchBuilder();
		ethernetMatchBuilder.setEthernetType(ethernetTypeBuilder.build());

		MatchBuilder matchBuilder = new MatchBuilder();
		matchBuilder.setEthernetMatch(ethernetMatchBuilder.build());


		return matchBuilder.build();
	}

	private static Instructions buildInstructions(NodeId nodeId) {
		OutputActionBuilder outputActionBuilder = new OutputActionBuilder();
		outputActionBuilder.setMaxLength(MAX_LENGTH);
		outputActionBuilder.setOutputNodeConnector(new NodeConnectorId(nodeId.getValue()+":CONTROLLER"));

		OutputActionCaseBuilder outputActionCaseBuilder = new OutputActionCaseBuilder();
		outputActionCaseBuilder.setOutputAction(outputActionBuilder.build());

		ActionBuilder sendToControllerAction = new ActionBuilder();
		sendToControllerAction.setAction(outputActionCaseBuilder.build());
		sendToControllerAction.setOrder(0);

		List<Action> actions = Lists.newArrayList();
		actions.add(sendToControllerAction.build());
		ApplyActionsBuilder applyActionsBuilder = new ApplyActionsBuilder();
		applyActionsBuilder.setAction(actions);

		ApplyActionsCaseBuilder applyActionsCaseBuilder = new ApplyActionsCaseBuilder();
		applyActionsCaseBuilder.setApplyActions(applyActionsBuilder.build());

		InstructionBuilder instructionBuilder = new InstructionBuilder();
		instructionBuilder.setInstruction(applyActionsCaseBuilder.build()).setOrder(0);

		List<Instruction> instructions = Lists.newArrayList();
		instructions.add(instructionBuilder.build());

		InstructionsBuilder instructionsBuilder = new InstructionsBuilder();
		instructionsBuilder.setInstruction(instructions);

		return instructionsBuilder.build();
	}
	private static Flow buildFlow(NodeId nodeId, Match match, Instructions instructions, short tableId, int proprity) {
		String flowName = "basic-lldp-flow-" + nodeId.getValue();
		FlowBuilder flowBuilder = new FlowBuilder();
		flowBuilder.setMatch(match);
		flowBuilder.setInstructions(instructions);
		flowBuilder.setFlowName(flowName);
		flowBuilder.setPriority(proprity);
		flowBuilder.setTableId((short) 0);
		flowBuilder.setKey(new FlowKey(new FlowId(flowName)));
		flowBuilder.setId(new FlowId(flowName));
		//flowBuilder.setStrict(true);
		//flowBuilder.setBarrier(false);
		flowBuilder.setHardTimeout(0);
		flowBuilder.setIdleTimeout(0);
		flowBuilder.setCookie(new FlowCookie(new BigInteger("11")));

		return flowBuilder.build();
	}

	private static InstanceIdentifier<Flow> createFlowPath(Flow flow, org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node node) {
		return InstanceIdentifier.builder(Nodes.class)
				.child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node.class,
						node.getKey())
				.augmentation(FlowCapableNode.class).child(Table.class, new TableKey(flow.getTableId()))
				.child(Flow.class, flow.getKey()).build();
	}

	private static boolean writeFlow(DataStoreUtils dataStoreUtils, Flow flow, Node node) {
		return dataStoreUtils.put(LogicalDatastoreType.CONFIGURATION, createFlowPath(flow, node), flow);
	}

}
