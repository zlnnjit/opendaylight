/*
 * Copyright © 2017 fnic and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.bcoder.initflow.utils;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataStoreUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DataStoreUtils.class);

    private DataBroker dataBroker;

    public DataStoreUtils(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }


    public DataBroker getDataBroker() {
        return dataBroker;
    }


    /**
     * Executes read as a blocking transaction.
     *
     * @param store {@link LogicalDatastoreType} to read
     * @param path  {@link InstanceIdentifier} for path to read
     * @param <D>   the data object type
     * @return the result as the data object requested
     */
    public <D extends DataObject> D read(final LogicalDatastoreType store, final InstanceIdentifier<D> path) {
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

    /**
     * Executes delete as a blocking transaction.
     *
     * @param store {@link LogicalDatastoreType} which should be modified
     * @param path  {@link InstanceIdentifier} to read from
     * @param <D>   the data object type
     * @return the result of the request
     */
    public <D extends DataObject> boolean delete(final LogicalDatastoreType store,
                                                 final InstanceIdentifier<D> path) {
        boolean result = false;
        final WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();
        transaction.delete(store, path);
        final CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        try {
            future.checkedGet();
            result = true;
        } catch (final TransactionCommitFailedException e) {
            LOG.warn("Failed to delete {} ", path, e);
        }

        return result;
    }

    /**
     * Executes merge as a blocking transaction.
     *
     * @param logicalDatastoreType {@link LogicalDatastoreType} which should be modified
     * @param path                 {@link InstanceIdentifier} for path to read
     * @param <D>                  the data object type
     * @return the result of the request
     */
    public <D extends DataObject> boolean merge(final LogicalDatastoreType logicalDatastoreType,
                                                final InstanceIdentifier<D> path, D data) {
        boolean result = false;
        final WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();
        transaction.merge(logicalDatastoreType, path, data, true);
        final CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        try {
            future.checkedGet();
            result = true;
        } catch (final TransactionCommitFailedException e) {
            LOG.warn("Failed to merge {} ", path, e);
        }

        return result;
    }

    /**
     * Executes put as a blocking transaction.
     *
     * @param logicalDatastoreType {@link LogicalDatastoreType} which should be modified
     * @param path                 {@link InstanceIdentifier} for path to read
     * @param <D>                  the data object type
     * @return the result of the request
     */
    public <D extends DataObject> boolean put(final LogicalDatastoreType logicalDatastoreType,
                                              final InstanceIdentifier<D> path, D data) {
        boolean result = false;
        final WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();
        transaction.put(logicalDatastoreType, path, data, true);
        final CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();
        try {
            future.checkedGet();
            result = true;
        } catch (final TransactionCommitFailedException e) {
            LOG.warn("Failed to put {} ", path, e);
        }

        return result;
    }
}
