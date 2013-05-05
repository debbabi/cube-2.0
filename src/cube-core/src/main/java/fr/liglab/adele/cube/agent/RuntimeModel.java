package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.metamodel.ManagedElement;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:08 PM
 */
public interface RuntimeModel {

    public void add(ManagedElement me);


    public List<ManagedElement> getManagedElements();
    public List<ManagedElement> getManagedElements(int state);


    public void addListener(RuntimeModelListener listener);

    public void deleteListener(RuntimeModelListener listener);

    public void deleteListeners();

    public boolean hasChanged();

    public ManagedElement getManagedElement(String uuid);
}
