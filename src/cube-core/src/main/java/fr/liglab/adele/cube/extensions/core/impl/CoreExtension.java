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


package fr.liglab.adele.cube.extensions.core.impl;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.cmf.InvalidNameException;
import fr.liglab.adele.cube.cmf.ManagedElement;
import fr.liglab.adele.cube.cmf.PropertyExistException;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionFactory;
import fr.liglab.adele.cube.extensions.core.constraints.*;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Master;
import fr.liglab.adele.cube.extensions.core.model.Node;
import fr.liglab.adele.cube.extensions.core.model.Scope;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:58 PM
 */
public class CoreExtension extends AbstractExtension {


    public CoreExtension(CubeAgent agent, ExtensionFactory bundle, Properties properties) {
        super(agent, bundle, properties);
    }


    public void run() {
        //System.out.println("---------------- Core Extension -----------------");
        Object master = getProperties().get("master");
        if (master != null) {
            if (master.toString().equalsIgnoreCase("true")) {
                Master m = new Master(getCubeAgent());
                m.setMasterURI(getCubeAgent().getUri());
                getCubeAgent().getRuntimeModel().add(m);
            }
        }
    }

    public void stop() {

    }

    public void destroy() {

    }

    /**
     * Creates a new Managed Element Instance of the given name;
     *
     * @param element_name
     * @return
     */
    public ManagedElement newManagedElement(String element_name) {
        if (element_name != null) {
            if (element_name.equalsIgnoreCase(Scope.NAME)) {
                return new Scope(getCubeAgent());
            }
            if (element_name.equalsIgnoreCase(Node.NAME)) {
                return new Node(getCubeAgent());
            }
            if (element_name.equalsIgnoreCase(Master.NAME)) {
                return new Master(getCubeAgent());
            }
            if (element_name.equalsIgnoreCase(Component.NAME)) {
                return new Component(getCubeAgent());
            }
        }
        return null;
    }

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    public ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException {
        if (element_name != null) {
            if (element_name.equalsIgnoreCase(Scope.NAME)) {
                return new Scope(getCubeAgent(), properties);
            }
            if (element_name.equalsIgnoreCase(Node.NAME)) {
                return new Node(getCubeAgent(), properties);
            }
            if (element_name.equalsIgnoreCase(Master.NAME)) {
                return new Master(getCubeAgent(), properties);
            }
            if (element_name.equalsIgnoreCase(Component.NAME)) {
                return new Component(getCubeAgent(), properties);
            }
        }
        return null;
    }

    public ConstraintResolver getConstraintResolver(String name) {
        if (name != null) {
            if (name.equalsIgnoreCase("connected")) {
                return Connected.instance();
            }
            if (name.equalsIgnoreCase("controlledby")) {
                return ControlledBy.instance();
            }
            if (name.equalsIgnoreCase("hasscopeid")) {
                return HasScopeId.instance();
            }
            if (name.equalsIgnoreCase("hasnodetype")) {
                return HasNodeType.instance();
            }
            if (name.equalsIgnoreCase("hascomponenttype")) {
                return HasComponentType.instance();
            }
            if (name.equalsIgnoreCase("incube")) {
                return InCube.instance();
            }
            if (name.equalsIgnoreCase("inScope")) {
                return InScope.instance();
            }
            if (name.equalsIgnoreCase("onnode")) {
                return OnNode.instance();
            }
        }
        return null;
    }
}
