/*
 * Copyright © 2018 zlnnjit and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.bcoder.initflow.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bcoder.initflow.listener.NodeChangeListener;

public class InitflowProvider{

    private static final Logger LOG = LoggerFactory.getLogger(InitflowProvider.class);

    private final DataBroker dataBroker;

    public InitflowProvider(final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
    	//注册监听
		NodeChangeListener nodeChangeListener = new NodeChangeListener(dataBroker);

		LOG.info("InitflowProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        LOG.info("InitflowProvider Closed");
    }


}