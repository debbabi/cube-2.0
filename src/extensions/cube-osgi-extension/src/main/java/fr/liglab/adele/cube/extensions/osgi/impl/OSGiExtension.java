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


package fr.liglab.adele.cube.extensions.osgi.impl;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.cmf.InvalidNameException;
import fr.liglab.adele.cube.cmf.ManagedElement;
import fr.liglab.adele.cube.cmf.PropertyExistException;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionFactory;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.extensions.core.impl.CoreExtension;
import fr.liglab.adele.cube.extensions.core.model.Node;
import org.osgi.framework.BundleContext;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 11:51 AM
 */
public class OSGiExtension extends AbstractExtension {

    private static final String CUBE_NODE_TYPE = "cube.node.type";
    private static final String CUBE_NODE_ID = "cube.node.id";
    private static int index = 1;

    public OSGiExtension(CubeAgent agent, ExtensionFactory bundle, Properties properties) {
        super(agent, bundle, properties);
    }

    public void run() {
        System.out.println("---------------- OSGi Extension -----------------");
        BundleContext btx = getCubeAgent().getPlatform().getBundleContext();
        String node_type = btx.getProperty(CUBE_NODE_TYPE);
        String node_id = btx.getProperty(CUBE_NODE_ID);

        if (node_type == null) node_type = "OSGi";
        if (node_id == null) node_id = "OSGi-" + index++;

        Properties properties = new Properties();
        try {
            ManagedElement me = getCubeAgent().newManagedElement(CoreExtensionFactory.NAMESPACE, Node.NAME, properties);
            if (me != null && me instanceof Node) {
                ((Node)me).setNodeId(node_id);
                ((Node)me).setNodeType(node_type);
                getCubeAgent().getRuntimeModel().add(me);
            }
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }

    }

    public void stop() {

    }

    public void destroy() {

    }

    public ConstraintResolver getConstraintResolver(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed Element Instance of the given name;
     *
     * @param element_name
     * @return
     */
    public ManagedElement newManagedElement(String element_name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
