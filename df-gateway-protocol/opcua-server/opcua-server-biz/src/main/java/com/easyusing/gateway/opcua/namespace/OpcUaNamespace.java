package com.easyusing.gateway.opcua.namespace;

import com.easyusing.gateway.opcua.service.impl.AttributeLoggingFilter;
import com.easyusing.gateway.cache.LevelDbCache;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.dtd.DataTypeDictionaryManager;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.util.List;
import java.util.Locale;

/**
 * @author outsider
 * @date 2023/7/29
 */
public abstract class OpcUaNamespace extends ManagedNamespaceWithLifecycle {

    private final DataTypeDictionaryManager dictionaryManager;
    private final SubscriptionModel subscriptionModel;

    protected abstract void createAndAddNodes();

    public OpcUaNamespace(OpcUaServer server, String namespaceUri) {
        super(server, namespaceUri);
        subscriptionModel = new SubscriptionModel(server, this);
        dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), namespaceUri);

        getLifecycleManager().addLifecycle(dictionaryManager);
        getLifecycleManager().addLifecycle(subscriptionModel);

        getLifecycleManager().addStartupTask(this::createAndAddNodes);
    }

    /**
     * description: addDynamicNode 增加动态节点
     * @param: folderNode
     * @param: path
     * @param: nodeId leveldb key
     * @param: displayName
     * @param: value
     * */
    public void addDynamicNode(UaFolderNode folderNode, String path, String nodeId, String displayName, String value) {
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                .setNodeId(newNodeId(path + "/" + nodeId))
                .setAccessLevel(AccessLevel.READ_ONLY)
                .setBrowseName(newQualifiedName(nodeId))
                .setDisplayName(new LocalizedText(Locale.CHINESE.getLanguage(), displayName))
                .setDescription(new LocalizedText(Locale.CHINESE.getLanguage(), displayName))
                .setDataType(Identifiers.String)
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .build();

        node.setValue(new DataValue(new Variant(value)));

        // 动态数据
        node.getFilterChain().addLast(
                new AttributeLoggingFilter(),
                AttributeFilters.getValue(
                        ctx -> {
                            String v = LevelDbCache.get(nodeId);
                            return new DataValue(new Variant(v));
                        })
                );

        /*加入到节点管理器*/
        getNodeManager().addNode(node);
        /*加入到节点分组*/
        folderNode.addOrganizes(node);
    }

    /**
     * description: addStaticNode 增加静态节点
     * @param: folderNode
     * @param: path
     * @param: nodeId
     * @param: displayName
     * @param: value
     * */
    public void addStaticNode(UaFolderNode folderNode, String path, String nodeId, String displayName, String value) {
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                .setNodeId(newNodeId(path + "/" + nodeId))
                .setAccessLevel(AccessLevel.READ_ONLY)
                .setBrowseName(newQualifiedName(nodeId))
                .setDisplayName(new LocalizedText(Locale.CHINESE.getLanguage(), displayName))
                .setDescription(new LocalizedText(Locale.CHINESE.getLanguage(), displayName))
                .setDataType(Identifiers.String)
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .build();

        node.setValue(new DataValue(new Variant(value)));

        node.getFilterChain().addLast(new AttributeLoggingFilter(AttributeId.Value::equals));

        /*加入到节点管理器*/
        getNodeManager().addNode(node);
        /*加入到节点分组*/
        folderNode.addOrganizes(node);
    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }
}
