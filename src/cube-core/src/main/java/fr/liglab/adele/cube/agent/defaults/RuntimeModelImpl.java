/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.Notification;

import java.util.*;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 9:32 PM
 */
public class RuntimeModelImpl implements RuntimeModel {

    private CubeAgent agent;

    private boolean changed = false;
    private Vector<RuntimeModelListener> listeners;

    Map<String, ManagedElement> elements = new HashMap<String, ManagedElement>();

    private RuntimeModelController controller;

    public RuntimeModelImpl(CubeAgent agent) {
        this.agent = agent;
        controller = new RuntimeModelControllerImpl(this);
        listeners = new Vector<RuntimeModelListener>();
    }

    /**
     * Adds a Managed Element instance to the Runtime Model.
     * It will hold automatically the UNMANAGED state, until it will be resolved!
     *
     * @param me Managed Element instance to be added.
     */
    public void add(ManagedElement me) {
        this.controller.addManagedElement(me);
    }

    public List<ManagedElement> getManagedElements() {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        for (String key : this.elements.keySet()) {
            result.add(this.elements.get(key));
        }
        return result;
    }

    public List<ManagedElement> getManagedElements(int state) {
        List<ManagedElement> result = new ArrayList<ManagedElement>();
        for (String key : this.elements.keySet()) {
            if (this.elements.get(key).getState() == state) {
                result.add(this.elements.get(key));
            }
        }
        return result;
    }

    void validate(ManagedElement me) {
        if (me != null) {
            ((AbstractManagedElement)me).updateState(ManagedElement.VALID);
        }
    }

    void addUnmanagedElement(ManagedElement me) {
        if (me.getState() == ManagedElement.UNMANAGED) {
            this.elements.put(me.getUUID(), me);
            setChanged();
            notifyListeners(new Notification(RuntimeModelListener.NEW_UNMANAGED_INSTANCE, me));
        }
    }
    private void addUncheckedElement(ManagedElement me) {
        if (me != null) {
            ((AbstractManagedElement)me).updateState(ManagedElement.UNCHECKED);
        }
        this.elements.put(me.getUUID(), me);
        //System.out.println("[RM] adding unchecked instance + notification");
        setChanged();
        notifyListeners(new Notification(RuntimeModelListener.NEW_UNCHECKED_INSTANCE, me));
    }

    private void addValidElement(AbstractManagedElement me) {
        Notification n = new Notification(RuntimeModelListener.NEW_VALID_INSTANCE);
    }

    public RuntimeModelController getController() {
        return this.controller;
    }

    /**
     * ================================ NOTIFICATIONS ===============================================
     */

    /**
     * Adds a listener to the set of listeners for this object, provided
     * that it is not the same as some listener already in the set.
     * The order in which notifications will be delivered to multiple
     * listeners is not specified.
     *
     * @param  listener   a Runtime Model Listener to be added.
     * @throws NullPointerException  if the parameter o is null.
     */
     public synchronized void addListener(RuntimeModelListener listener) {
         if (listener == null)
            throw new NullPointerException();
         if (!this.listeners.contains(listener)) {
            listeners.addElement(listener);
         }
     }

    /**
     * Deletes a Listener from the set of listeners of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   listener   the listener to be deleted.
     */
     public synchronized void deleteListener(RuntimeModelListener listener) {
        this.listeners.removeElement(listener);
     }

    /**
     * Clears the listeners list so that this object no longer has any observers.
     */
     public synchronized void deleteListeners() {
         this.listeners.removeAllElements();
     }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   notification   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
     private void notifyListeners(Notification notification) {
         /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Listeners.
         */
         Object[] arrLocal;

         synchronized (this) {
             /* We don't want the Observer doing callbacks into
              * arbitrary code while holding its own Monitor.
              * The code where we extract each Observable from
              * the Vector and store the state of the Observer
              * needs synchronization, but notifying observers
              * does not (should not).  The worst result of any
              * potential race-condition here is that:
              * 1) a newly-added Observer will miss a
              *   notification in progress
              * 2) a recently unregistered Observer will be
              *   wrongly notified when it doesn't care
              */
              if (!changed)
                 return;
              arrLocal = listeners.toArray();
              clearChanged();
          }

          for (int i = arrLocal.length-1; i>=0; i--)
            ((RuntimeModelListener)arrLocal[i]).update(this, notification);
      }

    /**
     * Marks the <tt>RuntimeModelImpl</tt> as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
     private synchronized void setChanged() {
        changed = true;
     }

     /**
      * Indicates that this object has no longer changed, or that it has
      * already notified all of its listeners of its most recent change,
      * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
      * This method is called automatically by the
      * <code>notifyListeners</code> methods.
      *
      */
     protected synchronized void clearChanged() {
        changed = false;
     }

     /**
      * Tests if the Runtime Model has changed.
      *
      * @return  <code>true</code> if and only if the <code>setChanged</code>
      *          method has been called more recently than the
      *          <code>clearChanged</code> method on this object;
      *          <code>false</code> otherwise.
      */
      public synchronized boolean hasChanged() {
        return changed;
      }

    public ManagedElement getManagedElement(String uuid) {
         return this.elements.get(uuid);
    }

}