package top.bcoder.initflow.listener;

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public interface DataProcessor<D extends DataObject> {
	 /**
     * Method removes DataObject which is identified by InstanceIdentifier.
     *
     * @param identifier - the whole path to DataObject
     * @param del - DataObject for removing
     */
    void remove(InstanceIdentifier<D> identifier, D del);

    /**
     * Method updates the original DataObject to the update DataObject.
     * Both are identified by same InstanceIdentifier.
     *
     * @param identifier - the whole path to DataObject
     * @param original - original DataObject (for update)
     * @param update - changed DataObject (contain updates)
     */
    void update(InstanceIdentifier<D> identifier, D original, D update);

    /**
     * Method adds the DataObject which is identified by InstanceIdentifier
     * to device.
     *
     * @param identifier - the whole path to new DataObject
     * @param add - new DataObject
     */
    void add(InstanceIdentifier<D> identifier, D add);

}
