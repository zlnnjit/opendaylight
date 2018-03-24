/*
 * Copyright © 2018 zlnnjit and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.bcoder.initflow.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bcoder.initflow.cli.api.InitflowCliCommands;

public class InitflowCliCommandsImpl implements InitflowCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(InitflowCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public InitflowCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("InitflowCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}